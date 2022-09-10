package com.pxf.fftv.plus.model.account;

public class LoginResult {

    private boolean success = false;

    private String message;

    // 会员到期时间，-1为永久，0为无会员，否则为到期时间戳
    private long expirationDate;

    private String token;

    public LoginResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
