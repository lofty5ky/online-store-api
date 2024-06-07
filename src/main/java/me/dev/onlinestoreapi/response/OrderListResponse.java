package me.dev.onlinestoreapi.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponse {
    private List<OrderResponse> orders;
    private int totalPages;
}
