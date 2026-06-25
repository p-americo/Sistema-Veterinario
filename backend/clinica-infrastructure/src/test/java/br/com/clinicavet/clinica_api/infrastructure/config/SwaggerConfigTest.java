package br.com.clinicavet.clinica_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void customOpenAPI_ConfiguraInfoESegurancaBearerJwt() {
        OpenAPI openAPI = new SwaggerConfig().customOpenAPI();

        assertNotNull(openAPI);
        assertEquals("Clínica Veterinária API", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion());

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearer-jwt");
        assertNotNull(scheme);
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());

        assertNotNull(openAPI.getSecurity());
        assertEquals(1, openAPI.getSecurity().size());
        assertTrue(openAPI.getSecurity().get(0).containsKey("bearer-jwt"));
    }
}
