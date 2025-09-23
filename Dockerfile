FROM gradle:latest AS builder

WORKDIR /app

COPY . .

RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine3.14

RUN apk add --no-cache \
    fontconfig \
    ttf-dejavu \
    freetype \
    libx11 \
    libxrender \
    libxext

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar RubberDucky.jar
COPY --from=builder /app/bot_data/ /app/bot_data/

CMD ["java", "-Djava.awt.headless=true", "-jar", "RubberDucky.jar"]
