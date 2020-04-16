package com.opexos.imageuploader;

import com.opexos.imageuploader.exception.InvalidBase64Exception;
import com.opexos.imageuploader.exception.InvalidUrlException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class UtilsTest {


    @Test
    public void readStream_ok() throws IOException {
        byte[] data = Helpers.getRandomByteArray(1000);

        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            byte[] data2 = Utils.readStream(is);
            assertThat(data2, equalTo(data));
        }

    }

    @Test
    public void getResourceBytes_ok() {
        byte[] resourceArray = Utils.getResourceBytes("staticfile");
        assertThat(resourceArray, equalTo("Do not modify the contents of this file. It is used in tests.".getBytes()));
    }

    @Test
    public void decodeBase64_ok() {
        byte[] data = Utils.decodeBase64("dGVzdCBtZXNzYWdl");
        assertThat(data, equalTo("test message".getBytes()));
    }

    @Test(expected = InvalidBase64Exception.class)
    public void decodeBase64_fail() {
        Utils.decodeBase64("dGVzdCBtZXNzYWdl2");
    }

    @Test
    public void encodeToBase64_ok() {
        String str = Utils.encodeToBase64("test message".getBytes());
        assertThat(str, equalTo("dGVzdCBtZXNzYWdl"));
    }


    @Test
    public void parseUrl_ok() {
        URL url = Utils.parseUrl("http://google.com");
        assertThat(url.getProtocol(), equalTo("http"));
        assertThat(url.getHost(), equalTo("google.com"));
    }

    @Test(expected = InvalidUrlException.class)
    public void parseUrl_fail() {
        Utils.parseUrl("htz://google.com");
    }

    @Test
    public void substringAfterLast_ok() {
        String result = Utils.substringAfterLast("123:456:7890", ":");
        assertThat(result, equalTo("7890"));
    }

    @Test
    public void substringAfterLast_ok_noDelimiterInString() {
        String result = Utils.substringAfterLast("1234567890", ":");
        assertThat(result, equalTo("1234567890"));
    }

    @Test
    public void substringBeforeFirst_ok() {
        String result = Utils.substringBeforeFirst("123:456:7890", ":");
        assertThat(result, equalTo("123"));
    }

    @Test
    public void substringBeforeFirst_ok_noDelimiterInString() {
        String result = Utils.substringBeforeFirst("1234567890", ":");
        assertThat(result, equalTo("1234567890"));
    }
}
