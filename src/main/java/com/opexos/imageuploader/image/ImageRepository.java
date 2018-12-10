package com.opexos.imageuploader.image;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * Image repository
 */
public interface ImageRepository extends Repository<ImageData, Integer> {
    /**
     * Saves a given entity
     */
    ImageData save(ImageData entity);

    @Query("select i.original from ImageData i where i.id = ?1")
    byte[] getOriginal(int id);

    @Query("select i.preview from ImageData i where i.id = ?1")
    byte[] getPreview(int id);



}
