package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InternacaoServiceImplementTest {

    @Mock
    private InternacaoRepository repository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private InternacaoServiceImplement internacaoService;

    private Animal animal;
    private Internacao internacao;
    private InternacaoRequestDTO requestDTO;
    private InternacaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(1L);
        animal.setNome("Rex");

        internacao = new Internacao();
        internacao.setId(10L);
        internacao.setAnimal(animal);
        internacao.setDataEntrada(LocalDateTime.of(2026, 6, 1, 10, 0));

        requestDTO = new InternacaoRequestDTO();
        requestDTO.setAnimalId(1L);
        requestDTO.setDataEntrada(LocalDateTime.of(2026, 6, 1, 10, 0));

        responseDTO = new InternacaoResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setDataEntrada(LocalDateTime.of(2026, 6, 1, 10, 0));
        responseDTO.setStatus(EnumInternacaoStatus.ATIVA);
    }

    @Test
    void buscarPorId_Sucesso() {
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNomeAnimal());
        assertEquals(1L, resultado.getAnimalId());
        assertNotNull(resultado.getDiarias());
        assertTrue(resultado.getDiarias().isEmpty());
        verify(repository, times(1)).findById(10L);
    }

    @Test
    void buscarPorId_ExceptionNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.buscarPorId(99L);
        });

        assertEquals("Internação não encontrada com o ID: 99", exception.getMessage());
    }

    @Test
    void listarTodos_Sucesso() {
        when(repository.findAll()).thenReturn(Collections.singletonList(internacao));
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        List<InternacaoResponseDTO> resultado = internacaoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Rex", resultado.get(0).getNomeAnimal());
        verify(repository, times(1)).findAll();
    }

    @Test
    void criar_Sucesso() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(repository.save(any(Internacao.class))).thenReturn(internacao);
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNomeAnimal());
        verify(animalRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Internacao.class));
    }

    @Test
    void criar_ExceptionAnimalNaoEncontrado() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.criar(requestDTO);
        });

        assertEquals("Animal não encontrado com o ID: 1", exception.getMessage());
        verify(repository, never()).save(any(Internacao.class));
    }

    @Test
    void buscarInternacaoAtivaPorAnimalId_Sucesso() {
        when(repository.findByAnimalIdAndStatus(1L, EnumInternacaoStatus.ATIVA))
                .thenReturn(Collections.singletonList(internacao));
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.buscarInternacaoAtivaPorAnimalId(1L);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNomeAnimal());
        verify(repository, times(1)).findByAnimalIdAndStatus(1L, EnumInternacaoStatus.ATIVA);
    }

    @Test
    void buscarInternacaoAtivaPorAnimalId_ExceptionNenhumaAtiva() {
        when(repository.findByAnimalIdAndStatus(1L, EnumInternacaoStatus.ATIVA))
                .thenReturn(Collections.emptyList());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.buscarInternacaoAtivaPorAnimalId(1L);
        });

        assertEquals("Nenhuma internação ativa encontrada para o animal ID: 1", exception.getMessage());
    }

    @Test
    void darAltaInternacao_Sucesso() {
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(repository.save(internacao)).thenReturn(internacao);
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.darAltaInternacao(10L);

        assertNotNull(resultado);
        assertEquals(EnumInternacaoStatus.ALTA, internacao.getStatus());
        assertNotNull(internacao.getDataSaida());
        verify(repository, times(1)).save(internacao);
    }

    @Test
    void darAltaInternacao_ExceptionNaoEncontrada() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.darAltaInternacao(10L);
        });

        assertEquals("Internação não encontrada", exception.getMessage());
        verify(repository, never()).save(any(Internacao.class));
    }

    @Test
    void darAltaInternacao_ExceptionJaTemAlta() {
        internacao.darAlta();
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            internacaoService.darAltaInternacao(10L);
        });

        assertEquals("Esta internação já recebeu alta.", exception.getMessage());
        verify(repository, never()).save(any(Internacao.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(repository.save(internacao)).thenReturn(internacao);
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.atualizar(10L, requestDTO);

        assertNotNull(resultado);
        verify(mapper, times(1)).map(requestDTO, internacao);
        verify(repository, times(1)).save(internacao);
    }

    @Test
    void atualizar_ExceptionInternacaoNaoEncontrada() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.atualizar(10L, requestDTO);
        });

        assertEquals("Internação não encontrada", exception.getMessage());
        verify(repository, never()).save(any(Internacao.class));
    }

    @Test
    void atualizar_ExceptionAnimalNaoEncontrado() {
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            internacaoService.atualizar(10L, requestDTO);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(repository, never()).save(any(Internacao.class));
    }

    @Test
    void buscarPorId_SucessoComAnimalNulo() {
        internacao.setAnimal(null);
        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertNull(resultado.getNomeAnimal());
        assertNull(resultado.getAnimalId());
    }

    @Test
    void buscarPorId_SucessoComDiariasNulas() {
        Internacao internacaoSemDiarias = new Internacao(10L, animal, LocalDateTime.of(2026, 6, 1, 10, 0), null, EnumInternacaoStatus.ATIVA, null);
        when(repository.findById(10L)).thenReturn(Optional.of(internacaoSemDiarias));
        when(mapper.map(internacaoSemDiarias, InternacaoResponseDTO.class)).thenReturn(responseDTO);

        InternacaoResponseDTO resultado = internacaoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertNotNull(resultado.getDiarias());
        assertTrue(resultado.getDiarias().isEmpty());
    }

    @Test
    void buscarPorId_SucessoComDiariasEMedicamentos() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(50L);
        Produto produto = new Produto();
        produto.setId(60L);
        produto.setNome("Dipirona");
        produto.inicializarEstoque(50);
        medicamento.setProduto(produto);

        Funcionario funcionarioExecutor = new Funcionario();
        funcionarioExecutor.setId(70L);
        funcionarioExecutor.setNome("Dra. Ana");

        AdministracaoMedicamento administracaoCompleta = new AdministracaoMedicamento();
        administracaoCompleta.setId(80L);
        administracaoCompleta.registrarAdministracao(medicamento, funcionarioExecutor, BigDecimal.valueOf(2), LocalDateTime.now(), "10mg");

        AdministracaoMedicamento administracaoSemAssociacoes =
                new AdministracaoMedicamento(81L, null, null, null, BigDecimal.valueOf(1), LocalDateTime.now(), "5mg", null);

        Medicamento medicamentoSemProduto = new Medicamento();
        medicamentoSemProduto.setId(51L);
        AdministracaoMedicamento administracaoComMedSemProduto = new AdministracaoMedicamento();
        administracaoComMedSemProduto.setId(82L);
        administracaoComMedSemProduto.registrarAdministracao(medicamentoSemProduto, funcionarioExecutor, BigDecimal.valueOf(1), LocalDateTime.now(), "5mg");

        DiariaInternacao diariaComMedicamentos = new DiariaInternacao();
        diariaComMedicamentos.setId(90L);
        diariaComMedicamentos.getMedicamentos().add(administracaoCompleta);
        diariaComMedicamentos.getMedicamentos().add(administracaoSemAssociacoes);
        diariaComMedicamentos.getMedicamentos().add(administracaoComMedSemProduto);

        DiariaInternacao diariaSemMedicamentos = new DiariaInternacao();
        diariaSemMedicamentos.setId(91L);
        diariaSemMedicamentos.setMedicamentos(null);

        internacao.adicionarDiaria(diariaComMedicamentos);
        internacao.adicionarDiaria(diariaSemMedicamentos);

        when(repository.findById(10L)).thenReturn(Optional.of(internacao));
        when(mapper.map(internacao, InternacaoResponseDTO.class)).thenReturn(responseDTO);
        when(mapper.map(any(DiariaInternacao.class), eq(DiariaResponseDTO.class))).thenAnswer(invocation -> new DiariaResponseDTO());

        InternacaoResponseDTO resultado = internacaoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(2, resultado.getDiarias().size());

        DiariaResponseDTO comMedicamentos = resultado.getDiarias().stream()
                .filter(d -> d.getMedicamentos() != null && !d.getMedicamentos().isEmpty())
                .findFirst().orElseThrow();
        assertEquals(3, comMedicamentos.getMedicamentos().size());

        AdministracaoMedicamentoResponseDTO completaDTO = comMedicamentos.getMedicamentos().stream()
                .filter(m -> "Dipirona".equals(m.getNomeMedicamento()))
                .findFirst().orElseThrow();
        assertEquals(70L, completaDTO.getFuncionarioExecutorId());
        assertEquals("Dra. Ana", completaDTO.getNomeFuncionarioExecutor());

        AdministracaoMedicamentoResponseDTO medSemProdutoDTO = comMedicamentos.getMedicamentos().stream()
                .filter(m -> medicamentoSemProduto.getId().equals(m.getMedicamentoId()))
                .findFirst().orElseThrow();
        assertNull(medSemProdutoDTO.getNomeMedicamento());

        AdministracaoMedicamentoResponseDTO semAssociacoesDTO = comMedicamentos.getMedicamentos().stream()
                .filter(m -> m.getMedicamentoId() == null)
                .findFirst().orElseThrow();
        assertNull(semAssociacoesDTO.getMedicamentoId());
        assertNull(semAssociacoesDTO.getFuncionarioExecutorId());

        DiariaResponseDTO semMedicamentos = resultado.getDiarias().stream()
                .filter(d -> d.getMedicamentos() == null || d.getMedicamentos().isEmpty())
                .findFirst().orElseThrow();
        assertNotNull(semMedicamentos.getMedicamentos());
        assertTrue(semMedicamentos.getMedicamentos().isEmpty());
    }
}
