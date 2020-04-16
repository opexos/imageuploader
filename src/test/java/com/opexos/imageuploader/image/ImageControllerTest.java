package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import com.opexos.imageuploader.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${image.max-file-size}")
    int imageMaxFileSize;


    @Test
    public void image_get_fail_notFound() throws Exception {
        mockMvc.perform(get("/image/0"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{errorMessage:\"Image is not found. Id: 0\"}"));

    }

    @Test
    public void image_post_ok_base64Json() throws Exception {
        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getResourceBytes("base64_two_images.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(2)));
    }

    @Test
    public void image_post_ok_urlJson() throws Exception {
        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getResourceBytes("urls_two_images.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(2)));
    }

    @Test
    public void image_post_ok_multipart() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", Utils.getResourceBytes("img1.jpg"));
        MockMultipartFile img2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", Utils.getResourceBytes("img2.jpg"));

        mockMvc.perform(multipart("/image")
                .file(img1)
                .file(img2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(2)));
    }

    @Test
    public void image_post_fail_invalidJson() throws Exception {
        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("incorrect json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", not(blankOrNullString())));
    }

    @Test
    public void image_post_fail_multiPart_invalidImage() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", Utils.getResourceBytes("img1.jpg"));
        MockMultipartFile img2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", "some data".getBytes());

        mockMvc.perform(multipart("/image")
                .file(img1)
                .file(img2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("invalid image format")));
    }

    @Test
    public void image_post_fail_invalidBase64() throws Exception {
        String json = "{ \"images\": [\"/9j/4AAQSkZJR\"] }"; //incorrect base64

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("cannot decode base64")));
    }

    @Test
    public void image_post_fail_invalidImageInBase64() throws Exception {
        String json = "{ \"images\": [\"1234\"] }"; // base64 is correct. image is not correct

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("invalid image format")));
    }

    @Test
    public void image_post_fail_urlTo404() throws Exception {
        //I hope that nobody uploads image at this url ))
        String json = "{ \"urls\": [\"https://images.freeimages.com/images/large-previews/a0d/adopsfoqqqpkopasdj.jpg\"]}";

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("cannot read data from")));
    }

    @Test
    public void image_post_fail_multipart_tooBigImage() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", new byte[imageMaxFileSize + 1]);

        mockMvc.perform(multipart("/image")
                .file(img1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("size exceeded")));
    }

    @Test
    public void image_post_fail_base64_tooBigImage() throws Exception {
        String json = "{ \"images\": [\"" + Helpers.getRandomBase64(imageMaxFileSize + 1) + "\"] }";

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("size exceeded")));
    }

    @Test
    public void image_post_fail_url_tooBigImage() throws Exception {
        String json = "{ \"urls\": [\"https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg\"] }";

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("size exceeded")));
    }

    @Test
    public void image_post_fail_url_invalid() throws Exception {
        String json = "{ \"urls\": [\"file:///etc/passwd\"] }";

        mockMvc.perform(post("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", containsStringIgnoringCase("only http and https protocols allowed")));
    }

}