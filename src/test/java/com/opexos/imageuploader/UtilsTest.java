package com.opexos.imageuploader;

import com.opexos.imageuploader.exceptions.DownloadException;
import com.opexos.imageuploader.exceptions.InvalidBase64Exception;
import com.opexos.imageuploader.exceptions.JsonParseException;
import com.opexos.imageuploader.exceptions.UrlException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.opexos.imageuploader.matchers.StringContainsIgnoreCase.containsStringIgnoreCase;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UtilsTest {

    @Test
    public void testGetBytesUrl() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource("staticfile");

        byte[] bytes = Utils.getBytes(url, Integer.MAX_VALUE);

        assertThat(bytes, equalTo("Do not modify the contents of this file. It is used in tests.".getBytes()));
    }

    @Test
    public void testGetBytesUrlOversize() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource("staticfile");
        try {
            Utils.getBytes(url, 10);
            fail("Size exceed check failed");
        } catch (DownloadException e) {
            assertThat(e.getMessage(), containsStringIgnoreCase("maximum size exceeded"));
        }

    }

    @Test
    public void testGetBytesInputStream() throws IOException {
        byte[] data = Helpers.getRandomByteArray(1000);

        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            byte[] data2 = Utils.getBytes(is);
            assertThat(data2, equalTo(data));
        }

    }

    @Test
    public void testGetResourceBytes() {
        byte[] resourceArray = Utils.getResourceBytes("staticfile");
        assertThat(resourceArray, equalTo("Do not modify the contents of this file. It is used in tests.".getBytes()));
    }

    @Test
    public void testGetIntArray() {
        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add("2");
        list.add("3");
        int[] intArray = Utils.getIntArray(list);
        assertThat(intArray, equalTo(new int[]{1, 2, 3}));
    }

    @Test
    public void testDecodeBase64() {
        byte[] data = Utils.decodeBase64("dGVzdCBtZXNzYWdl");
        assertThat(data, equalTo("test message".getBytes()));
    }

    @Test(expected = InvalidBase64Exception.class)
    public void testDecodeBase64Exception() {
        Utils.decodeBase64("dGVzdCBtZXNzYWdl2");
    }

    @Test
    public void testEncodeToBase64() {
        String str = Utils.encodeToBase64("test message".getBytes());
        assertThat(str, equalTo("dGVzdCBtZXNzYWdl"));
    }

    @Test
    public void testParseJson() {
        BaseResponse data = Utils.parseJson("{\"success\":\"true\", \"message\": \"test message\"}", BaseResponse.class);
        assertThat(data.isSuccess(), equalTo(true));
        assertThat(data.getMessage(), equalTo("test message"));
    }

    @Test(expected = JsonParseException.class)
    public void testParseJsonException() {
        Utils.parseJson("{success\":\"true\", \"message\": \"test message\"}", BaseResponse.class);
    }

    @Test
    public void testParseUrl() {
        URL url = Utils.parseUrl("http://google.com");
        assertThat(url.getProtocol(), equalTo("http"));
        assertThat(url.getHost(), equalTo("google.com"));
    }

    @Test(expected = UrlException.class)
    public void testParseUrlException() {
        Utils.parseUrl("htz://google.com");
    }
}
