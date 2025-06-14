# ─── Dockerfile (place in the same folder that has pom.xml) ───
FROM eclipse-temurin:17-jre

# Pass the exact JAR name in at build-time (defaults shown below)
ARG JAR_FILE=target/photographer-service-0.0.1-SNAPSHOT.jar

# Copy that JAR from your local FS into the image
COPY ${JAR_FILE} /app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
