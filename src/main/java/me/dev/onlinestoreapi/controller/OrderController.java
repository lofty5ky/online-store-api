package me.dev.onlinestoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.dto.OrderDTO;
import me.dev.onlinestoreapi.model.Order;
import me.dev.onlinestoreapi.response.OrderListResponse;
import me.dev.onlinestoreapi.response.OrderResponse;
import me.dev.onlinestoreapi.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get orders by user_id
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("userId") Long userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Long orderId) {
        try {
            Order order = orderService.getOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromOrder(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long orderId,
                                         @Valid @RequestBody OrderDTO orderDTO) {
        try {
            Order updatedOrder = orderService.updateOrder(orderId, orderDTO);
            return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderListResponse> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword, pageable)
                .map(OrderResponse::fromOrder);

        List<OrderResponse> orderResponses = orderPage.getContent();
        int totalPages = orderPage.getTotalPages();

        return ResponseEntity.ok(OrderListResponse.builder()
                .orders(orderResponses)
                .totalPages(totalPages)
                .build());
    }
}
