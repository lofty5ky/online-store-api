package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}