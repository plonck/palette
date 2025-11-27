FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
RUN ./gradlew --version > /dev/null

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN ./gradlew --no-daemon dependencies > /dev/null && ./gradlew --no-daemon build --dry-run > /dev/null

COPY src/ src/
COPY build.sh .

RUN chmod +x build.sh

CMD ["./build.sh"]
