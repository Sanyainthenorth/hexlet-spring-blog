FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Сначала копируем только файлы для сборки зависимостей
COPY build.gradle.kts .    # <- Исправлено здесь
COPY gradlew .
COPY gradle ./gradle

# Копируем исходный код
COPY src ./src

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Собираем проект
RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/hexlet-spring-blog.jar", "--spring.profiles.active=production"]