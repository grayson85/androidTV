package com.pxf.fftv.plus.contract.personal;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.FocusAction;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.contract.ProtocolActivity;
import com.pxf.fftv.plus.contract.collect.VideoCollectActivity;
import com.pxf.fftv.plus.contract.history.VideoHistoryActivity;
import com.pxf.fftv.plus.contract.home.HomeActivity;
import com.pxf.fftv.plus.contract.VideoScreenActivity;
import com.pxf.fftv.plus.contract.VipActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;
import static com.pxf.fftv.plus.Const.SETTING_REQUEST_CODE;

public class PersonalFragment extends Fragment {

    @BindView(R.id.personal_root_account)
    ViewGroup personal_root_account;

    @BindView(R.id.personal_root_vip)
    ViewGroup personal_root_vip;

    @BindView(R.id.personal_root_setting)
    ViewGroup personal_root_setting;

    @BindView(R.id.personal_root_collection)
    ViewGroup personal_root_collection;

    @BindView(R.id.personal_root_history)
    ViewGroup personal_root_history;

    @BindView(R.id.personal_root_video_screen)
    ViewGroup personal_root_video_screen;

    @BindView(R.id.personal_root_app)
    ViewGroup personal_root_app;

    @BindView(R.id.personal_root_website)
    ViewGroup personal_root_website;

    @BindView(R.id.personal_root_download)
    ViewGroup personal_root_download;

    @BindView(R.id.personal_root_protocol)
    ViewGroup personal_root_protocal;

    public static final String UP_FOCUS = "up_focus";

    private HomeActivity mActivity;

    private int upFocus = -1;

    private CompositeDisposable mDisposable;

    public PersonalFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
        if (getArguments() != null) {
            upFocus = getArguments().getInt(UP_FOCUS, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @OnClick(R.id.personal_root_history)
    public void onHistoryClick() {
        if (Const.FEATURE_9) {
            Intent intent = new Intent(mActivity, VideoHistoryActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.personal_root_account)
    public void onAccountClick() {
        Intent intent = new Intent(mActivity, AccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.personal_root_setting)
    public void onSettingClick() {
        Intent intent = new Intent(mActivity, SettingActivity.class);
        startActivityForResult(intent, SETTING_REQUEST_CODE);
    }

    @OnClick(R.id.personal_root_vip)
    public void onVipClick() {
        Intent intent;
        if (FFTVApplication.login) {
            intent = new Intent(getActivity(), VipActivity.class);
        } else {
            Toast.makeText(getActivity(), "请先登录账号", Toast.LENGTH_LONG).show();
            intent = new Intent(getActivity(), AccountActivity.class);
        }
        startActivity(intent);

    }

    @OnClick(R.id.personal_root_collection)
    public void onCollectClick() {
        if (Const.FEATURE_10) {
            Intent intent = new Intent(mActivity, VideoCollectActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.personal_root_protocol)
    public void onProtocolClick() {
        Intent intent = new Intent(mActivity, ProtocolActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.personal_root_video_screen)
    public void onVideoScreenClick() {
        Intent intent = new Intent(mActivity, VideoScreenActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.personal_root_app)
    public void onAppClick() {
        Ui.showNotice(mActivity, FFTVApplication.baseDataBean.getHBTV().getCfbt1(), FFTVApplication.baseDataBean.getHBTV().getCfbu1());
    }

    @OnClick(R.id.personal_root_website)
    public void onWebsiteClick() {
        Ui.showNotice(mActivity, FFTVApplication.baseDataBean.getHBTV().getCfbt2(), FFTVApplication.baseDataBean.getHBTV().getCfbu2());
    }

    @OnClick(R.id.personal_root_download)
    public void onDownloadClick() {
        Toast.makeText(mActivity, "功能暂未开发，敬请期待", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        setMenuFocusAnimator(personal_root_account, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_account.getChildAt(0)).setImageResource(R.drawable.ic_personal_account_focus);
                ((TextView)personal_root_account.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_account.getChildAt(0)).setImageResource(R.drawable.ic_personal_account_normal);
                ((TextView)personal_root_account.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        }, true);

        setMenuFocusAnimator(personal_root_vip, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_vip.getChildAt(0)).setImageResource(R.drawable.ic_personal_vip_focus);
                ((TextView)personal_root_vip.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorVipTextFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_vip.getChildAt(0)).setImageResource(R.drawable.ic_personal_vip_normal);
                ((TextView)personal_root_vip.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorVipTextNormal));
            }
        }, true);

        setMenuFocusAnimator(personal_root_setting, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_setting.getChildAt(0)).setImageResource(R.drawable.ic_personal_setting_focus);
                ((TextView)personal_root_setting.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_setting.getChildAt(0)).setImageResource(R.drawable.ic_personal_setting_normal);
                ((TextView)personal_root_setting.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        }, true);

        setMenuFocusAnimator(personal_root_collection, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_collection.getChildAt(0)).setImageResource(R.drawable.ic_personal_collection_focus);
                ((TextView)personal_root_collection.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_collection.getChildAt(0)).setImageResource(R.drawable.ic_personal_collection_normal);
                ((TextView)personal_root_collection.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        }, true);

        setMenuFocusAnimator(personal_root_history, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_history.getChildAt(0)).setImageResource(R.drawable.ic_personal_history_focus);
                ((TextView)personal_root_history.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_history.getChildAt(0)).setImageResource(R.drawable.ic_personal_history_normal);
                ((TextView)personal_root_history.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        }, true);

        setMenuFocusAnimator(personal_root_website, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_website.getChildAt(0)).setImageResource(R.drawable.ic_personal_website_focus);
                ((TextView)personal_root_website.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_website.getChildAt(0)).setImageResource(R.drawable.ic_personal_website_normal);
                ((TextView)personal_root_website.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });

        setMenuFocusAnimator(personal_root_app, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_app.getChildAt(0)).setImageResource(R.drawable.ic_personal_app_focus);
                ((TextView)personal_root_app.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_app.getChildAt(0)).setImageResource(R.drawable.ic_personal_app_normal);
                ((TextView)personal_root_app.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });

        setMenuFocusAnimator(personal_root_protocal, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_protocal.getChildAt(0)).setImageResource(R.drawable.ic_personal_protocol_normal);
                ((TextView)personal_root_protocal.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_protocal.getChildAt(0)).setImageResource(R.drawable.ic_personal_protocol_focus);
                ((TextView)personal_root_protocal.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });

