package br.com.clinicavet.clinica_api.domain.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Estes enums são persistidos via {@code @Enumerated(EnumType.STRING)}.
 * Renomear ou remover uma constante quebra silenciosamente dados já gravados no banco;
 */
class EnumsTest {

    @Test
    void enumEspecie_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumEspecie[]{EnumEspecie.CANINO, EnumEspecie.FELINO, EnumEspecie.EQUINO, EnumEspecie.BOVINO,
                        EnumEspecie.AVE, EnumEspecie.ROEDOR, EnumEspecie.REPTIL, EnumEspecie.OUTRO},
                EnumEspecie.values());
    }

    @Test
    void enumPorte_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumPorte[]{EnumPorte.PEQUENO, EnumPorte.MEDIO, EnumPorte.GRANDE},
                EnumPorte.values());
    }

    @Test
    void enumSexo_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumSexo[]{EnumSexo.MACHO, EnumSexo.FEMEA},
                EnumSexo.values());
    }

    @Test
    void enumAgendamento_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumAgendamento[]{EnumAgendamento.AGENDADO, EnumAgendamento.REALIZADO,
                        EnumAgendamento.CANCELADO, EnumAgendamento.CONFIRMADO},
                EnumAgendamento.values());
    }

    @Test
    void enumServico_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumServico[]{EnumServico.CONSULTA, EnumServico.VACINACAO, EnumServico.CIRURGIA,
                        EnumServico.EXAME_LABORATORIAL, EnumServico.BANHO_E_TOSA, EnumServico.INTERNACAO},
                EnumServico.values());
    }

    @Test
    void enumCargo_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumCargo[]{EnumCargo.VETERINARIO, EnumCargo.ORTOPEDISTA,
                        EnumCargo.ANESTEASIOLOGISTA, EnumCargo.RECEPCIONISTA},
                EnumCargo.values());
    }

    @Test
    void enumUsuarioRole_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumUsuarioRole[]{EnumUsuarioRole.ROLE_CLIENTE, EnumUsuarioRole.ROLE_VETERINARIO,
                        EnumUsuarioRole.ROLE_ADMIN},
                EnumUsuarioRole.values());
    }

    @Test
    void enumInternacaoStatus_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumInternacaoStatus[]{EnumInternacaoStatus.ATIVA, EnumInternacaoStatus.ALTA,
                        EnumInternacaoStatus.CANCELADA},
                EnumInternacaoStatus.values());
    }

    @Test
    void enumCategoriaMedicameno_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumCategoriaMedicameno[]{EnumCategoriaMedicameno.ANTIBIOTICO, EnumCategoriaMedicameno.ANALGESICO,
                        EnumCategoriaMedicameno.ANTI_INFLAMATORIO, EnumCategoriaMedicameno.VACINA,
                        EnumCategoriaMedicameno.VERMIFUGO, EnumCategoriaMedicameno.ANTIPULGAS_CARRAPATOS,
                        EnumCategoriaMedicameno.VITAMINA, EnumCategoriaMedicameno.OUTROS},
                EnumCategoriaMedicameno.values());
    }

    @Test
    void enumViaMedicamento_DeveConterValoresEsperados() {
        assertArrayEquals(
                new EnumViaMedicamento[]{EnumViaMedicamento.ORAL, EnumViaMedicamento.INJETAVEL,
                        EnumViaMedicamento.TOPICO, EnumViaMedicamento.OTOLOGICO,
                        EnumViaMedicamento.OFTALMICO, EnumViaMedicamento.INALATORIO},
                EnumViaMedicamento.values());
    }

    @Test
    void enumTipoProduto_AindaNaoPossuiConstantesDefinidas() {
        // Enum declarado sem nenhuma constante
        assertEquals(0, EnumTipoProduto.values().length);
    }

    @Test
    void valueOf_ComNomeInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> EnumEspecie.valueOf("INEXISTENTE"));
    }
}
