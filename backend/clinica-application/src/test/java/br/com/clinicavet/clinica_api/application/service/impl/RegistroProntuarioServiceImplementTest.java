package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.Agendamento;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.model.Prontuario;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.repository.AgendamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.domain.repository.ProntuarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.RegistroProntuarioRepository;
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
import java.util.ArrayList;
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
public class RegistroProntuarioServiceImplementTest {

    @Mock
    private RegistroProntuarioRepository registroProntuarioRepository;

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private InternacaoRepository internacaoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RegistroProntuarioServiceImplement registroProntuarioService;

    private Animal animal;
    private Prontuario prontuario;
    private Funcionario veterinario;
    private Agendamento agendamento;
    private Internacao internacao;
    private RegistroProntuario registro;
    private RegistroProntuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(1L);
        animal.setNome("Rex");

        prontuario = new Prontuario();
        prontuario.setId(2L);
        prontuario.setAnimal(animal);

        veterinario = new Funcionario();
        veterinario.setId(3L);
        veterinario.setNome("Dr. João");

        agendamento = new Agendamento();
        agendamento.setId(4L);

        internacao = new Internacao();
        internacao.setId(5L);

        registro = new RegistroProntuario();
        registro.setId(10L);
        registro.setDataHora(LocalDateTime.of(2026, 6, 1, 9, 0));
        registro.setPesoNoDia(BigDecimal.valueOf(12.5));
        registro.setObservacoesClinicas("Animal estável");
        registro.setDiagnostico("Gastroenterite");
        prontuario.adicionarRegistro(registro);
        registro.setVeterinarioResponsavel(veterinario);

