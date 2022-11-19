FROM maven:latest AS build
COPY ./src ./build/src
COPY ./pom.xml ./build
RUN mvn -f ./build/pom.xml -DskipTests clean package

FROM openjdk:latest
COPY --from=build ./build/target/producer-0.0.1-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

#docker run -dt --name rabbitmq -e DEVICE_ID=af7c1fe6-d669-414e-b066-e9733f0de7a8 -p 15672:15672 -p 5672:5672 rabbitmq:3-management-alpine