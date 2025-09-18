FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./gradlew build
ENTRYPOINT ["java", "-jar", "build/libs/hexlet-spring-blog.jar", "--spring.profiles.active=production"]