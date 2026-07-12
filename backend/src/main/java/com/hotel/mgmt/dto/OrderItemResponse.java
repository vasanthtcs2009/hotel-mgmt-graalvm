package com.hotel.mgmt.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long menuItemId,
    String menuItemName,
    Integer quantity,
    BigDecimal unitPrice,
    String comments
) {
    public static OrderItemResponseBuilder builder() {
        return new OrderItemResponseBuilder();
    }

    public static class OrderItemResponseBuilder {
        private Long id;
        private Long menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String comments;

        public OrderItemResponseBuilder id(Long id) { this.id = id; return this; }
        public OrderItemResponseBuilder menuItemId(Long menuItemId) { this.menuItemId = menuItemId; return this; }
        public OrderItemResponseBuilder menuItemName(String menuItemName) { this.menuItemName = menuItemName; return this; }
        public OrderItemResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public OrderItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemResponseBuilder comments(String comments) { this.comments = comments; return this; }

        public OrderItemResponse build() {
            return new OrderItemResponse(id, menuItemId, menuItemName, quantity, unitPrice, comments);
        }
    }
}
