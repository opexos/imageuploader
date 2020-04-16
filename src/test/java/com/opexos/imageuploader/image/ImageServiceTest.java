package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import com.opexos.imageuploader.Utils;
import com.opexos.imageuploader.config.ByteArrayRedisTemplate;
import com.opexos.imageuploader.config.MemcachedClient;
import com.opexos.imageuploader.exception.*;
import com.opexos.imageuploader.net.NetworkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;
    @Mock
    MemcachedClient memcachedClient;
    @Mock
    ByteArrayRedisTemplate byteArrayRedisTemplate;
    @Mock
    NetworkService networkService;

    ImageService imageService;
    int imageMaxFileSize = 1024 * 1024;
    long imageIdAfterSave = 1L;

    @Before
    public void init() {
        imageService = new ImageService(imageRepository, memcachedClient, byteArrayRedisTemplate, networkService);
        imageService.setImageMaxFileSize(imageMaxFileSize);
        when(imageRepository.save(any())).then(invocation -> {
            Image image = (Image) invocation.getArguments()[0];
            assertThat(image.getOriginal(), notNullValue());
            assertThat(image.getPreview(), notNullValue());
            image.setId(imageIdAfterSave);
            return image;
        });
    }

    @Test
    public void save_ok_byteArray() throws IOException {
        long id = imageService.save(Utils.getResourceBytes("img1.jpg"));
        assertThat(id, equalTo(imageIdAfterSave));
    }

    @Test(expected = ImageTooBigException.class)
    public void save_fail_byteArray_imageTooBig() throws IOException {
        imageService.save(Helpers.getRandomByteArray(imageMaxFileSize + 1));
    }

    @Test(expected = InvalidImageException.class)
    public void save_fail_byteArray_invalidImage() throws IOException {
        imageService.save(Helpers.getRandomByteArray(1024));
    }


    @Test
    public void save_ok_base64() throws IOException {
        long id = imageService.save(new String(Utils.getResourceBytes("base64_image")));
        assertThat(id, equalTo(imageIdAfterSave));
    }

    @Test(expected = InvalidBase64Exception.class)
    public void save_fail_base64_invalidBase64() throws IOException {
        imageService.save("dGVzdCBtZXNzYWdl2");
    }

    @Test(expected = InvalidImageException.class)
    public void save_fail_base64_invalidImage() throws IOException {
        imageService.save("YWJjZA==");
    }

    @Test
    public void save_ok_url() throws IOException {
        when(networkService.download(any(), anyInt())).thenReturn(Utils.getResourceBytes("img1.jpg"));
        long id = imageService.save(new URL("http://dummy"));
        assertThat(id, equalTo(imageIdAfterSave));
    }

    @Test(expected = InvalidUrlException.class)
    public void save_fail_url_invalidUrl() throws IOException {
        imageService.save(new URL("ftp://dummy"));
    }

    @Test
    public void getOriginalImage_ok_repo() {
        byte[] bytes = {1, 2, 3};
        when(imageRepository.getOriginalById(1L)).thenReturn(bytes);
        byte[] originalImage = imageService.getOriginalImage(1L);
        assertThat(originalImage, equalTo(bytes));
    }

    @Test
    public void getOriginalImage_ok_memcached() {
        byte[] bytes = {1, 2, 3};
        when(memcachedClient.get(any())).thenReturn(bytes);
        byte[] originalImage = imageService.getOriginalImage(anyLong());
        assertThat(originalImage, equalTo(bytes));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getOriginalImage_fail_notFound() {
        imageService.getOriginalImage(0L);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getPreviewImage_ok_repo() {
        byte[] bytes = {1, 2, 3};
        when(byteArrayRedisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(imageRepository.getPreviewById(1L)).thenReturn(bytes);
        byte[] image = imageService.getPreviewImage(1L);
        assertThat(image, equalTo(bytes));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void getPreviewImage_ok_redis() {
        byte[] bytes = {1, 2, 3};
        ValueOperations redisMock = mock(ValueOperations.class);
        when(redisMock.get(any())).thenReturn(bytes);
        when(byteArrayRedisTemplate.opsForValue()).thenReturn(redisMock);
        byte[] image = imageService.getPreviewImage(1L);
        assertThat(image, equalTo(bytes));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void getPreviewImage_fail_notFound() {
        when(byteArrayRedisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        imageService.getPreviewImage(0L);
    }
}