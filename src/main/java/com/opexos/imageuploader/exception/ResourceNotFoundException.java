package com.opexos.imageuploader.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown to indicate that application cannot find the resource
 */
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
