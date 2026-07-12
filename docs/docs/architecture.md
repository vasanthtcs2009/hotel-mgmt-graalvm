---
sidebar_position: 2
title: Architecture
description: System architecture, domain model, caching strategy, and technology stack
---

# Architecture

Aetheria Resort is designed as a monorepo containing three independent tiers: a **Spring Boot API backend**, a **React SPA frontend**, and this **Docusaurus documentation site**.

---

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 25 |
| **Backend Framework** | Spring Boot | 4.1.0 |
| **Frontend Framework** | React + Vite | 19 / 8 |
| **Database** | PostgreSQL | 16 |
| **Cache** | Redis | 7 |
| **Serialization** | Jackson | 3 (`tools.jackson`) |
| **Migrations** | Flyway | Latest |
| **Testing** | JUnit 5, Testcontainers | Latest |
| **Native Compilation** | GraalVM | 25 |
| **Documentation** | Docusaurus | 3.x |

---

## High-Level Architecture

```mermaid
graph TB
    subgraph Client
        UI["React + Vite SPA<br/>Port 3001"]
    end
    subgraph Backend
        API["Spring Boot 4.1 API<br/>Port 8080"]
        Cache["Redis Cache<br/>Port 6379"]
        DB["PostgreSQL<br/>Port 5432"]
    end

    UI -->|"/api/*" proxy| API
    API -->|Read/Write| DB
    API -->|Cache queries| Cache
```

---

## Domain Model

The system models **8 core entities** across hotel and restaurant operations:

```mermaid
erDiagram
    CUSTOMER ||--o{ RESERVATION : books
    ROOM ||--o{ RESERVATION : hosts
    ROOM ||--o{ ORDER : "room service"
    ORDER ||--|{ ORDER_ITEM : contains
    MENU_ITEM ||--o{ ORDER_ITEM : "ordered as"
    INVENTORY_ITEM }o--o{ MENU_ITEM : "ingredient for"

    CUSTOMER {
        Long id PK
        String firstName
        String lastName
        String email
        String phone
        String passportNumber
    }
    ROOM {
        Long id PK
        String roomNumber
        RoomType roomType
        BigDecimal pricePerNight
        RoomStatus status
        Integer bedCount
        String amenities
    }
    RESERVATION {
        Long id PK
        Long roomId FK
        Long customerId FK
        LocalDate checkInDate
        LocalDate checkOutDate
        BigDecimal totalAmount
        ReservationStatus status
    }
    MENU_ITEM {
        Long id PK
        String name
        String description
        BigDecimal price
        MenuCategory category
        Boolean available
    }
    ORDER {
        Long id PK
        Long roomId FK
        OrderStatus status
        BigDecimal totalAmount
        LocalDateTime createdAt
    }
    ORDER_ITEM {
        Long id PK
        Long orderId FK
        Long menuItemId FK
        Integer quantity
        BigDecimal unitPrice
    }
    INVENTORY_ITEM {
        Long id PK
        String itemName
        BigDecimal quantity
        String unit
        BigDecimal minimumStockLevel
    }
    STAFF {
        Long id PK
        String firstName
        String lastName
        StaffRole role
        BigDecimal salary
    }
```

---

## Caching Strategy

Redis caching is implemented with **domain-specific TTL policies** to balance freshness against load:

| Cache Region | TTL | Rationale |
|-------------|-----|-----------|
| `menuItems` | **1 Hour** | Static catalog data, rarely changes |
| `rooms` | **30 Minutes** | Semi-static; room attributes change infrequently |
| `roomAvailability` | **5 Minutes** | Highly volatile; check-ins/check-outs happen frequently |

The serializer uses Jackson 3's `GenericJacksonJsonRedisSerializer` with `BasicPolymorphicTypeValidator` for secure polymorphic deserialization — a requirement after the deprecation of `GenericJackson2JsonRedisSerializer` in Spring Data Redis 4.0.

---

## Design Principles

- **Pure Java (Lombok-Free):** Avoids annotation processors like Lombok to prevent compiler conflicts on modern JDK runtimes.
- **Atomic Stock Deductions:** Kitchen orders automatically verify and deduct inventory using JPA `@Modifying` queries.
- **Unified Billing:** Checkout invoices combine room stay charges with all restaurant charges billed to the room.
- **Batch Data Generation:** High-performance `JdbcTemplate` batch inserts with Postgres-specific `reWriteBatchedInserts=true` for seeding millions of test records.
