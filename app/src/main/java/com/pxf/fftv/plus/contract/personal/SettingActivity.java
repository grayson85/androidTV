package com.pxf.fftv.plus.contract.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pxf.fftv.plus.BuildConfig;
import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.BaseDataBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.home.VideoEngineChangeEvent;
import com.pxf.fftv.plus.contract.UpdateActivity;
import com.pxf.fftv.plus.contract.UpdateEvent;
import com.pxf.fftv.plus.model.Model;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.PLAY_4;
import static com.pxf.fftv.plus.Const.PLAY_3;
import static com.pxf.fftv.plus.Const.PLAY_1;
// PLAY_2 (TBS) removed
import static com.pxf.fftv.plus.Const.VIDEO_2;
import static com.pxf.fftv.plus.Const.VIDEO_3;
import static com.pxf.fftv.plus.Const.VIDEO_1;
import static com.pxf.fftv.plus.Const.VIDEO_4;

public class SettingActivity extends AppCompatActivity {

    TextView setting_tv_video_weiduo;

    TextView setting_tv_video_cms;

    TextView setting_tv_video_ok;

    TextView setting_tv_video_zd;

    TextView setting_tv_player_native;

    // TextView setting_tv_player_tbs; // TBS removed

    TextView setting_tv_player_ijkplayer;

    TextView setting_tv_player_exo;

    TextView setting_tv_version_update;

    TextView setting_tv_clear_cache;

    TextView setting_tv_auto_login_on;

    TextView setting_tv_auto_login_off;

    TextView setting_tv_new_version;
    TextView setting_tv_dns_info;
    TextView setting_tv_cms_info;

    ImageView setting_iv_new_version;

    private TextView currentAutoLogin;
    private TextView currentVideoEngine;
    private TextView currentPlayerEngine;

    private CompositeDisposable mDisposable;
    private boolean newVersion = false;
    private BaseDataBean mBaseDataBean;

    private boolean isVideoEngineChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting_tv_video_weiduo = findViewById(R.id.setting_tv_video_weiduo);
        setting_tv_video_cms = findViewById(R.id.setting_tv_video_cms);
        setting_tv_video_ok = findViewById(R.id.setting_tv_video_ok);
        setting_tv_video_zd = findViewById(R.id.setting_tv_video_zd);
        setting_tv_player_native = findViewById(R.id.setting_tv_player_native);
        // setting_tv_player_tbs removed
        setting_tv_player_ijkplayer = findViewById(R.id.setting_tv_player_ijkplayer);
        setting_tv_player_exo = findViewById(R.id.setting_tv_player_exo);
        setting_tv_version_update = findViewById(R.id.setting_tv_version_update);
        setting_tv_clear_cache = findViewById(R.id.setting_tv_clear_cache);
        setting_tv_auto_login_on = findViewById(R.id.setting_tv_auto_login_on);
        setting_tv_auto_login_off = findViewById(R.id.setting_tv_auto_login_off);
        setting_tv_new_version = findViewById(R.id.setting_tv_new_version);
        setting_tv_dns_info = findViewById(R.id.setting_tv_dns_info);
        setting_tv_cms_info = findViewById(R.id.setting_tv_cms_info);
        setting_iv_new_version = findViewById(R.id.setting_iv_new_version);

        setting_tv_video_weiduo.setOnClickListener(v -> onWeiduoResClick());
        setting_tv_video_cms.setOnClickListener(v -> onCMSResCLick());
        setting_tv_video_ok.setOnClickListener(v -> onOKResClick());
        setting_tv_video_zd.setOnClickListener(v -> onZDResClick());
        setting_tv_player_native.setOnClickListener(v -> onNativePlayerClick());
        // TBS player click listener removed
        setting_tv_player_ijkplayer.setOnClickListener(v -> onIjkPlayerClick());
        setting_tv_player_exo.setOnClickListener(v -> onEXOPlayerClick());
        setting_tv_auto_login_on.setOnClickListener(v -> onAutoLoginOnClick());
        setting_tv_auto_login_off.setOnClickListener(v -> onAutoLoginOffClick());
        setting_tv_version_update.setOnClickListener(v -> onVersionUpdateClick());
        setting_tv_clear_cache.setOnClickListener(v -> onClearCacheClick());

