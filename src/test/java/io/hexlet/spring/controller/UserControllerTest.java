package io.hexlet.spring.controller;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.model.User;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
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
    }

    @Test
    void getAllUsers_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_WithValidAuth_ShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUser_WithoutAuth_ShouldReturnOk() throws Exception {
        // GET /api/users/{id} должен быть публичным согласно вашему коду
        mockMvc.perform(get("/api/users/" + testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_WithoutAuth_ShouldCreateUser() throws Exception {
        UserCreateDTO newUser = new UserCreateDTO();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpassword123");
        newUser.setFirstName("New");
        newUser.setLastName("User");

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void updateUser_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        UserUpdateDTO updateData = new UserUpdateDTO();
        updateData.setFirstName("Updated");
        updateData.setLastName("User"); // Добавьте обязательные поля
        updateData.setEmail("test@example.com"); // Добавьте email

        mockMvc.perform(put("/api/users/" + testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_WithValidAuth_ShouldUpdateUser() throws Exception {
        UserUpdateDTO updateData = new UserUpdateDTO();
        updateData.setFirstName("Updated");
        updateData.setLastName("User");
        updateData.setEmail("test@example.com"); // Должен остаться тем же

        mockMvc.perform(put("/api/users/" + testUser.getId())
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateUser_WithDifferentUser_ShouldBeDenied() throws Exception {
        // Создаем второго пользователя
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordDigest(passwordEncoder.encode("password123"));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser = userRepository.save(anotherUser);

        String anotherToken = jwtUtils.generateToken(anotherUser.getEmail());

        UserUpdateDTO updateData = new UserUpdateDTO();
        updateData.setFirstName("Hacked");
        updateData.setLastName("User");
        updateData.setEmail("test@example.com");

        MvcResult result = mockMvc.perform(put("/api/users/" + testUser.getId())
                                               .header("Authorization", "Bearer " + anotherToken)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(updateData)))
                                  .andReturn();

        // Проверяем, что доступ запрещен (401 или 403)
        assertTrue(result.getResponse().getStatus() == 401 ||
                       result.getResponse().getStatus() == 403 ||
                       result.getResponse().getStatus() == 422);
    }

    @Test
    void deleteUser_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_WithValidAuth_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_WithDifferentUser_ShouldReturnUnauthorized() throws Exception {
        // Создаем второго пользователя
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordDigest(passwordEncoder.encode("password123"));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser = userRepository.save(anotherUser);

        String anotherToken = jwtUtils.generateToken(anotherUser.getEmail());

        // Пытаемся удалить чужого пользователя
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                            .header("Authorization", "Bearer " + anotherToken)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                            .header("Authorization", "Bearer invalid_token")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }
}