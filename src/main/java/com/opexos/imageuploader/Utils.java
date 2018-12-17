package com.opexos.imageuploader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.opexos.imageuploader.exceptions.DownloadException;
import com.opexos.imageuploader.exceptions.InvalidBase64Exception;
import com.opexos.imageuploader.exceptions.JsonParseException;
import com.opexos.imageuploader.exceptions.UrlException;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

/**
 * Helper methods
 */
public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads a resource at the specified address and returns as an array of bytes.
     *
     * @param url       address to resource
     * @param sizeLimit specify maximum allowed size
     */
    public static byte[] getBytes(@NonNull URL url, int sizeLimit) {
        if (sizeLimit <= 0)
            throw new IllegalArgumentException("sizeLimit cannot be less or equal zero");

        try (InputStream input = url.openStream()) {
            try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024 * 4];
                int n;
                while (-1 != (n = input.read(buffer))) {
                    data.write(buffer, 0, n);
                    if (data.size() > sizeLimit)
                        throw new DownloadException(
                                String.format("Maximum size exceeded: %d URL: %s", sizeLimit, url.toExternalForm()));
                }
                return data.toByteArray();
            }
        } catch (DownloadException e) {
            throw e;
        } catch (Exception e) {
            throw new DownloadException("Cannot read data from " + url.toExternalForm());
        }
    }

    /**
     * Reads data from InputStream and returns array of bytes
     *
     * @param inputStream input stream for read
     */
    @SneakyThrows
    public static byte[] getBytes(@NonNull InputStream inputStream) {
        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024 * 4];
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                data.write(buffer, 0, n);
            }
            return data.toByteArray();
        }
    }

    /**
     * Read resource and returns array of bytes
     */
    @SneakyThrows
    public static byte[] getResourceBytes(@NonNull String resourceFileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(resourceFileName)) {
            return Utils.getBytes(is);
        }
    }

    /**
     * Decodes Base64 string to array of bytes.
     *
     * @param base64 string encoded in base64 format
     */
    public static byte[] decodeBase64(@NonNull String base64) {
        if (base64.isEmpty())
            throw new IllegalArgumentException("base64 string cannot be empty");

        try {
            return Base64.getDecoder().decode(base64.getBytes());
        } catch (Exception e) {
            throw new InvalidBase64Exception("Cannot decode base64 string: " + e.getMessage());
        }
    }

    /**
     * Encodes array of bytes to Base64 string
     *
     * @param data data to encode
     */
    public static String encodeToBase64(@NonNull byte[] data) {
        if (data.length == 0)
            throw new IllegalArgumentException("data cannot be empty");

        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Parse string as Json and returns java object
     *
     * @param jsonString Json string
     * @param valueType  java class
     */
    public static <T> T parseJson(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (UnrecognizedPropertyException e) {
            //thrown when json contain unknown property
            throw new JsonParseException("Cannot parse JSON: "
                    //extract human readable part
                    + StringUtils.substringBeforeFirst(e.getMessage(), "(").trim());
        } catch (Exception e) {
            throw new JsonParseException("Cannot parse JSON");
        }
    }

    /**
     * Parse string and returns URL
     */
    public static URL parseUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            throw new UrlException("Invalid URL: " + urlString);
        }
    }

}
