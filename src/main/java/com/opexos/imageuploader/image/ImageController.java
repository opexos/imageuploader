package com.opexos.imageuploader.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.commondto.IdsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Image controller
 */
@RequiredArgsConstructor
@RestController
@Api(tags = "Image")
public class ImageController {

    private final ImageService imageService;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;
    private ObjectReader readerUploadImageDTO;

    @PostConstruct
    private void init() {
        readerUploadImageDTO = objectMapper.readerFor(UploadImageDTO.class);
    }

    /**
     * Upload image. One API method support 3 different methods to upload an image.
     */
    @ApiOperation("The method for uploading images. Supports three different " +
            "loading options: multipart/form-data, json with urls, json with images in" +
            "base64 format.")
    @PostMapping("/image")
    @Transactional
    public IdsDTO uploadImage(
            @ApiParam("You can pass files in multipart/form-data format")
            @RequestParam(value = "file", required = false)
                    MultipartFile[] multipartFiles,

            @ApiParam("You can pass JSON in the following format:\n" +
                    "{\n\"images\": [\"first image in base64 format\",\"second image in base64 format\"],\n" +
                    "\"urls\": [\"first image url\",\"second image url\"]\n}")
            @RequestBody(required = false)
                    //if specify the DTO class here, the method stops accepting multipart requests
                    String requestBody) throws IOException {

        val result = new IdsDTO(new ArrayList<>());

        //files received in multipart/form-data
        if (multipartFiles != null) {
            for (MultipartFile mf : multipartFiles) {
                if (mf.getBytes().length > 0) {
                    result.getIds().add(imageService.save(mf.getBytes()));
                }
            }
        }

        //parse data in body
        if (requestBody != null) {
            //try to convert the request body to an object
            requestBody = requestBody.replace('“', '"').replace('”', '"');
            UploadImageDTO req = readerUploadImageDTO.readValue(requestBody);

            //save base64 images
            if (req.getImages() != null) {
                for (String it : req.getImages()) {
                    if (StringUtils.hasText(it)) {
                        result.getIds().add(imageService.save(it));
                    }
                }
            }

            //save images using urls
            if (req.getUrls() != null) {
                for (String it : req.getUrls()) {
                    URL url = Utils.parseUrl(it);
                    result.getIds().add(imageService.save(url));
                }
            }
        }

        return result;
    }

    /**
     * Retrieve an image
     */
    @ApiOperation("Returns an image by id")
    @GetMapping(value = "/image/{id:[\\d]+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable long id,
                           @RequestParam(value = "preview", required = false) boolean preview) {
        return preview ? imageService.getPreviewImage(id) : imageService.getOriginalImage(id);
    }

    /**
     * Returns statistics
     */
    @ApiOperation("Returns statistics for the service")
    @GetMapping(value = "/image/statistic")
    public ImageStatisticDTO getStats() {
        return conversionService.convert(imageService.getStatistic(), ImageStatisticDTO.class);
    }

}
