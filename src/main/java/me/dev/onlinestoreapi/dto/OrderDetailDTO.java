package me.dev.onlinestoreapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO implements Serializable {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's id must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's id must be > 0")
    private Long productId;

    @JsonProperty("price")
    @Min(value = 0, message = "Price must be >= 0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "Price must be > 0")
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private Float totalMoney;

    @JsonProperty("color")
    private String color;
}
