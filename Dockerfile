# Use uma imagem base do OpenJDK como ponto de partida
FROM openjdk:17-oracle

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR gerado para o contêiner
COPY target/heinekenquiz-0.0.1-SNAPSHOT.jar app.jar

# Comando a ser executado quando o contêiner for iniciado
CMD ["java", "-jar", "app.jar"]
