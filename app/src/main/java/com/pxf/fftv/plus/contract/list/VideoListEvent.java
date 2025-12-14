package com.pxf.fftv.plus.contract.list;

import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.contract.VideoItem;

import java.util.ArrayList;

public class VideoListEvent {

    private String title;

    private ArrayList<VideoItem> videoItemList;

    private int focusIndex;

    private VideoConfig.Video1 video1;

    private  VideoConfig.Video2[] video2s;

    public VideoListEvent(String title, ArrayList<VideoItem> videoItemList, int focusIndex, VideoConfig.Video1 video1, VideoConfig.Video2[] video2s) {
        this.title = title;
        this.videoItemList = videoItemList;
        this.focusIndex = focusIndex;
        this.video1 = video1;
        this.video2s = video2s;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<VideoItem> getVideoItemList() {
        return videoItemList;
    }

    public int getFocusIndex() {
        return focusIndex;
    }

    public VideoConfig.Video1 getVideo1() {
        return video1;
    }

    public VideoConfig.Video2[] getVideo2s() {
        return video2s;
    }
}
