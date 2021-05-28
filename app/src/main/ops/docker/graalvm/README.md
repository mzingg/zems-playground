# Borrowed from the Graalvm Community Repository

Since ther is not (yet) a Java 16 image of the Graalvm image pushed
this Dockerfile is made available here to build a Java 16 variant ourselves.

- https://www.graalvm.org/docs/getting-started/container-images/#graalvm-community-images


Build like this (from the official readme):
`docker buildx build --platform linux/amd64 --build-arg GRAALVM_VERSION=21.1.0 --build-arg JAVA_VERSION=java16 -t ghcr.io/graalvm/graalvm-ce:java16-21.1.0-local --output=type=docker -f Dockerfile .`