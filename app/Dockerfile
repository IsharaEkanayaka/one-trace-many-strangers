# Stage 1: compile the single-file app
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY Main.java .
RUN javac -d /out Main.java

# Stage 2: run it with the OpenTelemetry Java agent
FROM eclipse-temurin:21-jre
ARG OTEL_AGENT_VERSION=2.30.0
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar
COPY --from=build /out /app
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/opt/opentelemetry-javaagent.jar", "-cp", "/app", "Main"]
