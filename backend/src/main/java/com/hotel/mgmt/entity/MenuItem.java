package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MenuCategory category;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "prep_time_minutes", nullable = false)
    private Integer prepTimeMinutes;

    public MenuItem() {}

    public MenuItem(Long id, String name, String description, BigDecimal price, MenuCategory category, Boolean available, Integer prepTimeMinutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        if (available != null) {
            this.available = available;
        }
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public MenuCategory getCategory() { return category; }
    public void setCategory(MenuCategory category) { this.category = category; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }

    public static MenuItemBuilder builder() {
        return new MenuItemBuilder();
    }

    public static class MenuItemBuilder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private MenuCategory category;
        private Boolean available;
        private Integer prepTimeMinutes;

        public MenuItemBuilder id(Long id) { this.id = id; return this; }
        public MenuItemBuilder name(String name) { this.name = name; return this; }
        public MenuItemBuilder description(String description) { this.description = description; return this; }
        public MenuItemBuilder price(BigDecimal price) { this.price = price; return this; }
        public MenuItemBuilder category(MenuCategory category) { this.category = category; return this; }
        public MenuItemBuilder available(Boolean available) { this.available = available; return this; }
        public MenuItemBuilder prepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; return this; }

        public MenuItem build() {
            return new MenuItem(id, name, description, price, category, available, prepTimeMinutes);
        }
    }
}
