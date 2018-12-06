package com.opexos.imageuploader;

import com.opexos.imageuploader.exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Contains exception handlers
 */
@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse handleException(Exception e) {
        BaseResponse resp = new BaseResponse();
        resp.setSuccess(false);

        if (e instanceof AppException) {
            //custom application exceptions, usually contain human readable messages
            resp.setMessage(e.getMessage());
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            //thrown when inconsistent request params passed.
            //only last part of message is human readable
            resp.setMessage(StringUtils.substringAfterLast(e.getMessage(), ":").trim());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            //thrown when api is not properly accessed
            resp.setMessage(e.getMessage());
        } else if (e instanceof MaxUploadSizeExceededException) {
            //thrown when an attempt is made to upload a file that is too large
            resp.setMessage("Maximum upload size exceeded");
        } else {
            //write to log all other exceptions
            resp.setMessage("Internal server error");
            log.error("Unhandled exception", e);
        }
        return resp;
    }

}
