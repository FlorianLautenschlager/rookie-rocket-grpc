FROM openjdk:12
COPY build/libs/*.jar /grpc-client.jar
CMD ["java", "-jar", "/grpc-client.jar", "--daemon"]
