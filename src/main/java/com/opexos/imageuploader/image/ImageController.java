package com.opexos.imageuploader.image;

import com.google.common.primitives.Longs;
import com.opexos.imageuploader.Utils;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;

/**
 * Image controller
 */
@RestController
public class ImageController {

    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Upload image
     */
    @PostMapping("/image")
    @Transactional
    public ImageUploadResponse uploadImage(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles,
                                           @RequestBody(required = false) String requestBody) throws Exception {

        //used for collect ids
        val imageIds = new ArrayList<Long>();

        //files received in multipart/form-data
        if (multipartFiles != null) {
            for (MultipartFile mf : multipartFiles) {
                imageIds.add(imageService.save(mf.getBytes()));
            }
        }

        //parse data in body
        if (requestBody != null) {
            //try to convert the request body to an object
            val req = Utils.parseJson(requestBody, ImageUploadRequest.class);

            //save base64 images
            if (req.getImages() != null) {
                for (String it : req.getImages()) {
                    imageIds.add(imageService.save(it));
                }
            }

            //save images using urls
            if (req.getUrls() != null) {
                for (String it : req.getUrls()) {
                    URL url = Utils.parseUrl(it);
                    imageIds.add(imageService.save(url));
                }
            }

        }

        //return ids of uploaded images
        val resp = new ImageUploadResponse();
        resp.setIdList(Longs.toArray(imageIds));

        return resp;
    }

    /**
     * Retrieve an image
     */
    @GetMapping(value = "/image/{id:[\\d]+}", produces = "image/jpeg")
    public byte[] getImage(@PathVariable long id, @RequestParam(value = "preview", required = false) boolean preview) {
        val image = preview ? imageService.getPreviewImage(id) : imageService.getOriginalImage(id);
        return image.getData();
    }

    /**
     * Returns some statistics about images
     */
    @GetMapping(value = "/image/stats")
    public ImageStatsResponse getStats() {
        val stats = imageService.getStats();
        return new ImageStatsResponse(stats.getTotalBytesStored(), stats.getMemcachedTriggeredCount());
    }

}
