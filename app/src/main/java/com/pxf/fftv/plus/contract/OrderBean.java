package com.pxf.fftv.plus.contract;

public class OrderBean {


    /**
     * orderno : 2019121422514394836
     * url : http://yz.meetpt.cn/sdk/epayapi.php?paytype=alipay&amount=1.0&orderno=2019121422514394836
     */

    private String orderno;
    private String url;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
