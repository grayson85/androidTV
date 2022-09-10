package com.pxf.fftv.plus.contract.detail;

import com.pxf.fftv.plus.model.video.Video;

public class VideoDetailEvent {

    private Video video;

    public VideoDetailEvent(Video video) {
        this.video = video;
    }

    public Video getVideo() {
        return video;
    }
}
