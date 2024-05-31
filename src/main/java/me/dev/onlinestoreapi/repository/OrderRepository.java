package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}