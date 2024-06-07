package me.dev.onlinestoreapi.service;

import me.dev.onlinestoreapi.dto.OrderDetailDTO;
import me.dev.onlinestoreapi.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO);

    OrderDetail getOrderDetail(Long id);

    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailDTO);

    void deleteOrderDetail(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
