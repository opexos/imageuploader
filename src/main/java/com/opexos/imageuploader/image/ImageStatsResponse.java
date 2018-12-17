package com.opexos.imageuploader.image;

import com.opexos.imageuploader.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Response with some statistics about images
 */
@AllArgsConstructor
public class ImageStatsResponse extends BaseResponse {
    @Getter @Setter @NonNull private long totalBytesStored;
    @Getter @Setter @NonNull private long memcachedTriggeredCount;
}
