# CLAUDE.md

This file provides guidance to AI coding assistants working in this repository.

## Project Overview

VetManager - Monorepo for a veterinary clinic management system.

- backend/ - Spring Boot 3.2.6 API. Parent module is clinica-parent. Modules:
  - clinica-domain: Business models and repository interfaces.
  - clinica-application: Services, DTOs, and event listeners.
  - clinica-infrastructure: Security (JWT), configurations, and Spring Data JPA implementations.
  - clinica-web: REST controllers (API endpoints). Main class: ClinicaApiApplication.
- frontend/ - Angular SPA served on http://localhost:4200. Consumes API at http://localhost:8080.
- Docs/ - PDF diagrams and project README.

## Common Commands

### Docker (from root /)
- Start DB: docker compose up -d
- Stop DB: docker compose down
- Status: docker compose ps

### Backend (from backend/)
- Run dev (H2 database): .\mvnw.cmd spring-boot:run -pl clinica-web -Dspring-boot.run.profiles=dev
- Run prod (PostgreSQL): .\mvnw.cmd spring-boot:run -pl clinica-web -Dspring-boot.run.profiles=prod
- Clean & Build: .\mvnw.cmd clean package
- Run tests: .\mvnw.cmd clean test
- Run single test: .\mvnw.cmd test -pl clinica-web -Dtest=ClassName#methodName

Swagger UI lives at http://localhost:8080/swagger-ui/index.html when running.

### Frontend (from frontend/)
- Install deps: npm install
- Run dev server: npm start
- Build: npm run build
- Run tests: npm test

## Architecture & Code Structure

### Package Structure (DDD + Clean Architecture)
- br.com.clinicavet.clinica_api.domain:
  - model/: Entities (use rich domain models, encapsulate state, prevent invalid states).
  - repository/: Generic and entity-specific repository interfaces.
  - exception/: Domain exceptions (e.g. ResourceNotFoundException, BusinessRuleException).
- br.com.clinicavet.clinica_api.application:
  - service/: Application service interfaces.
  - service.impl/: Use case implementations.
  - dto/: Request, response, and update DTOs.
  - event/: Domain events (e.g. ClienteCriadoEvent) and handlers (UsuarioEventListener).
- br.com.clinicavet.clinica_api.infrastructure:
  - security/: JWT Token generation, validation, and filter configuration.
  - config/: ModelMapper, WebMvc configurations.
  - persistence.jpa/: Spring Data JPA repository implementations.
- br.com.clinicavet.clinica_api.api:
  - controller/: REST APIs exposing endpoints.
  - exception/: Global exception handler returning RFC 7807 ProblemDetail response.

### DDD & Clean Code Guidelines
- Aggregate Roots & Repositories: Only declare repository interfaces for aggregate roots. Child entities (like DiariaInternacao inside Internacao) must be modified, created, and deleted through their aggregate root repository.
- DDD Enforcements: Avoid reflection-based mapping (ModelMapper) for complex domain entities with business rules (like Funcionario, Produto, Internacao). Use manual mappers (e.g., DiariaMapper) or MapStruct to ensure domain constructors/setters and constraints are respected.
- Database Migrations: Flyway manages migrations under backend/clinica-web/src/main/resources/db/migration/. JPA ddl-auto is set to validate, so every schema change requires a V{n}__.sql script.

### Language & Naming
- Code-facing names (entities, properties, tables, routes, variables, folders) must be in Brazilian Portuguese (e.g., Agendamento, Veterinario, diarias).
- AI logs, reasoning, and git commits must be in English.
