---
sidebar_position: 9
title: Deployment
description: Docker Compose setup, GraalVM native image compilation, and GitHub Pages deployment
---

# Deployment

Aetheria Resort supports multiple deployment strategies — from local Docker Compose to GraalVM native images for ultra-fast container startups.

---

## Docker Compose (Full Stack)

The `docker-compose.yml` in the project root orchestrates all services:

```yaml
services:
  postgres:     # PostgreSQL 16 database
  redis:        # Redis 7 cache
  hotel-mgmt-service:  # Spring Boot backend (port 8080)
  hotel-mgmt-ui:       # React frontend (port 3001)
```

### Start Everything

```bash
docker compose up --build
```

### Service URLs

| Service | URL |
|---------|-----|
| Frontend UI | [http://localhost:3001](http://localhost:3001) |
| Backend API | [http://localhost:8080](http://localhost:8080) |
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |

---

## GraalVM Native Image

The backend Dockerfile uses a **multi-stage build** to compile the Spring Boot application into a GraalVM native binary:

1. **Build Stage:** Uses `ghcr.io/graalvm/native-image-community:25` to compile the app
2. **Runtime Stage:** Runs on `debian:bookworm-slim` — no JVM required

### Benefits

- **Startup Time:** ~50ms vs ~3s for a JVM-based jar
- **Memory Footprint:** ~80MB RSS vs ~400MB for a traditional Spring Boot app
- **Container Size:** ~120MB vs ~500MB+ with a full JDK

:::caution Build Time
Native image compilation takes **5–10 minutes** on a modern machine. Ensure you have at least 8GB of RAM available for the Docker build.
:::

---

## GitHub Pages (Documentation)

This documentation site is deployed via GitHub Pages using Docusaurus.

### Manual Deployment

```bash
cd docs
npm install
npm run build
```

The built static files are generated in `docs/build/` and can be deployed to GitHub Pages.

### GitHub Actions (CI/CD)

You can automate deployment using GitHub Actions. Create `.github/workflows/deploy-docs.yml`:

```yaml
name: Deploy Documentation

on:
  push:
    branches: [main]
    paths: ['docs/**']

permissions:
  contents: read
  pages: write
  id-token: write

jobs:
  deploy:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: docs
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: npm
          cache-dependency-path: docs/package-lock.json
      - run: npm ci
      - run: npm run build
      - uses: actions/upload-pages-artifact@v3
        with:
          path: docs/build
      - uses: actions/deploy-pages@v4
```

### Configuration

The `docusaurus.config.js` is preconfigured for GitHub Pages:

```js
url: 'https://vasanthtcs2009.github.io',
baseUrl: '/hotel-mgmt-graalvm/',
organizationName: 'vasanthtcs2009',
projectName: 'hotel-mgmt-graalvm',
deploymentBranch: 'gh-pages',
```

---

## Environment Variables

| Variable | Service | Default | Description |
|----------|---------|---------|-------------|
| `SPRING_DATASOURCE_URL` | Backend | `jdbc:postgresql://postgres:5432/hoteldb` | Database connection string |
| `SPRING_REDIS_HOST` | Backend | `redis` | Redis hostname |
| `BACKEND_URL` | Frontend | `http://hotel-mgmt-service:8080` | API proxy target |
