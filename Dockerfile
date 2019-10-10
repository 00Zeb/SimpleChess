FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=build/docker/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
CMD ["java","-jar","/app/build/libs/cyberchess-0.0.1-SNAPSHOT.jar"]