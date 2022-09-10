package com.pxf.fftv.plus.player;

import com.pxf.fftv.plus.contract.history.VideoHistory;

public class VideoHistoryEvent {

    private VideoHistory videoHistory;

    public VideoHistoryEvent(VideoHistory videoHistory) {
        this.videoHistory = videoHistory;
    }

    public VideoHistory getVideoHistory() {
        return videoHistory;
    }
}
