package com.pxf.fftv.plus.contract.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.PermissionActivity;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.bean.BaseDataBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.common.ModernDialog;
import com.pxf.fftv.plus.custom.shine.FocusBorder;
import com.pxf.fftv.plus.model.account.Account;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.account.LoginResult;
import com.pxf.fftv.plus.BuildConfig;
import com.pxf.fftv.plus.contract.UpdateActivity;
import com.pxf.fftv.plus.contract.UpdateEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.ACCOUNT_NO_VIP;

import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.HOME_PAGE_PERSON;
import static com.pxf.fftv.plus.Const.VIDEO_2;
import static com.pxf.fftv.plus.Const.VIDEO_3;
import static com.pxf.fftv.plus.Const.VIDEO_1;
import static com.pxf.fftv.plus.Const.VIDEO_4;

public class HomeActivity extends AppCompatActivity implements FocusBorder.OnFocusCallback {

    TextView home_tv_person;

    TextView home_tv_title_0;

    TextView home_tv_title_1;

    TextView home_tv_title_2;

    TextView home_tv_title_3;

    TextView home_tv_title_4;

    TextView home_tv_title_5;

    ViewPager home_view_pager;

    TextView top_bar_menu_right_note;

    private FocusBorder mFocusBorder;
    private PagerAdapter mPagerAdapter;
    private TextView currentTabTitle;
    private HashSet<TextView> tabSet = new HashSet<>();

    private ArrayList<TextView> titleList = new ArrayList<>();

    private VideoConfig.Video1[] video1s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home_tv_person = findViewById(R.id.home_tv_person);
        home_tv_title_0 = findViewById(R.id.home_tv_title_0);
        home_tv_title_1 = findViewById(R.id.home_tv_title_1);
        home_tv_title_2 = findViewById(R.id.home_tv_title_2);
        home_tv_title_3 = findViewById(R.id.home_tv_title_3);
        home_tv_title_4 = findViewById(R.id.home_tv_title_4);
        home_tv_title_5 = findViewById(R.id.home_tv_title_5);

        // Setup circular focus navigation for tabs
        setupTabFocusNavigation();
        home_view_pager = findViewById(R.id.home_view_pager);
        top_bar_menu_right_note = findViewById(R.id.top_bar_menu_right_note);

