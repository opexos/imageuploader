package com.opexos.imageuploader.net;

import com.opexos.imageuploader.exception.DownloadException;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class NetworkServiceTest {

    NetworkService networkService;
    URL urlToStaticFile;

    @Before
    public void init() {
        networkService = new NetworkService();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        urlToStaticFile = classloader.getResource("staticfile");
    }

    @Test
    public void download_ok() {
        byte[] bytes = networkService.download(urlToStaticFile, Integer.MAX_VALUE);
        assertThat(bytes, equalTo("Do not modify the contents of this file. It is used in tests.".getBytes()));
    }

    @Test
    public void download_fail_tooBig() {
        try {
            networkService.download(urlToStaticFile, 1);
            fail("Size exceed check failed");
        } catch (DownloadException e) {
            assertThat(e.getMessage(), containsStringIgnoringCase("maximum size exceeded"));
        }

    }
}