package com.opexos.imageuploader;

/**
 * Base class for all REST responses
 */
public class BaseResponse {
    private boolean success = true;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
