package com.pxf.fftv.plus.bean;

/**
 * M3U频道数据模型
 */
public class M3uChannel {
    private String name;
    private String url;
    private String logo;
    private String group;
    private String tvgId;

    public M3uChannel() {
    }

    public M3uChannel(String name, String url, String logo, String group) {
        this.name = name;
        this.url = url;
        this.logo = logo;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTvgId() {
        return tvgId;
    }

    public void setTvgId(String tvgId) {
        this.tvgId = tvgId;
    }
}
