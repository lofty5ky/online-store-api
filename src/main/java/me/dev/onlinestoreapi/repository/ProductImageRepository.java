package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}