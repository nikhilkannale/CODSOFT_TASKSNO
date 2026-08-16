# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -m scrsuser
COPY --from=build /app/target/student-course-registration-system.jar app.jar
RUN mkdir -p /app/uploads/profile-pictures && chown -R scrsuser:scrsuser /app
USER scrsuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
