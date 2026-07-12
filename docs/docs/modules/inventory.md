---
sidebar_position: 8
title: Inventory
description: Kitchen inventory tracking with stock levels, reorder alerts, and automatic deductions
---

# Inventory

The Inventory module tracks raw ingredients and supplies used by the kitchen, with stock-level monitoring, reorder alerts, and automatic deductions when orders are placed.

---

## Inventory Categories

| Category | Description | Examples |
|----------|------------|---------|
| `PROTEIN` | Meats, fish, and plant-based proteins | Chicken Breast, Salmon Fillet, Tofu |
| `PRODUCE` | Fruits and vegetables | Tomatoes, Lettuce, Onions, Garlic |
| `DAIRY` | Milk products, cheese, and eggs | Butter, Heavy Cream, Parmesan, Eggs |
| `DRY_GOODS` | Grains, pastas, and shelf-stable items | Flour, Rice, Olive Oil, Pasta |
| `BEVERAGE` | Drink stocks and bases | Coffee Beans, Orange Juice, Wine |
| `SPICES` | Seasonings and herbs | Salt, Black Pepper, Paprika, Thyme |

---

## Stock Management

### Automatic Deductions

When a kitchen order is placed, the system:
1. Maps ordered menu items to their ingredient requirements
2. Deducts the required quantities using **atomic JPA `@Modifying` queries**
3. Logs warnings for any missing ingredient mappings

### Reorder Alerts

Items that fall below their configured `reorderLevel` appear in the **Low Stock** alert section on the frontend dashboard and can be queried via the reorder API endpoint.

---

## Frontend UI

The Inventory page provides:
- **Categorized inventory table** with item name, category, quantity, unit, and reorder level
- **Stock adjustment controls** — add or subtract stock quantities
- **Low stock highlighting** — items below reorder level are flagged
- **Add Inventory Item modal** for new ingredients

---

## API Endpoints

See [API Reference → Inventory](/docs/api-reference#inventory) for complete endpoint details.
