# VetManager - Sistema de Clínica Veterinária

Este repositório contém o código-fonte completo do **VetManager**, um sistema de gerenciamento para clínicas veterinárias. O projeto é um monorepo, contendo tanto a API back-end quanto a aplicação front-end.

- **Backend:** API REST desenvolvida em Java 17 com Spring Boot.
- **Frontend:** Aplicação Single-Page (SPA) desenvolvida com Angular 17+.

## Estrutura do Projeto

O projeto está organizado em uma estrutura de monolito para facilitar o desenvolvimento e o gerenciamento:

- **`backend/`**: Contém a API REST responsável por toda a lógica de negócio, interações com o banco de dados e autenticação. Para trabalhar nesta parte, abra esta pasta com o IntelliJ IDEA.
- **`frontend/`**: Contém a aplicação de interface do usuário (UI) consumida pelos usuários no navegador. Para trabalhar nesta parte, abra esta pasta com o Visual Studio Code.

## Funcionalidades Principais
- **Autenticação Segura:** Sistema de login com Tokens JWT para clientes e funcionários.
- **Gestão de Clientes e Pacientes:** Cadastro e gerenciamento completo de clientes e seus animais.
- **Controle de Funcionários:** Gerenciamento de funcionários, cargos e permissões de acesso.
- **Agendamentos:** Sistema para marcar, visualizar e gerenciar consultas e serviços.
- **Prontuário Eletrônico:** Histórico médico detalhado para cada animal.
- **Gestão de Internações:** Controle de animais internados, com registros diários de evolução.
- **Controle de Estoque:** Gerenciamento de medicamentos e outros produtos da clínica.

---

## Como Rodar o Projeto (Desenvolvimento Local)

Para rodar o projeto completo, você precisará executar tanto o back-end quanto o front-end simultaneamente.

### 1. Pré-requisitos
- **Java 17+** e **Maven**
- **Node.js 18+** e **NPM**
- **Angular CLI** (`npm install -g @angular/cli`)
- **Docker** e **Docker Compose** (para rodar o PostgreSQL localmente)

### 2. Iniciando o Banco de Dados (PostgreSQL via Docker)

Por padrão, a aplicação está configurada para conectar a um banco de dados PostgreSQL (perfil `prod`). Para subir o banco via Docker:

1. Na raiz do projeto, faça uma cópia do arquivo `.env.example` nomeando-a como `.env`:
   ```bash
   cp .env.example .env
   ```
2. O arquivo `.env` está configurado no `.gitignore` para que você possa colocar suas próprias credenciais locais (como portas ou senhas) com privacidade.
3. Suba o banco de dados usando o comando:
   ```bash
   docker compose up -d
   ```
4. O contêiner do PostgreSQL ficará rodando em segundo plano e exposto na porta configurada no seu `.env` (padrão: `5432`).

### 3. Rodando o Backend (API)

1.  Abra a pasta `backend/` no seu IntelliJ IDEA (ou outra IDE Java).
2.  Aguarde o Maven baixar todas as dependências.
3.  Execute a classe principal `ClinicaApiApplication.java` ou rode via linha de comando com `./mvnw spring-boot:run` (iniciará com perfil `prod` usando o banco Docker).
4.  Caso prefira usar o banco em memória H2 (sem precisar do Docker/PostgreSQL), altere a propriedade `spring.profiles.active=dev` no `application.properties` ou execute `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.
5.  A API estará disponível em `http://localhost:8080`.
6.  Crie um cliente ou Funcionario pelo Swagger e o use para fazer o login no front

### 4. Rodando o Frontend (Aplicação Web)

1.  Abra a pasta `frontend/` no seu Visual Studio Code (ou outro editor de código).
2.  Abra um terminal dentro desta pasta.
3.  Instale as dependências do Node.js:
    ```bash
    npm install
    ```
4.  Inicie o servidor de desenvolvimento do Angular:
    ```bash
    ng serve
    ```
5.  A aplicação front-end estará disponível em `http://localhost:4200` e se conectará automaticamente à API que está rodando na porta 8080.

---

## Autenticação com JWT

A API utiliza autenticação via Token JWT para proteger seus endpoints. Com exceção do login e do cadastro de clientes, todas as outras chamadas precisam de um token válido.

### Fluxo de Autenticação

1.  **Crie um Usuário:** Primeiro, você precisa de um usuário.
    * **Cliente:** Faça uma requisição `POST` para `/api/clientes` com os dados do cliente (nome, cpf, senha, etc.). O sistema criará um cliente e um usuário associado, usando o **CPF** como login.
    * **Funcionário:** Um funcionário deve ser cadastrado internamente (ex: por um administrador). Ao ser criado, um usuário associado também é gerado, usando o **CRMV** (se veterinário) ou o **CPF** como login.

2.  **Faça o Login:**
    * Envie uma requisição `POST` para `/api/auth/login` com o seguinte corpo:
        ```json
        {
            "login": "SEU_CPF_OU_CRMV",
            "senha": "SUA_SENHA"
        }
        ```

3.  **Receba e Utilize o Token:**
    * A API retornará um token JWT.
    * Para todas as chamadas subsequentes a endpoints protegidos, você deve incluir este token no cabeçalho (Header) da requisição da seguinte forma:
        ```
        Authorization: Bearer <SEU_TOKEN_JWT_AQUI>
        ```

---

## Documentação da API (Swagger)

A API possui uma documentação interativa gerada com o Swagger (OpenAPI). Através dela, é possível visualizar todos os endpoints disponíveis, seus parâmetros, e até mesmo testá-los diretamente pelo navegador.

Para acessar a documentação, com o back-end em execução, acesse o seguinte link:

[**http://localhost:8080/swagger-ui/index.html**](http://localhost:8080/swagger-ui/index.html)

---

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3
- Spring Security (com JWT)
- Spring Data JPA / Hibernate
- PostgreSQL / H2
- Maven

### Frontend
- Angular 17+
- TypeScript
- HTML5 / CSS3
