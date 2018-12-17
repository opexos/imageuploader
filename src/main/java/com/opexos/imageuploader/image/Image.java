package com.opexos.imageuploader.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Image entity. Business layer.
 */
@AllArgsConstructor
public class Image {
    @Getter @Setter @NonNull private long id;
    @Getter @Setter @NonNull private byte[] data;
}
