package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private AnimalRepository animalRepository;

    private String adminToken;
    private Cliente seededCliente;
    private Animal testAnimal;

    @BeforeEach
    void setUp() {
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN).orElseThrow();
        adminToken = tokenService.gerarToken(adminUser);

        Usuario clienteUser = usuarioRepository.findByLogin(DataSeederFixtures.CLIENTE_LOGIN).orElseThrow();
        seededCliente = (Cliente) clienteUser.getPessoa();

        Animal animal = new Animal();
        animal.setNome("Rex");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.GRANDE);
        animal.setRaca("Pastor Alemão");
        animal.setSexo(EnumSexo.MACHO);
        animal.setPeso(32.0);
        animal.setCastrado(false);
        animal.setDataNascimento(LocalDate.of(2019, 5, 10));
        animal.setCliente(seededCliente);
        testAnimal = animalRepository.save(animal);
    }

    @Test
    void criarAnimal_ComImagemEMultipart_DeveRetornar201Created() throws Exception {
        String jsonDados = String.format("""
                {
                    "nome": "Thor",
                    "especie": "CANINO",
                    "porte": "GRANDE",
                    "raca": "Rottweiler",
                    "sexo": "MACHO",
                    "peso": 42.5,
                    "castrado": false,
                    "dataNascimento": "2020-01-10",
                    "clienteId": %d
                }
                """, seededCliente.getId());

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

    @Test
    void buscarAnimalPorId_ComIdExistente_DeveRetornar200Ok() throws Exception {
        mockMvc.perform(get("/api/animais/" + testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAnimal.getId()))
                .andExpect(jsonPath("$.nome").value("Rex"))
                .andExpect(jsonPath("$.especie").value("CANINO"))
                .andExpect(jsonPath("$.cliente.id").value(seededCliente.getId()));
    }

    @Test
    void buscarAnimalPorId_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/animais/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImagemDoAnimal_ComAnimalSemImagem_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(get("/api/animais/imagem/" + testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImagemDoAnimal_ComAnimalComImagem_DeveRetornar200ComConteudoJpeg() throws Exception {
        testAnimal.setImagem(new byte[]{1, 2, 3, 4});
        animalRepository.save(testAnimal);

        mockMvc.perform(get("/api/animais/imagem/" + testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentType = result.getResponse().getContentType();
                    assert contentType != null && contentType.contains("image/jpeg");
                });
    }

    @Test
    void atualizarAnimal_ComDadosValidos_DeveRetornar200Ok() throws Exception {
        String jsonRequest = String.format("""
                {
                    "nome": "Rex Atualizado",
                    "especie": "CANINO",
                    "porte": "GRANDE",
                    "raca": "Pastor Alemão",
                    "sexo": "MACHO",
                    "peso": 35.0,
                    "castrado": true,
                    "dataNascimento": "2019-05-10",
                    "clienteId": %d
                }
                """, seededCliente.getId());

        mockMvc.perform(put("/api/animais/" + testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAnimal.getId()))
                .andExpect(jsonPath("$.nome").value("Rex Atualizado"))
                .andExpect(jsonPath("$.castrado").value(true));
    }

    @Test
    void atualizarAnimal_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        String jsonRequest = String.format("""
                {
                    "nome": "Inexistente",
                    "especie": "CANINO",
                    "porte": "PEQUENO",
                    "raca": "SRD",
                    "sexo": "FEMEA",
                    "peso": 5.0,
                    "castrado": true,
                    "dataNascimento": "2022-01-01",
                    "clienteId": %d
                }
                """, seededCliente.getId());

        mockMvc.perform(put("/api/animais/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarAnimal_ComIdExistente_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/animais/" + testAnimal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarAnimal_ComIdInexistente_DeveRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/api/animais/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
