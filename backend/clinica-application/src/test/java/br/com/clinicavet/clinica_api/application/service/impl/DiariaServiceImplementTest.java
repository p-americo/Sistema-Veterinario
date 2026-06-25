package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.ResourceNotFoundException;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DiariaServiceImplementTest {

    @Mock
    private InternacaoRepository internacaoRepository;

    @InjectMocks
    private DiariaServiceImplement diariaService;

    private Internacao internacao;
    private DiariaInternacao diaria;
    private DiariaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        internacao = new Internacao();
        internacao.setId(1L);

        diaria = new DiariaInternacao();
        diaria.setId(10L);
        diaria.setDataHora(LocalDateTime.now());
        diaria.setDiagnostico("Estável");
        diaria.setObservacoesClinicas("Sem alterações");
        diaria.setPesoNoDia(BigDecimal.valueOf(12.5));
        diaria.setInternacao(internacao);
        internacao.getDiarias().add(diaria);

        requestDTO = new DiariaRequestDTO();
        requestDTO.setInternacaoId(1L);
        requestDTO.setDataHora(LocalDateTime.now());
        requestDTO.setPesoNoDia(BigDecimal.valueOf(13.0));
        requestDTO.setObservacoesClinicas("Melhorou o apetite");
        requestDTO.setDiagnostico("Em recuperação");
    }

    @Test
    void criar_Sucesso() {
        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));
        when(internacaoRepository.save(internacao)).thenReturn(internacao);

        DiariaResponseDTO resultado = diariaService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Em recuperação", resultado.getDiagnostico());
        assertEquals("Melhorou o apetite", resultado.getObservacoesClinicas());
        verify(internacaoRepository, times(1)).save(internacao);
    }

    @Test
    void criar_ExceptionInternacaoNaoEncontrada() {
        when(internacaoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            diariaService.criar(requestDTO);
        });

        assertEquals("Internação não encontrada com o ID: 1", exception.getMessage());
        verify(internacaoRepository, never()).save(any(Internacao.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.of(diaria));
        when(internacaoRepository.save(internacao)).thenReturn(internacao);

        DiariaResponseDTO resultado = diariaService.atualizar(10L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Em recuperação", resultado.getDiagnostico());
        assertEquals(BigDecimal.valueOf(13.0), diaria.getPesoNoDia());
        verify(internacaoRepository, times(1)).save(internacao);
    }

    @Test
    void atualizar_ExceptionDiariaNaoEncontrada() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            diariaService.atualizar(10L, requestDTO);
        });

        assertEquals("Diária não encontrada com o ID: 10", exception.getMessage());
        verify(internacaoRepository, never()).save(any(Internacao.class));
    }

    @Test
    void buscarPorId_Sucesso() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.of(diaria));

        DiariaResponseDTO resultado = diariaService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Estável", resultado.getDiagnostico());
        verify(internacaoRepository, times(1)).findDiariaById(10L);
    }

    @Test
    void buscarPorId_ExceptionNaoEncontrada() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            diariaService.buscarPorId(10L);
        });

        assertEquals("Diária não encontrada com o ID: 10", exception.getMessage());
    }

    @Test
    void listarTodos_Sucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DiariaInternacao> page = new PageImpl<>(Collections.singletonList(diaria), pageable, 1);
        when(internacaoRepository.findAllDiarias(pageable)).thenReturn(page);

        Page<DiariaResponseDTO> resultado = diariaService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Estável", resultado.getContent().get(0).getDiagnostico());
        verify(internacaoRepository, times(1)).findAllDiarias(pageable);
    }

    @Test
    void listarPorInternacao_Sucesso() {
        when(internacaoRepository.findDiariasByInternacaoId(1L)).thenReturn(Collections.singletonList(diaria));

        List<DiariaResponseDTO> resultado = diariaService.listarPorInternacao(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Estável", resultado.get(0).getDiagnostico());
        verify(internacaoRepository, times(1)).findDiariasByInternacaoId(1L);
    }

    @Test
    void deletar_Sucesso() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.of(diaria));
        when(internacaoRepository.save(internacao)).thenReturn(internacao);

        assertDoesNotThrow(() -> diariaService.deletar(10L));

        assertFalse(internacao.getDiarias().contains(diaria));
        assertNull(diaria.getInternacao());
        verify(internacaoRepository, times(1)).save(internacao);
    }

    @Test
    void deletar_ExceptionDiariaNaoEncontrada() {
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            diariaService.deletar(10L);
        });

        assertEquals("Diária não encontrada com o ID: 10", exception.getMessage());
        verify(internacaoRepository, never()).save(any(Internacao.class));
    }

    @Test
    void deletar_DiariaSemInternacao_NaoChamaSave() {
        diaria.setInternacao(null);
        when(internacaoRepository.findDiariaById(10L)).thenReturn(Optional.of(diaria));

        assertDoesNotThrow(() -> diariaService.deletar(10L));

        verify(internacaoRepository, never()).save(any(Internacao.class));
    }
}
