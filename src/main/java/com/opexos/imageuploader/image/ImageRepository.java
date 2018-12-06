package com.opexos.imageuploader.image;

import org.springframework.data.repository.CrudRepository;

/**
 * Image repository
 */
public interface ImageRepository extends CrudRepository<ImageData, Integer> {
}
