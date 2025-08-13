# Clínica Veterinária - API

## Visão Geral
Este projeto é uma API REST desenvolvida em **Java 17** com **Spring Boot** para gerenciar todos os processos de uma clínica veterinária. O sistema permite o cadastro de clientes, animais, funcionários, agendamentos, internações, administração de medicamentos e controle de estoque de produtos.

A arquitetura segue o padrão **MVC** e boas práticas de separação de responsabilidades, utilizando DTOs para comunicação e validações automáticas.

---

## Funcionalidades Principais
- Cadastro e gerenciamento de clientes e animais
- Controle de funcionários e cargos
- Agendamento de consultas e serviços
- Gestão de internações e registros diários
- Administração de medicamentos
- Controle de estoque de produtos e medicamentos
- Prontuário eletrônico do animal

---

## Tecnologias Utilizadas
- Java 17
- Spring Boot 3
- Spring Data JPA
- Hibernate
- H2 Database (desenvolvimento) / PostgreSQL (produção)
- Bean Validation (jakarta.validation)
- Lombok
- Maven

---

## Como Rodar o Projeto

### Pré-requisitos
- Java 17 ou superior
- Maven

### Passos para execução
1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd sistema-veterinario
   ```
2. Configure o banco de dados em `src/main/resources/application.properties` se necessário (por padrão usa H2 em memória para desenvolvimento).
3. Execute o projeto:
   ```bash
   ./mvnw spring-boot:run
   # ou, se preferir usar o Maven instalado no sistema
   mvn spring-boot:run
   ```
4. A API estará disponível em: `http://localhost:8080`

---

## Estrutura do Projeto
- `controller/` — Endpoints REST
- `service/` — Lógica de negócio
- `repository/` — Persistência de dados
- `model/` — Entidades JPA
- `dto/` — Objetos de transferência de dados

---

## Observações
- Para ambiente de produção, configure o banco PostgreSQL em `application-prod.properties`.
- Documentação dos endpoints pode ser acessada via ferramentas como Postman (veja exemplos em `Docs/postaman scripts.txt`).
- A documentação interativa Swagger está em desenvolvimento e pode ser acessada em: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
