FROM openjdk:8
COPY ./target/scala-2.12/player-assembly-0.1.0-SNAPSHOT.jar /usr/src/app.jar
WORKDIR /usr/src/
CMD ["java", "-jar", "app.jar"]
