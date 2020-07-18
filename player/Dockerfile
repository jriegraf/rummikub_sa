FROM openjdk:8
RUN cp target/scala-2.12/player.jar /usr/src/app.jar
WORKDIR /usr/src/
CMD ["java", "-jar", "app.jar"]
