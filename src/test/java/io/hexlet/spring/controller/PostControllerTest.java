package io.hexlet.spring.controller;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import io.hexlet.spring.util.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String authToken;
    private Post testPost;

    @BeforeEach
    void setUp() {
        // Очищаем базы перед каждым тестом
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем тестового пользователя
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordDigest(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);

        // Генерируем токен для аутентификации
        authToken = jwtUtils.generateToken(testUser.getEmail());

        // Создаем тестовый пост
        testPost = new Post();
        testPost.setTitle("Test Post");
        testPost.setContent("Test content for the post with more than 10 characters");
        testPost.setPublished(true);
        testPost.setUser(testUser);
        testPost = postRepository.save(testPost);
    }

    @Test
    void getAllPosts_WithoutAuth_ShouldReturnPosts() throws Exception {
        // GET /api/posts должен быть публичным
        mockMvc.perform(get("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[0].title").value("Test Post"));
    }


    @Test
    void createPost_WithInvalidData_ShouldReturnUnprocessableEntity() throws Exception {
        String invalidPostJson = """
        {
          "title": "Test",
          "content": "Short"
        }
        """;

        mockMvc.perform(post("/api/posts")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidPostJson))
               .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updatePost_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        String updateJson = """
        {
          "title": "Updated Title",
          "content": "Updated content with enough characters for validation"
        }
        """;

        mockMvc.perform(put("/api/posts/" + testPost.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePost_WithValidAuth_ShouldUpdatePost() throws Exception {
        String updateJson = """
        {
          "title": "Updated Title",
          "content": "Updated content with enough characters for validation"
        }
        """;

        mockMvc.perform(put("/api/posts/" + testPost.getId())
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updatePost_WithDifferentUser_ShouldBeDenied() throws Exception {
        // Создаем второго пользователя
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordDigest(passwordEncoder.encode("password123"));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser = userRepository.save(anotherUser);

        String anotherToken = jwtUtils.generateToken(anotherUser.getEmail());

        String updateJson = """
        {
          "title": "Hacked Title",
          "content": "Trying to update someone else's post"
        }
        """;

        MvcResult result = mockMvc.perform(put("/api/posts/" + testPost.getId())
                                               .header("Authorization", "Bearer " + anotherToken)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(updateJson))
                                  .andReturn();

        // Проверяем, что доступ запрещен (401, 403 или 422)
        assertTrue(result.getResponse().getStatus() == 401 ||
                       result.getResponse().getStatus() == 403 ||
                       result.getResponse().getStatus() == 422);
    }

    @Test
    void deletePost_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/posts/" + testPost.getId())
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePost_WithValidAuth_ShouldDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/" + testPost.getId())
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_WithDifferentUser_ShouldReturnUnauthorized() throws Exception {
        // Создаем второго пользователя
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordDigest(passwordEncoder.encode("password123"));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser = userRepository.save(anotherUser);

        String anotherToken = jwtUtils.generateToken(anotherUser.getEmail());

        // Пытаемся удалить чужой пост
        mockMvc.perform(delete("/api/posts/" + testPost.getId())
                            .header("Authorization", "Bearer " + anotherToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePost_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/posts/" + testPost.getId())
                            .header("Authorization", "Bearer invalid_token")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }


    @Test
    void updateNonExistentPost_ShouldReturnNotFound() throws Exception {
        String updateJson = """
        {
          "title": "Non-existent",
          "content": "This post doesn't exist but has enough characters"
        }
        """;

        mockMvc.perform(put("/api/posts/9999")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentPost_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/posts/9999")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    void getPostsWithQueryParams_ShouldWork() throws Exception {
        mockMvc.perform(get("/api/posts")
                            .param("published", "true")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    // Дополнительный тест для отладки, если нужно
    @Test
    void debugCreatePost() throws Exception {
        String validPostJson = """
        {
          "title": "New Post",
          "content": "This is a valid post content with more than 10 characters",
          "published": true
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/posts")
                                               .header("Authorization", "Bearer " + authToken)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(validPostJson))
                                  .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}