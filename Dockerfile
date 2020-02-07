FROM openjdk:8-jre-alpine
RUN apk add --no-cache tzdata
ENV TZ Europe/Moscow
ARG JAR_FILE
COPY target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "app.jar"]
