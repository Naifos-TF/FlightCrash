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
# --- runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Ton build produit un .war repackagé Spring Boot => exécutable via java -jar
COPY --from=be /app/backend/target/*.war /app/app.war

ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/app.war --server.port=${PORT}"]
