package com.pxf.fftv.plus.contract.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.RefreshTokenBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.collect.VideoCollect;
import com.pxf.fftv.plus.contract.personal.AccountActivity;
import com.pxf.fftv.plus.model.Model;
import com.pxf.fftv.plus.model.video.Video;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoBean2;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoBean3;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoEngine;
import com.pxf.fftv.plus.player.AutoNextEvent;
import com.pxf.fftv.plus.player.VideoPlayer;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.ACCOUNT_EVER_VIP;
import static com.pxf.fftv.plus.Const.ACCOUNT_NO_VIP;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.REFRESH_TOKEN_URL;
import static com.pxf.fftv.plus.Const.RETURN_URL;

public class VideoDetailActivity extends AppCompatActivity implements VideoPlayListAdapter.OnPartClickListener {



    @BindView(R.id.detail_root)
    View detail_root;

    @BindView(R.id.top_bar_menu_root_home)
    View top_bar_menu_root_home;

    @BindView(R.id.top_bar_menu_root_search)
    View top_bar_menu_root_search;

    @BindView(R.id.detail_root_content)
    View detail_root_content;

    @BindView(R.id.detail_tv_content)
    TextView detail_tv_content;

    @BindView(R.id.detail_tv_content_more)
    TextView detail_tv_content_more;

    @BindView(R.id.detail_menu_root_collect)
    View detail_menu_root_collect;

    @BindView(R.id.detail_iv_collect)
    ImageView detail_iv_collect;

    @BindView(R.id.detail_tv_collect)
    TextView detail_tv_collect;

    @BindView(R.id.video_detail_recycler_view)
    RecyclerView video_detail_recycler_view;

    @BindView(R.id.detail_iv_image)
    ImageView detail_iv_image;

    @BindView(R.id.detail_tv_title)
    TextView detail_tv_title;

    @BindView(R.id.detail_tv_video_tag)
    TextView detail_tv_video_tag;

    @BindView(R.id.detail_tv_director)
    TextView detail_tv_director;

    @BindView(R.id.detail_tv_actor)
    TextView detail_tv_actor;

    @BindView(R.id.detail_menu_root_return_error)
    View detail_menu_root_return_error;

    @BindView(R.id.detail_iv_return_error)
    ImageView detail_iv_return_error;

    @BindView(R.id.detail_tv_return_error)
    TextView detail_tv_return_error;

    @BindView(R.id.top_bar_menu_right_note)
    TextView top_bar_menu_right_note;

    //20220910 - Added new feature sorting
    @BindView(R.id.detail_tv_sort)
    View detail_tv_sort;

    @BindView(R.id.tvSort)
    TextView tvSort;

    @BindView(R.id.detail_tv_sort_image)
    ImageView detail_tv_sort_image;

