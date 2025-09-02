package io.hexlet.spring.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@Setter
@Getter
public class Post {
    private String slug;

    @NotBlank(message = "Title cannot be empty") // Валидация: не может быть пустым
    private String title;

    @NotBlank(message = "Title cannot be empty") // Валидация: не может быть пустым
    private String content;

    private String author;
    private LocalDateTime createdAt = LocalDateTime.now();
}