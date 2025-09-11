package io.hexlet.spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import io.hexlet.spring.model.User;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
        userRepository.deleteAll();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Сначала создаем пользователя, чтобы было что возвращать
        var userJson = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
               .andExpect(status().isCreated());

        // Теперь проверяем получение всех пользователей
        mockMvc.perform(get("/api/users"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    public void testCreateUser() throws Exception {
        var userJson = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").exists())
               .andExpect(jsonPath("$.firstName").value("John"))
               .andExpect(jsonPath("$.lastName").value("Doe"))
               .andExpect(jsonPath("$.email").value("john@example.com"))
               // ✅ Пароль не должен возвращаться в DTO!
               .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void testGetUserById() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Jane",
              "lastName": "Doe",
              "email": "jane@example.com",
              "password": "password123"
            }
            """;

        var result = mockMvc.perform(post("/api/users")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(userJson))
                            .andExpect(status().isCreated())
                            .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var id = JsonPath.read(responseBody, "$.id").toString();

        // Проверяем, что пользователь доступен по id
        mockMvc.perform(get("/api/users/" + id))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("jane@example.com"))
               .andExpect(jsonPath("$.password").doesNotExist()); // ✅ Пароль скрыт
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Mike",
              "lastName": "Smith",
              "email": "mike@example.com",
              "password": "password123"
            }
            """;

        var result = mockMvc.perform(post("/api/users")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(userJson))
                            .andExpect(status().isCreated())
                            .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var id = JsonPath.read(responseBody, "$.id").toString();

        // Обновляем имя пользователя
        var updateJson = """
            {
              "firstName": "Michael",
              "lastName": "Smith",
              "email": "mike@example.com",
              "password": "newpassword456"
            }
            """;

        mockMvc.perform(put("/api/users/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Michael"))
               .andExpect(jsonPath("$.password").doesNotExist()); // ✅ Пароль скрыт
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Tom",
              "lastName": "Jones",
              "email": "tom@example.com",
              "password": "password123"
            }
            """;

        var result = mockMvc.perform(post("/api/users")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(userJson))
                            .andExpect(status().isCreated())
                            .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var id = JsonPath.read(responseBody, "$.id").toString();

        // Удаляем пользователя
        mockMvc.perform(delete("/api/users/" + id))
               .andExpect(status().isNoContent());

        // Проверяем, что пользователь больше не существует
        mockMvc.perform(get("/api/users/" + id))
               .andExpect(status().isNotFound());
    }

    // ✅ Добавьте тест на валидацию
    @Test
    public void testCreateUserWithInvalidData() throws Exception {
        // Тест на пустое имя
        var invalidUserJson = """
            {
              "firstName": "",
              "lastName": "Doe",
              "email": "invalid@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidUserJson))
               .andExpect(status().isBadRequest());

        // Тест на невалидный email
        var invalidEmailJson = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "invalid-email",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidEmailJson))
               .andExpect(status().isBadRequest());
    }
}
