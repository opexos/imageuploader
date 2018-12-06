package com.opexos.imageuploader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.opexos.imageuploader.exceptions.DownloadException;
import com.opexos.imageuploader.exceptions.InvalidBase64Exception;
import com.opexos.imageuploader.exceptions.JsonParseException;
import com.opexos.imageuploader.exceptions.UrlException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

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
    public static byte[] getBytes(URL url, int sizeLimit) {
        if (url == null)
            throw new IllegalArgumentException("url cannot be null");
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
    public static byte[] getBytes(InputStream inputStream) {
        try {
            try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024 * 4];
                int n;
                while (-1 != (n = inputStream.read(buffer))) {
                    data.write(buffer, 0, n);
                }
                return data.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read resource and returns array of bytes
     */
    public static byte[] getResourceBytes(String resourceFileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            try (InputStream is = classloader.getResourceAsStream(resourceFileName)) {
                return Utils.getBytes(is);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts list of objects to array of integers.
     * Each object in list will be converted to string, then parsed to integer.
     *
     * @param list list of objects
     */
    public static int[] getIntArray(List list) {
        if (list == null)
            throw new IllegalArgumentException("list cannot be null");

        return IntStream.range(0, list.size()).map(i -> Integer.parseInt(list.get(i).toString())).toArray();
    }

    /**
     * Decodes Base64 string to array of bytes.
     *
     * @param base64 string encoded in base64 format
     */
    public static byte[] decodeBase64(String base64) {
        if (base64 == null)
            throw new IllegalArgumentException("base64 cannot be null");
        if (base64.isEmpty())
            throw new IllegalArgumentException("base64 is empty string");

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
    public static String encodeToBase64(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        if (data.length == 0)
            throw new IllegalArgumentException("data is empty");

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
