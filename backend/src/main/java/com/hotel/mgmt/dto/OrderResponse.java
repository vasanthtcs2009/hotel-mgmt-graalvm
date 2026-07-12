package com.hotel.mgmt.dto;

import com.hotel.mgmt.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String roomNumber,
    Integer tableNumber,
    OrderStatus status,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {
    public static OrderResponseBuilder builder() {
        return new OrderResponseBuilder();
    }

    public static class OrderResponseBuilder {
        private Long id;
        private String roomNumber;
        private Integer tableNumber;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> items;

        public OrderResponseBuilder id(Long id) { this.id = id; return this; }
        public OrderResponseBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public OrderResponseBuilder tableNumber(Integer tableNumber) { this.tableNumber = tableNumber; return this; }
        public OrderResponseBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public OrderResponseBuilder items(List<OrderItemResponse> items) { this.items = items; return this; }

        public OrderResponse build() {
            return new OrderResponse(id, roomNumber, tableNumber, status, totalAmount, createdAt, items);
        }
    }
}
