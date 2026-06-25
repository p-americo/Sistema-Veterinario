package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.*;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumViaMedicamento;
import br.com.clinicavet.clinica_api.domain.repository.*;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import br.com.clinicavet.clinica_api.testsupport.DataSeederFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class AdministracaoMedicamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private InternacaoRepository internacaoRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private RegistroProntuarioRepository registroProntuarioRepository;

    @Autowired
    private AdministracaoMedicamentoRepository administracaoMedicamentoRepository;

    private String adminToken;
    private Funcionario executor;
    private Medicamento testMedicamento;
    private Internacao testInternacao;
    private DiariaInternacao testDiaria;
    private Prontuario testProntuario;
    private RegistroProntuario testRegistro;
    private AdministracaoMedicamento testAdministracao;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);
        executor = (Funcionario) adminUser.getPessoa();

        // Seed Medicamento
        Produto produto = new Produto();
        produto.setNome("Analgesico Super Vet");
        produto.setDescricao("Analgesico forte");
        produto.inicializarEstoque(50);
        Medicamento medicamento = new Medicamento();
        medicamento.setProduto(produto);
        medicamento.setCategoria(EnumCategoriaMedicameno.ANALGESICO);
        medicamento.setViaAdministracao(EnumViaMedicamento.ORAL);
        medicamento.setDosagemPadrao("1ml");
        medicamento.setPrincipioAtivo("Principio X");
        testMedicamento = medicamentoRepository.save(medicamento);

        // Seed Animal
        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        Cliente seededCliente = (Cliente) clienteUser.getPessoa();
        Animal animal = new Animal();
        animal.setNome("Pipoca");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.MEDIO);
        animal.setRaca("Beagle");
        animal.setSexo(EnumSexo.FEMEA);
        animal.setPeso(12.0);
        animal.setCastrado(true);
        animal.setDataNascimento(LocalDate.of(2019, 5, 10));
        animal.setCliente(seededCliente);
        Animal savedAnimal = animalRepository.save(animal);

        // Seed Prontuario
        Prontuario prontuario = new Prontuario();
        prontuario.setAnimal(savedAnimal);
        prontuario.setAlergiasConhecidas("Sem alergias");
        testProntuario = prontuarioRepository.save(prontuario);

        // Seed RegistroProntuario
        RegistroProntuario registro = new RegistroProntuario();
        registro.setDataHora(LocalDateTime.now().minusHours(5));
        registro.setVeterinarioResponsavel(executor);
        registro.setPesoNoDia(new BigDecimal("12.0"));
        registro.setObservacoesClinicas("Consulta de rotina");
        registro.setDiagnostico("Saudável");
        registro.associarAoProntuario(testProntuario);
        testRegistro = registroProntuarioRepository.save(registro);

        // Seed Internacao
        Internacao internacao = new Internacao();
        internacao.setAnimal(savedAnimal);
        internacao.setDataEntrada(LocalDateTime.now().minusDays(1));
        testInternacao = internacaoRepository.save(internacao);

        // Seed Diaria
        DiariaInternacao diaria = new DiariaInternacao();
        diaria.setDataHora(LocalDateTime.now().minusHours(2));
        diaria.setPesoNoDia(new BigDecimal("11.9"));
        diaria.setObservacoesClinicas("Diária observação");
        diaria.setDiagnostico("Observação");
        testInternacao.adicionarDiaria(diaria);
        internacaoRepository.save(testInternacao);
        testDiaria = testInternacao.getDiarias().iterator().next();

        // Seed Administracao
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();
        administracao.registrarAdministracao(testMedicamento, executor, new BigDecimal("2.0"), LocalDateTime.now().minusHours(1), "2ml");
        administracao.associarAoProntuario(testRegistro);
        administracao.associarDiaria(testDiaria);
        testAdministracao = administracaoMedicamentoRepository.save(administracao);
    }

    @Test
    void criarAdministracao_ComRegistroProntuario_DeveRetornar201Created() throws Exception {
        String jsonRequest = String.format("""
                {
                    "medicamentoId": %d,
                    "entradaProntuarioId": %d,
                    "funcionarioExecutorId": %d,
                    "quantidadeAdministrada": 1.5,
                    "dataHora": "2026-06-25T10:00:00",
                    "dosagem": "1.5ml"
                }
                """, testMedicamento.getId(), testRegistro.getId(), executor.getId());

        mockMvc.perform(post("/api/administracoes-medicamento")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantidadeAdministrada").value(1.5))
                .andExpect(jsonPath("$.dosagem").value("1.5ml"));
    }

    @Test
    void criarAdministracao_ComDiaria_DeveRetornar201Created() throws Exception {
        String jsonRequest = String.format("""
                {
                    "medicamentoId": %d,
                    "diariaId": %d,
                    "funcionarioExecutorId": %d,
                    "quantidadeAdministrada": 1.0,
                    "dataHora": "2026-06-25T10:00:00",
                    "dosagem": "1ml"
                }
                """, testMedicamento.getId(), testDiaria.getId(), executor.getId());

        mockMvc.perform(post("/api/administracoes-medicamento")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.diariaId").value(testDiaria.getId()));
    }

    @Test
    void criarAdministracao_SemProntuarioESemDiaria_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = String.format("""
                {
                    "medicamentoId": %d,
                    "funcionarioExecutorId": %d,
                    "quantidadeAdministrada": 1.0,
                    "dataHora": "2026-06-25T10:00:00",
                    "dosagem": "1ml"
                }
                """, testMedicamento.getId(), executor.getId());

        // A regra de negócio exige um dos dois, resultando em erro 400 do handler
        mockMvc.perform(post("/api/administracoes-medicamento")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTodasAdministracoes_DeveRetornarPaginado200Ok() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void buscarAdministracaoPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento/" + testAdministracao.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAdministracao.getId()));
    }

    @Test
    void buscarAdministracaoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarAdministracoesPorEntradaProntuario_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento/entrada-prontuario/" + testRegistro.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entradaProntuarioId").value(testRegistro.getId()));
    }

    @Test
    void buscarAdministracoesPorMedicamento_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento/medicamento/" + testMedicamento.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].medicamentoId").value(testMedicamento.getId()));
    }

    @Test
    void buscarAdministracoesPorFuncionario_DeveRetornarLista200Ok() throws Exception {
        mockMvc.perform(get("/api/administracoes-medicamento/funcionario/" + executor.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].funcionarioExecutorId").value(executor.getId()));
    }

    @Test
    void buscarAdministracoesPorPeriodo_DeveRetornarLista200Ok() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String inicio = now.minusDays(2).toString();
        String fim = now.plusDays(1).toString();

        mockMvc.perform(get("/api/administracoes-medicamento/periodo")
                        .param("inicio", inicio)
                        .param("fim", fim)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void atualizarAdministracao_ComDadosValidos_DeveRetornarAtualizado200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "medicamentoId": %d,
                    "entradaProntuarioId": %d,
                    "funcionarioExecutorId": %d,
                    "quantidadeAdministrada": 3.0,
                    "dataHora": "2026-06-25T11:00:00",
                    "dosagem": "3ml"
                }
                """, testMedicamento.getId(), testRegistro.getId(), executor.getId());

        mockMvc.perform(put("/api/administracoes-medicamento/" + testAdministracao.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeAdministrada").value(3.0))
                .andExpect(jsonPath("$.dosagem").value("3ml"));
    }

    @Test
    void deletarAdministracao_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/administracoes-medicamento/" + testAdministracao.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(administracaoMedicamentoRepository.findById(testAdministracao.getId()).isPresent());
    }
}
