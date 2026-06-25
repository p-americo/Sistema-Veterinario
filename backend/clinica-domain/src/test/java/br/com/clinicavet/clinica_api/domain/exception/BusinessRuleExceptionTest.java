package br.com.clinicavet.clinica_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRuleExceptionTest {

    @Test
    void construtorComMensagem_DeveArmazenarMensagem() {
        BusinessRuleException exception = new BusinessRuleException("Regra de negócio violada");

        assertEquals("Regra de negócio violada", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void construtorComMensagemECausa_DeveArmazenarAmbos() {
        Throwable causa = new IllegalStateException("Causa raiz");

        BusinessRuleException exception = new BusinessRuleException("Regra de negócio violada", causa);

        assertEquals("Regra de negócio violada", exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void deveSerUmaRuntimeException() {
        assertTrue(new BusinessRuleException("erro") instanceof RuntimeException);
    }
}
