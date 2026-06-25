package br.com.clinicavet.clinica_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistroProntuarioTest {

    @Test
    void associarAoProntuario_ComProntuarioNulo_DeveLancarExcecao() {
        RegistroProntuario registro = new RegistroProntuario();

        assertThrows(IllegalArgumentException.class, () -> registro.associarAoProntuario(null));
    }

    @Test
    void associarAoProntuario_ComProntuarioValido_DeveAssociarBidirecionalmente() {
        Prontuario prontuario = new Prontuario();
        RegistroProntuario registro = new RegistroProntuario();

        registro.associarAoProntuario(prontuario);

        assertEquals(prontuario, registro.getProntuario());
        assertTrue(prontuario.getRegistros().contains(registro));
    }
}
