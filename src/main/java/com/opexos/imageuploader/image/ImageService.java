package com.opexos.imageuploader.image;

import com.google.common.primitives.Longs;
import com.opexos.imageuploader.MemcachedClient;
import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.exceptions.ImageTooBigException;
import com.opexos.imageuploader.exceptions.InvalidImageException;
import com.opexos.imageuploader.exceptions.ResourceNotFoundException;
import com.opexos.imageuploader.exceptions.UrlException;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Image service
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemcachedClient memcachedClient;
    private final StringRedisTemplate redisTemplate;
    private static final String KEY_TOTAL_BYTES_STORED = "TOTAL_BYTES_STORED";
    private static final String KEY_MEMCACHED_TRIGGERED_COUNT = "MEMCACHED_TRIGGERED_COUNT";

    @Value("${image.preview-size}")
    private int previewSize;

    @Value("${image.max-file-size}")
    private int imageMaxFileSize;

    @Autowired
    public ImageService(ImageRepository imageRepository, MemcachedClient memcachedClient, StringRedisTemplate redisTemplate) {
        this.imageRepository = imageRepository;
        this.memcachedClient = memcachedClient;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Writes an image to the repository and returns its id
     *
     * @param imageData image as byte array
     */
    public long save(@NonNull byte[] imageData) {
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

        redisTemplate.opsForValue().increment(KEY_TOTAL_BYTES_STORED, imgData.getOriginal().length + imgData.getPreview().length);

        return imgData.getId();
    }

    /**
     * Writes an image to the repository and returns its id
     *
     * @param imageDataBase64 image as Base64 string
     */
    public long save(@NonNull String imageDataBase64) {
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
    public long save(@NonNull URL imageUrl) {

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
    public Image getOriginalImage(long imageId) {
        val key = "originalImage" + imageId;
        byte[] original = memcachedClient.get(key);
        if (original == null) {
            original = imageRepository.getOriginal(imageId);
            if (original == null)
                throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
            memcachedClient.set(key, 3600, original);
        } else {
            redisTemplate.opsForValue().increment(KEY_MEMCACHED_TRIGGERED_COUNT, 1);
        }
        return new Image(imageId, original);
    }

    /**
     * Returns preview image by id
     *
     * @param imageId image id
     */
    public Image getPreviewImage(long imageId) {
        val key = "previewImage" + imageId;
        byte[] preview = memcachedClient.get(key);
        if (preview == null) {
            preview = imageRepository.getPreview(imageId);
            if (preview == null)
                throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
            memcachedClient.set(key, 3600, preview);
        } else {
            redisTemplate.opsForValue().increment(KEY_MEMCACHED_TRIGGERED_COUNT, 1);
        }
        return new Image(imageId, preview);
    }

    /**
     * Converts BufferedImage to array of bytes.
     * Jpeg format is used.
     *
     * @param image image to convert
     */
    @SneakyThrows
    public byte[] getBytes(@NonNull BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Converts array of bytes to image.
     *
     * @param data array of bytes
     */
    @SneakyThrows
    public BufferedImage getBufferedImage(@NonNull byte[] data) {
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
    }

    public ImageStats getStats() {
        var totalBytesStored = redisTemplate.opsForValue().get(KEY_TOTAL_BYTES_STORED);
        if (totalBytesStored == null || totalBytesStored.isEmpty())
            totalBytesStored = "0";

        var memcachedTriggeredCount = redisTemplate.opsForValue().get(KEY_MEMCACHED_TRIGGERED_COUNT);
        if (memcachedTriggeredCount == null || memcachedTriggeredCount.isEmpty())
            memcachedTriggeredCount = "0";

        return new ImageStats(
                Longs.tryParse(totalBytesStored),
                Longs.tryParse(memcachedTriggeredCount));
    }
}
