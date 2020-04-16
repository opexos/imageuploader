package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void save_ok() {
        Image img = Image.builder()
                .original(Helpers.getRandomByteArray(1))
                .preview(Helpers.getRandomByteArray(1))
                .uploadDate(OffsetDateTime.now())
                .build();
        imageRepository.save(img);
        assertThat(img.getId(), greaterThan(0L));
    }

    @Test
    public void getOriginalById_ok() {
        byte[] original = Helpers.getRandomByteArray(100);
        Image img = Image.builder()
                .original(original)
                .preview(Helpers.getRandomByteArray(1))
                .uploadDate(OffsetDateTime.now())
                .build();
        imageRepository.save(img);
        Long newId = img.getId();
        byte[] loadedData = imageRepository.getOriginalById(newId);
        assertThat(loadedData, equalTo(original));
    }

    @Test
    public void getPreviewById_ok() {
        byte[] preview = Helpers.getRandomByteArray(100);
        Image img = Image.builder()
                .original(Helpers.getRandomByteArray(1))
                .preview(preview)
                .uploadDate(OffsetDateTime.now())
                .build();
        imageRepository.save(img);
        Long newId = img.getId();
        byte[] loadedData = imageRepository.getPreviewById(newId);
        assertThat(loadedData, equalTo(preview));
    }
}
