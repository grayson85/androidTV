package com.pxf.fftv.plus.custom.shine;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

public class ColorFocusBorder extends AbsFocusBorder {
    private float mRoundRadius;
    private ObjectAnimator mRoundRadiusAnimator;

    private ColorFocusBorder(Context context, int shimmerColor, long shimmerDuration, boolean isShimmerAnim, long animDuration, RectF paddingOfsetRectF) {
        super(context, shimmerColor, shimmerDuration, isShimmerAnim, animDuration, paddingOfsetRectF);
        this.mRoundRadius = 0.0F;
    }

    protected void setRoundRadius(float roundRadius) {
        if (this.mRoundRadius != roundRadius) {
            this.mRoundRadius = roundRadius;
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    public float getRoundRadius() {
        return this.mRoundRadius;
    }

    List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight, AbsFocusBorder.Options options) {
        if (options instanceof ColorFocusBorder.Options) {
            ColorFocusBorder.Options rawOptions = (ColorFocusBorder.Options)options;
            List<Animator> animators = new ArrayList();
            animators.add(this.getRoundRadiusAnimator(rawOptions.roundRadius));
            return animators;
        } else {
            return null;
        }
    }

    List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight, AbsFocusBorder.Options options) {
        return null;
    }

    private ObjectAnimator getRoundRadiusAnimator(float roundRadius) {
        if (null == this.mRoundRadiusAnimator) {
            this.mRoundRadiusAnimator = ObjectAnimator.ofFloat(this, "roundRadius", new float[]{this.getRoundRadius(), roundRadius});
        } else {
            this.mRoundRadiusAnimator.setFloatValues(new float[]{this.getRoundRadius(), roundRadius});
        }

        return this.mRoundRadiusAnimator;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public static final class Builder extends AbsFocusBorder.Builder {
        public Builder() {
        }

        public AbsFocusBorder.Builder shimmerColor(int color) {
            return super.shimmerColor(color);
        }

        public FocusBorder build(Activity activity) {
            if (null == activity) {
                throw new NullPointerException("The activity cannot be null");
            } else {
                return this.build((ViewGroup)activity.findViewById(android.R.id.content));
            }
        }

        public FocusBorder build(ViewGroup parent) {
            if (null == parent) {
                throw new NullPointerException("The FocusBorder parent cannot be null");
            } else {
                ColorFocusBorder boriderView = new ColorFocusBorder(parent.getContext(), this.mShimmerColor, this.mShimmerDuration, this.mIsShimmerAnim, this.mAnimDuration, this.mPaddingOfsetRectF);
                LayoutParams lp = new LayoutParams(1, 1);
                parent.addView(boriderView, lp);
                return boriderView;
            }
        }
    }

    public static class Options extends AbsFocusBorder.Options {
        private float roundRadius = 0.0F;

        Options() {
        }

        public static ColorFocusBorder.Options get(float roundRadius) {
            ColorFocusBorder.Options.OptionsHolder.INSTANCE.roundRadius = roundRadius;
            return ColorFocusBorder.Options.OptionsHolder.INSTANCE;
        }

        private static class OptionsHolder {
            private static final ColorFocusBorder.Options INSTANCE = new ColorFocusBorder.Options();

            private OptionsHolder() {
            }
        }
    }
}

