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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.BASE_DATA_URL;
import static com.pxf.fftv.plus.Const.PLAY_4;
import static com.pxf.fftv.plus.Const.PLAY_3;
import static com.pxf.fftv.plus.Const.PLAY_1;
import static com.pxf.fftv.plus.Const.PLAY_2;
import static com.pxf.fftv.plus.Const.VIDEO_2;
import static com.pxf.fftv.plus.Const.VIDEO_3;
import static com.pxf.fftv.plus.Const.VIDEO_1;
import static com.pxf.fftv.plus.Const.VIDEO_4;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.setting_tv_video_weiduo)
    TextView setting_tv_video_weiduo;

    @BindView(R.id.setting_tv_video_cms)
    TextView setting_tv_video_cms;

    @BindView(R.id.setting_tv_video_ok)
    TextView setting_tv_video_ok;

    @BindView(R.id.setting_tv_video_zd)
    TextView setting_tv_video_zd;

    @BindView(R.id.setting_tv_player_native)
    TextView setting_tv_player_native;

    @BindView(R.id.setting_tv_player_tbs)
    TextView setting_tv_player_tbs;

    @BindView(R.id.setting_tv_player_ijkplayer)
    TextView setting_tv_player_ijkplayer;

    @BindView(R.id.setting_tv_player_exo)
    TextView setting_tv_player_exo;

    @BindView(R.id.setting_tv_version_update)
    TextView setting_tv_version_update;

    @BindView(R.id.setting_tv_clear_cache)
    TextView setting_tv_clear_cache;

    @BindView(R.id.setting_tv_auto_login_on)
    TextView setting_tv_auto_login_on;

    @BindView(R.id.setting_tv_auto_login_off)
    TextView setting_tv_auto_login_off;

    @BindView(R.id.setting_tv_new_version)
    TextView setting_tv_new_version;

    @BindView(R.id.setting_iv_new_version)
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
        ButterKnife.bind(this);

        initView();

        refreshCache();
    }

    private void initView() {
        // 版本号
        setting_tv_version_update.setText("当前版本 : " + BuildConfig.VERSION_NAME);

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
            case PLAY_2:
                setting_tv_player_tbs.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                currentPlayerEngine = setting_tv_player_tbs;
                break;
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
        Ui.setViewFocusScaleAnimator(setting_tv_player_tbs, new FocusAction() {
            @Override
            public void onFocus() {
                setting_tv_player_tbs.setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                if (setting_tv_player_tbs == currentPlayerEngine) {
                    setting_tv_player_tbs.setTextColor(getResources().getColor(R.color.colorPersonalCurrent));
                } else {
                    setting_tv_player_tbs.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
                }
            }
        });
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

    @OnClick(R.id.setting_tv_video_weiduo)
    public void onWeiduoResClick() {
        Model.getData().setVideoEngine(this, VIDEO_1);
        currentVideoEngine = setting_tv_video_weiduo;
        defaultOtherVideoEngine(setting_tv_video_weiduo);
        isVideoEngineChange = true;
    }

    @OnClick(R.id.setting_tv_video_cms)
    public void onCMSResCLick() {
        Model.getData().setVideoEngine(this, VIDEO_2);
        currentVideoEngine = setting_tv_video_cms;
        defaultOtherVideoEngine(setting_tv_video_cms);
        isVideoEngineChange = true;
    }

    @OnClick(R.id.setting_tv_video_ok)
    public void onOKResClick() {
        Model.getData().setVideoEngine(this, VIDEO_3);
        currentVideoEngine = setting_tv_video_ok;
        defaultOtherVideoEngine(setting_tv_video_ok);
        isVideoEngineChange = true;
    }

    @OnClick(R.id.setting_tv_video_zd)
    public void onZDResClick() {
        Model.getData().setVideoEngine(this, VIDEO_4);
        currentVideoEngine = setting_tv_video_zd;
        defaultOtherVideoEngine(setting_tv_video_zd);
        isVideoEngineChange = true;
    }

    @OnClick(R.id.setting_tv_player_native)
    public void onNativePlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_1);
        currentPlayerEngine = setting_tv_player_native;
        defaultOtherPlayerEngine(setting_tv_player_native);
    }

    @OnClick(R.id.setting_tv_player_tbs)
    public void onTbsPlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_2);
        currentPlayerEngine = setting_tv_player_tbs;
        defaultOtherPlayerEngine(setting_tv_player_tbs);
    }

    @OnClick(R.id.setting_tv_player_ijkplayer)
    public void onIjkPlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_3);
        currentPlayerEngine = setting_tv_player_ijkplayer;
        defaultOtherPlayerEngine(setting_tv_player_ijkplayer);
    }

    @OnClick(R.id.setting_tv_player_exo)
    public void onEXOPlayerClick() {
        Model.getData().setPlayerEngine(this, PLAY_4);
        currentPlayerEngine = setting_tv_player_exo;
        defaultOtherPlayerEngine(setting_tv_player_exo);
    }

    @OnClick(R.id.setting_tv_auto_login_on)
    public void onAutoLoginOnClick() {
        Model.getData().setAutoLogin(this, true);
        currentAutoLogin = setting_tv_auto_login_on;
        defaultOtherAutoLogin(setting_tv_auto_login_on);
    }

    @OnClick(R.id.setting_tv_auto_login_off)
    public void onAutoLoginOffClick() {
        Model.getData().setAutoLogin(this, false);
        currentAutoLogin = setting_tv_auto_login_off;
        defaultOtherAutoLogin(setting_tv_auto_login_off);
    }

    @OnClick(R.id.setting_tv_version_update)
    public void onVersionUpdateClick() {
        if (newVersion) {
            Intent intent = new Intent(this, UpdateActivity.class);
            startActivity(intent);

            EventBus.getDefault().postSticky(new UpdateEvent("版本更新至 " + mBaseDataBean.getHBbb(), mBaseDataBean.getHBnr(), mBaseDataBean.getHBxz()));
        } else {
            Toast.makeText(this, "当前版本已经是最新", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.setting_tv_clear_cache)
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
                text = cacheSize  + " B";
            } else if (cacheSize < 1024 * 1024) {
                text = String.format("%.2f", ((double)cacheSize / 1024)) + " KB";
            } else if (cacheSize < 1024 * 1024 * 1024) {
                text = String.format("%.2f", ((double)cacheSize / 1024 / 1024)) + " MB";
            } else {
                text = String.format("%.2f", ((double)cacheSize / 1024 / 1024 / 1024)) + " GB";
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
                    try {
                        Thread.sleep(1000);
                        Request request = new Request.Builder()
                                .url(BASE_DATA_URL)
                                .build();
                        Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            emitter.onNext(CommonUtils.getGson().fromJson(result.trim().substring(2), BaseDataBean.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
        if (setting_tv_player_tbs != textView) {
            setting_tv_player_tbs.setTextColor(getResources().getColor(R.color.colorPersonalNormal));
        }
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
}
