# ======================== Stage 1: Build ========================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Копируем Gradle wrapper и зависимости (чтобы кэшировались)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon

# ======================== Stage 2: Run ========================
FROM eclipse-temurin:21.0.2_13-jre-alpine
WORKDIR /app

# Копируем собранный jar из первого stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082

# Запускаем
ENTRYPOINT ["java", "-jar", "app.jar"]
