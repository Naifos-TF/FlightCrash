# syntax=docker/dockerfile:1

############################
# 1) Build frontend (Vite)
############################
FROM node:20-alpine AS fe
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ .
RUN npm run build


############################
# 2) Build backend (Spring Boot)
############################
FROM maven:3.9-eclipse-temurin-21 AS be
WORKDIR /app/backend

# Dépendances d'abord (cache)
COPY backend/pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Sources
COPY backend/ ./

# Injecte le build frontend dans Spring static
RUN rm -rf src/main/resources/static && mkdir -p src/main/resources/static
COPY --from=fe /app/frontend/dist/ src/main/resources/static/

# Build JAR
RUN mvn -DskipTests package


############################
# 3) Runtime (Render)
############################
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copie du jar (si plusieurs, adapte le pattern)
COPY --from=be /app/backend/target/*.jar /app/app.jar

# Render fournit PORT automatiquement
ENV PORT=8080
EXPOSE 8080

# IMPORTANT: force Spring à écouter sur $PORT
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]
