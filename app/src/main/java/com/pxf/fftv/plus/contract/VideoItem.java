package com.pxf.fftv.plus.contract;

import com.pxf.fftv.plus.Const;

public class VideoItem {

    private int index;

    private Const.VideoType type;

    private String title;

    private String url;

    public VideoItem(int index, Const.VideoType type, String title) {
        this.index = index;
        this.type = type;
        this.title = title;
    }

    public VideoItem(int index, String url, String title) {
        this.index = index;
        this.url = url;
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public Const.VideoType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
