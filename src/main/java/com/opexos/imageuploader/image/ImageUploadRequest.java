package com.opexos.imageuploader.image;

/**
 * Describes image upload request
 */
public class ImageUploadRequest {
    private String[] images;
    private String[] urls;

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }
}
