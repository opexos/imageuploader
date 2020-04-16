package com.opexos.imageuploader.net;

import com.opexos.imageuploader.exception.DownloadException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Service
public class NetworkService {
    /**
     * Reads a resource at the specified address and returns as an array of bytes.
     *
     * @param url       address to resource
     * @param sizeLimit specify maximum allowed size
     */
    public byte[] download(@NonNull URL url, int sizeLimit) {
        if (sizeLimit <= 0) {
            throw new IllegalArgumentException("sizeLimit cannot be less or equal zero");
        }

        try (InputStream input = url.openStream()) {
            try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024 * 4];
                int n;
                while (-1 != (n = input.read(buffer))) {
                    data.write(buffer, 0, n);
                    if (data.size() > sizeLimit) {
                        throw new DownloadException(
                                String.format("Maximum size exceeded: %d URL: %s", sizeLimit, url.toExternalForm()));
                    }
                }
                return data.toByteArray();
            }
        } catch (DownloadException e) {
            throw e;
        } catch (Exception e) {
            throw new DownloadException("Cannot read data from " + url.toExternalForm());
        }
    }
}
