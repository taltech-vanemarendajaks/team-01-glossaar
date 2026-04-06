FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

# tests are run before creating a Docker image, we can skip them here
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copy the pre-built JAR from the target directory
COPY --from=build /app/target/*.jar app.jar

# label necessary so the image on ghcr.io gets linked to the repository on github.com when pushing locally
LABEL org.opencontainers.image.source=https://github.com/taltech-vanemarendajaks/team-01-glossaar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
