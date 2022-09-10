package com.pxf.fftv.plus.contract;

public class VipPayEvent {

    private String title;

    private float price;

    // 支付后增加的时间秒数
    private long addSecond;

    public VipPayEvent(String title, float price, long addSecond) {
        this.title = title;
        this.price = price;
        this.addSecond = addSecond;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getAddSecond() {
        return addSecond;
    }

    public void setAddSecond(long addSecond) {
        this.addSecond = addSecond;
    }
}
