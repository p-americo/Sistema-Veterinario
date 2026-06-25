package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import br.com.clinicavet.clinica_api.testsupport.DataSeederFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class AnimalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);
    }

    @Test
    void criarAnimal_ComImagemEMultipart_DeveRetornar201Created() throws Exception {
        String jsonDados = """
                {
                    "nome": "Thor",
                    "especie": "CANINO",
                    "porte": "GRANDE",
                    "raca": "Rottweiler",
                    "sexo": "MACHO",
                    "peso": 42.5,
                    "castrado": false,
                    "dataNascimento": "2020-01-10",
                    "clienteId": 2
                }
                """;

        MockMultipartFile dadosPart = new MockMultipartFile("dados", "", "application/json", jsonDados.getBytes());
        MockMultipartFile imagemPart = new MockMultipartFile("imagem", "thor.jpg", "image/jpeg", new byte[]{1, 2, 3, 4});

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/animais")
                        .file(dadosPart)
                        .file(imagemPart)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Thor"));
    }

    @Test
    void listarTodos_DeveRetornarAnimaisPaginados200Ok() throws Exception {
        mockMvc.perform(get("/api/animais")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}