package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.*;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.*;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private String adminToken;
    private Cliente seededCliente;
    private Funcionario seededVeterinario;
    private Animal testAnimal;
    private Servico testServico;

    @BeforeEach
    void setUp() {
        // Obter usuário administrador do DataSeeder para gerar token
        Usuario adminUser = usuarioRepository.findByLogin("12345678900")
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);

        // Obter cliente do DataSeeder
        Usuario clienteUser = usuarioRepository.findByLogin("11111111111")
                .orElseThrow(() -> new IllegalStateException("Cliente do DataSeeder não encontrado"));
        seededCliente = (Cliente) clienteUser.getPessoa();

        // Obter funcionário do DataSeeder
        seededVeterinario = (Funcionario) adminUser.getPessoa();

        // Cadastrar um animal pertencente ao cliente de teste
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

        // Cadastrar um serviço
        Servico servico = new Servico();
        servico.setTipo(EnumServico.CONSULTA);
        servico.setVeterinario(seededVeterinario);
        servico.setValor(new BigDecimal("150.00"));
        testServico = servicoRepository.save(servico);
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
        // Criar outro cliente no banco
        Cliente outroCliente = new Cliente();
        outroCliente.setNome("Outro Cliente");
        outroCliente.setCpf("99999999999");
        outroCliente.setDataNascimento(LocalDate.of(1995, 1, 1));
        outroCliente.setTelefone("11977777777");
        outroCliente.setEmail("outro@email.com");
        outroCliente.setDataCadastro(LocalDate.now());
        outroCliente = clienteRepository.save(outroCliente);

        // Associar Pipoca a outroCliente
        testAnimal.setCliente(outroCliente);
        animalRepository.save(testAnimal);

        // Tentar criar agendamento associando o cliente original (seededCliente) a Pipoca (que agora é do outroCliente)
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
}
