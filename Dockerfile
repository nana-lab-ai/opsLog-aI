# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw -DskipTests package -B

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/opslogai-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
