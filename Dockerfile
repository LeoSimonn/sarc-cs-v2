# ===== stage de build (usa Maven oficial) =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas o pom e baixa dependências (cache mais eficiente)
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Agora copia o código-fonte e empacota
COPY src ./src
RUN mvn -B -DskipTests package

# ===== stage final (imagem leve p/ rodar a API) =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o .jar construído
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
