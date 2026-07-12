---
sidebar_position: 1
title: Getting Started
description: Prerequisites, setup instructions, and quick start guide for Aetheria Resort
---

# Getting Started

Welcome to the **Aetheria Resort** Hotel & Restaurant Management System — a full-stack, production-grade application built with **Spring Boot 4.1**, **React + Vite**, **PostgreSQL**, **Redis**, and optimized for **GraalVM Native Image** compilation on **Java 25**.

---

## Prerequisites

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| **Java JDK** | 25+ | Backend compilation |
| **Node.js** | 20+ | Frontend tooling |
| **Docker Desktop** | 29.5+ | Container orchestration |
| **Maven** | 3.9+ | Build automation (or use bundled `./mvnw`) |

---

## Project Structure

```
hotel-mgmt-graalvm/
├── backend/          # Spring Boot 4.1 API service
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── mvnw
├── frontend/         # React + Vite SPA dashboard
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
├── docs/             # Docusaurus documentation (this site)
├── docker-compose.yml
└── README.md
```

---

## Quick Start (Docker Compose)

The fastest way to run the complete stack (Postgres, Redis, Backend, Frontend) is via Docker Compose:

```bash
docker compose up --build
```

This will start:
- **PostgreSQL** on port `5432`
- **Redis** on port `6379`
- **Backend API** on port `8080`
- **Frontend UI** on port `3001`

Once running, open [http://localhost:3001](http://localhost:3001) to access the Aetheria Resort dashboard.

---

## Manual Development Setup

### Backend

```bash
cd backend
./mvnw clean compile
./mvnw spring-boot:run
```

The API server starts on `http://localhost:8080`.

:::tip
Make sure PostgreSQL and Redis are running locally (or via `docker compose up postgres redis`) before starting the backend manually.
:::

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server starts on `http://localhost:3001` and automatically proxies `/api/*` requests to the backend at port `8080`.

---

## Seed Test Data

After the backend is running, load the baseline catalog (rooms, menu items, inventory, staff) by calling the setup endpoint:

```bash
curl -X POST http://localhost:8080/api/generator/setup
```

You can then open the frontend Dashboard and begin creating bookings, placing kitchen orders, and managing inventory.
