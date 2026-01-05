# ==============================
# Builder Stage
# ==============================
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /shootpointer

COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew ./

COPY apps apps
COPY domains domains


RUN chmod +x gradlew

ARG MODULE_NAME=api-server

RUN ./gradlew :apps:${MODULE_NAME}:bootJar --no-daemon -x test


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

ARG MODULE_NAME=api-server
COPY --from=builder /shootpointer/apps/${MODULE_NAME}/build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]