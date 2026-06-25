package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.domain.repository.AdministracaoMedicamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.domain.repository.MedicamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.RegistroProntuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdministracaoMedicamentoServiceImplementTest {

    @Mock
    private AdministracaoMedicamentoRepository administracaoMedicamentoRepository;

    @Mock
    private RegistroProntuarioRepository registroProntuarioRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private InternacaoRepository internacaoRepository;

    @Mock
    private ModelMapper modelMapper;

    private AdministracaoMedicamentoServiceImplement administracaoMedicamentoService;

    private AdministracaoMedicamento administracao;
    private Funcionario funcionarioExecutor;
    private Medicamento medicamento;
    private Produto produto;
    private RegistroProntuario entradaProntuario;
    private DiariaInternacao diaria;
    private AdministracaoMedicamentoRequestDTO requestDTO;
    private AdministracaoMedicamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        when(modelMapper.getConfiguration()).thenReturn(mock(Configuration.class));
        administracaoMedicamentoService = new AdministracaoMedicamentoServiceImplement(
                administracaoMedicamentoRepository, registroProntuarioRepository, funcionarioRepository,
                medicamentoRepository, internacaoRepository, modelMapper);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Amoxicilina");
        produto.inicializarEstoque(50);

        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setProduto(produto);
        medicamento.setCategoria(EnumCategoriaMedicameno.ANTIBIOTICO);

        funcionarioExecutor = new Funcionario();
        funcionarioExecutor.setId(10L);
        funcionarioExecutor.setNome("Dr. João");

        entradaProntuario = new RegistroProntuario();
        entradaProntuario.setId(100L);

        diaria = new DiariaInternacao();
        diaria.setId(200L);

        administracao = new AdministracaoMedicamento();
        administracao.setId(1L);
        administracao.registrarAdministracao(medicamento, funcionarioExecutor, BigDecimal.valueOf(5), LocalDateTime.now(), "500mg");
        administracao.associarAoProntuario(entradaProntuario);

        requestDTO = new AdministracaoMedicamentoRequestDTO();
        requestDTO.setMedicamentoId(1L);
        requestDTO.setEntradaProntuarioId(100L);
        requestDTO.setFuncionarioExecutorId(10L);
        requestDTO.setQuantidadeAdministrada(BigDecimal.valueOf(5));
        requestDTO.setDataHora(LocalDateTime.now());
        requestDTO.setDosagem("500mg");

        responseDTO = new AdministracaoMedicamentoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setMedicamentoId(1L);
        responseDTO.setEntradaProntuarioId(100L);
        responseDTO.setFuncionarioExecutorId(10L);
        responseDTO.setQuantidadeAdministrada(BigDecimal.valueOf(5));
    }

    @Test
    void criar_SucessoComEntradaProntuario() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(registroProntuarioRepository.findById(100L)).thenReturn(Optional.of(entradaProntuario));
        when(administracaoMedicamentoRepository.save(any(AdministracaoMedicamento.class))).thenReturn(administracao);
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        AdministracaoMedicamentoResponseDTO resultado = administracaoMedicamentoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getMedicamentoId());
        assertEquals("Amoxicilina", resultado.getNomeMedicamento());
        assertEquals(100L, resultado.getEntradaProntuarioId());
        assertEquals(10L, resultado.getFuncionarioExecutorId());
        assertEquals("Dr. João", resultado.getNomeFuncionarioExecutor());
        verify(administracaoMedicamentoRepository, times(1)).save(any(AdministracaoMedicamento.class));
        verify(internacaoRepository, never()).findDiariaById(any());
    }

    @Test
    void criar_SucessoComDiaria() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(200L);

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(internacaoRepository.findDiariaById(200L)).thenReturn(Optional.of(diaria));
        when(administracaoMedicamentoRepository.save(any(AdministracaoMedicamento.class))).thenReturn(administracao);
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        AdministracaoMedicamentoResponseDTO resultado = administracaoMedicamentoService.criar(requestDTO);

        assertNotNull(resultado);
        verify(registroProntuarioRepository, never()).findById(any());
        verify(administracaoMedicamentoRepository, times(1)).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void criar_ExceptionSemEntradaProntuarioEDiaria() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(null);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            administracaoMedicamentoService.criar(requestDTO);
        });

        assertEquals("É necessário informar ou o ID da entrada do prontuário ou o ID da diária.", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void criar_ExceptionFuncionarioExecutorNaoEncontrado() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.criar(requestDTO);
        });

        assertEquals("Funcionário executor não encontrado com o ID: 10", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void criar_ExceptionMedicamentoNaoEncontrado() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.criar(requestDTO);
        });

        assertEquals("Medicamento não encontrado com o ID: 1", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void criar_ExceptionEntradaProntuarioNaoEncontrada() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(registroProntuarioRepository.findById(100L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.criar(requestDTO);
        });

        assertEquals("Entrada do prontuário não encontrada com o ID: 100", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void criar_ExceptionDiariaNaoEncontrada() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(200L);

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(internacaoRepository.findDiariaById(200L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.criar(requestDTO);
        });

        assertEquals("Diária de internação não encontrada com o ID: 200", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(registroProntuarioRepository.findById(100L)).thenReturn(Optional.of(entradaProntuario));
        when(administracaoMedicamentoRepository.save(administracao)).thenReturn(administracao);
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        AdministracaoMedicamentoResponseDTO resultado = administracaoMedicamentoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getMedicamentoId());
        verify(administracaoMedicamentoRepository, times(1)).findById(1L);
        verify(administracaoMedicamentoRepository, times(1)).save(administracao);
    }

    @Test
    void atualizar_ExceptionSemEntradaProntuarioEDiaria() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(null);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("É necessário informar ou o ID da entrada do prontuário ou o ID da diária.", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_ExceptionAdministracaoNaoEncontrada() {
        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Administração de medicamento não encontrada com o ID: 1", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_ExceptionFuncionarioExecutorNaoEncontrado() {
        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Funcionário executor não encontrado com o ID: 10", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_ExceptionMedicamentoNaoEncontrado() {
        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Medicamento não encontrado com o ID: 1", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_ExceptionEntradaProntuarioNaoEncontrada() {
        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(registroProntuarioRepository.findById(100L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Entrada do prontuário não encontrada com o ID: 100", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void atualizar_ExceptionDiariaNaoEncontrada() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(200L);

        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(internacaoRepository.findDiariaById(200L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            administracaoMedicamentoService.atualizar(1L, requestDTO);
        });

        assertEquals("Diária de internação não encontrada com o ID: 200", exception.getMessage());
        verify(administracaoMedicamentoRepository, never()).save(any(AdministracaoMedicamento.class));
    }

    @Test
    void buscarPorEntradaProntuarioId_Sucesso() {
        when(administracaoMedicamentoRepository.findByEntradaProntuarioId(100L)).thenReturn(Collections.singletonList(administracao));
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorEntradaProntuarioId(100L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getEntradaProntuarioId());
        verify(administracaoMedicamentoRepository, times(1)).findByEntradaProntuarioId(100L);
    }

    @Test
    void buscarPorMedicamentoId_Sucesso() {
        when(administracaoMedicamentoRepository.findByMedicamentoId(1L)).thenReturn(Collections.singletonList(administracao));
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorMedicamentoId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getMedicamentoId());
        verify(administracaoMedicamentoRepository, times(1)).findByMedicamentoId(1L);
    }

    @Test
    void buscarPorFuncionarioId_Sucesso() {
        when(administracaoMedicamentoRepository.findByFuncionarioExecutorId(10L)).thenReturn(Collections.singletonList(administracao));
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorFuncionarioId(10L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getFuncionarioExecutorId());
        verify(administracaoMedicamentoRepository, times(1)).findByFuncionarioExecutorId(10L);
    }

    @Test
    void atualizar_SucessoComDiaria() {
        requestDTO.setEntradaProntuarioId(null);
        requestDTO.setDiariaId(200L);

        when(administracaoMedicamentoRepository.findById(1L)).thenReturn(Optional.of(administracao));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionarioExecutor));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(internacaoRepository.findDiariaById(200L)).thenReturn(Optional.of(diaria));
        when(administracaoMedicamentoRepository.save(administracao)).thenReturn(administracao);
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        AdministracaoMedicamentoResponseDTO resultado = administracaoMedicamentoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        verify(registroProntuarioRepository, never()).findById(any());
        verify(administracaoMedicamentoRepository, times(1)).save(administracao);
    }

    @Test
    void buscarPorMedicamentoId_ComDiariaAssociada_Sucesso() {
        administracao.associarDiaria(diaria);
        when(administracaoMedicamentoRepository.findByMedicamentoId(1L)).thenReturn(Collections.singletonList(administracao));
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorMedicamentoId(1L);

        assertNotNull(resultado);
        assertEquals(200L, resultado.get(0).getDiariaId());
    }

    @Test
    void buscarPorFuncionarioId_AdministracaoSemAssociacoes_Sucesso() {
        AdministracaoMedicamento administracaoMinima = new AdministracaoMedicamento(
                99L, null, null, null, BigDecimal.ONE, LocalDateTime.now(), "dose", null);

        when(administracaoMedicamentoRepository.findByFuncionarioExecutorId(10L))
                .thenReturn(Collections.singletonList(administracaoMinima));
        when(modelMapper.map(administracaoMinima, AdministracaoMedicamentoResponseDTO.class))
                .thenReturn(new AdministracaoMedicamentoResponseDTO());

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorFuncionarioId(10L);

        assertNotNull(resultado);
        assertNull(resultado.get(0).getNomeMedicamento());
        assertNull(resultado.get(0).getEntradaProntuarioId());
        assertNull(resultado.get(0).getDiariaId());
        assertNull(resultado.get(0).getFuncionarioExecutorId());
    }

    @Test
    void buscarPorEntradaProntuarioId_MedicamentoSemProduto_Sucesso() {
        Medicamento medicamentoSemProduto = new Medicamento();
        medicamentoSemProduto.setId(2L);

        AdministracaoMedicamento administracaoComMedSemProduto = new AdministracaoMedicamento();
        administracaoComMedSemProduto.setId(98L);
        administracaoComMedSemProduto.registrarAdministracao(medicamentoSemProduto, funcionarioExecutor, BigDecimal.ONE, LocalDateTime.now(), "dose");

        when(administracaoMedicamentoRepository.findByEntradaProntuarioId(100L))
                .thenReturn(Collections.singletonList(administracaoComMedSemProduto));
        when(modelMapper.map(administracaoComMedSemProduto, AdministracaoMedicamentoResponseDTO.class))
                .thenReturn(new AdministracaoMedicamentoResponseDTO());

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorEntradaProntuarioId(100L);

        assertNotNull(resultado);
        assertNull(resultado.get(0).getNomeMedicamento());
    }

    @Test
    void buscarPorPeriodo_Sucesso() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(administracaoMedicamentoRepository.findByDataHoraBetween(inicio, fim)).thenReturn(Collections.singletonList(administracao));
        when(modelMapper.map(administracao, AdministracaoMedicamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AdministracaoMedicamentoResponseDTO> resultado = administracaoMedicamentoService.buscarPorPeriodo(inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(administracaoMedicamentoRepository, times(1)).findByDataHoraBetween(inicio, fim);
    }
}
