FROM ubuntu:latest
LABEL authors="Sofian"

ENTRYPOINT ["top", "-b"]
# ---------- build frontend ----------
FROM node:20-alpine AS fe
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# ---------- build backend ----------
FROM maven:3.9-eclipse-temurin-21 AS be
WORKDIR /app

COPY backend/pom.xml backend/pom.xml
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/mvnw
COPY backend/mvnw.cmd backend/mvnw.cmd
RUN chmod +x backend/mvnw

COPY backend/src backend/src

# ✅ injecte le build Vue dans Spring static
RUN rm -rf backend/src/main/resources/static && mkdir -p backend/src/main/resources/static
COPY --from=fe /app/frontend/dist/ backend/src/main/resources/static/

WORKDIR /app/backend
RUN mvn -DskipTests package

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=be /app/backend/target/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "]()
