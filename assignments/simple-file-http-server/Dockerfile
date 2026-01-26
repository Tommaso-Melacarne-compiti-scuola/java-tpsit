FROM maven:3.9-eclipse-temurin-20 AS build
WORKDIR /workspace

# Copy pom first to leverage dependency caching
COPY pom.xml .

# Copy the source tree and build the application
COPY src src
RUN mvn -B package

FROM eclipse-temurin:20-jre-jammy AS runtime
WORKDIR /app

# Copy static assets (if any) and the packaged jar
COPY htdocs ./htdocs
COPY --from=build /workspace/target/simple-file-http-server-1.0-SNAPSHOT.jar ./app.jar

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "./app.jar"]
