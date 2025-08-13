#  Clínica Veterinária - API

##  Visão Geral
Este projeto é uma API REST para gerenciar todos os processos de uma clínica veterinária, desde o cadastro de clientes e animais até a administração de medicamentos, internações, agendamentos e controle de estoque de produtos.

O sistema foi desenvolvido em **Java 17** com **Spring Boot**, seguindo o padrão **MVC** e boas práticas de arquitetura em camadas, além de contar com **DTOs** para comunicação e **Validações com Bean Validation**.

---

##  Arquitetura em Camadas

O código está organizado em:

- **Controller**: Recebe requisições HTTP e retorna respostas. Ex.: `AnimalController`, `AgendamentoController`.
- **Service**: Contém a lógica de negócio. Ex.: `AnimalServiceImplement`.
- **Repository**: Camada de persistência que se comunica diretamente com o banco de dados via Spring Data JPA.
- **Entity**: Classes que representam tabelas do banco (mapeadas com JPA/Hibernate).
- **DTO**: Objetos de transferência de dados (Request e Response) usados para comunicação entre cliente e servidor.


---


### Principais tabelas e funções:
- **pessoas**: Registro de dados pessoais (nome, CPF, telefone, e-mail).
- **clientes**: Associa uma pessoa como cliente da clínica.
- **funcionarios**: Armazena dados de funcionários, com cargo e CRMV (quando aplicável).
- **animais**: Cadastro de animais vinculados a clientes.
- **servicos**: Serviços veterinários prestados.
- **agendamentos**: Marcação de consultas e serviços.
- **internacao**: Controle de internações de animais.
- **diaria_internacao**: Registros diários da internação (peso, diagnósticos, observações).
- **administracoes_medicamentos**: Controle de medicamentos administrados.
- **produto**: Estoque de produtos.
- **medicamento**: Medicamentos cadastrados, vinculados a produtos.
- **prontuario / registro_prontuario**: Histórico médico do animal.


---

## 🚀 Funcionalidades
- **Gerenciamento de clientes** (dados pessoais, histórico de animais).
- **Cadastro e gerenciamento de animais** (espécie, raça, peso, porte, castração etc.).
- **Controle de funcionários** (dados pessoais, cargo, CRMV, salário).
- **Serviços veterinários** (consultas, cirurgias, vacinas, banho, tosa, etc.).
- **Agendamentos** com controle de status (confirmar, cancelar, reagendar).
- **Gestão de internações** com registro diário.
- **Administração de medicamentos** (histórico, dosagem, responsável).
- **Controle de estoque de produtos e medicamentos**.
- **Prontuário eletrônico** (registro de consultas e internações).

---

## Arquitetura e Camadas

O projeto segue uma arquitetura em camadas, organizada da seguinte forma:

### 1. Controller (Camada de Apresentação)**
- Contém os **endpoints da API REST**.
- Recebe requisições HTTP, delega a lógica aos *services* e retorna respostas.
- Não implementa lógica de negócio, apenas orquestra chamadas.
- Usa DTOs para entrada e saída de dados.
- Exemplos:
    - `AnimalController` → CRUD de animais.
    - `AgendamentoController` → CRUD e ações específicas de agendamentos.
    - `AdministracaoMedicamentoController` → CRUD e consultas de aplicações de medicamentos.

### 2. **Service**
- Implementa a **lógica de negócio**.
- Garante as regras do domínio antes de salvar ou processar dados.
- Orquestra interações entre Controllers e Repositories.

### 3. **Repository**
- Interfaces que estendem `JpaRepository` ou `CrudRepository`.
- Executam operações no banco de dados (find, save, delete).

### 4. DTOs 
   Objetos que transportam dados entre as camadas.

Evitam expor diretamente as entidades.

Tipos:

RequestDTO → Entrada de dados.

ResponseDTO → Saída de dados.

UpdateDTO → Atualizações parciais.

### 5. Entities 
   Representam as tabelas do banco de dados.

Anotadas com @Entity, @Table, @Id etc.

Atributos correspondem às colunas.

### 6. Mapper 
   Convertem DTOs ↔ Entidades.

Centralizam conversões para manter o código organizado.

Utilizam bibliotecas como ModelMapper.

## ⚙️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Hibernate**
- **H2 Database** (desenvolvimento) / PostgreSQL (produção)
- **Bean Validation** (`jakarta.validation`)
- **Lombok**
- **ModelMapper**
- **Maven**

---
### Estrutura do Banco de Dados
As entidades da API representam as tabelas do banco de dados. Alguns exemplos:

pessoas → Dados pessoais (nome, CPF, telefone, e-mail).

clientes → Vínculo de uma pessoa como cliente.

funcionarios → Registro de funcionários e seu cargo.

animais → Cadastro de animais vinculados a clientes.

servicos → Lista de serviços oferecidos.

agendamentos → Controle de agendamentos e status.

internacao → Registros de internação de animais.

diaria_internacao → Registros diários de cada internação.

administracoes_medicamentos → Histórico de medicamentos administrados.

produto → Estoque de produtos.

medicamento → Medicamentos cadastrados e vinculados a produtos.

prontuario / registro_prontuario → Histórico clínico do animal.

### funcionalidades
Gerenciar clientes e seus dados pessoais.

Cadastrar e gerenciar animais (espécie, raça, porte, castração, peso etc.).

Gerenciar funcionários (cargo, salário, CRMV).

Cadastrar serviços veterinários (consultas, vacinas, cirurgias etc.).

Criar e gerenciar agendamentos, com confirmação ou cancelamento.

Controlar internações e registrar informações diárias.

Administrar medicamentos com histórico e responsável técnico.

Gerenciar estoque de produtos e medicamentos.

Manter um prontuário eletrônico atualizado.

