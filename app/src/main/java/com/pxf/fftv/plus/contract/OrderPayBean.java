package com.pxf.fftv.plus.contract;

public class OrderPayBean {

    /**
     * orderno : 2019121423385554981
     * status : 0 暂未支付 1 支付成功
     */

    private String orderno;
    private String status;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
