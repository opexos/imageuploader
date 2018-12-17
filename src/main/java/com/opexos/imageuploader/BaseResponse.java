package com.opexos.imageuploader;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all REST responses
 */
public class BaseResponse {
    @Getter @Setter private boolean success = true;
    @Getter @Setter private String message;
}
