package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        List<Integer> imageIds = new ArrayList<>();

        //files received in multipart/form-data
        if (multipartFiles != null) {
            for (MultipartFile mf : multipartFiles) {
                imageIds.add(imageService.save(mf.getBytes()));
            }
        }

        //parse data in body
        if (requestBody != null) {
            //try to convert the request body to an object
            ImageUploadRequest req = Utils.parseJson(requestBody, ImageUploadRequest.class);

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
        ImageUploadResponse resp = new ImageUploadResponse();
        resp.setIdList(Utils.getIntArray(imageIds));

        return resp;
    }

    /**
     * Retrieve an image
     */
    @GetMapping(value = "/image/{id:[\\d]+}", produces = "image/jpeg")
    public byte[] getImage(@PathVariable int id, @RequestParam(value = "preview", required = false) boolean preview) {
        Image image = preview ? imageService.getPreviewImage(id) : imageService.getOriginalImage(id);
        return image.getData();
    }

}
