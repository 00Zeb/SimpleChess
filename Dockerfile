FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/docker/dependency/cyberchess-0.0.1-SNAPSHOT.jar /app
ENTRYPOINT [“java”]
CMD ["-jar","cyberchess-0.0.1.jar"]