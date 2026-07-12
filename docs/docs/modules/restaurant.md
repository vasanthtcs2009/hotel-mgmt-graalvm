---
sidebar_position: 5
title: Restaurant & Menu
description: Food & beverage menu management with categorized items
---

# Restaurant & Menu

The Restaurant module manages the food and beverage menu catalog, organized by categories with pricing and availability tracking.

---

## Menu Categories

| Category | Description | Examples |
|----------|------------|---------|
| `STARTER` | Appetizers and small plates | Bruschetta, Caesar Salad, Soup |
| `MAIN_COURSE` | Primary entrées | Ribeye Steak, Grilled Salmon, Truffle Risotto |
| `DESSERT` | Sweet courses | Tiramisu, Crème Brûlée, Chocolate Lava Cake |
| `BEVERAGE` | Drinks (alcoholic and non-alcoholic) | Espresso, Fresh Juice, Cocktails |

---

## Menu Item Properties

Each menu item tracks:
- **Name** and **Description** — displayed in the frontend menu grid
- **Price** — used for order calculations and billing
- **Category** — determines tab placement in the UI
- **Availability** — toggle to mark items in/out of stock

Items are cached in Redis under the `menuItems` cache region with a **1-hour TTL** since menu data changes infrequently.

---

## Frontend UI

The Restaurant Menu page in the dashboard features:
- **Category filter tabs** — `ALL`, `STARTER`, `MAIN COURSE`, `DESSERT`, `BEVERAGE`
- **Glassmorphic card grid** showing each item with category badge, stock indicator, description, and price
- **Add Menu Item modal** for creating new dishes with all fields

---

## API Endpoints

See [API Reference → Restaurant Menu](/docs/api-reference#restaurant-menu) for complete endpoint details.
