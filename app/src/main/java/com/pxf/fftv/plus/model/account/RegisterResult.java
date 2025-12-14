package com.pxf.fftv.plus.model.account;

public class RegisterResult {

    private boolean success = false;

    private String message;

    public RegisterResult() {
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
