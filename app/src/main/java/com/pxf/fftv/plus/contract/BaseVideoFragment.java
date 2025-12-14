package com.pxf.fftv.plus.contract;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.bean.BaseDataBean;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.contract.QrCodeEvent;
import com.pxf.fftv.plus.contract.QrCodeActivity;
import com.pxf.fftv.plus.contract.home.HomeActivity;
import com.pxf.fftv.plus.model.video.Video;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import com.pxf.fftv.plus.Const;
import static com.pxf.fftv.plus.Const.ANIMATION_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_IN_SCALE;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_DURATION;
import static com.pxf.fftv.plus.Const.ANIMATION_ZOOM_OUT_SCALE;

public abstract class BaseVideoFragment extends Fragment {

    public static final String UP_FOCUS = "up_focus";

    protected HomeActivity mActivity;

    private int upFocus = -1;

    protected CompositeDisposable mDisposable;

    public BaseVideoFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            upFocus = getArguments().getInt(UP_FOCUS, -1);
        }
    }

    /**
     * Setup left focus navigation for leftmost video cards in each row
     * Call this method with the leftmost card IDs after all views are initialized
     */
    protected void setupLeftFocusNavigation(View... leftmostCards) {
        if (upFocus != -1) {
            for (View card : leftmostCards) {
                if (card != null) {
                    card.setNextFocusLeftId(upFocus);
                    card.setNextFocusUpId(upFocus);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAd();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Only clear disposables on destroy to prevent cancelling ongoing loads
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    protected void setVideoCardFocusAnimator(ViewGroup viewGroup) {
        viewGroup.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                viewGroup.getChildAt(1).setSelected(true);
                ((TextView) viewGroup.getChildAt(1))
                        .setTextColor(mActivity.getResources().getColor(R.color.colorVideoCardTextFocus));

                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (viewGroup.isFocused()) {
                        viewGroup.setScaleX((float) animation.getAnimatedValue());
                        viewGroup.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });

                animatorFirst.setInterpolator(new OvershootInterpolator());
                animatorFirst.start();

                // Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(0.0f, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    viewGroup.getChildAt(4).setScaleX((float) animation.getAnimatedValue());
                    viewGroup.getChildAt(4).setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            } else {
                viewGroup.getChildAt(1).setSelected(false);
                ((TextView) viewGroup.getChildAt(1))
                        .setTextColor(mActivity.getResources().getColor(R.color.colorVideoCardTextNormal));

                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    viewGroup.setScaleX((float) animation.getAnimatedValue());
                    viewGroup.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();

                // Play icon 动画
                ValueAnimator animatorPlayIcon = ValueAnimator.ofFloat(1.0f, 0.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animatorPlayIcon.addUpdateListener(animation -> {
                    viewGroup.getChildAt(4).setScaleX((float) animation.getAnimatedValue());
                    viewGroup.getChildAt(4).setScaleY((float) animation.getAnimatedValue());
                });
                animatorPlayIcon.start();
            }
        });

        if (upFocus != -1) {
            viewGroup.setNextFocusUpId(upFocus);
        }
    }

    protected void setTypeFocusAnimator(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ValueAnimator animatorFirst = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_IN_SCALE)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                ValueAnimator animatorSecond = ValueAnimator.ofFloat(ANIMATION_ZOOM_IN_SCALE, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_ZOOM_OUT_DURATION);

                animatorFirst.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorSecond.addUpdateListener(animation -> {
                    if (view.isFocused()) {
                        view.setScaleX((float) animation.getAnimatedValue());
                        view.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animatorFirst.cancel();
                    }
                });
                animatorFirst.start();
                animatorSecond.setStartDelay(ANIMATION_ZOOM_IN_DURATION);
                animatorSecond.start();
            } else {
                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    view.setScaleX((float) animation.getAnimatedValue());
                    view.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }

    protected void setCardData(ViewGroup viewGroup, Video video) {
        GlideApp.with(mActivity).load(video.getImageUrl()).skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) viewGroup.getChildAt(0));
        ((TextView) viewGroup.getChildAt(1)).setText(video.getTitle());

        // Display remarks badge (child 3) if available
        TextView remarksView = (TextView) viewGroup.getChildAt(3);
        String remarks = video.getRemarks();
        if (remarks != null && !remarks.trim().isEmpty()) {
            remarksView.setText(remarks);
            remarksView.setVisibility(View.VISIBLE);
        } else {
            remarksView.setVisibility(View.GONE);
        }
    }

    protected abstract View getAdRoot();

    protected abstract ImageView getAdImage();

    private void initAd() {
        DisposableObserver<BaseDataBean> observer = new DisposableObserver<BaseDataBean>() {
            @Override
            public void onNext(BaseDataBean adBean) {
                // Check if ad data is available
                if (adBean.getHBggao() == null || adBean.getHBggao().isEmpty()) {
                    // No ad data, hide ad view
                    if (getAdRoot() != null) {
                        getAdRoot().setVisibility(View.GONE);
                    }
                    return;
                }

                int length = adBean.getHBggao().size();
                int randomAdIndex = (int) (Math.random() * length);

                GlideApp.with(mActivity).load(adBean.getHBggao().get(randomAdIndex).getImgurl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(getAdImage());

                QrCodeEvent adEvent = new QrCodeEvent(adBean.getHBggao().get(randomAdIndex).getTzurl(), "请用手机扫码观看");

                getAdRoot().setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), QrCodeActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().postSticky(adEvent);

                });
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
                        Request request = new Request.Builder()
                                .url(Const.getBaseDataUrl())
                                .build();
                        response = CommonUtils.getOkHttpClient().newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            emitter.onNext(
                                    CommonUtils.getGson().fromJson(result.trim().substring(2), BaseDataBean.class));
                        } else {
                            mActivity.runOnUiThread(() -> getAdRoot().setVisibility(View.GONE));
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

        // 动画
        getAdRoot().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ValueAnimator animator = ValueAnimator.ofFloat(1.0f, ANIMATION_ZOOM_OUT_SCALE)
                        .setDuration(ANIMATION_DURATION);

                animator.addUpdateListener(animation -> {
                    if (v.isFocused()) {
                        v.setScaleX((float) animation.getAnimatedValue());
                        v.setScaleY((float) animation.getAnimatedValue());
                    } else {
                        animator.cancel();
                    }
                });

                animator.setInterpolator(new OvershootInterpolator());
                animator.start();
            } else {
                ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_ZOOM_OUT_SCALE, 1.0f)
                        .setDuration(ANIMATION_ZOOM_IN_DURATION);
                animator.addUpdateListener(animation -> {
                    v.setScaleX((float) animation.getAnimatedValue());
                    v.setScaleY((float) animation.getAnimatedValue());
                });
                animator.start();
            }
        });
    }
}
