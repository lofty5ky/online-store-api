package me.dev.onlinestoreapi.service;

import me.dev.onlinestoreapi.dto.OrderDTO;
import me.dev.onlinestoreapi.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);

    Order getOrder(Long id);

    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);

    Order updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    List<Order> getOrdersByUserId(Long userId);
}
