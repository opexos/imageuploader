package com.opexos.imageuploader.image;

import com.opexos.imageuploader.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Describes image upload response
 */
public class ImageUploadResponse extends BaseResponse {
    @Getter @Setter @NonNull private long[] idList;
}
