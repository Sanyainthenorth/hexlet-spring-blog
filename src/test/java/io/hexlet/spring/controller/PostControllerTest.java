package io.hexlet.spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testCreatePostWithInvalidData() throws Exception {
        // Тест на слишком короткий контент
        var invalidPostJson = """
            {
              "title": "Test",
              "content": "Short",
              "published": true
            }
            """;

        mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidPostJson))
               .andExpect(status().isBadRequest());

        // Тест на пустой заголовок
        var emptyTitleJson = """
            {
              "title": "",
              "content": "Valid content with more than 10 characters",
              "published": true
            }
            """;

        mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyTitleJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetNonExistentPost() throws Exception {
        mockMvc.perform(get("/api/posts/9999"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNonExistentPost() throws Exception {
        var updateJson = """
            {
              "title": "Non-existent",
              "content": "This post doesn't exist but has enough characters"
            }
            """;

        mockMvc.perform(put("/api/posts/9999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistentPost() throws Exception {
        mockMvc.perform(delete("/api/posts/9999"))
               .andExpect(status().isNotFound());
    }
}