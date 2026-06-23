package br.com.clinicavet.clinica_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void construtorComMensagem_DeveArmazenarMensagem() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

        assertEquals("Recurso não encontrado", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void construtorComMensagemECausa_DeveArmazenarAmbos() {
        Throwable causa = new IllegalStateException("Causa raiz");

        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado", causa);

        assertEquals("Recurso não encontrado", exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void deveSerUmaRuntimeException() {
        assertTrue(new ResourceNotFoundException("erro") instanceof RuntimeException);
    }
}
