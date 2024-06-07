package me.dev.onlinestoreapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dev.onlinestoreapi.dto.OrderDetailDTO;
import me.dev.onlinestoreapi.exception.ResourceNotFoundException;
import me.dev.onlinestoreapi.model.Order;
import me.dev.onlinestoreapi.model.OrderDetail;
import me.dev.onlinestoreapi.model.Product;
import me.dev.onlinestoreapi.repository.OrderDetailRepository;
import me.dev.onlinestoreapi.repository.OrderRepository;
import me.dev.onlinestoreapi.repository.ProductRepository;
import me.dev.onlinestoreapi.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) {
        // Check if orderId and productId exists or not
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't find order with id = " + orderDetailDTO.getOrderId()));

        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't find product with id = " + orderDetailDTO.getProductId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .price(orderDetailDTO.getPrice())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't found order detail with id = " + id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailDTO) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find order detail with id = " + id));
        Order existingOrder = orderRepository.findById(newOrderDetailDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't find order with id = " + id));
        Product existingProduct = productRepository.findById(newOrderDetailDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't find product with id = " + id));

        existingOrderDetail.setPrice(newOrderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(newOrderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(newOrderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(newOrderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        log.info("Order detail deleted with id = {}", id);
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
