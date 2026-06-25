package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.*;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class AgendamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    private String adminToken;
    private Cliente seededCliente;
    private Funcionario seededVeterinario;
    private Animal testAnimal;
    private Servico testServico;
    private Agendamento testAgendamento;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Cliente do DataSeeder não encontrado"));
        seededCliente = (Cliente) clienteUser.getPessoa();
        seededVeterinario = (Funcionario) adminUser.getPessoa();

        Animal animal = new Animal();
        animal.setNome("Pipoca");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.PEQUENO);
        animal.setRaca("Poodle");
        animal.setSexo(EnumSexo.MACHO);
        animal.setPeso(6.5);
        animal.setCastrado(true);
        animal.setDataNascimento(LocalDate.of(2022, 3, 10));
        animal.setCliente(seededCliente);
        testAnimal = animalRepository.save(animal);

        Servico servico = new Servico();
        servico.setTipo(EnumServico.CONSULTA);
        servico.setVeterinario(seededVeterinario);
        servico.setValor(new BigDecimal("150.00"));
        testServico = servicoRepository.save(servico);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(seededCliente);
        agendamento.setAnimal(testAnimal);
        agendamento.setServico(testServico);
        agendamento.setDataHoraAgendamento(LocalDateTime.now().plusDays(3));
        testAgendamento = agendamentoRepository.save(agendamento);
    }

    @Test
    void criarAgendamento_SemToken_DeveRetornar401Unauthorized() throws Exception {
        String jsonRequest = """
                {
                    "clienteId": 1,
                    "animalId": 1,
                    "servicoId": 1,
                    "dataHoraAgendamento": "2030-10-10T14:30:00"
                }
                """;

        mockMvc.perform(post("/api/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarAgendamento_ComTokenEEntidadesValidas_DeveCriarERetornar201Created() throws Exception {
        LocalDateTime dataHoraFutura = LocalDateTime.now().plusDays(5);
        String jsonRequest = String.format("""
                {
                    "clienteId": %d,
                    "animalId": %d,
                    "servicoId": %d,
                    "dataHoraAgendamento": "%s",
                    "observacoes": "Consulta de rotina para Pipoca"
                }
                """, seededCliente.getId(), testAnimal.getId(), testServico.getId(), dataHoraFutura);

        mockMvc.perform(post("/api/agendamentos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cliente.id").value(seededCliente.getId()))
                .andExpect(jsonPath("$.animal.id").value(testAnimal.getId()))
                .andExpect(jsonPath("$.servico.id").value(testServico.getId()))
                .andExpect(jsonPath("$.status").value("AGENDADO"));
    }

    @Test
    void criarAgendamento_ComAnimalNaoPertencenteAoCliente_DeveRetornar400BadRequest() throws Exception {
        Cliente outroCliente = new Cliente();
        outroCliente.setNome("Outro Cliente");
        outroCliente.setCpf("99999999999");
        outroCliente.setDataNascimento(LocalDate.of(1995, 1, 1));
        outroCliente.setTelefone("11977777777");
        outroCliente.setEmail("outro@email.com");
        outroCliente.setDataCadastro(LocalDate.now());
        outroCliente = clienteRepository.save(outroCliente);

        testAnimal.setCliente(outroCliente);
        animalRepository.save(testAnimal);

        LocalDateTime dataHoraFutura = LocalDateTime.now().plusDays(5);
        String jsonRequest = String.format("""
                {
                    "clienteId": %d,
                    "animalId": %d,
                    "servicoId": %d,
                    "dataHoraAgendamento": "%s"
                }
                """, seededCliente.getId(), testAnimal.getId(), testServico.getId(), dataHoraFutura);

        mockMvc.perform(post("/api/agendamentos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Regra de Negócio Violada"))
                .andExpect(jsonPath("$.detail").value("O animal informado não pertence ao cliente especificado."));
    }

    @Test
    void listarAgendamentos_DeveRetornarListaPaginada200Ok() throws Exception {
        mockMvc.perform(get("/api/agendamentos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == " + testAgendamento.getId() + ")]").exists());
    }

    @Test
    void buscarAgendamentoPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/agendamentos/" + testAgendamento.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAgendamento.getId()))
                .andExpect(jsonPath("$.cliente.id").value(seededCliente.getId()))
                .andExpect(jsonPath("$.animal.id").value(testAnimal.getId()));
    }

    @Test
    void buscarAgendamentoPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/agendamentos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarAgendamento_ComIdExistente_DeveRetornar200ComStatusCancelado() throws Exception {
        mockMvc.perform(patch("/api/agendamentos/" + testAgendamento.getId() + "/cancelar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAgendamento.getId()))
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    void cancelarAgendamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(patch("/api/agendamentos/999999/cancelar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmarAgendamento_ComIdExistente_DeveRetornar200ComStatusConfirmado() throws Exception {
        mockMvc.perform(patch("/api/agendamentos/" + testAgendamento.getId() + "/confirmar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAgendamento.getId()))
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    void confirmarAgendamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(patch("/api/agendamentos/999999/confirmar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarAgendamento_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(7);
        String jsonRequest = String.format("""
                {
                    "clienteId": %d,
                    "animalId": %d,
                    "servicoId": %d,
                    "dataHoraAgendamento": "%s",
                    "observacoes": "Observação atualizada"
                }
                """, seededCliente.getId(), testAnimal.getId(), testServico.getId(), novaDataHora);

        mockMvc.perform(put("/api/agendamentos/" + testAgendamento.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAgendamento.getId()))
                .andExpect(jsonPath("$.observacoes").value("Observação atualizada"));
    }

    @Test
    void atualizarAgendamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(7);
        String jsonRequest = String.format("""
                {
                    "clienteId": %d,
                    "animalId": %d,
                    "servicoId": %d,
                    "dataHoraAgendamento": "%s"
                }
                """, seededCliente.getId(), testAnimal.getId(), testServico.getId(), novaDataHora);

        mockMvc.perform(put("/api/agendamentos/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarAgendamento_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        Long id = testAgendamento.getId();

        mockMvc.perform(delete("/api/agendamentos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(agendamentoRepository.findById(id).isPresent());
    }

    @Test
    void deletarAgendamento_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/agendamentos/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
