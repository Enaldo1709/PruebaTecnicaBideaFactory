# Build stage
FROM gradle:7.4.2-jdk17-alpine AS builder

COPY ms-book-service ./ms-book-service
WORKDIR /home/gradle/ms-book-service
RUN ls -la
RUN gradle wrapper
RUN ./gradlew clean build

# Deployment stage
FROM eclipse-temurin:17-alpine
VOLUME /tmp

# Low privileges user creation
RUN apk --no-cache add sudo
RUN apk --no-cache add curl
RUN sudo adduser --disabled-password -u 10111 appuser

# Container configuration
COPY --from=builder --chown=appuser:appuser /home/gradle/ms-book-service/build/ms-book-service.jar .
EXPOSE 8080

# Service configuration
ENV JAVA_OPTS="-XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

USER appuser
ENTRYPOINT [ "sh", "-c", "java ${JAVA_OPTS} -jar ms-book-service.jar" ]