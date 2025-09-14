package io.hexlet.spring.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TagUpdateDTO {
    @Size(min = 2, max = 50)
    private JsonNullable<String> name = JsonNullable.undefined();
}
