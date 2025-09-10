package io.hexlet.spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
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

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser() throws Exception {
        var userJson = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").exists())
               .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void testGetUserById() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Jane",
              "lastName": "Doe",
              "email": "jane@example.com"
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
               .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Mike",
              "lastName": "Smith",
              "email": "mike@example.com"
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
              "email": "mike@example.com"
            }
            """;

        mockMvc.perform(put("/api/users/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Michael"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Создаем пользователя
        var userJson = """
            {
              "firstName": "Tom",
              "lastName": "Jones",
              "email": "tom@example.com"
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
}
