FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
# Las pruebas se ejecutan en el job CI; la imagen de producción solo empaqueta la aplicación.
RUN mvn -B -q -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget \
    && apk upgrade --no-cache \
    && addgroup -S spring \
    && adduser -S -G spring -h /app spring
COPY --from=build --chown=spring:spring /app/target/pagos-svc-1.0.0.jar /app/app.jar
USER spring:spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -q -O /dev/null http://127.0.0.1:8080/ || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
