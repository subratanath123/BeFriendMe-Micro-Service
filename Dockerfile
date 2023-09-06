FROM openjdk:17-jdk-slim
CMD ["gradle", "build"]
COPY /build/libs/befriendme-0.0.1-SNAPSHOT.jar /app/befriendme.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "/app/befriendme.jar"]
