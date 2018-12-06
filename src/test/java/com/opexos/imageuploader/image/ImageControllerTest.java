package com.opexos.imageuploader.image;

import com.opexos.imageuploader.Helpers;
import com.opexos.imageuploader.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.opexos.imageuploader.matchers.StringContainsIgnoreCase.containsStringIgnoreCase;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${image.max-file-size}")
    private int imageMaxFileSize;

    @Test
    public void shouldNotFoundImageWhenIdEquals0() throws Exception {
        mockMvc.perform(get("/image/0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{message:\"Image is not found. Id: 0\",success:false}"));

    }

    @Test
    public void shouldReturnIdsWhenUploadBase64() throws Exception {
        mockMvc.perform(post("/image")
                .content(Utils.getResourceBytes("base64_two_images.json")))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:true}"))
                .andExpect(jsonPath("$.idList", hasSize(2)));
    }

    @Test
    public void shouldReturnIdsWhenUploadUrls() throws Exception {
        mockMvc.perform(post("/image")
                .content(Utils.getResourceBytes("urls_two_images.json")))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:true}"))
                .andExpect(jsonPath("$.idList", hasSize(2)));
    }

    @Test
    public void shouldReturnIdsWhenUploadMultipart() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", Utils.getResourceBytes("img1.jpg"));
        MockMultipartFile img2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", Utils.getResourceBytes("img2.jpg"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/image")
                .file(img1)
                .file(img2))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:true}"))
                .andExpect(jsonPath("$.idList", hasSize(2)));
    }

    @Test
    public void shouldRaiseErrorWhenUploadIncorrectJson() throws Exception {
        mockMvc.perform(post("/image")
                .content("incorrect json"))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("cannot parse JSON")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadIncorrectImageInMultipart() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", Utils.getResourceBytes("img1.jpg"));
        MockMultipartFile img2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", "some data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/image")
                .file(img1)
                .file(img2))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("invalid image format")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadIncorrectBase64() throws Exception {
        String json = "{ \"images\": [\"/9j/4AAQSkZJR\"] }"; //incorrect base64

        mockMvc.perform(post("/image")
                .content(json.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("cannot decode base64")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadIncorrectImageUsingBase64() throws Exception {
        String json = "{ \"images\": [\"1234\"] }"; // base64 is correct. image is not correct

        mockMvc.perform(post("/image")
                .content(json.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("invalid image format")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadUsingInvalidUrl() throws Exception {
        //I hope that nobody uploads image at this url ))
        String json = "{ \"urls\": [\"https://images.freeimages.com/images/large-previews/a0d/adopsfoqqqpkopasdj.jpg\"]}";

        mockMvc.perform(post("/image")
                .content(json.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("cannot read data from")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadTooBigImageInMultipart() throws Exception {
        MockMultipartFile img1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", new byte[imageMaxFileSize + 1]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/image")
                .file(img1))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("size exceeded")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadTooBigImageUsingBase64() throws Exception {
        String json = "{ \"images\": [\"" + Helpers.getRandomBase64(imageMaxFileSize + 1) + "\"] }";

        mockMvc.perform(post("/image")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("size exceeded")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadTooBigImageUsingUrl() throws Exception {
        String json = "{ \"urls\": [\"https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg\"] }";

        mockMvc.perform(post("/image")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("size exceeded")));
    }

    @Test
    public void shouldRaiseErrorWhenUploadUsingNotHttpUrl() throws Exception {
        String json = "{ \"urls\": [\"file:///etc/passwd\"] }";

        mockMvc.perform(post("/image")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json("{success:false}"))
                .andExpect(jsonPath("$.message", containsStringIgnoreCase("only http and https protocols allowed")));
    }

}