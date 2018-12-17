package com.opexos.imageuploader.image;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * Image repository
 */
public interface ImageRepository extends Repository<ImageData, Long> {
    /**
     * Saves a given entity
     */
    ImageData save(ImageData entity);

    /**
     * Returns original image by id
     */
    @Query("select i.original from ImageData i where i.id = ?1")
    byte[] getOriginal(long id);

    /**
     * Returns preview image by id
     */
    @Query("select i.preview from ImageData i where i.id = ?1")
    byte[] getPreview(long id);

}
