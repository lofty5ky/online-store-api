package me.dev.onlinestoreapi.service;

import me.dev.onlinestoreapi.dto.ProductDTO;
import me.dev.onlinestoreapi.dto.ProductImageDTO;
import me.dev.onlinestoreapi.model.Product;
import me.dev.onlinestoreapi.model.ProductImage;
import me.dev.onlinestoreapi.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Product addProduct(ProductDTO productDTO);

    Product getProductById(Long productId);

    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(Long productId, ProductDTO productDTO);

    void deleteProduct(Long productId);

    boolean existsByName(String name);

    ProductImage addProductImage(Long productId, ProductImageDTO productImageDTO);

    List<Product> findProductsByIds(List<Long> productIds);
}
