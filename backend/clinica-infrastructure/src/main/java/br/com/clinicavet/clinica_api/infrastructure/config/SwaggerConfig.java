package br.com.clinicavet.clinica_api.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearer-jwt";
        return new OpenAPI()
                .info(new Info()
                        .title("Clínica Veterinária API")
                        .description("Documentação da API da Clínica Veterinária. Utilize o endpoint de login para obter o token JWT e depois clique em Authorize para autenticá-lo.")
                        .version("v1")
                        .license(new License().name("MIT"))
                        .contact(new Contact().name("Equipe Clínica").email("contato@clinica.com")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Informe apenas o token JWT sem o prefixo 'Bearer ' (a UI adiciona automaticamente).")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
