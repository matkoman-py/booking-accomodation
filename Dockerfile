FROM maven:3.8.5-openjdk-17-slim AS MAVEN_BUILD
COPY ./ ./
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=MAVEN_BUILD /target/booking-accomodation-0.0.1-SNAPSHOT.jar /booking-accomodation.jar
EXPOSE 8082
CMD ["java", "-jar", "/booking-accomodation.jar"]