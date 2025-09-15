package io.hexlet.spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test content for the post");
        post.setPublished(true);
        post.setUser(user);
        postRepository.save(post);
    }

    @Test
    public void testGetPublishedPosts() throws Exception {
        mockMvc.perform(get("/api/posts")
                            .param("page", "0")
                            .param("size", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray()) // ✅ Теперь просто массив, без content
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    public void testCreatePostSuccess() throws Exception {
        User user = userRepository.findAll().get(0);

        var validPostJson = """
        {
          "title": "New Post",
          "content": "This is a valid post content with more than 10 characters",
          "published": true,
          "userId": %d
        }
        """.formatted(user.getId());

        mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validPostJson))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").exists())
               .andExpect(jsonPath("$.title").value("New Post"))
               .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    public void testCreatePostWithInvalidData() throws Exception {
        User user = userRepository.findAll().get(0);

        var invalidPostJson = """
        {
          "title": "Test",
          "content": "Short"
        }
        """;

        mockMvc.perform(post("/api/posts")
                            .param("userId", user.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidPostJson))
               .andExpect(status().isUnprocessableEntity());

        var emptyTitleJson = """
        {
          "title": "",
          "content": "Valid content with more than 10 characters"
        }
        """;

        mockMvc.perform(post("/api/posts")
                            .param("userId", user.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyTitleJson))
               .andExpect(status().isUnprocessableEntity()); // Меняем на 422
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

    @Test
    public void testUpdatePostSuccess() throws Exception {
        Post existingPost = postRepository.findAll().get(0);
        var updateJson = """
        {
          "title": "Updated Title",
          "content": "Updated content with enough characters"
        }
        """;

        mockMvc.perform(put("/api/posts/" + existingPost.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Updated Title"))
               .andExpect(jsonPath("$.content").value("Updated content with enough characters"));
    }
}