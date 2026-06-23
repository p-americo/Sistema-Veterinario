package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DiariaMapperTest {

    @Test
    void toEntity_ComDtoNulo_DeveRetornarNulo() {
        assertNull(DiariaMapper.toEntity(null));
    }

    @Test
    void toEntity_ComDtoValido_DeveMapearCampos() {
        DiariaRequestDTO dto = new DiariaRequestDTO();
        dto.setDataHora(LocalDateTime.of(2026, 6, 23, 10, 0));
        dto.setDiagnostico("Em recuperação");
        dto.setObservacoesClinicas("Animal estável");
        dto.setPesoNoDia(new BigDecimal("15.5"));

        DiariaInternacao entity = DiariaMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getDataHora(), entity.getDataHora());
        assertEquals(dto.getDiagnostico(), entity.getDiagnostico());
        assertEquals(dto.getObservacoesClinicas(), entity.getObservacoesClinicas());
        assertEquals(dto.getPesoNoDia(), entity.getPesoNoDia());
    }

    @Test
    void toResponseDTO_ComEntidadeNula_DeveRetornarNulo() {
        assertNull(DiariaMapper.toResponseDTO(null));
    }

    @Test
    void toResponseDTO_ComEntidadeSemMedicamentos_DeveRetornarListaVaziaDeMedicamentos() {
        DiariaInternacao entity = new DiariaInternacao();
        entity.setId(1L);
        entity.setDataHora(LocalDateTime.of(2026, 6, 23, 10, 0));
        entity.setDiagnostico("Em recuperação");
        entity.setObservacoesClinicas("Animal estável");
        entity.setPesoNoDia(new BigDecimal("15.5"));

        DiariaResponseDTO dto = DiariaMapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getDataHora(), dto.getDataHora());
        assertEquals(entity.getDiagnostico(), dto.getDiagnostico());
        assertNotNull(dto.getMedicamentos());
        assertTrue(dto.getMedicamentos().isEmpty());
    }
}
