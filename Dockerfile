FROM gradle:8.7-jdk21 AS builder
COPY . .
RUN gradle build --no-daemon
FROM openjdk:21
WORKDIR /app
COPY --from=builder /home/gradle/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]