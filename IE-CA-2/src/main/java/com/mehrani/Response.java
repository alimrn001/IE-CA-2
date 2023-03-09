package com.mehrani;

public class Response {
    private boolean success;
    private String data;

    public void setData(String data) {
        this.data = data;
    }
    public void setSuccess(boolean status) {
        this.success = status;
    }
    public boolean getSuccess() {
        return success;
    }
    public String getData() {
        return data;
    }
}
