FROM gradle:latest AS builder

WORKDIR /app

COPY . .

RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine3.14

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar RubberDucky.jar
COPY --from=builder /app/bot_data /app/bot_data

CMD ["java", "-jar", "RubberDucky.jar"]
