package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InternacaoTest {

    @Test
    void darAlta_InternacaoAtiva_DeveDefinirStatusEDataSaida() {
        Internacao internacao = new Internacao();
        internacao.setDataEntrada(LocalDateTime.now().minusDays(3));
        assertEquals(EnumInternacaoStatus.ATIVA, internacao.getStatus());
        assertNull(internacao.getDataSaida());

        internacao.darAlta();

        assertEquals(EnumInternacaoStatus.ALTA, internacao.getStatus());
        assertNotNull(internacao.getDataSaida());
        // A data de saída deve ser recente (dentro de 1 segundo)
        assertTrue(internacao.getDataSaida().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    void darAlta_InternacaoJaComAlta_DeveLancarExcecao() {
        Internacao internacao = new Internacao();
        internacao.darAlta(); // Primeiro alta

        assertThrows(BusinessRuleException.class, internacao::darAlta);
    }

    @Test
    void adicionarDiaria_InternacaoAtiva_DeveAdicionarComSucesso() {
        Internacao internacao = new Internacao();
        DiariaInternacao diaria = new DiariaInternacao();
        diaria.setId(1L);

        internacao.adicionarDiaria(diaria);

        assertEquals(1, internacao.getDiarias().size());
        assertTrue(internacao.getDiarias().contains(diaria));
        assertEquals(internacao, diaria.getInternacao());
    }

    @Test
    void adicionarDiaria_InternacaoComAlta_DeveLancarExcecao() {
        Internacao internacao = new Internacao();
        internacao.darAlta();

        DiariaInternacao diaria = new DiariaInternacao();

        assertThrows(BusinessRuleException.class, () -> {
            internacao.adicionarDiaria(diaria);
        });
        
        assertTrue(internacao.getDiarias().isEmpty());
    }
}
