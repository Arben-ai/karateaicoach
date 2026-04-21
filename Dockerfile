FROM eclipse-temurin:25-jdk AS backend-build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -B package -DskipTests

FROM node:22-slim AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend ./
RUN npm run build

FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y supervisor nodejs npm && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=backend-build /app/target/karateaicoach-0.0.1-SNAPSHOT.jar backend.jar
COPY --from=frontend-build /app/build frontend/build
COPY --from=frontend-build /app/package*.json frontend/

WORKDIR /app/frontend
RUN npm ci --omit=dev

WORKDIR /app

COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 8080 3000

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
