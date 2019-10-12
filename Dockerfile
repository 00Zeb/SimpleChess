FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY dependency/cyberchess-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar","/app/cyberchess-0.0.1.jar"]
#CMD ["java" "-jar","cyberchess-0.0.1.jar"]
#CMD ["java","-cp","app:app/lib/*","web.Application"]
