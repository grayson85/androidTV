package com.pxf.fftv.plus.custom.shine;

import android.view.View;
import androidx.annotation.NonNull;

public interface FocusBorder {
    void setVisible(boolean var1);

    boolean isVisible();

    void onFocus(@NonNull View var1, FocusBorder.Options var2);

    void boundGlobalFocusListener(@NonNull FocusBorder.OnFocusCallback var1);

    void unBoundGlobalFocusListener();

    public static class OptionsFactory {
        public OptionsFactory() {
        }

        public static final FocusBorder.Options get(float scaleX, float scaleY) {
            return AbsFocusBorder.Options.get(scaleX, scaleY);
        }

        public static final FocusBorder.Options get(float roundRadius) {
            return ColorFocusBorder.Options.get(roundRadius);
        }
    }

    public static class Builder {
        public Builder() {
        }

        public final ColorFocusBorder.Builder asColor() {
            return new ColorFocusBorder.Builder();
        }
    }

    public abstract static class Options {
        public Options() {
        }
    }

    public interface OnFocusCallback {
        FocusBorder.Options onFocus(View var1, View var2);
    }
}
