FROM maven:3.6.3-openjdk-14-slim AS build

COPY settings.xml /usr/share/maven/conf/

COPY pom.xml pom.xml
COPY mis-api/pom.xml mis-api/pom.xml
COPY mis-model/pom.xml mis-model/pom.xml
COPY mis-base/pom.xml mis-base/pom.xml

RUN mvn dependency:go-offline package -B

COPY mis-api/src mis-api/src
COPY mis-model/src mis-model/src
COPY mis-base/src mis-base/src

RUN mvn install -Prunnable

FROM openjdk:14-ea-jdk-alpine
USER root

RUN mkdir service

COPY --from=build /mis-base/target/ /service/
COPY /wz /service/wz

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

EXPOSE 5005

CMD /wait && java --enable-preview -jar /service/mis-base-1.0-SNAPSHOT.jar -Xdebug