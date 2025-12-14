package com.pxf.fftv.plus.custom.shine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsFocusBorder extends View implements FocusBorder, OnGlobalFocusChangeListener {
    private static final long DEFAULT_ANIM_DURATION_TIME = 300L;
    private static final long DEFAULT_SHIMMER_DURATION_TIME = 1000L;
    protected long mAnimDuration = 0L;
    protected long mShimmerDuration = 1000L;
    protected RectF mFrameRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mPaddingOfsetRectF = new RectF();
    protected RectF mTempRectF = new RectF();
    private LinearGradient mShimmerLinearGradient;
    private Matrix mShimmerGradientMatrix;
    private Paint mShimmerPaint;
    private int mShimmerColor = 1728053247;
    private float mShimmerTranslate = 0.0F;
    private boolean mShimmerAnimating = false;
    private boolean mIsShimmerAnim = true;
    private ObjectAnimator mTranslationXAnimator;
    private ObjectAnimator mTranslationYAnimator;
    private ObjectAnimator mWidthAnimator;
    private ObjectAnimator mHeightAnimator;
    private ObjectAnimator mShimmerAnimator;
    private AnimatorSet mAnimatorSet;
    private WeakReference<View> mOldFocusView;
    private OnFocusCallback mOnFocusCallback;
    private boolean mIsVisible = false;

    protected AbsFocusBorder(Context context, int shimmerColor, long shimmerDuration, boolean isShimmerAnim, long animDuration, RectF paddingOfsetRectF) {
        super(context);
        this.mShimmerColor = shimmerColor;
        this.mShimmerDuration = shimmerDuration;
        this.mIsShimmerAnim = isShimmerAnim;
        this.mAnimDuration = 0L;
        if (null != paddingOfsetRectF) {
            this.mPaddingOfsetRectF.set(paddingOfsetRectF);
        }

        this.setLayerType(1, (Paint) null);
        this.setVisibility(INVISIBLE);
        this.mShimmerPaint = new Paint();
        this.mShimmerGradientMatrix = new Matrix();
    }

    public boolean isInEditMode() {
        return true;
    }

    protected void onDrawShimmer(Canvas canvas) {
        if (this.mShimmerAnimating) {
            canvas.save();
            this.mTempRectF.set(this.mFrameRectF);
            this.mTempRectF.inset(2.0F, 2.0F);
            float shimmerTranslateX = this.mTempRectF.width() * this.mShimmerTranslate;
            float shimmerTranslateY = this.mTempRectF.height() * this.mShimmerTranslate;
            this.mShimmerGradientMatrix.setTranslate(shimmerTranslateX, shimmerTranslateY);
            this.mShimmerLinearGradient.setLocalMatrix(this.mShimmerGradientMatrix);
            canvas.drawRoundRect(this.mTempRectF, this.getRoundRadius(), this.getRoundRadius(), this.mShimmerPaint);
            canvas.restore();
        }

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            this.mFrameRectF.set(this.mPaddingRectF.left, this.mPaddingRectF.top, (float) w - this.mPaddingRectF.right, (float) h - this.mPaddingRectF.bottom);
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.onDrawShimmer(canvas);
    }

    protected void onDetachedFromWindow() {
        this.unBoundGlobalFocusListener();
        super.onDetachedFromWindow();
    }

    private void setShimmerAnimating(boolean shimmerAnimating) {
        this.mShimmerAnimating = shimmerAnimating;
        if (this.mShimmerAnimating) {
            this.mShimmerLinearGradient = new LinearGradient(0.0F, 0.0F, this.mFrameRectF.width(), this.mFrameRectF.height(), new int[]{16777215, 452984831, this.mShimmerColor, 452984831, 16777215}, new float[]{0.0F, 0.2F, 0.5F, 0.8F, 1.0F}, TileMode.CLAMP);
            this.mShimmerPaint.setShader(this.mShimmerLinearGradient);
        }

    }

    protected void setShimmerTranslate(float shimmerTranslate) {
        if (this.mIsShimmerAnim && this.mShimmerTranslate != shimmerTranslate) {
            this.mShimmerTranslate = shimmerTranslate;
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    protected float getShimmerTranslate() {
        return this.mShimmerTranslate;
    }

    protected void setWidth(int width) {
        if (this.getLayoutParams().width != width) {
            this.getLayoutParams().width = width;
            this.requestLayout();
        }

    }

    protected void setHeight(int height) {
        if (this.getLayoutParams().height != height) {
            this.getLayoutParams().height = height;
            this.requestLayout();
        }

    }

    public void setVisible(boolean visible) {
        if (this.mIsVisible != visible) {
            this.mIsVisible = visible;
            this.setVisibility(visible ? VISIBLE : INVISIBLE);
            if (!visible && null != this.mOldFocusView && null != this.mOldFocusView.get()) {
                this.mOldFocusView.clear();
                this.mOldFocusView = null;
            }
        }

    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    protected Rect findLocationWithView(View view) {
        return this.findOffsetDescendantRectToMyCoords(view);
    }

    protected Rect findOffsetDescendantRectToMyCoords(View descendant) {
        ViewGroup root = (ViewGroup) this.getParent();
        Rect rect = new Rect();
        if (descendant == root) {
            return rect;
        } else {
            ViewParent theParent;
            for (theParent = descendant.getParent(); theParent != null && theParent instanceof View && theParent != root; theParent = descendant.getParent()) {
                rect.offset(descendant.getLeft() - descendant.getScrollX(), descendant.getTop() - descendant.getScrollY());
                descendant = (View) theParent;
            }

            if (theParent == root) {
                rect.offset(descendant.getLeft() - descendant.getScrollX(), descendant.getTop() - descendant.getScrollY());
            }

            return rect;
        }
    }

    public void onFocus(@NonNull View focusView, FocusBorder.Options options) {
        if (null != this.mOldFocusView && null != this.mOldFocusView.get()) {
            this.mOldFocusView.clear();
        }

        if (options instanceof AbsFocusBorder.Options) {
            AbsFocusBorder.Options baseOptions = (AbsFocusBorder.Options) options;
            if (baseOptions.isScale()) {
                this.mOldFocusView = new WeakReference(focusView);
            }

            this.runFocusAnimation(focusView, baseOptions);
        }

    }

    public void boundGlobalFocusListener(@NonNull OnFocusCallback callback) {
        this.mOnFocusCallback = callback;
        this.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    public void unBoundGlobalFocusListener() {
        if (null != this.mOnFocusCallback) {
            this.mOnFocusCallback = null;
            this.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        }

    }

    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        AbsFocusBorder.Options options = null != this.mOnFocusCallback ? (AbsFocusBorder.Options) this.mOnFocusCallback.onFocus(oldFocus, newFocus) : null;
        if (null != options) {
            this.runFocusAnimation(newFocus, options);
        }

    }

    private void runFocusAnimation(View focusView, AbsFocusBorder.Options options) {
        this.setVisible(true);
        this.runBorderAnimation(focusView, options);
    }

    protected void runBorderAnimation(View focusView, AbsFocusBorder.Options options) {
        if (null != focusView) {
            if (null != this.mAnimatorSet) {
                this.mAnimatorSet.cancel();
            }

            this.createBorderAnimation(focusView, options);
            this.mAnimatorSet.start();
        }
    }

    protected void createBorderAnimation(View focusView, AbsFocusBorder.Options options) {
        float paddingWidth = this.mPaddingRectF.left + this.mPaddingRectF.right + this.mPaddingOfsetRectF.left + this.mPaddingOfsetRectF.right;
        float paddingHeight = this.mPaddingRectF.top + this.mPaddingRectF.bottom + this.mPaddingOfsetRectF.top + this.mPaddingOfsetRectF.bottom;
        int newWidth = (int) ((float) focusView.getMeasuredWidth() * options.scaleX + paddingWidth);
        int newHeight = (int) ((float) focusView.getMeasuredHeight() * options.scaleY + paddingHeight);

        Rect fromRect = this.findLocationWithView(this);
        Rect toRect = this.findLocationWithView(focusView);
        int x = toRect.left - fromRect.left;

        // 第一个卡片流光特效手动计算
        if (x + newWidth > FFTVApplication.screenWidth || x < 0) {
            if (focusView.getId() == R.id.video_card_root_1 || focusView.getId() == R.id.video_card_root_7
                    || focusView.getId() == R.id.video_type_1) {
                int margin = getContext().getResources().getDimensionPixelSize(R.dimen.x24);
                x = ((FFTVApplication.screenWidth - margin - margin) / 6
                        - getContext().getResources().getDimensionPixelSize(R.dimen.x70)) / 2
                        + margin;
            }
        }

        int y = toRect.top - fromRect.top;
        float newX = (float) x - (float) Math.abs(focusView.getMeasuredWidth() - newWidth) / 2.0F;
        float newY = (float) y - (float) Math.abs(focusView.getMeasuredHeight() - newHeight) / 2.0F;
        List<Animator> together = new ArrayList();
        List<Animator> appendTogether = this.getTogetherAnimators(newX, newY, newWidth, newHeight, options);
        /*together.add(this.getTranslationXAnimator(newX));
        together.add(this.getTranslationYAnimator(newY));*/
        setX(newX);
        setY(newY);
        together.add(this.getWidthAnimator(newWidth));
        together.add(this.getHeightAnimator(newHeight));
        if (null != appendTogether && !appendTogether.isEmpty()) {
            together.addAll(appendTogether);
        }

        List<Animator> sequentially = new ArrayList();
        List<Animator> appendSequentially = this.getSequentiallyAnimators(newX, newY, newWidth, newHeight, options);
        if (this.mIsShimmerAnim) {
            sequentially.add(this.getShimmerAnimator());
        }

        if (null != appendSequentially && !appendSequentially.isEmpty()) {
            sequentially.addAll(appendSequentially);
        }

        this.mAnimatorSet = new AnimatorSet();
        this.mAnimatorSet.setInterpolator(new DecelerateInterpolator(1.0F));
        this.mAnimatorSet.playTogether(together);
        this.mAnimatorSet.playSequentially(sequentially);
    }

    private ObjectAnimator getTranslationXAnimator(float x) {
        if (null == this.mTranslationXAnimator) {
            this.mTranslationXAnimator = ObjectAnimator.ofFloat(this, "translationX", new float[]{x}).setDuration(this.mAnimDuration);
        } else {
            this.mTranslationXAnimator.setFloatValues(new float[]{x});
        }

        return this.mTranslationXAnimator;
    }

    private ObjectAnimator getTranslationYAnimator(float y) {
        if (null == this.mTranslationYAnimator) {
            this.mTranslationYAnimator = ObjectAnimator.ofFloat(this, "translationY", new float[]{y}).setDuration(this.mAnimDuration);
        } else {
            this.mTranslationYAnimator.setFloatValues(new float[]{y});
        }

        return this.mTranslationYAnimator;
    }

    private ObjectAnimator getHeightAnimator(int height) {
        if (null == this.mHeightAnimator) {
            this.mHeightAnimator = ObjectAnimator.ofInt(this, "height", new int[]{this.getMeasuredHeight(), height}).setDuration(this.mAnimDuration);
        } else {
            this.mHeightAnimator.setIntValues(new int[]{this.getMeasuredHeight(), height});
        }

        return this.mHeightAnimator;
    }

    private ObjectAnimator getWidthAnimator(int width) {
        if (null == this.mWidthAnimator) {
            this.mWidthAnimator = ObjectAnimator.ofInt(this, "width", new int[]{this.getMeasuredWidth(), width}).setDuration(this.mAnimDuration);
        } else {
            this.mWidthAnimator.setIntValues(new int[]{this.getMeasuredWidth(), width});
        }

        return this.mWidthAnimator;
    }

    private ObjectAnimator getShimmerAnimator() {
        if (null == this.mShimmerAnimator) {
            this.mShimmerAnimator = ObjectAnimator.ofFloat(this, "shimmerTranslate", new float[]{-1.0F, 1.0F});
            this.mShimmerAnimator.setInterpolator(new LinearInterpolator());
            this.mShimmerAnimator.setDuration(this.mShimmerDuration);
            this.mShimmerAnimator.setStartDelay(400L);
            this.mShimmerAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    AbsFocusBorder.this.setShimmerAnimating(true);
                }

                public void onAnimationEnd(Animator animation) {
                    AbsFocusBorder.this.setShimmerAnimating(false);
                }
            });
        }

        return this.mShimmerAnimator;
    }

    abstract float getRoundRadius();

    abstract List<Animator> getTogetherAnimators(float var1, float var2, int var3, int var4, AbsFocusBorder.Options var5);

    abstract List<Animator> getSequentiallyAnimators(float var1, float var2, int var3, int var4, AbsFocusBorder.Options var5);

    public abstract static class Builder {
        protected int mShimmerColor = -1879048193;
        protected boolean mIsShimmerAnim = true;

        protected long mAnimDuration = 900L;
        // 动画时长
        protected long mShimmerDuration = 3000L;
        protected RectF mPaddingOfsetRectF = new RectF();

        public Builder() {
        }

        public AbsFocusBorder.Builder shimmerColor(int color) {
            this.mShimmerColor = color;
            return this;
        }

        public AbsFocusBorder.Builder shimmerDuration(long duration) {
            this.mShimmerDuration = duration;
            return this;
        }

        public AbsFocusBorder.Builder noShimmer() {
            this.mIsShimmerAnim = false;
            return this;
        }

        public AbsFocusBorder.Builder padding(float padding) {
            return this.padding(padding, padding, padding, padding);
        }

        public AbsFocusBorder.Builder padding(float left, float top, float right, float bottom) {
            this.mPaddingOfsetRectF.left = left;
            this.mPaddingOfsetRectF.top = top;
            this.mPaddingOfsetRectF.right = right;
            this.mPaddingOfsetRectF.bottom = bottom;
            return this;
        }

        public abstract FocusBorder build(Activity var1);

        public abstract FocusBorder build(ViewGroup var1);
    }

    public static class Options extends FocusBorder.Options {
        protected float scaleX = 1.12F;
        protected float scaleY = 1.12F;

        Options() {
        }

        public static AbsFocusBorder.Options get(float scaleX, float scaleY) {
            AbsFocusBorder.Options.OptionsHolder.INSTANCE.scaleX = scaleX;
            AbsFocusBorder.Options.OptionsHolder.INSTANCE.scaleY = scaleY;
            return AbsFocusBorder.Options.OptionsHolder.INSTANCE;
        }

        public boolean isScale() {
            return this.scaleX != 1.0F || this.scaleY != 1.0F;
        }

        private static class OptionsHolder {
            private static final AbsFocusBorder.Options INSTANCE = new AbsFocusBorder.Options();

            private OptionsHolder() {
            }
        }
    }
}

