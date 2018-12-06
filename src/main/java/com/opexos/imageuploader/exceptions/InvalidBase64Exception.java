package com.opexos.imageuploader.exceptions;

/**
 * Thrown when cannot decode base64 string
 */
public class InvalidBase64Exception extends AppException {
    public InvalidBase64Exception() {
    }

    public InvalidBase64Exception(String message) {
        super(message);
    }

    public InvalidBase64Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBase64Exception(Throwable cause) {
        super(cause);
    }

    public InvalidBase64Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
