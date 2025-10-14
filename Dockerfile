FROM eclipse-temurin:17.0.11-jdk

ARG JAR_FILE=build/libs/shootpointer-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=es,test-real-data,prod


RUN apt-get update \
    && apt-get install -y tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
