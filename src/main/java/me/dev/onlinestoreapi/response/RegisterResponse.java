package me.dev.onlinestoreapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.dev.onlinestoreapi.model.User;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse implements Serializable {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
