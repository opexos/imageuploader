package com.opexos.imageuploader.exception;

/**
 * Thrown to indicate that image format is invalid
 */
public class InvalidImageException extends AppException {
    public InvalidImageException(String message) {
        super(message);
    }
}
