---
sidebar_position: 7
title: API Reference
description: Complete REST API endpoint reference for all backend services
---

# API Reference

All endpoints are served from the Spring Boot backend at `http://localhost:8080`. The frontend proxies `/api/*` requests automatically.

---

## Room Management

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/rooms` | List all rooms |
| `GET` | `/api/rooms/{id}` | Get room by ID |
| `GET` | `/api/rooms/number/{roomNumber}` | Get room by room number |
| `GET` | `/api/rooms/{id}/availability?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD` | Check room availability for dates |
| `POST` | `/api/rooms` | Create a new room |
| `PUT` | `/api/rooms/{id}` | Update room details |
| `DELETE` | `/api/rooms/{id}` | Delete a room |

### Create Room — Request Body

```json
{
  "roomNumber": "R501",
  "roomType": "SUITE",
  "pricePerNight": 350.00,
  "bedCount": 2,
  "amenities": "Ocean View, Jacuzzi, Smart TV",
  "status": "AVAILABLE"
}
```

---

## Reservations

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/reservations` | List all reservations |
| `GET` | `/api/reservations/{id}` | Get reservation by ID |
| `POST` | `/api/reservations` | Create a new reservation |
| `PUT` | `/api/reservations/{id}/cancel` | Cancel a reservation |
| `PUT` | `/api/reservations/{id}/complete` | Complete checkout |

### Create Reservation — Request Body

```json
{
  "customerName": "John Smith",
  "roomNumber": "R101",
  "checkInDate": "2026-07-16",
  "checkOutDate": "2026-07-20"
}
```

---

## Restaurant Menu

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/menu` | List all menu items |
| `GET` | `/api/menu/{id}` | Get menu item by ID |
| `GET` | `/api/menu/category/{category}` | Filter by category (`STARTER`, `MAIN_COURSE`, `DESSERT`, `BEVERAGE`) |
| `POST` | `/api/menu` | Create a new menu item |
| `PUT` | `/api/menu/{id}` | Update a menu item |
| `DELETE` | `/api/menu/{id}` | Delete a menu item |

### Create Menu Item — Request Body

```json
{
  "name": "Truffle Risotto",
  "description": "Creamy arborio rice with black truffle shavings",
  "price": 28.50,
  "category": "MAIN_COURSE",
  "available": true
}
```

---

## Orders

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/orders` | List all orders |
| `GET` | `/api/orders/{id}` | Get order by ID |
| `GET` | `/api/orders/room/{roomId}` | Get orders by room |
| `POST` | `/api/orders` | Place a new order |
| `PUT` | `/api/orders/{id}/status?status=STATUS` | Update order status (`PENDING`, `PREPARING`, `DELIVERED`) |

### Place Order — Request Body

```json
{
  "roomNumber": "R101",
  "items": [
    { "menuItemId": 5, "quantity": 2 },
    { "menuItemId": 12, "quantity": 1 }
  ]
}
```

:::info
Placing an order automatically deducts ingredient stock from the inventory system.
:::

---

## Billing

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/billing/reservation/{reservationId}` | Generate unified checkout invoice |

### Invoice Response Structure

The invoice combines room charges (nights × nightly rate) with all restaurant/room service orders billed to the room, including a computed tax amount and grand total.

---

## Inventory

| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/inventory` | List all inventory items |
| `GET` | `/api/inventory/{id}` | Get item by ID |
| `POST` | `/api/inventory` | Create a new inventory item |
| `PUT` | `/api/inventory/{id}/stock?quantityChange=N` | Adjust stock quantity (positive to restock, negative to deduct) |
| `GET` | `/api/inventory/reorder` | List items below minimum stock level |

---

## Data Generator

| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/api/generator/setup` | Seed baseline catalog (rooms, menu, inventory, staff) |
| `POST` | `/api/generator/millions?customersCount=N&reservationsCount=N&ordersCount=N&orderItemsCount=N` | Batch-generate test data at scale |
