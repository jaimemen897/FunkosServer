FROM gradle:jdk17 AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle y src
COPY build.gradle.kts .
COPY gradlew .
COPY gradle gradle
COPY src src
# Instala Gradle en la imagen
RUN gradle wrapper --gradle-version 8.4

# Compila y construye el proyecto, evitando la ejecución de los tests
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Etapa de ejecución
FROM openjdk:17-jdk-alpine AS runtime

# Directorio de trabajo
WORKDIR /app

# Copia el archivo .jar de la etapa de compilación
COPY --from=build /app/build/libs/*.jar mi-aplicacion.jar

# Define el comando para ejecutar tu aplicación
ENTRYPOINT ["java","-jar","mi-aplicacion.jar"]