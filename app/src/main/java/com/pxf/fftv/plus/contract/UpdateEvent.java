package com.pxf.fftv.plus.contract;

public class UpdateEvent {

    private String title;

    private String message;

    private String apkUrl;

    public UpdateEvent(String title, String message, String apkUrl) {
        this.title = title;
        this.message = message;
        this.apkUrl = apkUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
