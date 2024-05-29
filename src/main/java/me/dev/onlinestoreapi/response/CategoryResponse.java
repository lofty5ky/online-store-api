package me.dev.onlinestoreapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.dev.onlinestoreapi.model.Category;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("category")
    private Category category;
}
