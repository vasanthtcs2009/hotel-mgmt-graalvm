# Stage 1: Build the native image
FROM ghcr.io/graalvm/native-image-community:25 AS builder
WORKDIR /app

# Copy the pom.xml and maven wrapper files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer is cached)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the native image
RUN ./mvnw -Pnative native:compile -DskipTests

# Stage 2: Runtime image
FROM debian:bookworm-slim
WORKDIR /app

# Install standard certificates and libraries needed by JVM
RUN apt-get update && apt-get install -y --no-install-recommends \
    libc6 \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copy the compiled native binary from builder
COPY --from=builder /app/target/hotel-mgmt-graalvm /app/hotel-mgmt-graalvm

# Expose port
EXPOSE 8080

# Run the native image
ENTRYPOINT ["/app/hotel-mgmt-graalvm"]
