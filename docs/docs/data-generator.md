---
sidebar_position: 10
title: Data Generator
description: High-performance batch data seeder for testing and performance benchmarking
---

# Data Generator

The Data Generator module provides a high-performance batch data injection system capable of seeding **millions of records** into the database for load testing, performance benchmarking, and development workflows.

---

## Overview

The generator uses **optimized `JdbcTemplate` batch inserts** with Postgres-specific `reWriteBatchedInserts=true` query rewrites for maximum throughput, designed to sustain **100+ Transactions Per Second (TPS)**.

---

## Two-Phase Seeding

### Phase 1: Base Catalog Setup

```bash
curl -X POST http://localhost:8080/api/generator/setup
```

This resets all tables and loads a baseline catalog:

| Entity | Count | Description |
|--------|-------|-------------|
| Rooms | 120 | Mix of STANDARD, DELUXE, and SUITE |
| Menu Items | 150 | Across all menu categories |
| Inventory Items | 100 | Kitchen ingredients and supplies |
| Staff | 50 | Various roles (CHEF, WAITER, MANAGER, etc.) |

### Phase 2: Millions of Records

```bash
curl -X POST "http://localhost:8080/api/generator/millions?\
  customersCount=50000&\
  reservationsCount=100000&\
  ordersCount=200000&\
  orderItemsCount=800000"
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `customersCount` | 50,000 | Number of customer records |
| `reservationsCount` | 100,000 | Number of reservation records |
| `ordersCount` | 200,000 | Number of order records |
| `orderItemsCount` | 800,000 | Number of order item records |

---

## Performance Characteristics

The batch generator achieves high throughput through:

1. **JDBC Batch Rewrites:** Postgres's `reWriteBatchedInserts=true` combines multiple `INSERT` statements into multi-row inserts
2. **Chunked Processing:** Records are inserted in configurable batch sizes (default: 5,000 per batch)
3. **Minimal Object Allocation:** Uses raw JDBC parameter arrays instead of JPA entity hydration
4. **Sequential Foreign Keys:** Uses pre-calculated ID ranges for foreign key relationships

:::tip Performance Tip
For maximum throughput, ensure PostgreSQL's `max_wal_size` and `checkpoint_completion_target` are tuned for bulk writes. The default Docker Compose Postgres config handles standard loads well.
:::

---

## Frontend UI

The Data Generator page in the dashboard provides:
- **Setup button** to trigger Phase 1 (base catalog)
- **Custom count inputs** for Phase 2 parameters
- **Generate button** with progress feedback