        requestDTO = new RegistroProntuarioRequestDTO();
        requestDTO.setProntuarioId(2L);
        requestDTO.setVeterinarioResponsavelId(3L);
        requestDTO.setDataHora(LocalDateTime.of(2026, 6, 1, 9, 0));
        requestDTO.setPesoNoDia(BigDecimal.valueOf(12.5));
        requestDTO.setObservacoesClinicas("Animal estável");
        requestDTO.setDiagnostico("Gastroenterite");
    }

    @Test
    void criar_Sucesso() {
        when(prontuarioRepository.findById(2L)).thenReturn(Optional.of(prontuario));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(registroProntuarioRepository.save(any(RegistroProntuario.class))).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getProntuarioId());
        assertEquals(3L, resultado.getVeterinarioResponsavelId());
        assertEquals("Dr. João", resultado.getNomeVeterinario());
        assertEquals("Gastroenterite", resultado.getDiagnostico());
        verify(registroProntuarioRepository, times(1)).save(any(RegistroProntuario.class));
    }

    @Test
    void criar_ComAgendamentoEInternacao_Sucesso() {
        requestDTO.setAgendamentoId(4L);
        requestDTO.setInternacaoId(5L);

        when(prontuarioRepository.findById(2L)).thenReturn(Optional.of(prontuario));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(agendamentoRepository.findById(4L)).thenReturn(Optional.of(agendamento));
        when(internacaoRepository.findById(5L)).thenReturn(Optional.of(internacao));
        when(registroProntuarioRepository.save(any(RegistroProntuario.class))).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.criar(requestDTO);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(4L);
        verify(internacaoRepository, times(1)).findById(5L);
        verify(registroProntuarioRepository, times(1)).save(any(RegistroProntuario.class));
    }

    @Test
    void criar_ExceptionProntuarioNaoEncontrado() {
        when(prontuarioRepository.findById(2L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.criar(requestDTO);
        });

        assertEquals("Prontuário não encontrado com o ID: 2", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void criar_ExceptionVeterinarioNaoEncontrado() {
        when(prontuarioRepository.findById(2L)).thenReturn(Optional.of(prontuario));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.criar(requestDTO);
        });

        assertEquals("Veterinário não encontrado com o ID: 3", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void criar_ExceptionAgendamentoNaoEncontrado() {
        requestDTO.setAgendamentoId(4L);
        when(prontuarioRepository.findById(2L)).thenReturn(Optional.of(prontuario));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(agendamentoRepository.findById(4L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.criar(requestDTO);
        });

        assertEquals("Agendamento não encontrado com o ID: 4", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void criar_ExceptionInternacaoNaoEncontrada() {
        requestDTO.setInternacaoId(5L);
        when(prontuarioRepository.findById(2L)).thenReturn(Optional.of(prontuario));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(internacaoRepository.findById(5L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.criar(requestDTO);
        });

        assertEquals("Internação não encontrada com o ID: 5", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(registroProntuarioRepository.save(registro)).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.atualizar(10L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Gastroenterite", resultado.getDiagnostico());
        verify(registroProntuarioRepository, times(1)).save(registro);
    }

    @Test
    void atualizar_ComAgendamentoEInternacao_Sucesso() {
        requestDTO.setAgendamentoId(4L);
        requestDTO.setInternacaoId(5L);

        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(agendamentoRepository.findById(4L)).thenReturn(Optional.of(agendamento));
        when(internacaoRepository.findById(5L)).thenReturn(Optional.of(internacao));
        when(registroProntuarioRepository.save(registro)).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.atualizar(10L, requestDTO);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(4L);
        verify(internacaoRepository, times(1)).findById(5L);
        verify(registroProntuarioRepository, times(1)).save(registro);
    }

    @Test
    void atualizar_ExceptionRegistroNaoEncontrado() {
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.atualizar(10L, requestDTO);
        });

        assertEquals("Registro do prontuário não encontrado com o ID: 10", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void atualizar_ExceptionVeterinarioNaoEncontrado() {
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.atualizar(10L, requestDTO);
        });

        assertEquals("Veterinário não encontrado com o ID: 3", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void atualizar_ExceptionAgendamentoNaoEncontrado() {
        requestDTO.setAgendamentoId(4L);
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(agendamentoRepository.findById(4L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.atualizar(10L, requestDTO);
        });

        assertEquals("Agendamento não encontrado com o ID: 4", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void atualizar_ExceptionInternacaoNaoEncontrada() {
        requestDTO.setInternacaoId(5L);
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(veterinario));
        when(internacaoRepository.findById(5L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.atualizar(10L, requestDTO);
        });

        assertEquals("Internação não encontrada com o ID: 5", exception.getMessage());
        verify(registroProntuarioRepository, never()).save(any(RegistroProntuario.class));
    }

    @Test
    void buscarPorProntuarioId_Sucesso() {
        when(registroProntuarioRepository.findByProntuarioIdOrderByDataHoraDesc(2L))
                .thenReturn(Collections.singletonList(registro));

        List<RegistroProntuarioResponseDTO> resultado = registroProntuarioService.buscarPorProntuarioId(2L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getProntuarioId());
        verify(registroProntuarioRepository, times(1)).findByProntuarioIdOrderByDataHoraDesc(2L);
    }

    @Test
    void buscarPorVeterinarioId_Sucesso() {
        when(registroProntuarioRepository.findByVeterinarioResponsavelId(3L))
                .thenReturn(Collections.singletonList(registro));

        List<RegistroProntuarioResponseDTO> resultado = registroProntuarioService.buscarPorVeterinarioId(3L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dr. João", resultado.get(0).getNomeVeterinario());
        verify(registroProntuarioRepository, times(1)).findByVeterinarioResponsavelId(3L);
    }

    @Test
    void buscarPorInternacaoId_Sucesso() {
        registro.setInternacao(internacao);
        when(registroProntuarioRepository.findByInternacaoId(5L))
                .thenReturn(Collections.singletonList(registro));

        List<RegistroProntuarioResponseDTO> resultado = registroProntuarioService.buscarPorInternacaoId(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getInternacaoId());
        verify(registroProntuarioRepository, times(1)).findByInternacaoId(5L);
    }

    @Test
    void buscarPorPeriodo_Sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 30, 23, 59);
        when(registroProntuarioRepository.findByDataHoraBetween(inicio, fim))
                .thenReturn(Collections.singletonList(registro));

        List<RegistroProntuarioResponseDTO> resultado = registroProntuarioService.buscarPorPeriodo(inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(registroProntuarioRepository, times(1)).findByDataHoraBetween(inicio, fim);
    }

    @Test
    void buscarPorIdComMedicamentos_SemMedicamentos_Sucesso() {
        when(registroProntuarioRepository.findByIdWithMedicamentos(10L)).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.buscarPorIdComMedicamentos(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertNotNull(resultado.getMedicamentosAdministrados());
        assertTrue(resultado.getMedicamentosAdministrados().isEmpty());
        verify(registroProntuarioRepository, times(1)).findByIdWithMedicamentos(10L);
    }

    @Test
    void buscarPorIdComMedicamentos_ExceptionNaoEncontrado() {
        when(registroProntuarioRepository.findByIdWithMedicamentos(10L)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            registroProntuarioService.buscarPorIdComMedicamentos(10L);
        });

        assertEquals("Registro do prontuário não encontrado com o ID: 10", exception.getMessage());
    }

    @Test
    void atualizar_SemAlterarVeterinario_Sucesso() {
        requestDTO.setVeterinarioResponsavelId(null);
        when(registroProntuarioRepository.findById(10L)).thenReturn(Optional.of(registro));
        when(registroProntuarioRepository.save(registro)).thenReturn(registro);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.atualizar(10L, requestDTO);

        assertNotNull(resultado);
        assertEquals(veterinario, registro.getVeterinarioResponsavel());
        verify(funcionarioRepository, never()).findById(any());
        verify(registroProntuarioRepository, times(1)).save(registro);
    }

    @Test
    void buscarPorIdComMedicamentos_ComMedicamentos_Sucesso() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(50L);
        Produto produto = new Produto();
        produto.setNome("Dipirona");
        produto.inicializarEstoque(50);
        medicamento.setProduto(produto);

        Funcionario executor = new Funcionario();
        executor.setId(60L);
        executor.setNome("Dra. Ana");

        AdministracaoMedicamento administracaoCompleta = new AdministracaoMedicamento();
        administracaoCompleta.setId(70L);
        administracaoCompleta.registrarAdministracao(medicamento, executor, BigDecimal.ONE, LocalDateTime.now(), "5mg");

        AdministracaoMedicamento administracaoSemAssociacoes =
                new AdministracaoMedicamento(71L, null, null, null, BigDecimal.ONE, LocalDateTime.now(), "5mg", null);

        Medicamento medicamentoSemProduto = new Medicamento();
        medicamentoSemProduto.setId(52L);
        AdministracaoMedicamento administracaoComMedSemProduto = new AdministracaoMedicamento();
        administracaoComMedSemProduto.setId(72L);
        administracaoComMedSemProduto.registrarAdministracao(medicamentoSemProduto, executor, BigDecimal.ONE, LocalDateTime.now(), "5mg");

        registro.getMedicamentosAdministrados().add(administracaoCompleta);
        registro.getMedicamentosAdministrados().add(administracaoSemAssociacoes);
        registro.getMedicamentosAdministrados().add(administracaoComMedSemProduto);

        when(registroProntuarioRepository.findByIdWithMedicamentos(10L)).thenReturn(registro);
        when(modelMapper.map(any(AdministracaoMedicamento.class), eq(AdministracaoMedicamentoResponseDTO.class)))
                .thenAnswer(invocation -> new AdministracaoMedicamentoResponseDTO());

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.buscarPorIdComMedicamentos(10L);

        assertNotNull(resultado);
        assertEquals(3, resultado.getMedicamentosAdministrados().size());

        AdministracaoMedicamentoResponseDTO completaDTO = resultado.getMedicamentosAdministrados().stream()
                .filter(m -> "Dipirona".equals(m.getNomeMedicamento()))
                .findFirst().orElseThrow();
        assertEquals("Dra. Ana", completaDTO.getNomeFuncionarioExecutor());

        AdministracaoMedicamentoResponseDTO medSemProdutoDTO = resultado.getMedicamentosAdministrados().stream()
                .filter(m -> m.getNomeMedicamento() == null && m.getNomeFuncionarioExecutor() != null)
                .findFirst().orElseThrow();
        assertEquals("Dra. Ana", medSemProdutoDTO.getNomeFuncionarioExecutor());

        AdministracaoMedicamentoResponseDTO semAssociacoesDTO = resultado.getMedicamentosAdministrados().stream()
                .filter(m -> m.getNomeMedicamento() == null && m.getNomeFuncionarioExecutor() == null)
                .findFirst().orElseThrow();
        assertNull(semAssociacoesDTO.getNomeFuncionarioExecutor());
    }

    @Test
    void buscarPorIdComMedicamentos_ComListaNula_Sucesso() {
        RegistroProntuario registroSemLista = new RegistroProntuario(
                10L, prontuario, null, veterinario, LocalDateTime.now(), null, BigDecimal.TEN, "obs", "diag", null);

        when(registroProntuarioRepository.findByIdWithMedicamentos(10L)).thenReturn(registroSemLista);

        RegistroProntuarioResponseDTO resultado = registroProntuarioService.buscarPorIdComMedicamentos(10L);

        assertNotNull(resultado);
        assertNull(resultado.getMedicamentosAdministrados());
    }

    @Test
    void buscarPorInternacaoId_SemProntuarioEVeterinario_Sucesso() {
        RegistroProntuario registroSemAssociacoes = new RegistroProntuario(
                30L, null, null, null, LocalDateTime.of(2026, 6, 1, 9, 0), internacao, BigDecimal.TEN, "obs", "diag", new ArrayList<>());

        when(registroProntuarioRepository.findByInternacaoId(5L)).thenReturn(Collections.singletonList(registroSemAssociacoes));

        List<RegistroProntuarioResponseDTO> resultado = registroProntuarioService.buscarPorInternacaoId(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).getProntuarioId());
        assertNull(resultado.get(0).getVeterinarioResponsavelId());
        assertEquals(5L, resultado.get(0).getInternacaoId());
    }
}
