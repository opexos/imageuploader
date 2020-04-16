package com.opexos.imageuploader.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.commondto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contains exception handlers
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO("Internal server error"));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(ResponseStatusException e) {
        log.info("Exception handled: {}", e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErrorDTO(e.getReason()));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(MethodArgumentTypeMismatchException e) {
        log.info("Exception handled: {}", e.getMessage());
        //thrown when inconsistent request params passed.
        //only last part of message is human readable
        val message = Utils.substringAfterLast(e.getMessage(), ":");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(message));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(HttpRequestMethodNotSupportedException e) {
        log.info("Exception handled: {}", e.getMessage());
        //thrown when api is not properly accessed
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(MaxUploadSizeExceededException e) {
        log.info("Exception handled: {}", e.getMessage());
        //thrown when an attempt is made to upload a file that is too large
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("Maximum upload size exceeded"));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(HttpMessageNotReadableException e) {
        log.info("Exception handled: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(JsonParseException e) {
        log.info("Exception handled: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler
    public HttpEntity<ErrorDTO> handle(JsonMappingException e) {
        log.info("Exception handled: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler
    public void handle(InterruptedException e) {
        //do nothing. the server may be shutting down
    }

}
