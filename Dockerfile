FROM eclipse-temurin:11-jdk-alpine AS build

COPY ./ ./

RUN ./gradlew build

FROM eclipse-temurin:11-jre-alpine

COPY --from=build /build/libs/exchange-api.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]