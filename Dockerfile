FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY artifact/cyberchess-0.0.1-SNAPSHOT.jar /app/cyberchess-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar","/app/cyberchess-0.0.1-SNAPSHOT.jar"]