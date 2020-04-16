package com.opexos.imageuploader.exception;

/**
 * Thrown to indicate URL errors
 */
public class InvalidUrlException extends AppException {
    public InvalidUrlException(String message) {
        super(message);
    }
}
