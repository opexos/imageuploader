package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
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
        LocalDateTime date = LocalDateTime.now();

        ImageData img = new ImageData();
        img.setOriginal(original);
        img.setPreview(preview);
        img.setUploadDate(date);

        imageRepository.save(img);

        assertThat(img.getId(), notNullValue());

        Optional<ImageData> loaded = imageRepository.findById(img.getId());
        assertThat(loaded.isPresent(), equalTo(true));

        ImageData loadedImg = loaded.get();

        assertThat(loadedImg.getOriginal(), equalTo(original));
        assertThat(loadedImg.getPreview(), equalTo(preview));
        assertThat(loadedImg.getUploadDate(), equalTo(date));

    }
}
