package io.hexlet.spring.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    private Post testPost;
    private User testUser;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = Instancio.of(User.class)
                            .ignore(Select.field(User::getId))
                            .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                            .create();
        testUser = userRepository.save(testUser);

        testPost = Instancio.of(Post.class)
                            .ignore(Select.field(Post::getId))
                            .supply(Select.field(Post::getTitle), () -> faker.lorem().sentence())
                            .supply(Select.field(Post::getContent), () -> faker.lorem().paragraph())
                            .create();
        testPost.setUser(testUser);
        testPost = postRepository.save(testPost);
    }

    @Test
    public void testGetPublishedPosts() throws Exception {
        testPost.setPublished(true);
        postRepository.save(testPost);

        var result = mockMvc.perform(get("/api/posts")
                                         .param("page", "0")
                                         .param("size", "10"))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject().containsKey("content");
    }

    @Test
    public void testCreatePost() throws Exception {
        var postData = new HashMap<>();
        postData.put("title", "Test Post");
        postData.put("content", "Test content for the post");
        postData.put("published", true);

        var request = post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(postData));

        var result = mockMvc.perform(request)
                            .andExpect(status().isCreated())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject()
                            .containsKey("id")
                            .containsEntry("title", "Test Post");
    }

    @Test
    public void testGetPost() throws Exception {
        var result = mockMvc.perform(get("/api/posts/" + testPost.getId()))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject()
                            .containsEntry("id", testPost.getId())
                            .containsEntry("title", testPost.getTitle());
    }

    @Test
    public void testUpdatePost() throws Exception {
        var data = new HashMap<>();
        data.put("title", "Updated Title");
        data.put("content", "Updated content for the post");

        var request = put("/api/posts/" + testPost.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request)
               .andExpect(status().isOk());

        var updatedPost = postRepository.findById(testPost.getId()).get();
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent()).isEqualTo("Updated content for the post");
    }

    @Test
    public void testDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/" + testPost.getId()))
               .andExpect(status().isNoContent());

        assertThat(postRepository.existsById(testPost.getId())).isFalse();
    }

    @Test
    public void testGetPublishedPostsWithSorting() throws Exception {
        testPost.setPublished(true);
        postRepository.save(testPost);

        var result = mockMvc.perform(get("/api/posts/published")
                                         .param("page", "0")
                                         .param("size", "10")
                                         .param("sort", "createdAt,desc"))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject().containsKey("content");
    }

    @Test
    public void testGetPostNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/9999"))
               .andExpect(status().isNotFound());
    }
}