FROM openjdk:21-jdk-slim

# Установка curl и Maven для healthcheck и сборки
RUN apt-get update && apt-get install -y curl maven && rm -rf /var/lib/apt/lists/*

# Создание рабочей директории
WORKDIR /app

# Копирование только необходимых файлов для основного сервиса
COPY ewm-stats-dto/pom.xml ewm-stats-dto/
COPY ewm-stats-client/pom.xml ewm-stats-client/
COPY ewm-main-service/pom.xml ewm-main-service/

# Копирование исходного кода
COPY ewm-stats-dto/src ewm-stats-dto/src
COPY ewm-stats-client/src ewm-stats-client/src
COPY ewm-main-service/src ewm-main-service/src

# Создание временного pom.xml только для модулей основного сервиса
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > pom.xml && \
    echo '<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> pom.xml && \
    echo '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">' >> pom.xml && \
    echo '    <modelVersion>4.0.0</modelVersion>' >> pom.xml && \
    echo '    <parent>' >> pom.xml && \
    echo '        <groupId>org.springframework.boot</groupId>' >> pom.xml && \
    echo '        <artifactId>spring-boot-starter-parent</artifactId>' >> pom.xml && \
    echo '        <version>3.2.5</version>' >> pom.xml && \
    echo '        <relativePath/>' >> pom.xml && \
    echo '    </parent>' >> pom.xml && \
    echo '    <groupId>ru.practicum</groupId>' >> pom.xml && \
    echo '    <artifactId>explore-with-me</artifactId>' >> pom.xml && \
    echo '    <version>0.0.1-SNAPSHOT</version>' >> pom.xml && \
    echo '    <packaging>pom</packaging>' >> pom.xml && \
    echo '    <modules>' >> pom.xml && \
    echo '        <module>ewm-stats-dto</module>' >> pom.xml && \
    echo '        <module>ewm-stats-client</module>' >> pom.xml && \
    echo '        <module>ewm-main-service</module>' >> pom.xml && \
    echo '    </modules>' >> pom.xml && \
    echo '    <properties>' >> pom.xml && \
    echo '        <java.version>21</java.version>' >> pom.xml && \
    echo '        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>' >> pom.xml && \
    echo '        <spring-cloud.version>2023.0.0</spring-cloud.version>' >> pom.xml && \
    echo '    </properties>' >> pom.xml && \
    echo '    <dependencyManagement>' >> pom.xml && \
    echo '        <dependencies>' >> pom.xml && \
    echo '            <dependency>' >> pom.xml && \
    echo '                <groupId>org.springframework.cloud</groupId>' >> pom.xml && \
    echo '                <artifactId>spring-cloud-dependencies</artifactId>' >> pom.xml && \
    echo '                <version>${spring-cloud.version}</version>' >> pom.xml && \
    echo '                <type>pom</type>' >> pom.xml && \
    echo '                <scope>import</scope>' >> pom.xml && \
    echo '            </dependency>' >> pom.xml && \
    echo '        </dependencies>' >> pom.xml && \
    echo '    </dependencyManagement>' >> pom.xml && \
    echo '</project>' >> pom.xml

# Сборка приложения
RUN mvn clean package -DskipTests

# Копирование jar файла
RUN cp ewm-main-service/target/*.jar app.jar

# Открытие порта
EXPOSE 8080

# Запуск приложения
CMD ["java", "-jar", "app.jar"]
