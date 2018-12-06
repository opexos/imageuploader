package com.opexos.imageuploader.exceptions;

/**
 * Thrown when an attempt is made to upload a file that is too large
 */
public class ImageTooBigException extends AppException {
    public ImageTooBigException() {
    }

    public ImageTooBigException(String message) {
        super(message);
    }

    public ImageTooBigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageTooBigException(Throwable cause) {
        super(cause);
    }

    public ImageTooBigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
