FROM openjdk:17-alpine

ADD build/libs/news-service-0.0.1-SNAPSHOT.jar /app/news-service-0.0.1.jar

ENTRYPOINT ["java", "-jar", "app/news-service-0.0.1.jar"]