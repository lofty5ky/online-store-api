package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}