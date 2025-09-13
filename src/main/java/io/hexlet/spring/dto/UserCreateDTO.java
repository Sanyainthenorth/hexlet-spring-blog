package io.hexlet.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    @NotBlank
    @Size
    private String firstName;

    @NotBlank
    @Size
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size
    private String password;
}
