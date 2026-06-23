package br.com.clinicavet.clinica_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProntuarioTest {

    @Test
    void adicionarRegistro_ComRegistroNulo_DeveLancarExcecao() {
        Prontuario prontuario = new Prontuario();

        assertThrows(IllegalArgumentException.class, () -> prontuario.adicionarRegistro(null));
    }

    @Test
    void adicionarRegistro_ComRegistroValido_DeveAssociarBidirecionalmente() {
        Prontuario prontuario = new Prontuario();
        RegistroProntuario registro = new RegistroProntuario();

        prontuario.adicionarRegistro(registro);

        assertEquals(prontuario, registro.getProntuario());
        assertEquals(1, prontuario.getRegistros().size());
        assertTrue(prontuario.getRegistros().contains(registro));
    }

    @Test
    void adicionarRegistro_ComMultiplosRegistros_DeveAdicionarTodosNaLista() {
        Prontuario prontuario = new Prontuario();
        RegistroProntuario registro1 = new RegistroProntuario();
        RegistroProntuario registro2 = new RegistroProntuario();

        prontuario.adicionarRegistro(registro1);
        prontuario.adicionarRegistro(registro2);

        assertEquals(2, prontuario.getRegistros().size());
    }
}
