package me.dev.onlinestoreapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dev.onlinestoreapi.dto.CartItemDTO;
import me.dev.onlinestoreapi.dto.OrderDTO;
import me.dev.onlinestoreapi.exception.ResourceNotFoundException;
import me.dev.onlinestoreapi.model.*;
import me.dev.onlinestoreapi.repository.OrderDetailRepository;
import me.dev.onlinestoreapi.repository.OrderRepository;
import me.dev.onlinestoreapi.repository.ProductRepository;
import me.dev.onlinestoreapi.repository.UserRepository;
import me.dev.onlinestoreapi.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id = " + orderDTO.getUserId());
                    return new ResourceNotFoundException("User not found with id = " + orderDTO.getUserId());
                });

        // Convert OrderDTO => Order using Model Mapper library
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        // Update order fields from orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);

        // Check shippingDate must be greater than or equal today's date
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException("Date must be at least today!");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);

        // Create a list of OrderDetail objects from cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());

            orderDetails.add(orderDetail);
        }

        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find order with id = " + id));
        return order;
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find order with id = " + id));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id = " + orderDTO.getUserId() + " not found"));

        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);
        return orderRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        // soft-delete
        if (order != null) {
            log.info("Soft-delete order with id={}", id);
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
