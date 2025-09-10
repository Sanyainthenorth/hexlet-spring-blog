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

import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = Instancio.of(User.class)
                            .ignore(Select.field(User::getId))
                            .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                            .create();
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreateUser() throws Exception {
        var userData = new HashMap<>();
        userData.put("firstName", "John");
        userData.put("lastName", "Doe");
        userData.put("email", faker.internet().emailAddress());

        var request = post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(userData));

        var result = mockMvc.perform(request)
                            .andExpect(status().isCreated())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject()
                            .containsKey("id")
                            .containsEntry("firstName", "John")
                            .containsEntry("lastName", "Doe");
    }

    @Test
    public void testGetUser() throws Exception {
        var result = mockMvc.perform(get("/api/users/" + testUser.getId()))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject()
                            .containsEntry("id", testUser.getId())
                            .containsEntry("email", testUser.getEmail());
    }

    @Test
    public void testUpdateUser() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "UpdatedName");
        data.put("lastName", "UpdatedLastName");
        data.put("email", testUser.getEmail());

        var request = put("/api/users/" + testUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request)
               .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser.getFirstName()).isEqualTo("UpdatedName");
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedLastName");
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
               .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    public void testGetUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
               .andExpect(status().isNotFound());
    }
}
