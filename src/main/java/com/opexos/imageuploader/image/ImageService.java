package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.config.ByteArrayRedisTemplate;
import com.opexos.imageuploader.config.MemcachedClient;
import com.opexos.imageuploader.exception.ImageTooBigException;
import com.opexos.imageuploader.exception.InvalidImageException;
import com.opexos.imageuploader.exception.InvalidUrlException;
import com.opexos.imageuploader.exception.ResourceNotFoundException;
import com.opexos.imageuploader.net.NetworkService;
import lombok.*;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Image service
 */
@RequiredArgsConstructor
@Transactional
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemcachedClient memcachedClient;
    private final ByteArrayRedisTemplate redisTemplate;
    private final NetworkService networkService;

    private final AtomicLong redisUsedAmount = new AtomicLong(0);
    private final AtomicLong memcachedUsedAmount = new AtomicLong(0);

    @Value("${image.preview-size}")
    private int previewSize;

    @Setter
    @Value("${image.max-file-size}")
    private int imageMaxFileSize;

    @Value("${image.cache-expiration}")
    private int cacheExpiration;

    /**
     * Writes an image to the repository and returns id
     *
     * @param imageData image as byte array
     */
    public long save(@NonNull byte[] imageData) throws IOException {
        if (imageData.length > imageMaxFileSize) {
            throw new ImageTooBigException(String.format("Maximum image size exceeded: %d, actual: %d",
                    imageMaxFileSize, imageData.length));
        }

        BufferedImage original = getImage(imageData);
        BufferedImage preview = getPreviewImage(original);

        Image image = Image.builder()
                .original(getBytes(original))
                .preview(getBytes(preview))
                .uploadDate(OffsetDateTime.now())
                .build();

        imageRepository.save(image);
        return image.getId();
    }

    /**
     * Writes an image to the repository and returns id
     *
     * @param imageDataBase64 image as Base64 string
     */
    public long save(@NonNull String imageDataBase64) throws IOException {
        byte[] data = Utils.decodeBase64(imageDataBase64);
        return save(data);
    }

    /**
     * Writes an image to the repository and returns id
     *
     * @param imageUrl url to image
     */
    public long save(@NonNull URL imageUrl) throws IOException {
        if (!"http".equals(imageUrl.getProtocol()) &&
                !"https".equals(imageUrl.getProtocol())) {
            throw new InvalidUrlException("Only http and https protocols allowed for image upload: "
                    + imageUrl.toExternalForm());
        }

        byte[] data = networkService.download(imageUrl, imageMaxFileSize);
        try {
            return save(data);
        } catch (InvalidImageException e) {
            //rethrow with url
            throw new InvalidImageException(e.getMessage() + " URL: " + imageUrl.toExternalForm());
        }
    }

    /**
     * Returns original image by id
     * Caching images in Memcached for test purposes
     *
     * @param imageId image id
     */
    public byte[] getOriginalImage(long imageId) {
        val key = "originalImage" + imageId;
        byte[] original = memcachedClient.get(key);
        if (original == null) {
            original = imageRepository.getOriginalById(imageId);
            if (original == null) {
                throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
            }
            memcachedClient.set(key, cacheExpiration, original);
        } else {
            memcachedUsedAmount.incrementAndGet();
        }
        return original;
    }

    /**
     * Returns preview image by id
     * Caching images in Redis for test purposes
     *
     * @param imageId image id
     */
    public byte[] getPreviewImage(long imageId) {
        val key = "previewImage" + imageId;
        byte[] preview = redisTemplate.opsForValue().get(key);
        if (preview == null) {
            preview = imageRepository.getPreviewById(imageId);
            if (preview == null) {
                throw new ResourceNotFoundException("Image is not found. Id: " + imageId);
            }
            redisTemplate.opsForValue().set(key, preview, cacheExpiration, TimeUnit.SECONDS);
        } else {
            redisUsedAmount.incrementAndGet();
        }
        return preview;
    }

    private BufferedImage getPreviewImage(@NonNull BufferedImage image) {
        //we need square shape. crop image
        int w = image.getWidth();
        int h = image.getHeight();

        BufferedImage croped;
        if (w > h) {
            croped = Scalr.crop(image, (w - h) / 2, 0, h, h);
        } else if (w < h) {
            croped = Scalr.crop(image, 0, (h - w) / 2, w, w);
        } else {
            //already square
            croped = image;
        }

        return Scalr.resize(croped, previewSize);
    }

    private byte[] getBytes(@NonNull BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        }
    }

    private BufferedImage getImage(@NonNull byte[] data) throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            try {
                BufferedImage img = ImageIO.read(is);
                if (img == null) {
                    throw new Exception(); //sometimes ImageIO.read returns null instead exception
                }
                return img;
            } catch (Exception e) {
                throw new InvalidImageException("Invalid image format or data is " +
                        "corrupted. Content size: " + data.length);
            }
        }
    }

    @Builder
    @Data
    public static class Statistic {
        private long redisUsedAmount;
        private long memcachedUsedAmount;
        private long totalPicturesSaved;
    }

    /**
     * Returns service statistic
     */
    public Statistic getStatistic() {
        return Statistic.builder()
                .redisUsedAmount(redisUsedAmount.get())
                .memcachedUsedAmount(memcachedUsedAmount.get())
                .totalPicturesSaved(imageRepository.count())
                .build();
    }

}
