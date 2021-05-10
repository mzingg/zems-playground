FROM ghcr.io/graalvm/graalvm-ce:java11-21.1.0
ARG APPLICATION_JAR
COPY ${APPLICATION_JAR} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]