package io.hexlet.spring.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class PostPatchDTO {
    @Size(min = 3, max = 100)
    private JsonNullable<String> title = JsonNullable.undefined();

    @Size(min = 10)
    private JsonNullable<String> content = JsonNullable.undefined();

    private JsonNullable<Boolean> published = JsonNullable.undefined();
}
