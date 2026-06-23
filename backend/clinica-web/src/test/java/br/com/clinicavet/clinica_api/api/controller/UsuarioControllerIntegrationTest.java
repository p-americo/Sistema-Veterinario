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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Usuario adminUser;

    @BeforeEach
    void setUp() {
        adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));
        adminToken = tokenService.gerarToken(adminUser);
    }

    @Test
    void listarTodosUsuarios_SemToken_DeveRetornar401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarTodosUsuarios_ComToken_DeveRetornarUsuariosSeedadosPaginados() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.content[?(@.login == '" + DataSeederFixtures.ADMIN_LOGIN + "')]").exists())
                .andExpect(jsonPath("$.content[?(@.login == '" + DataSeederFixtures.CLIENTE_LOGIN + "')]").exists());
    }

    @Test
    void atualizarSenha_SemToken_DeveRetornar401Unauthorized() throws Exception {
        String jsonRequest = String.format("""
                {
                    "senhaAtual": "%s",
                    "novaSenha": "NovaSenha456!",
                    "confirmarNovaSenha": "NovaSenha456!"
                }
                """, DataSeederFixtures.ADMIN_SENHA);

        mockMvc.perform(put("/api/usuarios/{id}/atualizar-senha", adminUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void atualizarSenha_ComDadosValidos_DeveRetornar200EAlterarSenha() throws Exception {
        String jsonRequest = String.format("""
                {
                    "senhaAtual": "%s",
                    "novaSenha": "NovaSenha456!",
                    "confirmarNovaSenha": "NovaSenha456!"
                }
                """, DataSeederFixtures.ADMIN_SENHA);

        mockMvc.perform(put("/api/usuarios/{id}/atualizar-senha", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha atualizada com sucesso."));

        Usuario usuarioAtualizado = usuarioRepository.findById(adminUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("NovaSenha456!", usuarioAtualizado.getSenha())).isTrue();
    }

    @Test
    void atualizarSenha_ComSenhaAtualIncorreta_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = """
                {
                    "senhaAtual": "SenhaErrada123!",
                    "novaSenha": "NovaSenha456!",
                    "confirmarNovaSenha": "NovaSenha456!"
                }
                """;

        mockMvc.perform(put("/api/usuarios/{id}/atualizar-senha", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Senha atual incorreta."));
    }

    @Test
    void atualizarSenha_ComNovaSenhaEConfirmacaoDiferentes_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = String.format("""
                {
                    "senhaAtual": "%s",
                    "novaSenha": "NovaSenha456!",
                    "confirmarNovaSenha": "OutraSenha789!"
                }
                """, DataSeederFixtures.ADMIN_SENHA);

        mockMvc.perform(put("/api/usuarios/{id}/atualizar-senha", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nova senha e confirmação não conferem."));
    }

    @Test
    void atualizarSenha_ComUsuarioInexistente_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = String.format("""
                {
                    "senhaAtual": "%s",
                    "novaSenha": "NovaSenha456!",
                    "confirmarNovaSenha": "NovaSenha456!"
                }
                """, DataSeederFixtures.ADMIN_SENHA);

        mockMvc.perform(put("/api/usuarios/{id}/atualizar-senha", 999999)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado."));
    }
}
