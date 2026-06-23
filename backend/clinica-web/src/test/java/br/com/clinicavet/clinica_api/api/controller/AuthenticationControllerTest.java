package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.testsupport.DataSeederFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_ComCredenciaisValidas_DeveRetornarTokenJWT() throws Exception {
        String jsonRequest = String.format("""
                {
                    "login": "%s",
                    "senha": "%s"
                }
                """, DataSeederFixtures.ADMIN_LOGIN, DataSeederFixtures.ADMIN_SENHA);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_ComCredenciaisInvalidas_DeveRetornar401Ou403() throws Exception {
        String jsonRequest = String.format("""
                {
                    "login": "%s",
                    "senha": "SenhaIncorreta"
                }
                """, DataSeederFixtures.ADMIN_LOGIN);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized()); // Spring Security standard for BadCredentialsException
    }

    @Test
    void login_ComDadosInvalidos_DeveRetornar400BadRequest() throws Exception {
        String jsonRequest = """
                {
                    "login": "",
                    "senha": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
