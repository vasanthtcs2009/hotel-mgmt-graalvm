package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(length = 255)
    private String comments;

    public OrderItem() {}

    public OrderItem(Long id, Order order, MenuItem menuItem, Integer quantity, BigDecimal unitPrice, String comments) {
        this.id = id;
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.comments = comments;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", menuItemId=" + (menuItem != null ? menuItem.getId() : null) +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", comments='" + comments + '\'' +
                '}';
    }

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    public static class OrderItemBuilder {
        private Long id;
        private Order order;
        private MenuItem menuItem;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String comments;

        public OrderItemBuilder id(Long id) { this.id = id; return this; }
        public OrderItemBuilder order(Order order) { this.order = order; return this; }
        public OrderItemBuilder menuItem(MenuItem menuItem) { this.menuItem = menuItem; return this; }
        public OrderItemBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public OrderItemBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemBuilder comments(String comments) { this.comments = comments; return this; }

        public OrderItem build() {
            return new OrderItem(id, order, menuItem, quantity, unitPrice, comments);
        }
    }
}
