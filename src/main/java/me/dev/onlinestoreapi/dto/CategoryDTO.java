package me.dev.onlinestoreapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {
    @NotEmpty(message = "The category's name is required")
    private String name;
}
