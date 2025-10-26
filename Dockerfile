# Builder
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /shootpointer

COPY build.gradle settings.gradle ./
COPY gradle gradle

COPY src src
RUN gradle clean bootJar --no-daemon -x test

# Running
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache tzdata \
        && ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
        && echo "Asia/Seoul" > /etc/timezone

ENV SPRING_PROFILES_ACTIVE=es,test-real-data,batch
ENV TZ=Asia/Seoul

COPY --from=builder /shootpointer/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