        // Editable Listeners
        setting_tv_dns_info.setOnClickListener(v -> onDnsInfoClick());
        setting_tv_cms_info.setOnClickListener(v -> onCmsInfoClick());

        initView();

        refreshCache();
    }

    private void initView() {
        // 版本号
        setting_tv_version_update.setText("当前版本 : " + BuildConfig.VERSION_NAME);

        // Network Info
        String dnsVal = Model.getData().getDnsServer(this);
        String cmsVal = Model.getData().getCmsApiUrl(this);
        if (dnsVal.equals("8.8.8.8")) {
            setting_tv_dns_info.setText("DNS: 8.8.8.8 (DoH)");
        } else {
            setting_tv_dns_info.setText("DNS: " + (dnsVal.isEmpty() ? "System" : dnsVal));
        }
        setting_tv_cms_info.setText("CMS: " + cmsVal);

        switch (Model.getData().getVideoEngine(this)) {
            case VIDEO_1:
                currentVideoEngine = setting_tv_video_weiduo;
                setting_tv_video_weiduo.requestFocus();
                break;
            case VIDEO_2:
                currentVideoEngine = setting_tv_video_cms;
                setting_tv_video_cms.requestFocus();
                break;
            case VIDEO_3:
                currentVideoEngine = setting_tv_video_ok;
                setting_tv_video_ok.requestFocus();
                break;
            case VIDEO_4:
                currentVideoEngine = setting_tv_video_zd;
                setting_tv_video_zd.requestFocus();
                break;
        }

        switch (Model.getData().getPlayerEngine(this)) {
            case PLAY_1:
                setting_tv_player_native.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                currentPlayerEngine = setting_tv_player_native;
                break;
            // case PLAY_2 (TBS) removed - fallback to native handled in VideoPlayer
            case PLAY_3:
                setting_tv_player_ijkplayer.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                currentPlayerEngine = setting_tv_player_ijkplayer;
                break;
            case PLAY_4:
                setting_tv_player_exo.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                currentPlayerEngine = setting_tv_player_exo;
        }

        if (Model.getData().isAutoLogin(this)) {
            setting_tv_auto_login_on.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
            currentAutoLogin = setting_tv_auto_login_on;
        } else {
            setting_tv_auto_login_off.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
            currentAutoLogin = setting_tv_auto_login_off;
        }

        Ui.setViewFocusScaleAnimator(setting_tv_video_weiduo, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_video_weiduo.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_video_weiduo == currentVideoEngine) {
                    setting_tv_video_weiduo.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_video_weiduo.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_video_cms, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_video_cms.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_video_cms == currentVideoEngine) {
                    setting_tv_video_cms.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_video_cms.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_video_ok, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_video_ok.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_video_ok == currentVideoEngine) {
                    setting_tv_video_ok.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_video_ok.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_video_zd, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_video_zd.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_video_zd == currentVideoEngine) {
                    setting_tv_video_zd.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_video_zd.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_player_native, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_player_native.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_player_native == currentPlayerEngine) {
                    setting_tv_player_native.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_player_native.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        // TBS player focus animator removed
        Ui.setViewFocusScaleAnimator(setting_tv_player_ijkplayer, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_player_ijkplayer.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_player_ijkplayer == currentPlayerEngine) {
                    setting_tv_player_ijkplayer.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_player_ijkplayer.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_player_exo, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_player_exo.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_player_exo == currentPlayerEngine) {
                    setting_tv_player_exo.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_player_exo.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_version_update, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_version_update.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                setting_tv_version_update.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_clear_cache, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_clear_cache.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                setting_tv_clear_cache.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_auto_login_on, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_auto_login_on.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_auto_login_on == currentAutoLogin) {
                    setting_tv_auto_login_on.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_auto_login_on.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
        Ui.setViewFocusScaleAnimator(setting_tv_auto_login_off, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_auto_login_off.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_auto_login_off == currentAutoLogin) {
                    setting_tv_auto_login_off.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_auto_login_off.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });

        if (Const.FEATURE_2) {
            setting_tv_player_native.setVisibility(View.VISIBLE);
        } else {
            setting_tv_player_native.setVisibility(View.GONE);
        }
        if (Const.FEATURE_3) {
            setting_tv_player_ijkplayer.setVisibility(View.VISIBLE);
        } else {
            setting_tv_player_ijkplayer.setVisibility(View.GONE);
        }
        if (Const.FEATURE_4) {
            setting_tv_player_exo.setVisibility(View.VISIBLE);
        } else {
            setting_tv_player_exo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDisposable = new CompositeDisposable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (isVideoEngineChange) {
            EventBus.getDefault().postSticky(new VideoEngineChangeEvent());
        }
        finish();
    }

    public void onWeiduoResClick() {
        Model.getData().setVideoEngine(this, VIDEO_1);
        currentVideoEngine = setting_tv_video_weiduo;
        defaultOtherVideoEngine(setting_tv_video_weiduo);
        isVideoEngineChange = true;
    }

    public void onCMSResCLick() {
        Model.getData().setVideoEngine(this, VIDEO_2);
        currentVideoEngine = setting_tv_video_cms;
        defaultOtherVideoEngine(setting_tv_video_cms);
        isVideoEngineChange = true;
    }

    public void onOKResClick() {
        Model.getData().setVideoEngine(this, VIDEO_3);
        currentVideoEngine = setting_tv_video_ok;
        defaultOtherVideoEngine(setting_tv_video_ok);
        isVideoEngineChange = true;
    }

    public void onZDResClick() {
        Model.getData().setVideoEngine(this, VIDEO_4);
        currentVideoEngine = setting_tv_video_zd;
        defaultOtherVideoEngine(setting_tv_video_zd);
        isVideoEngineChange = true;
    }

    public void onNativePlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_1);
        currentPlayerEngine = setting_tv_player_native;
        defaultOtherPlayerEngine(setting_tv_player_native);
    }

    // onTbsPlayerClick() removed - TBS player deprecated

    public void onIjkPlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_3);
        currentPlayerEngine = setting_tv_player_ijkplayer;
        defaultOtherPlayerEngine(setting_tv_player_ijkplayer);
    }

    public void onEXOPlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_4);
        currentPlayerEngine = setting_tv_player_exo;
        defaultOtherPlayerEngine(setting_tv_player_exo);
    }

    public void onAutoLoginOnClick() {
        Model.getData().setAutoLogin(this, true);
        currentAutoLogin = setting_tv_auto_login_on;
        defaultOtherAutoLogin(setting_tv_auto_login_on);
    }

    public void onAutoLoginOffClick() {
        Model.getData().setAutoLogin(this, false);
        currentAutoLogin = setting_tv_auto_login_off;
        defaultOtherAutoLogin(setting_tv_auto_login_off);
    }

    public void onVersionUpdateClick() {
        if (newVersion) {
            Intent intent = new Intent(this, UpdateActivity.class);
            startActivity(intent);

            EventBus.getDefault().postSticky(new UpdateEvent("版本更新至 " + mBaseDataBean.getHBbb(),
                    mBaseDataBean.getHBnr(), mBaseDataBean.getHBxz()));
        } else {
            Toast.makeText(this, "当前版本已经是最新", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClearCacheClick() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("缓存清理中");
        progressDialog.show();

        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingActivity.this, "清理完成", Toast.LENGTH_SHORT).show();
                    refreshCache();
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
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        deleteFilesByDirectory(getCacheDir());
                        Glide.get(SettingActivity.this).clearDiskCache();
                        emitter.onNext(true);
                    }
                })
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        mDisposable.add(observer);
    }

    public void refreshCache() {
        try {
            long cacheSize = getFolderSize(getCacheDir());

            String text = "";
            if (cacheSize < 1024) {
                text = cacheSize + " B";
            } else if (cacheSize < 1024 * 1024) {
                text = String.format("%.2f", ((double) cacheSize / 1024)) + " KB";
            } else if (cacheSize < 1024 * 1024 * 1024) {
                text = String.format("%.2f", ((double) cacheSize / 1024 / 1024)) + " MB";
            } else {
                text = String.format("%.2f", ((double) cacheSize / 1024 / 1024 / 1024)) + " GB";
            }
            setting_tv_clear_cache.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File value : fileList) {
                if (value.isDirectory()) {
                    size = size + getFolderSize(value);
                } else {
                    size = size + value.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public void checkUpdate() {
        DisposableObserver<BaseDataBean> observer = new DisposableObserver<BaseDataBean>() {
            @Override
            public void onNext(BaseDataBean adBean) {
                if (Double.parseDouble(adBean.getHBbb()) > Double.parseDouble(BuildConfig.VERSION_NAME)) {
                    // 有可用新版本
                    setting_tv_new_version.setVisibility(View.VISIBLE);
                    setting_iv_new_version.setVisibility(View.VISIBLE);
                    newVersion = true;
                    mBaseDataBean = adBean;
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
                .create((ObservableOnSubscribe<BaseDataBean>) emitter -> {
                    Response response = null;
                    try {
                        Thread.sleep(1000);
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
                .subscribe(observer);
        mDisposable.add(observer);
    }

    private void defaultOtherVideoEngine(TextView textView) {
        if (setting_tv_video_weiduo != textView) {
            setting_tv_video_weiduo.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        if (setting_tv_video_cms != textView) {
            setting_tv_video_cms.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        if (setting_tv_video_ok != textView) {
            setting_tv_video_ok.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        if (setting_tv_video_zd != textView) {
            setting_tv_video_zd.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
    }

    private void defaultOtherPlayerEngine(TextView textView) {
        if (setting_tv_player_native != textView) {
            setting_tv_player_native.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        // TBS player color reset removed
        if (setting_tv_player_ijkplayer != textView) {
            setting_tv_player_ijkplayer.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        if (setting_tv_player_exo != textView) {
            setting_tv_player_exo.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
    }

    private void defaultOtherAutoLogin(TextView textView) {
        if (setting_tv_auto_login_on != textView) {
            setting_tv_auto_login_on.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
        if (setting_tv_auto_login_off != textView) {
            setting_tv_auto_login_off.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
    }

    private void onDnsInfoClick() {
        String current = Model.getData().getDnsServer(this);
        showEditDialog("Modify DNS (Empty/System for default)", current, newValue -> {
            Model.getData().setDnsServer(SettingActivity.this, newValue);
            Toast.makeText(this, "Restarting to apply changes...", Toast.LENGTH_LONG).show();
            new android.os.Handler().postDelayed(() -> {
                final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }, 1000);
        });
    }

    private void onCmsInfoClick() {
        String current = Model.getData().getCmsApiUrl(this);
        showEditDialog("Modify CMS API URL", current, newValue -> {
            Model.getData().setCmsApiUrl(SettingActivity.this, newValue);
            // Update Const.BASE_URL immediately so it takes effect
            Const.BASE_URL = newValue;
            Const.refreshUrls(); // Update all dependent URL fields

            // Post event to notify HomeActivity to refresh its data
            EventBus.getDefault().postSticky(new com.pxf.fftv.plus.contract.home.CmsUrlChangeEvent());

            // Show success message and navigate back to home to refresh data
            Toast.makeText(SettingActivity.this, "CMS API URL updated successfully!", Toast.LENGTH_SHORT).show();

            // Navigate back to home activity with clear top to refresh all fragments
            Intent intent = new Intent(SettingActivity.this, com.pxf.fftv.plus.contract.home.HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showEditDialog(String title, String currentValue, final OnValueChangeListener listener) {
        final android.widget.EditText editText = new android.widget.EditText(this);
        editText.setText(currentValue);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = editText.getText().toString().trim();
                    listener.onChanged(val);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void restartApp() {
        Toast.makeText(this, "Restarting to apply changes...", Toast.LENGTH_LONG).show();
        new android.os.Handler().postDelayed(() -> {
            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }, 1000);
    }

    interface OnValueChangeListener {
        void onChanged(String newValue);
    }
}
