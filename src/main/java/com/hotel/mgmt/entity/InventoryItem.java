package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, unique = true, length = 100)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InventoryCategory category;

    @Column(name = "stock_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockQuantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "reorder_level", nullable = false, precision = 10, scale = 2)
    private BigDecimal reorderLevel;

    public InventoryItem() {}

    public InventoryItem(Long id, String itemName, InventoryCategory category, BigDecimal stockQuantity, String unit, BigDecimal reorderLevel) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public InventoryCategory getCategory() { return category; }
    public void setCategory(InventoryCategory category) { this.category = category; }

    public BigDecimal getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(BigDecimal stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }

    public static InventoryItemBuilder builder() {
        return new InventoryItemBuilder();
    }

    public static class InventoryItemBuilder {
        private Long id;
        private String itemName;
        private InventoryCategory category;
        private BigDecimal stockQuantity;
        private String unit;
        private BigDecimal reorderLevel;

        public InventoryItemBuilder id(Long id) { this.id = id; return this; }
        public InventoryItemBuilder itemName(String itemName) { this.itemName = itemName; return this; }
        public InventoryItemBuilder category(InventoryCategory category) { this.category = category; return this; }
        public InventoryItemBuilder stockQuantity(BigDecimal stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public InventoryItemBuilder unit(String unit) { this.unit = unit; return this; }
        public InventoryItemBuilder reorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; return this; }

        public InventoryItem build() {
            return new InventoryItem(id, itemName, category, stockQuantity, unit, reorderLevel);
        }
    }
}
