# syntax=docker/dockerfile:1

ARG JAVA_VERSION=21

# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-${JAVA_VERSION} AS build
WORKDIR /app

# Cache de dependencias: solo se invalida si cambia el pom.xml
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime

# Usuario no-root
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=build --chown=app:app /app/target/*.jar app.jar

USER app

# Configurables en build y en runtime
ARG SERVER_PORT=8080
ENV SERVER_PORT=${SERVER_PORT} \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- "http://localhost:${SERVER_PORT}/actuator/health" > /dev/null || exit 1

# exec para que la JVM reciba SIGTERM (apagado limpio)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
