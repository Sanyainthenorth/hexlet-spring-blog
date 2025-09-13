package io.hexlet.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserPatchDTO {
    @Size(min = 2, max = 50)
    private JsonNullable<String> firstName = JsonNullable.undefined();

    @Size(min = 2, max = 50)
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @Email
    private JsonNullable<String> email = JsonNullable.undefined();

}
