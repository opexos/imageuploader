package com.opexos.imageuploader.image;

import lombok.Getter;
import lombok.Setter;

/**
 * Describes image upload request
 */
public class ImageUploadRequest {
    @Getter @Setter private String[] images;
    @Getter @Setter private String[] urls;
}
