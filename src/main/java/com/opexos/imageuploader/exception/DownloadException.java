package com.opexos.imageuploader.exception;

/**
 * Thrown to indicate that errors occurred while retrieving the contents of a resource
 */
public class DownloadException extends AppException {
    public DownloadException(String message) {
        super(message);
    }
}
