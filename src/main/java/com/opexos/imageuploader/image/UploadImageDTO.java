package com.opexos.imageuploader.image;

import lombok.Data;

/**
 * Describes image upload request
 */
@Data
public class UploadImageDTO {
    private String[] images;
    private String[] urls;
}
