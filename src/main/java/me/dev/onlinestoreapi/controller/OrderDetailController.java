package me.dev.onlinestoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.dto.OrderDetailDTO;
import me.dev.onlinestoreapi.model.OrderDetail;
import me.dev.onlinestoreapi.response.OrderDetailResponse;
import me.dev.onlinestoreapi.service.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @PostMapping()
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable("id") Long id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    // Retrieve a list of order details of an order by order id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetails(@PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();

        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable("id") Long id,
                                               @Valid @RequestBody OrderDetailDTO newOrderDetailData) {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, newOrderDetailData);
        return ResponseEntity.ok(orderDetail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable("id") Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok("Deleted order detail with id = " + id + " successfully");
    }
}
