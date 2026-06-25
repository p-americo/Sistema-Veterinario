package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.infrastructure.security.TokenService;
import br.com.clinicavet.clinica_api.testsupport.DataSeederFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
public class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Disparador de requisições HTTP para os testes

    @Autowired
    private TokenService tokenService; // Gerador de tokens JWT do projeto

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositório para buscar usuários do seeder

    private String adminToken; // Guardará o token do administrador

    @BeforeEach
    void setUp() {
        // Antes de cada teste de cliente, pegamos o Admin padrão e geramos seu token de acesso
        Usuario adminUser = usuarioRepository.findByLogin(DataSeederFixtures.ADMIN_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Admin do DataSeeder não encontrado"));

        adminToken = tokenService.gerarToken(adminUser);
    }
}