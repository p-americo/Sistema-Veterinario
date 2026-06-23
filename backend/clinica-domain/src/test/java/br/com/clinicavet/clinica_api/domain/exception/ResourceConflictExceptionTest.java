package br.com.clinicavet.clinica_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceConflictExceptionTest {

    @Test
    void construtorComMensagem_DeveArmazenarMensagem() {
        ResourceConflictException exception = new ResourceConflictException("Conflito de recurso");

        assertEquals("Conflito de recurso", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void construtorComMensagemECausa_DeveArmazenarAmbos() {
        Throwable causa = new IllegalStateException("Causa raiz");

        ResourceConflictException exception = new ResourceConflictException("Conflito de recurso", causa);

        assertEquals("Conflito de recurso", exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void deveSerUmaRuntimeException() {
        assertTrue(new ResourceConflictException("erro") instanceof RuntimeException);
    }
}
