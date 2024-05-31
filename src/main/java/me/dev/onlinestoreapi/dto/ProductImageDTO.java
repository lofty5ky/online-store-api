package me.dev.onlinestoreapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO implements Serializable {
    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's id must be > 0")
    private Long productId;

    @JsonProperty("image_url")
    private String imageUrl;
}
