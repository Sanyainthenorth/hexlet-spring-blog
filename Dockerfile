FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 1. Копируем исходный код и конфиги
COPY src ./src
COPY build.gradle .
COPY gradlew .
COPY gradle ./gradle

# 2. Собираем проект
RUN ./gradlew build

# 3. Копируем готовый JAR (в нем уже есть application-production.yml)
COPY build/libs/*.jar app.jar

# 4. Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=production"]