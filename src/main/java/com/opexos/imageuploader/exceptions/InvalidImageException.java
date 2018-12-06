package com.opexos.imageuploader.exceptions;

/**
 * Thrown to indicate that image format is invalid
 */
public class InvalidImageException extends AppException {
    public InvalidImageException() {
    }

    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidImageException(Throwable cause) {
        super(cause);
    }

    public InvalidImageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
