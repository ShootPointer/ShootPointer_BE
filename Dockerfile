# ==============================
# Builder Stage
# ==============================
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /shootpointer

COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

RUN gradle clean bootJar --no-daemon -x test


# ==============================
# Runtime Stage
# ==============================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ---- Timezone 설정 ----
RUN apk add --no-cache tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone

ENV SPRING_PROFILES_ACTIVE=es,test-real-data,batch,test-highlight-data
ENV TZ=Asia/Seoul

# ---- Spring Boot JAR 복사 ----
COPY --from=builder /shootpointer/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]