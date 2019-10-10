FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY cyberchess-0.0.1-SNAPSHOT.jar /app
CMD ["java","-jar","/app/cyberchess-0.0.1-SNAPSHOT.jar"
#ARG DEPENDENCY=build/docker/dependency
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app
#CMD ["java","-cp","app:app/lib/*","web.Application"]