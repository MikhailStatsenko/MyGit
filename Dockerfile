FROM openjdk:21-slim
EXPOSE 8080
COPY ./target/vitality-git-0.0.1-SNAPSHOT.jar /usr/src/app/vitality-git-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/usr/src/app/vitality-git-0.0.1-SNAPSHOT.jar"]