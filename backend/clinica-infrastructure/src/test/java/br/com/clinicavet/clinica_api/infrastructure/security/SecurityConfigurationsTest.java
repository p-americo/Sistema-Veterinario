package br.com.clinicavet.clinica_api.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationsTest {

    @Mock
    private SecurityFilter securityFilter;

    @Mock
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private SecurityConfigurations securityConfigurations;

    @BeforeEach
    void setUp() {
        securityConfigurations = new SecurityConfigurations(securityFilter, customAuthenticationEntryPoint);
    }

    @Test
    void passwordEncoder_RetornaBCrypt() {
        PasswordEncoder encoder = securityConfigurations.passwordEncoder();

        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);

        String hash = encoder.encode("Senha@123");
        assertNotEquals("Senha@123", hash);
        assertTrue(encoder.matches("Senha@123", hash));
        assertFalse(encoder.matches("SenhaErrada", hash));
    }
}
