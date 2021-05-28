FROM ghcr.io/graalvm/graalvm-ce:java16-21.1.0-local
ARG APPLICATION_JAR
COPY ${APPLICATION_JAR} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]