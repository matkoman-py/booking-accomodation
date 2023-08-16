FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/booking-accomodation-0.0.1-SNAPSHOT.jar booking-accomodation-0.0.1-SNAPSHOT.jar
EXPOSE 8082
CMD ["java", "-jar", "booking-accomodation-0.0.1-SNAPSHOT.jar"]