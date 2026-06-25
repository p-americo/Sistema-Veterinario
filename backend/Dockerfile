# Estágio 1: Compilar e empacotar a aplicação
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copia o pom.xml pai e os poms dos módulos para cache de dependências
COPY pom.xml .
COPY clinica-domain/pom.xml clinica-domain/pom.xml
COPY clinica-application/pom.xml clinica-application/pom.xml
COPY clinica-infrastructure/pom.xml clinica-infrastructure/pom.xml
COPY clinica-web/pom.xml clinica-web/pom.xml

# Baixa as dependências offline (otimização de cache do Docker)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY clinica-domain/src ./clinica-domain/src
COPY clinica-application/src ./clinica-application/src
COPY clinica-infrastructure/src ./clinica-infrastructure/src
COPY clinica-web/src ./clinica-web/src

# Compila e empacota a aplicação pulando os testes
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final leve de execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criação de um usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o jar gerado no estágio anterior
COPY --from=builder /app/clinica-web/target/clinica-web-0.0.1-SNAPSHOT.jar app.jar

# Porta padrão exposta
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
