package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void shouldReturnSameDataWhenSaveAndLoad() throws Exception {
        byte[] original = Helpers.getRandomByteArray(100);
        byte[] preview = Helpers.getRandomByteArray(50);

        ImageData img = new ImageData();
        img.setOriginal(original);
        img.setPreview(preview);
        img.setUploadDate(LocalDateTime.now());

        imageRepository.save(img);

        assertThat(img.getId(), notNullValue());

        byte[] originalLoaded = imageRepository.getOriginal(img.getId());
        assertThat(originalLoaded, notNullValue());
        assertThat(originalLoaded, equalTo(original));

        byte[] previewLoaded = imageRepository.getPreview(img.getId());
        assertThat(previewLoaded, notNullValue());
        assertThat(previewLoaded, equalTo(preview));

    }
}
