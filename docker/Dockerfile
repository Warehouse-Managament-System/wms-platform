# Shared multi-stage Dockerfile for every Spring Boot service in the platform.
# Build context is the repo root; SERVICE_MODULE selects which gradle module to package.
#
# Example (from repo root):
#   docker build -f docker/Dockerfile --build-arg SERVICE_MODULE=identity-service -t wms/identity .
#
# In docker-compose this is wired automatically — see docker-compose.yml.

# ── Stage 1: build the boot jar with Gradle ──────────────────────────────────
FROM eclipse-temurin:25-jdk AS builder
ARG SERVICE_MODULE
WORKDIR /workspace

# Copy the entire repo so multi-module builds work without enumerating dependencies.
COPY . .

# Make gradlew executable (the COPY may have lost the bit on Windows hosts).
RUN chmod +x gradlew && \
    ./gradlew --no-daemon -x test -x spotlessCheck -x installGitHooks :${SERVICE_MODULE}:bootJar

# ── Stage 2: minimal JRE runtime ─────────────────────────────────────────────
FROM eclipse-temurin:25-jre
ARG SERVICE_MODULE
WORKDIR /app

# Copy the produced jar (glob picks it up regardless of exact version suffix).
COPY --from=builder /workspace/${SERVICE_MODULE}/build/libs/*-SNAPSHOT.jar app.jar

# Run as a non-root user for safety.
RUN useradd --system --uid 1001 wms && chown wms:wms /app/app.jar
USER wms

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
