FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY dependency/cyberchess-0.0.1-SNAPSHOT.jar /app
ENTRYPOINT [“/bin/java”]
CMD ["-jar","cyberchess-0.0.1.jar"]