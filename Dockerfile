FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy build files first
COPY build.gradle.kts .
COPY gradlew .
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Make gradlew executable and build
RUN chmod +x gradlew
RUN ./gradlew build

# Run the application
ENTRYPOINT ["java", "-jar", "build/libs/hexlet-spring-blog.jar", "--spring.profiles.active=production"]