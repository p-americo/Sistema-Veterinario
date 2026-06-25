package br.com.clinicavet.clinica_api.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    void commence_Sucesso() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/animais");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Token expirado"));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertEquals("UTF-8", response.getCharacterEncoding());

        ArgumentCaptor<ProblemDetail> captor = ArgumentCaptor.forClass(ProblemDetail.class);
        verify(objectMapper).writeValue(any(java.io.Writer.class), captor.capture());

        ProblemDetail problemDetail = captor.getValue();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals("Não Autorizado", problemDetail.getTitle());
        assertEquals("/api/animais", problemDetail.getInstance().toString());
        assertEquals("Token expirado", problemDetail.getProperties().get("error"));
        assertEquals("Acesso negado: credenciais JWT ausentes, inválidas ou expiradas.", problemDetail.getDetail());
    }
}
