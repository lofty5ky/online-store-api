package me.dev.onlinestoreapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO implements Serializable {
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 350, message = "Product name must be between 3 and 350 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 15000000, message = "price must be less than or equal to 15.f000.000")
    private float price;

    private String thumbnail;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;
}