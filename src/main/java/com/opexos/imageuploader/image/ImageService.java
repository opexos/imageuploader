package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.exceptions.ImageTooBigException;
import com.opexos.imageuploader.exceptions.InvalidImageException;
import com.opexos.imageuploader.exceptions.ResourceNotFoundException;
import com.opexos.imageuploader.exceptions.UrlException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Image service
 */
@Service
public class ImageService {

    private ImageRepository imageRepository;

    @Value("${image.preview-size}")
    private int previewSize;

    @Value("${image.max-file-size}")
    private int imageMaxFileSize;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Writes an image to the repository and returns its id
     *
     * @param imageData image as byte array
     */
    public int save(byte[] imageData) {
        if (imageData == null)
            throw new IllegalArgumentException("imageData cannot be null");
        if (imageData.length > imageMaxFileSize)
            throw new ImageTooBigException(
                    String.format("Maximum image size exceeded: %d, actual: %d", imageMaxFileSize, imageData.length));

        BufferedImage original = getBufferedImage(imageData);

        //we need square shape. crop image
        int w = original.getWidth();
        int h = original.getHeight();

        BufferedImage croped;
        if (w > h) {
            croped = Scalr.crop(original, (w - h) / 2, 0, h, h);
        } else if (w < h) {
            croped = Scalr.crop(original, 0, (h - w) / 2, w, w);
        } else {
            //already square. magic
            croped = original;
        }

        BufferedImage preview = Scalr.resize(croped, previewSize);

        //make entity
        ImageData imgData = new ImageData();
        imgData.setOriginal(getBytes(original));
        imgData.setPreview(getBytes(preview));
        imgData.setUploadDate(LocalDateTime.now());

        //save to DB
        imageRepository.save(imgData);

        return imgData.getId();
    }

    /**
     * Writes an image to the repository and returns its id
     *
     * @param imageDataBase64 image as Base64 string
     */
    public int save(String imageDataBase64) {
        if (imageDataBase64 == null)
            throw new IllegalArgumentException("imageDataBase64 cannot be null");
        if (imageDataBase64.isEmpty())
            throw new IllegalArgumentException("imageDataBase64 cannot be empty");

        byte[] imgData = Utils.decodeBase64(imageDataBase64);
        return save(imgData);
    }

    /**
     * Writes an image to the repository and returns its id
     *
     * @param imageUrl url to image
     */
    public int save(URL imageUrl) {
        if (imageUrl == null)
            throw new IllegalArgumentException("imageUrl cannot be null");

        if (!"http".equals(imageUrl.getProtocol()) &&
                !"https".equals(imageUrl.getProtocol())) {
            throw new UrlException("Only http and https protocols allowed for image upload: "
                    + imageUrl.toExternalForm());
        }

        byte[] imgData = Utils.getBytes(imageUrl, imageMaxFileSize);
        try {
            return save(imgData);
        } catch (InvalidImageException e) {
            //add url to message
            throw new InvalidImageException(e.getMessage() + " URL: " + imageUrl.toExternalForm(), e);
        }
    }

    /**
     * Returns original image by id
     *
     * @param imageId image id
     */
    public Image getOriginalImage(int imageId) {
        Optional<ImageData> image = imageRepository.findById(imageId);
        if (image.isPresent()) {
            return new Image(imageId, image.get().getOriginal());
        }
        throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
    }

    /**
     * Returns preview image by id
     *
     * @param imageId image id
     */
    public Image getPreviewImage(int imageId) {
        Optional<ImageData> image = imageRepository.findById(imageId);
        if (image.isPresent()) {
            return new Image(imageId, image.get().getPreview());
        }
        throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
    }

    /**
     * Converts BufferedImage to array of bytes.
     * Jpeg format is used.
     *
     * @param image image to convert
     */
    public byte[] getBytes(BufferedImage image) {
        if (image == null)
            throw new IllegalArgumentException("image cannot be null");

        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "jpg", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts array of bytes to image.
     *
     * @param data array of bytes
     */
    public BufferedImage getBufferedImage(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        try {
            try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
                try {
                    BufferedImage img = ImageIO.read(is);
                    if (img == null) throw new Exception(); //sometimes ImageIO.read returns null
                    return img;
                } catch (Exception e) {
                    throw new InvalidImageException("Invalid image format or data is " +
                            "corrupted. Content size: " + data.length);
                }
            }
        } catch (IOException e) {
            //convert to runtime exception
            throw new RuntimeException(e);
        }
    }
}
