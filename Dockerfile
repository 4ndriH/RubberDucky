FROM gradle:latest AS builder

WORKDIR /app

COPY . .

RUN gradle build --no-daemon --continuous

FROM openjdk:17-jdk-alpine3.14

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar RubberDucky.jar

CMD ["java", "-jar", "RubberDucky.jar"]
