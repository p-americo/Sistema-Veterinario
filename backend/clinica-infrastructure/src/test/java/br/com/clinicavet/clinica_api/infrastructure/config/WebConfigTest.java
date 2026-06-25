package br.com.clinicavet.clinica_api.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    @Test
    void addCorsMappings_ConfiguraOrigemMetodosECredenciais() throws Exception {
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = new CorsRegistry();

        webConfig.addCorsMappings(registry);

        Method getCorsConfigurations = CorsRegistry.class.getDeclaredMethod("getCorsConfigurations");
        getCorsConfigurations.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, CorsConfiguration> configuracoes = (Map<String, CorsConfiguration>) getCorsConfigurations.invoke(registry);
        assertTrue(configuracoes.containsKey("/**"));

        CorsConfiguration configuracao = configuracoes.get("/**");
        assertEquals(java.util.List.of("http://localhost:4200"), configuracao.getAllowedOrigins());
        assertTrue(configuracao.getAllowedMethods().containsAll(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")));
        assertEquals(java.util.List.of("*"), configuracao.getAllowedHeaders());
        assertTrue(configuracao.getAllowCredentials());
    }
}
