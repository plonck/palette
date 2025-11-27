ARG JAVA_VERSION=21

FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy

WORKDIR /app

ARG JAVA_VERSION
ENV ORG_GRADLE_PROJECT_javaVersion=${JAVA_VERSION}

COPY gradlew .
COPY gradle/ gradle/
RUN ./gradlew --version > /dev/null

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN ./gradlew --no-daemon dependencies > /dev/null

COPY src/ src/
COPY build.sh .

RUN chmod +x build.sh

CMD ["./build.sh"]
