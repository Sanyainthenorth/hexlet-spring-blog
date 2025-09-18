FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Сначала копируем только файлы для сборки зависимостей
COPY build.gradle .
COPY gradlew .
COPY gradle ./gradle

# Копируем исходный код
COPY src ./src

# Собираем проект
RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/hexlet-spring-blog.jar", "--spring.profiles.active=production"]