FROM gradle:latest AS builder

WORKDIR /app

# Debug: Print current directory before COPY
RUN echo "Current directory before COPY:" && pwd && echo "Contents:" && ls -la

COPY . .

# Debug: Print destination directory after COPY
RUN echo "Current directory after COPY:" && pwd && echo "Contents of /app/database:" && ls -la /app/database

RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine3.14

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar RubberDucky.jar
COPY --from=builder /app/bot_data /app/bot_data

CMD ["java", "-jar", "RubberDucky.jar"]