        setMenuFocusAnimator(personal_root_download, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_download.getChildAt(0)).setImageResource(R.drawable.ic_personal_download_normal);
                ((TextView)personal_root_download.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_download.getChildAt(0)).setImageResource(R.drawable.ic_personal_download_normal);
                ((TextView)personal_root_download.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });

        setMenuFocusAnimator(personal_root_video_screen, new FocusAction() {
            @Override
            public void onFocus() {
                ((ImageView)personal_root_video_screen.getChildAt(0)).setImageResource(R.drawable.ic_personal_video_screen_normal);
                ((TextView)personal_root_video_screen.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalFocus));
            }

            @Override
            public void onLoseFocus() {
                ((ImageView)personal_root_video_screen.getChildAt(0)).setImageResource(R.drawable.ic_personal_video_screen_focus);
                ((TextView)personal_root_video_screen.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorPersonalNormal));
            }
        });
    }

    private void setMenuFocusAnimator(ViewGroup viewGroup, FocusAction focusAction) {
        setMenuFocusAnimator(viewGroup, focusAction, false);
    }

    private void setMenuFocusAnimator(ViewGroup viewGroup, FocusAction focusAction, boolean upFocusPersonal) {
        viewGroup.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusAction.onFocus();

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE).setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE).setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        viewGroup.setScaleX((float)animation.getAnimatedValue());
                        viewGroup.setScaleY((float)animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        viewGroup.setScaleX((float)animation.getAnimatedValue());
                        viewGroup.setScaleY((float)animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                focusAction.onLoseFocus();

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f).setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    viewGroup.setScaleX((float)animation.getAnimatedValue());
                    viewGroup.setScaleY((float)animation.getAnimatedValue());
                });
                animator.start();
            }
        });

        if (upFocusPersonal && upFocus != -1) {
            viewGroup.setNextFocusUpId(upFocus);
        }
    }
}
