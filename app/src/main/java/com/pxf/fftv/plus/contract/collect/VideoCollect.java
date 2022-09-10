package com.pxf.fftv.plus.contract.collect;

import com.pxf.fftv.plus.model.video.Video;

import java.io.Serializable;

public class VideoCollect implements Serializable {

    private static final long serialVersionUID = 6834993428543033756L;

    private Video video;

    private String videoEngine;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public String getVideoEngine() {
        return videoEngine;
    }

    public void setVideoEngine(String videoEngine) {
        this.videoEngine = videoEngine;
    }
}
