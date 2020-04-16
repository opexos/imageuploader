package com.opexos.imageuploader.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Image repository
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
    /**
     * Returns original image by id
     */
    @Query("select i.original from Image i where i.id = ?1")
    byte[] getOriginalById(long id);

    /**
     * Returns preview image by id
     * Native query and explicit parameter name are used for test purposes
     */
    @Query(value = "select preview from image where id = :id", nativeQuery = true)
    byte[] getPreviewById(@Param("id") long id);

}
