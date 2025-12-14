package com.pxf.fftv.plus.contract;

import com.pxf.fftv.plus.Const;

public class VideoTypeEvent {

    private Const.VideoType videoType;

    public VideoTypeEvent(Const.VideoType videoType) {
        this.videoType = videoType;
    }

    public Const.VideoType getVideoType() {
        return videoType;
    }
}
