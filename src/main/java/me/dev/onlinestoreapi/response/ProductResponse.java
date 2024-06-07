package me.dev.onlinestoreapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.dev.onlinestoreapi.model.Product;
import me.dev.onlinestoreapi.model.ProductImage;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private float price;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("description")
    private String description;

    @JsonProperty("product_images")
    private List<ProductImage> productImages;

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse from(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .productImages(product.getProductImages())
                .categoryId(product.getCategory().getId())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());

        return productResponse;
    }
}
