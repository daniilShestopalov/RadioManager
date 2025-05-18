FROM gradle:8.7-jdk21 AS builder
WORKDIR /home/gradle/project

COPY . .

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2,TLSv1.3"
ENV GRADLE_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3"

RUN mkdir -p /home/gradle/.gradle \
    && echo "systemProp.https.protocols=TLSv1.2,TLSv1.3" >> /home/gradle/.gradle/gradle.properties
RUN gradle clean build --no-daemon

FROM openjdk:21
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/RadioManager-2.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]