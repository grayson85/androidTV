package com.pxf.fftv.plus.model;

import java.io.Serializable;

public class VideoEngineParam implements Serializable {

    private static final long serialVersionUID = -37792020457731015L;

    private String video1Title;

    private String video2Title;

    private String url;

    public VideoEngineParam(String video1Title, String video2Title, String url) {
        this.video1Title = video1Title;
        this.video2Title = video2Title;
        this.url = url;
    }

    public String getVideo1Title() {
        return video1Title;
    }

    public String getUrl() {
        return url;
    }
}
