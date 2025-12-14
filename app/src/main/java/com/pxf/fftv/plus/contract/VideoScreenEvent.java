package com.pxf.fftv.plus.contract;

public class VideoScreenEvent {

    private String focusTitle;

    public VideoScreenEvent(String focusTitle) {
        this.focusTitle = focusTitle;
    }

    public String getFocusTitle() {
        return focusTitle;
    }
}
