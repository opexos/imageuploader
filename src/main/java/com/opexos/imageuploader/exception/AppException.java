package com.opexos.imageuploader.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Base class for all application exceptions
 */
public class AppException extends ResponseStatusException {

    public AppException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public AppException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

}
