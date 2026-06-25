package br.com.clinicavet.clinica_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataIntegrityViolationExceptionTest {

    @Test
    void construtorComMensagem_DeveArmazenarMensagem() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Violação de integridade");

        assertEquals("Violação de integridade", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void deveSerUmaRuntimeException() {
        assertTrue(new DataIntegrityViolationException("erro") instanceof RuntimeException);
    }
}
