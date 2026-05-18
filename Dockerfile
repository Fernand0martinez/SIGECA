# ---------- Build Stage ----------
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml y descargar dependencias (capa cacheada)
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
