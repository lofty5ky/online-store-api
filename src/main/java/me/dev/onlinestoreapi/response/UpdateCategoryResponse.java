package me.dev.onlinestoreapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryResponse implements Serializable {
    @JsonProperty("message")
    private String message;
}
