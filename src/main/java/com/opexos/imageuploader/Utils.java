package com.opexos.imageuploader;

import com.opexos.imageuploader.exception.InvalidBase64Exception;
import com.opexos.imageuploader.exception.InvalidUrlException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

/**
 * Helper methods
 */
@RequiredArgsConstructor
@Component
public class Utils {


    /**
     * Reads data from InputStream and returns array of bytes
     *
     * @param inputStream input stream for read
     */
    @SneakyThrows
    public static byte[] readStream(@NonNull InputStream inputStream) {
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
            assert is != null;
            return readStream(is);
        }
    }

    /**
     * Decodes Base64 string to array of bytes.
     *
     * @param base64 string encoded in base64 format
     */
    public static byte[] decodeBase64(@NonNull String base64) {
        if (base64.isEmpty()) {
            throw new IllegalArgumentException("base64 string cannot be empty");
        }

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
        if (data.length == 0) {
            throw new IllegalArgumentException("data cannot be empty");
        }

        return Base64.getEncoder().encodeToString(data);
    }


    /**
     * Parse string and returns URL
     */
    public static URL parseUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            throw new InvalidUrlException("Invalid URL: " + urlString);
        }
    }

    /**
     * Returns a substring after the last occurrence of delimiter.
     * If the string does not contain the delimiter, returns source string.
     *
     * @param str       string
     * @param delimiter delimiter
     */
    public static String substringAfterLast(String str, @NonNull String delimiter) {
        if (str == null) {
            return null;
        }
        int lastIndex = str.lastIndexOf(delimiter);
        return lastIndex == -1 ? str : str.substring(lastIndex + 1);
    }

    /**
     * Returns a substring before the first occurrence of delimiter.
     * If the string does not contain the delimiter, returns source string.
     *
     * @param str       string
     * @param delimiter delimiter
     */
    public static String substringBeforeFirst(String str, @NonNull String delimiter) {
        if (str == null) {
            return null;
        }
        int firstIndex = str.indexOf(delimiter);
        return firstIndex == -1 ? str : str.substring(0, firstIndex);
    }


}
