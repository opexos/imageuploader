package com.opexos.imageuploader.exception;

/**
 * Thrown when cannot decode base64 string
 */
public class InvalidBase64Exception extends AppException {
    public InvalidBase64Exception(String message) {
        super(message);
    }
}
