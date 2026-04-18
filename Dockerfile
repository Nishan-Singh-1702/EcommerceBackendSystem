FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/EcommerceBackendSystem-0.0.1-SNAPSHOT.jar /app/EcommerceBackendSystem-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "EcommerceBackendSystem-0.0.1-SNAPSHOT.jar"]