    private VideoPlayListAdapter mAdapter;
    private Video mVideo;

    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);

        initView();

        mDisposable = new CompositeDisposable();
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
        mDisposable.clear();
    }

    private void initView() {
        top_bar_menu_root_home.setNextFocusDownId(R.id.detail_root_content);
        top_bar_menu_root_search.setNextFocusDownId(R.id.detail_root_content);
        initGongGao();
        int[] bgArray = new int[]{R.drawable.bg_detail_1, R.drawable.bg_detail_2, R.drawable.bg_detail_3, R.drawable.bg_detail_4};
        detail_root.setBackground(getResources().getDrawable(bgArray[(int) (Math.random() * 4)]));

        Ui.configTopBar(this);
        setContentFocusAnimator(this, detail_root_content, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_content.setTextColor(getResources().getColor(R.color.colorTextFocus));
                detail_tv_content_more.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_tv_content.setTextColor(getResources().getColor(R.color.colorTextNormal));
                detail_tv_content_more.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        Ui.setViewFocusScaleAnimator(detail_menu_root_collect, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_collect.setTextColor(getResources().getColor(R.color.colorTextFocus));
                detail_menu_root_collect.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_focus));
                if (detail_tv_collect.getText().equals("收藏")) {
                    detail_iv_collect.setImageResource(R.drawable.ic_collected_focus);
                } else {
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_focus);
                }
            }

            @Override
            public void onLoseFocus() {
                detail_menu_root_collect.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_normal));
                detail_tv_collect.setTextColor(getResources().getColor(R.color.colorTextNormal));
                if (detail_tv_collect.getText().equals("收藏")) {
                    detail_iv_collect.setImageResource(R.drawable.ic_collected_normal);
                } else {
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_normal);
                }
            }
        });
        Ui.setViewFocusScaleAnimator(detail_menu_root_return_error, new FocusAction() {
            @Override
            public void onFocus() {
                detail_menu_root_return_error.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_focus));
                detail_iv_return_error.setImageResource(R.drawable.ic_return_error_focus);
                detail_tv_return_error.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_menu_root_return_error.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_normal));
                detail_iv_return_error.setImageResource(R.drawable.ic_return_error_normal);
                detail_tv_return_error.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });
        //20220910 - Added new feature sorting
        Ui.setViewFocusScaleAnimator(detail_tv_sort, new FocusAction() {
            @Override
            public void onFocus() {
                detail_tv_sort.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_focus));
                detail_tv_sort_image.setImageResource(R.drawable.ic_sort_focus);
                tvSort.setTextColor(getResources().getColor(R.color.colorTextFocus));
            }

            @Override
            public void onLoseFocus() {
                detail_tv_sort.setBackground(getResources().getDrawable(R.drawable.bg_common_menu_normal));
                detail_tv_sort_image.setImageResource(R.drawable.ic_sort_normal);
                tvSort.setTextColor(getResources().getColor(R.color.colorTextNormal));
            }
        });

        video_detail_recycler_view.setLayoutManager(new GridLayoutManager(this, 8));


    }

    private void initGongGao(){
        top_bar_menu_right_note.setSingleLine(true);
        top_bar_menu_right_note.setSelected(true);
    }

    private static void setContentFocusAnimator(Activity activity, View view, FocusAction action) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                view.setBackground(activity.getResources().getDrawable(R.drawable.video_detail_content_focus));
                if (action != null) {
                    action.onFocus();
                }

                ValueAnimator animatorFirst = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.02f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)
                ).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.02f, 1.01f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1.1f)
                ).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                        view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                        view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                view.setBackground(null);
                if (action != null) {
                    action.onLoseFocus();
                }
                ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                        PropertyValuesHolder.ofFloat("scaleX", 1.01f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1.0f)
                ).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    view.setScaleX((float) animation.getAnimatedValue("scaleX"));
                    view.setScaleY((float) animation.getAnimatedValue("scaleY"));
                });
                animator.start();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVideoDetailEvent(VideoDetailEvent event) {
        mVideo = event.getVideo();
        EventBus.getDefault().removeStickyEvent(event);

        // 加载图片和名称
        detail_tv_title.setText(mVideo.getTitle());
        GlideApp.with(this).load(mVideo.getImageUrl()).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).into(detail_iv_image);

        // 收藏判断
        LinkedList<VideoCollect> videoCollectList = (LinkedList<VideoCollect>) InternalFileSaveUtil.getInstance(this).get("video_collect");
        if (videoCollectList != null) {
            for (int i = 0; i < videoCollectList.size(); i++) {
                if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                        && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                    detail_tv_collect.setText("取消收藏");
                    detail_iv_collect.setImageResource(R.drawable.ic_not_collected_normal);
                    break;
                }
            }
        }

        if (mVideo.getWeiduoUrl() != null && !mVideo.getWeiduoUrl().isEmpty()) {
            // 维多解析
            DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        loadData();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            Observable
                    .create((ObservableOnSubscribe<Boolean>) emitter -> {
                        Request request = new Request.Builder()
                                .url(WeiduoVideoEngine.ANALYSIS_URL + mVideo.getWeiduoUrl())
                                .build();
                        Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            WeiduoVideoBean2 bean = CommonUtils.getGson().fromJson(response.body().string(), WeiduoVideoBean2.class);
                            if (bean.getError() == 0) {
                                mVideo.setTypeText(bean.getData().getType());
                                mVideo.setArea(bean.getData().getArea());
                                mVideo.setYear(bean.getData().getYear());
                                mVideo.setDescription(bean.getData().getDesc());

                                ArrayList<Video.Director> directors = new ArrayList<>();
                                Video.Director director = new Video.Director();
                                director.setName(bean.getData().getDirector());
                                directors.add(director);
                                mVideo.setDirectors(directors);

                                ArrayList<Video.Actor> actors = new ArrayList<>();
                                String[] stars = bean.getData().getStar().split("/ ");
                                for (String s : stars) {
                                    Video.Actor actor = new Video.Actor();
                                    actor.setName(s);
                                    actors.add(actor);
                                }
                                mVideo.setActors(actors);

                                ArrayList<Video.Part> parts = new ArrayList<>();
                                if ((mVideo.getTypeName() != null && mVideo.getTypeName().equals("电影")) ||
                                        (mVideo.getVideoEngineParam() != null && mVideo.getVideoEngineParam().getVideo1Title().equals("电影"))) {
                                    for (int i = 0; i < bean.getData().getUrls().size(); i++) {
                                        WeiduoVideoBean2.DataBean.UrlsBean urlsBean = bean.getData().getUrls().get(i);
                                        if (!urlsBean.getList().isEmpty()) {
                                            Video.Part part = new Video.Part();
                                            part.setUrl(urlsBean.getList().get(0).getLink());
                                            switch (urlsBean.getType()) {
                                                case "imgo":
                                                    part.setTitle("线路" + (i + 1) + "-芒果TV");
                                                    break;
                                                case "qq":
                                                    part.setTitle("线路" + (i + 1) + "-腾讯视频");
                                                    break;
                                                case "qiyi":
                                                    part.setTitle("线路" + (i + 1) + "-爱奇艺");
                                                    break;
                                                default:
                                                    part.setTitle("线路" + (i + 1));
                                                    break;
                                            }
                                            parts.add(part);
                                        }
                                    }
                                } else {
                                    if (!bean.getData().getUrls().isEmpty() && !bean.getData().getUrls().get(0).getList().isEmpty()) {
                                        for (int i = 0; i < bean.getData().getUrls().get(0).getList().size(); i++) {
                                            Video.Part part = new Video.Part();
                                            part.setUrl(bean.getData().getUrls().get(0).getList().get(i).getLink());
                                            part.setTitle(bean.getData().getUrls().get(0).getList().get(i).getTitle());
                                            parts.add(part);
                                        }
                                    }
                                }
                                mVideo.setParts(parts);

                                emitter.onNext(true);
                            }
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        } else {
            loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onAutoNextEvent(AutoNextEvent event) {
        int lastPart = event.getPosition();
        EventBus.getDefault().removeStickyEvent(event);

        // 自动播放下一集
        if (Const.FEATURE_7) {
            if ((lastPart + 1) < mVideo.getParts().size() && lastPart >= 0) {
                onPartClick(lastPart + 1);
            }
        }
    }

    @Override
    public void onPartClick(int position) {
        String url = mVideo.getParts().get(position).getUrl();
        // 保存url和名称作为反馈用
        String saveReturnUrl = url.substring(url.indexOf("://") + 3);
        if (saveReturnUrl.indexOf("?from=") > 0) {
            saveReturnUrl = saveReturnUrl.substring(0, saveReturnUrl.indexOf("?from="));
        }
        Model.getData().setLastPlayUrl(this, saveReturnUrl);

        Model.getData().setLastPlayName(this, mVideo.getTitle() + mVideo.getParts().get(position).getTitle());

        if (mVideo.getWeiduoUrl() != null && !mVideo.getWeiduoUrl().isEmpty()) {
            // 维多解析
            DisposableObserver<String[]> observer = new DisposableObserver<String[]>() {
                @Override
                public void onNext(String[] url) {
                    if (url[0].isEmpty()) {
                        Toast.makeText(VideoDetailActivity.this, "该视频暂时无法播放", Toast.LENGTH_SHORT).show();
                    } else {
                        playVideo(url[0], position);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            Observable
                    .create((ObservableOnSubscribe<String[]>) emitter -> {
                        String[] result = new String[2];

                        Request request = new Request.Builder()
                                .url(FFTVApplication.weiduo_analysis_play_url + url)
                                .build();
                        Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                String resultResponse = response.body().string();
                                WeiduoVideoBean3 bean = CommonUtils.getGson().fromJson(resultResponse, WeiduoVideoBean3.class);
                                if (bean.getCode().equals("200")) {
                                    result[0] = bean.getUrl();
                                } else {
                                    result[0] = "";
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            result[0] = "";
                        }

                       /* Request requestNext = new Request.Builder()
                                .url(FFTVApplication.weiduo_analysis_play_url + nextUrl)
                                .build();
                        Response responseNext = CommonUtils.getOkHttpClient().newCall(requestNext).execute();
                        try {
                            if (responseNext.isSuccessful() && responseNext.body() != null) {
                                String resultResponse = responseNext.body().string();
                                WeiduoVideoBean3 bean = CommonUtils.getGson().fromJson(resultResponse, WeiduoVideoBean3.class);
                                if (bean.getCode().equals("200")) {
                                    result[1] = bean.getUrl();
                                } else {
                                    result[1] = "";
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            result[1] = "";
                        }*/

                        emitter.onNext(result);
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            mDisposable.add(observer);
        } else {
            playVideo(url, position);
        }
    }

    private void playVideo(String url, int currentPart) {
        if (!url.startsWith("http")) {
            Toast.makeText(VideoDetailActivity.this, "线路资源错误无法播放，请更换其它线路", Toast.LENGTH_LONG).show();
            url = "https://" + url;
            // return;
        }

        // 判断账号是否登录
        //if (!BuildConfig.DEBUG) {
        if (false) {
            if (FFTVApplication.token.isEmpty()) {
                Toast.makeText(VideoDetailActivity.this, "请先登录账号", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return;
            }
        }

        String finalUrl = url;

        //if (BuildConfig.DEBUG || (Const.FEATURE_12 && FFTVApplication.VIP_MODE == 0)) {
        if (true) {
            Log.wtf("Play From -", "<"+mVideo.getVodPlayFrom()+">");
            switch (mVideo.getVodPlayFrom()){
                case "wjm3u8":
                    Log.wtf("VideoDetailActivity","Set Player to IJKPlayer "+ mVideo.getVodPlayFrom());
                    if (finalUrl.contains("cdtlas")){
                        VideoPlayer.getVideoPlayer(4).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                    }else{
                        VideoPlayer.getVideoPlayer(3).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                    }
                    break;
                case "fsm3u8":
                case "wolong":
                    Log.wtf("VideoDetailActivity","Set Player to EXOPlayer " + mVideo.getVodPlayFrom());
                    VideoPlayer.getVideoPlayer(4).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                    break;
            }
            //VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
            //VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
        } else {
            if (Const.FEATURE_11) {
                // 限制多端登录
                String requestBody = "username=" + FFTVApplication.account + "&token=" + FFTVApplication.token;
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, requestBody);
                final Request request = new Request.Builder()
                        .url(REFRESH_TOKEN_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                DisposableObserver<String> observer = new DisposableObserver<String>() {
                    @Override
                    public void onNext(String result) {
                        switch (result) {
                            case "112":
                                Toast.makeText(VideoDetailActivity.this, "账号已被禁用，无法观看视频", Toast.LENGTH_LONG).show();
                                break;
                            case "151":
                                Toast.makeText(VideoDetailActivity.this, "账号已在其他设备上登录，请重新登录账号", Toast.LENGTH_LONG).show();
                                // 注销账号
                                FFTVApplication.token = "";
                                FFTVApplication.login = false;
                                FFTVApplication.account = "";
                                FFTVApplication.password = "";
                                FFTVApplication.vipDate = ACCOUNT_NO_VIP;
                                break;
                            default:
                                try {
                                    RefreshTokenBean bean = CommonUtils.getGson().fromJson(result, RefreshTokenBean.class);
                                    FFTVApplication.token = bean.getOnline();
                                    // 验证通过开始播放
                                    if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
                                        VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                                    } else if (FFTVApplication.vipDate < System.currentTimeMillis()) {
                                        Toast.makeText(VideoDetailActivity.this, "您的VIP会员已过期，请及时续费", Toast.LENGTH_LONG).show();
                                    } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
                                        Toast.makeText(VideoDetailActivity.this, "您还不是VIP会员，无法观看", Toast.LENGTH_LONG).show();
                                    } else {
                                        VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(VideoDetailActivity.this, "账号验证异常，请联系管理员", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };

                Observable
                        .create((ObservableOnSubscribe<String>) emitter -> {
                            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                String result = response.body().string().substring(2).trim();
                                emitter.onNext(result);
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
                mDisposable.add(observer);
            } else {
                if (FFTVApplication.vipDate == ACCOUNT_EVER_VIP) {
                    VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                } else if (FFTVApplication.vipDate < System.currentTimeMillis()) {
                    Toast.makeText(VideoDetailActivity.this, "您的VIP会员已过期，请及时续费", Toast.LENGTH_LONG).show();
                } else if (FFTVApplication.vipDate == ACCOUNT_NO_VIP) {
                    Toast.makeText(VideoDetailActivity.this, "您还不是VIP会员，无法观看", Toast.LENGTH_LONG).show();
                } else {
                    VideoPlayer.getVideoPlayer(VideoDetailActivity.this).play(VideoDetailActivity.this, finalUrl, mVideo.getTitle(), mVideo.getParts().get(currentPart).getTitle(), currentPart, mVideo.getImageUrl(), -1);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        String tag = mVideo.getArea() + " · " + mVideo.getYear() + " · " + mVideo.getTypeText();
        if (mVideo.getLanguage() != null && !mVideo.getLanguage().isEmpty()) {
            tag = tag + " · " + mVideo.getLanguage();
        }
        detail_tv_video_tag.setText(tag);

        StringBuilder directors = new StringBuilder();
        directors.append("导演：");
        for (int i = 0; i < mVideo.getDirectors().size(); i++) {
            directors.append(mVideo.getDirectors().get(i).getName()).append(" ");
        }
        directors.substring(0, directors.length() - 1);
        detail_tv_director.setText(directors.toString());

        StringBuilder actors = new StringBuilder();
        actors.append("主演：");
        for (int i = 0; i < mVideo.getActors().size(); i++) {
            actors.append(mVideo.getActors().get(i).getName()).append(" ");
        }
        actors.substring(0, actors.length() - 1);
        detail_tv_actor.setText(actors.toString());

        detail_tv_content.setText("简介：" + mVideo.getDescription());

        mAdapter = new VideoPlayListAdapter(this, mVideo.getParts(), this);
        video_detail_recycler_view.setAdapter(mAdapter);
    }

    @OnClick(R.id.detail_menu_root_return_error)
    public void onReturnClick() {
        String url = Model.getData().getLastPlayUrl(this);
        if (url.isEmpty()) {
            return;
        }
        url = url.substring(url.indexOf("://") + 3);
        String videoName = Model.getData().getLastPlayName(this);
        String accountName = FFTVApplication.account;

        DisposableObserver<String> observer = new DisposableObserver<String>() {
            @Override
            public void onNext(String code) {
                if (code.equals("200")) {
                    Toast.makeText(VideoDetailActivity.this, "反馈错误视频成功", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(VideoDetailActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        final String urlUp = url;
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    String requestBody = "username=" + accountName + "&title=" + videoName + "&url=" + urlUp;
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    final Request request = new Request.Builder()
                            .url(RETURN_URL)
                            .post(body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string().substring(2).trim();
                        emitter.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        mDisposable.add(observer);
    }

    @OnClick(R.id.detail_menu_root_collect)
    public void onCollectClick() {
        LinkedList<VideoCollect> videoCollectList = (LinkedList<VideoCollect>) InternalFileSaveUtil.getInstance(this).get("video_collect");
        if (videoCollectList == null) {
            videoCollectList = new LinkedList<>();
        }

        if (detail_tv_collect.getText().equals("收藏")) {
            // 收藏
            VideoCollect videoCollect = new VideoCollect();
            videoCollect.setVideo(mVideo);
            videoCollect.setVideoEngine(Model.getData().getVideoEngine(this));

            videoCollectList.add(0, videoCollect);
            if (videoCollectList.size() > 1) {
                for (int i = 1; i < videoCollectList.size(); i++) {
                    if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                            && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                        videoCollectList.remove(i);
                        break;
                    }
                }
            }

            if (videoCollectList.size() > Const.VIDEO_COLLECTION_NUM) {
                videoCollectList.remove(videoCollectList.size() - 1);
            }
            InternalFileSaveUtil.getInstance(this).put("video_collect", videoCollectList);

            detail_tv_collect.setText("取消收藏");
            detail_iv_collect.setImageResource(R.drawable.ic_not_collected_focus);
        } else {
            // 取消收藏
            for (int i = 1; i < videoCollectList.size(); i++) {
                if (videoCollectList.get(i).getVideo().getTitle().equals(mVideo.getTitle())
                        && videoCollectList.get(i).getVideoEngine().equals(Model.getData().getVideoEngine(this))) {
                    videoCollectList.remove(i);
                    break;
                }
            }
            InternalFileSaveUtil.getInstance(this).put("video_collect", videoCollectList);

            detail_tv_collect.setText("收藏");
            detail_iv_collect.setImageResource(R.drawable.ic_collected_focus);
        }
    }

    //20220910 - Added new feature sorting
    @OnClick(R.id.detail_tv_sort)
    public void onSortClick() {
        int maxParts = mVideo.getParts().size();
        Video sortVideo = new Video();
        if (maxParts > 0 ) {
            ArrayList<Video.Part> parts = new ArrayList<>();
            Video.Part part;
            for(int i = maxParts; i>0; i--){
                part = mVideo.getParts().get(i-1);
                parts.add(part);
            }
            sortVideo.setParts(parts);
        }
        mVideo.synParts(sortVideo);
        mAdapter = new VideoPlayListAdapter(this, mVideo.getParts(), this);
        video_detail_recycler_view.setAdapter(mAdapter);
    }
}
