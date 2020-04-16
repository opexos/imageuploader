package com.opexos.imageuploader.exception;

/**
 * Thrown when an attempt is made to upload a file that is too large
 */
public class ImageTooBigException extends AppException {
    public ImageTooBigException(String message) {
        super(message);
    }
}
