FROM openjdk:21-jdk-slim

# Установка curl и Maven для healthcheck и сборки
RUN apt-get update && apt-get install -y curl maven && rm -rf /var/lib/apt/lists/*

# Создание рабочей директории
WORKDIR /app

# Копирование файлов проекта
COPY pom.xml .
COPY ewm-stats-dto/pom.xml ewm-stats-dto/
COPY ewm-stats-client/pom.xml ewm-stats-client/
COPY ewm-stats/pom.xml ewm-stats/
COPY ewm-main-service/pom.xml ewm-main-service/

# Копирование исходного кода
COPY ewm-stats-dto/src ewm-stats-dto/src
COPY ewm-stats-client/src ewm-stats-client/src
COPY ewm-stats/src ewm-stats/src
COPY ewm-main-service/src ewm-main-service/src

# Сборка приложения
RUN mvn clean package -DskipTests

# Копирование jar файла
RUN cp ewm-main-service/target/*.jar app.jar

# Открытие порта
EXPOSE 8080

# Запуск приложения
CMD ["java", "-jar", "app.jar"]