        // 检查权限并动态申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionActivity.needRequestPermission(this)) {
                return;
            }
        }

        FFTVApplication.screenWidth = CommonUtils.getScreenResolutions(this)[0];
        FFTVApplication.screenHeight = CommonUtils.getScreenResolutions(this)[1];

        initView();
        autoLogin();
        initBaseData();

        // 绑定流光特效回调
        mFocusBorder = new FocusBorder.Builder().asColor().shimmerColor(0x55FFFFFF).build(this);
        mFocusBorder.boundGlobalFocusListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVideoEngineChangeEvent(VideoEngineChangeEvent event) {
        refreshVideoEngine();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCmsUrlChangeEvent(CmsUrlChangeEvent event) {
        // CMS URL changed, need to refresh all video data
        refreshVideoEngine();
        initBaseData();
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void initView() {
        Ui.configTopBar(this);

        titleList.add(home_tv_title_0);
        titleList.add(home_tv_title_1);
        titleList.add(home_tv_title_2);
        titleList.add(home_tv_title_3);
        titleList.add(home_tv_title_4);
        titleList.add(home_tv_title_5);
        initGongGao();
        refreshVideoEngine();
    }

    private void refreshVideoEngine() {
        switch (Model.getData().getVideoEngine(this)) {
            case VIDEO_2:
                video1s = VideoConfig.getVideoConfigCms();
                break;
            case VIDEO_3:
                video1s = VideoConfig.VIDEO_CONFIG_OK;
                break;
            case VIDEO_1:
                video1s = VideoConfig.VIDEO_CONFIG_WEIDUO;
                break;
            case VIDEO_4:
                video1s = VideoConfig.VIDEO_CONFIG_ZD;
                break;
        }

        configTabText(home_tv_person, new FocusAction() {
            @Override
            public void onFocus() {
                home_view_pager.setCurrentItem(HOME_PAGE_PERSON);
            }
        });

        // 配置标题
        for (int i = 0; i < video1s.length; i++) {
            titleList.get(i).setVisibility(View.VISIBLE);
            titleList.get(i).setText(video1s[i].getTitle());
            int finalI = i;
            configTabText(titleList.get(i), new FocusAction() {
                @Override
                public void onFocus() {
                    home_view_pager.setCurrentItem(finalI + 1);
                }
            });
        }

        // 隐藏多余标题
        for (int i = video1s.length; i < titleList.size(); i++) {
            titleList.get(i).setVisibility(View.GONE);
        }

        // 清楚缓存以便于刷新
        if (home_view_pager.getAdapter() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
            }
        }

        mPagerAdapter = new HomePageAdapter(getSupportFragmentManager(), video1s, titleList);
        home_view_pager.setAdapter(mPagerAdapter);
        home_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                handleTabChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        home_view_pager.setOffscreenPageLimit(Const.HOME_PAGE_COUNT);

        // 第一次启动焦点
        int firstFocusIndex = 0;
        currentTabTitle = titleList.get(firstFocusIndex);
        titleList.get(firstFocusIndex).requestFocus();
        home_view_pager.setCurrentItem(1 + firstFocusIndex);
    }

    private void handleTabChange(int position) {
        if (position == 0) {
            currentTabTitle = home_tv_person;
        } else if (position > 0) {
            currentTabTitle = titleList.get(position - 1);
        }
        if (currentTabTitle != getCurrentFocus()) {
            setAllTabTextColorDefault();
            currentTabTitle.setTextColor(getResources().getColor(R.color.colorTabTextCurrentButNoFocus));
        }
    }

    private void configTabText(TextView textView, @Nullable FocusAction focusAction) {
        tabSet.add(textView);

        textView.setOnFocusChangeListener((view, focus) -> {
            if (focus) {
                currentTabTitle = textView;
                setAllTabTextColorDefault();
                textView.setTextColor(getResources().getColor(R.color.colorTextFocus));
                textView.setBackground(getDrawable(R.drawable.bg_text_focus));

                // Set ViewPager's left focus to current tab (not always 我的)
                if (home_view_pager != null) {
                    home_view_pager.setNextFocusLeftId(textView.getId());
                    home_view_pager.setNextFocusUpId(textView.getId());
                }

                if (focusAction != null) {
                    focusAction.onFocus();
                }
                ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.1f).setDuration(ANIMATION_DURATION);
                animator.addUpdateListener(valueAnimator -> {
                    if (!isFinishing()) {
                        textView.setScaleX((float) valueAnimator.getAnimatedValue());
                        textView.setScaleY((float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.setInterpolator(new OvershootInterpolator());
                animator.start();
            } else {
                if (!(getCurrentFocus() instanceof TextView) || tabSet.contains(getCurrentFocus())) {
                    currentTabTitle = textView;
                    textView.setTextColor(getResources().getColor(R.color.colorTabTextCurrentButNoFocus));
                }
                textView.setBackground(null);
                if (focusAction != null) {
                    focusAction.onLoseFocus();
                }

                ValueAnimator animator = ValueAnimator.ofFloat(1.1f, 1.0f).setDuration(ANIMATION_DURATION);
                animator.addUpdateListener(valueAnimator -> {
                    if (!isFinishing()) {
                        textView.setScaleX((float) valueAnimator.getAnimatedValue());
                        textView.setScaleY((float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();
            }
        });

        textView.setTextColor(getResources().getColor(R.color.colorTextNormal));
        textView.setBackground(null);
    }

    private void setAllTabTextColorDefault() {
        for (TextView textView : tabSet) {
            textView.setTextColor(getResources().getColor(R.color.colorTextNormal));
        }
    }

    private void setupTabFocusNavigation() {
        // Create array of all tab TextViews in order
        TextView[] tabs = {
                home_tv_person,
                home_tv_title_0,
                home_tv_title_1,
                home_tv_title_2,
                home_tv_title_3,
                home_tv_title_4,
                home_tv_title_5
        };

        // Setup circular navigation: last tab's right goes to first tab
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] != null) {
                // Set next focus right to the next tab (or wrap to first)
                int nextIndex = (i + 1) % tabs.length;
                tabs[i].setNextFocusRightId(tabs[nextIndex].getId());

                // Set next focus left to the previous tab (or wrap to last)
                int prevIndex = (i - 1 + tabs.length) % tabs.length;
                tabs[i].setNextFocusLeftId(tabs[prevIndex].getId());
            }
        }
    }

    private void initGongGao() {
        top_bar_menu_right_note.setSingleLine(true);
        top_bar_menu_right_note.setSelected(true);
    }

    private void autoLogin() {
        if (Model.getData().isAutoLogin(this)) {
            String name = Model.getData().getAccount(this);
            String password = Model.getData().getPassword(this);
            if (!name.isEmpty() && !password.isEmpty()) {
                FFTVApplication.token = "";
                FFTVApplication.login = false;
                FFTVApplication.account = "";
                FFTVApplication.password = "";
                FFTVApplication.vipDate = ACCOUNT_NO_VIP;
                Observable
                        .create((ObservableOnSubscribe<LoginResult>) emitter -> {
                            Account account = new Account();
                            account.setUsername(name);
                            account.setPassword(password);
                            emitter.onNext(Model.getAccountModel().login(account));
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<LoginResult>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(LoginResult loginResult) {
                                if (loginResult.isSuccess()) {
                                    Toast.makeText(HomeActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                                    FFTVApplication.login = true;
                                    FFTVApplication.account = name;
                                    FFTVApplication.password = password;
                                    FFTVApplication.vipDate = loginResult.getExpirationDate();
                                    FFTVApplication.token = loginResult.getToken();
                                } else {
                                    Toast.makeText(HomeActivity.this, "自动登录失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        }
    }

    private void initBaseData() {
        Observable
                .create((ObservableOnSubscribe<BaseDataBean>) emitter -> {
                    Response response = null;
                    try {
                        Request request = new Request.Builder()
                                .url(Const.getBaseDataUrl())
                                .build();
                        response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            emitter.onNext(
                                    CommonUtils.getGson().fromJson(result.trim().substring(2), BaseDataBean.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (response != null) {
                            response.close();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataBean baseDataBean) {
                        FFTVApplication.weiduo_analysis_play_url = baseDataBean.getHBmr();
                        FFTVApplication.baseDataBean = baseDataBean;
                        try {
                            FFTVApplication.VIP_MODE = Integer.parseInt(baseDataBean.getHBms());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Check for app updates on startup
                        checkForUpdates(baseDataBean);

                        // Update banner message from API
                        updateBanner(baseDataBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 检查应用更新
     * 如果服务器返回的版本号高于当前版本，显示更新对话框
     */
    private void checkForUpdates(BaseDataBean baseDataBean) {
        if (baseDataBean == null || baseDataBean.getHBbb() == null) {
            return; // No version info available
        }

        try {
            String serverVersion = baseDataBean.getHBbb();
            String currentVersion = BuildConfig.VERSION_NAME;

            // Compare versions: server version > current version means update available
            if (Double.parseDouble(serverVersion) > Double.parseDouble(currentVersion)) {
                // 延迟显示更新对话框，等待页面加载完成
                new Handler().postDelayed(() -> {
                    if (!isFinishing()) {
                        // 有可用新版本，显示更新对话框
                        Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
                        startActivity(intent);

                        EventBus.getDefault().postSticky(new UpdateEvent(
                                "版本更新至 " + serverVersion,
                                baseDataBean.getHBnr() != null ? baseDataBean.getHBnr() : "新版本已发布",
                                baseDataBean.getHBxz() != null ? baseDataBean.getHBxz() : "",
                                baseDataBean.isForceUpdate()));
                    }
                }, 2000); // 延迟2秒显示，让用户先看到主界面
            }
        } catch (NumberFormatException e) {
            // 版本号解析失败，忽略更新检查
            e.printStackTrace();
        }
    }

    /**
     * 更新公告消息
     * 从服务器获取HBgg字段并显示在顶部滚动栏
     */
    private void updateBanner(BaseDataBean baseDataBean) {
        if (baseDataBean != null && baseDataBean.getHBgg() != null && !baseDataBean.getHBgg().isEmpty()) {
            top_bar_menu_right_note.setText(baseDataBean.getHBgg());
        }
    }

    @Override
    public void onBackPressed() {
        ModernDialog.showConfirm(this, "是否确认退出？",
                () -> finish(), // 确定
                () -> {
                } // 取消
        );
    }

    @Override
    public FocusBorder.Options onFocus(View previousFocus, View currentFocus) {
        if (currentFocus != null) {
            int id = currentFocus.getId();
            if (id == R.id.home_tv_person ||
                    id == R.id.home_tv_title_0 ||
                    id == R.id.home_tv_title_1 ||
                    id == R.id.home_tv_title_2 ||
                    id == R.id.home_tv_title_3 ||
                    id == R.id.home_tv_title_4 ||
                    id == R.id.home_tv_title_5 ||
                    id == R.id.top_bar_menu_root_home ||
                    id == R.id.top_bar_menu_root_search ||
                    id == R.id.top_bar_menu_root_vip ||
                    id == R.id.top_bar_menu_root_tv_live ||
                    id == R.id.top_bar_menu_root_iptv ||
                    id == R.id.top_bar_menu_root_history ||
                    id == R.id.video_root_ad) {
                mFocusBorder.setVisible(false);
                return null;
            }
        }
        return FocusBorder.OptionsFactory.get(0);
    }
}
