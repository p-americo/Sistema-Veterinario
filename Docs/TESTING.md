# Guia de Testes — VetManager

Este documento descreve como executar os testes automatizados e gerar relatórios de
cobertura do sistema VetManager (backend Spring Boot + frontend Angular).

---

## Backend (Spring Boot)

Todos os comandos abaixo são executados a partir da pasta `backend/`.

```bash
cd backend
```

### Rodar todos os testes

```bash
./mvnw.cmd clean test
```

> No Git Bash use `./mvnw.cmd`; no PowerShell/CMD use `.\mvnw.cmd`.

### Rodar os testes de um único módulo

```bash
./mvnw.cmd test -pl clinica-domain        # regras de domínio (unidade pura)
./mvnw.cmd test -pl clinica-application    # services (unidade, Mockito)
./mvnw.cmd test -pl clinica-web            # controllers (integração)
```

### Rodar uma única classe de teste

```bash
./mvnw.cmd test -pl clinica-domain -Dtest=AgendamentoTest
```

### Rodar um único método de teste

```bash
./mvnw.cmd test -pl clinica-web -Dtest=AgendamentoControllerIntegrationTest#criarAgendamento_ComTokenEEntidadesValidas_DeveCriarERetornar201Created
```

### Pré-requisitos dos testes de integração

Os testes de integração de controllers usam o perfil **`dev`** (banco **H2 em arquivo**,
`./target/testdb`), portanto **não** exigem o PostgreSQL/Docker rodando — o H2 é criado
automaticamente. Eles também dependem dos usuários populados pelo `DataSeeder`:

- **Admin:** login `12345678900`
- **Cliente:** login `11111111111`

O JWT é gerado em tempo de teste via `TokenService`, e cada teste roda dentro de uma
transação (`@Transactional`) que sofre rollback ao final.

---

## Cobertura de testes (JaCoCo)

O `jacoco-maven-plugin` está configurado no POM pai ([backend/pom.xml](../backend/pom.xml)) e
roda automaticamente na fase `test`. Após executar os testes, os relatórios HTML ficam em:

```
backend/clinica-domain/target/site/jacoco/index.html
backend/clinica-application/target/site/jacoco/index.html
backend/clinica-infrastructure/target/site/jacoco/index.html
backend/clinica-web/target/site/jacoco/index.html
```

Para gerar e abrir o relatório de cobertura:

```bash
cd backend
./mvnw.cmd clean test
start clinica-web/target/site/jacoco/index.html   # Windows
```

> Cada módulo gera um relatório isolado. O `index.html` mostra a cobertura por pacote,
> classe e método (instruções e branches), permitindo identificar exatamente quais
> linhas ainda não estão cobertas.

---

## Estrutura e padrões de teste

| Camada | Localização | Tipo | Ferramentas |
|---|---|---|---|
| Domínio (regras de negócio) | `clinica-domain/src/test/...` | Unidade pura (sem Spring) | JUnit 5 |
| Services (casos de uso) | `clinica-application/src/test/...` | Unidade | JUnit 5 + Mockito |
| Controllers (endpoints REST) | `clinica-web/src/test/...` | Integração | `@SpringBootTest` + MockMvc + H2 |

**Convenção de nomes dos métodos de teste** (em português):

```
metodo_Cenario_ResultadoEsperado
```

Exemplos reais do projeto:

- `registrarAdministracao_ComProdutoSemEstoqueSuficiente_DeveLancarExcecao`
- `criarAgendamento_SemToken_DeveRetornar401Unauthorized`

### Esqueleto — teste de domínio (unidade pura)

```java
class ProdutoTest {
    @Test
    void debitarEstoque_ComQuantidadeMaiorQueEstoque_DeveLancarExcecao() {
        Produto produto = new Produto();
        produto.inicializarEstoque(5);
        assertThrows(BusinessRuleException.class, () -> produto.debitarEstoque(6));
    }
}
```

### Esqueleto — teste de service (unidade com Mockito)

```java
@ExtendWith(MockitoExtension.class)
class CargoServiceImplementTest {
    @Mock CargoRepository cargoRepository;
    @InjectMocks CargoServiceImplement service;

    @Test
    void criar_ComNomeDuplicado_DeveLancarExcecao() {
        when(cargoRepository.existsByNome("Veterinário")).thenReturn(true);
        // ... arrange DTO e assertThrows
    }
}
```

### Esqueleto — teste de integração de controller

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ExemploControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired TokenService tokenService;
    @Autowired UsuarioRepository usuarioRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        Usuario admin = usuarioRepository.findByLogin("12345678900").orElseThrow();
        adminToken = tokenService.gerarToken(admin);
    }

    @Test
    void endpoint_ComToken_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/...")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
```

Referência completa: [AgendamentoControllerIntegrationTest.java](../backend/clinica-web/src/test/java/br/com/clinicavet/clinica_api/api/controller/AgendamentoControllerIntegrationTest.java).

---

## Frontend (Angular)

A partir da pasta `frontend/`:

```bash
cd frontend
npm install        # apenas na primeira vez
npm test           # roda os testes unitários (Karma/Jasmine)
```

---

## Dicas

- **Build completo sem testes:** `./mvnw.cmd clean package -DskipTests`
- **Ver saída detalhada de um teste que falhou:** consulte
  `backend/<modulo>/target/surefire-reports/*.txt`.
- Os testes de domínio são os mais rápidos (não sobem contexto Spring) — prefira-os
  para validar regras de negócio puras.
