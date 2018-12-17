package com.opexos.imageuploader.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Some statistics about images
 */
@AllArgsConstructor
public class ImageStats {
    @Getter @Setter @NonNull private long totalBytesStored;
    @Getter @Setter @NonNull private long memcachedTriggeredCount;
}
