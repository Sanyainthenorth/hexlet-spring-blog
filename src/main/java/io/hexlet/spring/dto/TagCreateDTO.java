package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagCreateDTO {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
}
