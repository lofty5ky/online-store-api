package me.dev.onlinestoreapi.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListResponse implements Serializable {
    private List<ProductResponse> products;
    private int totalPages;
}
