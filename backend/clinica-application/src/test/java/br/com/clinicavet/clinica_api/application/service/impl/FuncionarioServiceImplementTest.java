package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioUpdateDTO;
import br.com.clinicavet.clinica_api.application.event.FuncionarioCriadoEvent;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FuncionarioServiceImplementTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FuncionarioServiceImplement funcionarioService;

    private Cargo cargoVeterinario;
    private Funcionario funcionario;
    private FuncionarioRequestDTO requestDTO;
    private FuncionarioResponseDTO responseDTO;
    private FuncionarioUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        cargoVeterinario = new Cargo();
        cargoVeterinario.setId(1L);
        cargoVeterinario.setCargo(EnumCargo.VETERINARIO);

        funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setNome("Dr. João");
        funcionario.setCpf("12345678901");
        funcionario.setEmail("joao@email.com");
        funcionario.setDataAdmissao(LocalDate.of(2024, 1, 10));
        funcionario.alterarCargo(cargoVeterinario, "12345-SP");

        requestDTO = new FuncionarioRequestDTO();
        requestDTO.setNome("Dr. João");
        requestDTO.setCpf("12345678901");
        requestDTO.setEmail("joao@email.com");
        requestDTO.setDataNascimento(LocalDate.of(1985, 8, 22));
        requestDTO.setTelefone("11977776666");
        requestDTO.setDataAdmissao(LocalDate.of(2024, 1, 10));
        requestDTO.setCrmv("12345-SP");
        requestDTO.setCargoId(1L);
        requestDTO.setSenha("Senha@123");

        responseDTO = new FuncionarioResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setNome("Dr. João");
        responseDTO.setCpf("12345678901");
        responseDTO.setEmail("joao@email.com");
        responseDTO.setCrmv("12345-SP");

        updateDTO = new FuncionarioUpdateDTO();
        updateDTO.setNome("Dr. João Silva");
        updateDTO.setCpf("12345678901");
        updateDTO.setEmail("joao@email.com");
        updateDTO.setDataNascimento(LocalDate.of(1985, 8, 22));
        updateDTO.setTelefone("11977776666");
        updateDTO.setDataAdmissao(LocalDate.of(2024, 1, 10));
        updateDTO.setCrmv("12345-SP");
        updateDTO.setCargoId(1L);
    }

    @Test
    void criar_Sucesso() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargoVeterinario));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(funcionarioRepository.existsByCrmv(requestDTO.getCrmv())).thenReturn(false);
        when(modelMapper.map(requestDTO, Funcionario.class)).thenReturn(funcionario);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Dr. João", resultado.getNome());
        verify(funcionarioRepository, times(1)).save(funcionario);
        verify(eventPublisher, times(1)).publishEvent(any(FuncionarioCriadoEvent.class));
    }

    @Test
    void criar_ExceptionCargoNaoEncontrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            funcionarioService.criar(requestDTO);
        });

        assertEquals("Cargo não encontrado com o ID: 1", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void criar_ExceptionCpfJaCadastrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargoVeterinario));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.criar(requestDTO);
        });

        assertEquals("CPF já cadastrado no sistema.", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void criar_ExceptionEmailJaCadastrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargoVeterinario));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.criar(requestDTO);
        });

        assertEquals("Email já cadastrado no sistema.", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void criar_ExceptionCrmvJaCadastrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargoVeterinario));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(funcionarioRepository.existsByCrmv(requestDTO.getCrmv())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.criar(requestDTO);
        });

        assertEquals("CRMV já cadastrado no sistema.", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void criar_SucessoComCrmvNulo() {
        Cargo cargoRecepcionista = new Cargo();
        cargoRecepcionista.setId(2L);
        cargoRecepcionista.setCargo(EnumCargo.RECEPCIONISTA);
        requestDTO.setCargoId(2L);
        requestDTO.setCrmv(null);

        when(cargoRepository.findById(2L)).thenReturn(Optional.of(cargoRecepcionista));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(requestDTO, Funcionario.class)).thenReturn(funcionario);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.criar(requestDTO);

        assertNotNull(resultado);
        verify(funcionarioRepository, never()).existsByCrmv(any());
    }

    @Test
    void criar_SucessoComCrmvEmBranco() {
        Cargo cargoRecepcionista = new Cargo();
        cargoRecepcionista.setId(2L);
        cargoRecepcionista.setCargo(EnumCargo.RECEPCIONISTA);
        requestDTO.setCargoId(2L);
        requestDTO.setCrmv("   ");

        when(cargoRepository.findById(2L)).thenReturn(Optional.of(cargoRecepcionista));
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(requestDTO, Funcionario.class)).thenReturn(funcionario);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.criar(requestDTO);

        assertNotNull(resultado);
        verify(funcionarioRepository, never()).existsByCrmv(any());
    }

    @Test
    void listarVeterinarios_Sucesso() {
        when(funcionarioRepository.findByCargoEnum(EnumCargo.VETERINARIO)).thenReturn(Collections.singletonList(funcionario));
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        List<FuncionarioResponseDTO> resultado = funcionarioService.listarVeterinarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dr. João", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findByCargoEnum(EnumCargo.VETERINARIO);
    }

    @Test
    void atualizar_Sucesso() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByCpf(anyString())).thenReturn(false);
        when(pessoaRepository.existsByEmail(anyString())).thenReturn(false);
        when(funcionarioRepository.existsByCrmv(anyString())).thenReturn(false);
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargoVeterinario));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        verify(funcionarioRepository, times(1)).findById(10L);
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void atualizar_ExceptionFuncionarioNaoEncontrado() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.empty());

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.atualizar(10L, updateDTO);
        });

        assertEquals("Funcionário não encontrado para atualização com o ID: 10", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizar_ExceptionCpfJaCadastrado() {
        updateDTO.setCpf("09876543210");
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByCpf(updateDTO.getCpf())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.atualizar(10L, updateDTO);
        });

        assertEquals("CPF já cadastrado no sistema.", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizar_ExceptionEmailJaCadastrado() {
        updateDTO.setEmail("outro@email.com");
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByEmail(updateDTO.getEmail())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.atualizar(10L, updateDTO);
        });

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizar_ExceptionCrmvJaCadastrado() {
        updateDTO.setCrmv("99999-SP");
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByCpf(anyString())).thenReturn(false);
        when(pessoaRepository.existsByEmail(anyString())).thenReturn(false);
        when(funcionarioRepository.existsByCrmv(updateDTO.getCrmv())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.atualizar(10L, updateDTO);
        });

        assertEquals("CRMV já cadastrado no sistema.", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizar_ExceptionNovoCargoNaoEncontrado() {
        updateDTO.setCargoId(2L);
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByCpf(anyString())).thenReturn(false);
        when(pessoaRepository.existsByEmail(anyString())).thenReturn(false);
        when(funcionarioRepository.existsByCrmv(anyString())).thenReturn(false);
        when(cargoRepository.findById(2L)).thenReturn(Optional.empty());

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            funcionarioService.atualizar(10L, updateDTO);
        });

        assertEquals("Novo cargo não encontrado com o ID: 2", exception.getMessage());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizar_SemTrocarCargoComCrmvAlterado_Sucesso() {
        updateDTO.setCargoId(null);
        updateDTO.setCrmv("99999-SP");

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.existsByCrmv("99999-SP")).thenReturn(false);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        verify(cargoRepository, never()).findById(any());
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void atualizar_TrocaCargoComSucesso() {
        Cargo novoCargoVeterinario = new Cargo();
        novoCargoVeterinario.setId(2L);
        novoCargoVeterinario.setCargo(EnumCargo.VETERINARIO);

        updateDTO.setCargoId(2L);

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(cargoRepository.findById(2L)).thenReturn(Optional.of(novoCargoVeterinario));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        assertEquals(novoCargoVeterinario, funcionario.getCargo());
        verify(cargoRepository, times(1)).findById(2L);
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void atualizar_SucessoSemAlterarCpfEmailCrmv() {
        updateDTO.setCpf(null);
        updateDTO.setEmail(null);
        updateDTO.setCrmv(null);
        updateDTO.setCargoId(null);

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        verify(pessoaRepository, never()).existsByCpf(any());
        verify(pessoaRepository, never()).existsByEmail(any());
        verify(funcionarioRepository, never()).existsByCrmv(any());
        verify(cargoRepository, never()).findById(any());
    }

    @Test
    void atualizar_SucessoComCpfEmailDiferentesSemConflito() {
        updateDTO.setCpf("11122233344");
        updateDTO.setEmail("novo@email.com");

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(pessoaRepository.existsByCpf("11122233344")).thenReturn(false);
        when(pessoaRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void atualizar_TrocaCargoSemCrmvNoDto_UsaCrmvExistente() {
        updateDTO.setCrmv(null);
        Cargo novoCargoVeterinario = new Cargo();
        novoCargoVeterinario.setId(2L);
        novoCargoVeterinario.setCargo(EnumCargo.VETERINARIO);
        updateDTO.setCargoId(2L);

        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(funcionario));
        when(cargoRepository.findById(2L)).thenReturn(Optional.of(novoCargoVeterinario));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        FuncionarioResponseDTO resultado = funcionarioService.atualizar(10L, updateDTO);

        assertNotNull(resultado);
        assertEquals("12345-SP", funcionario.getCrmv());
        assertEquals(novoCargoVeterinario, funcionario.getCargo());
    }

    @Test
    void deletar_Sucesso() {
        when(funcionarioRepository.existsById(10L)).thenReturn(true);
        when(servicoRepository.existsByVeterinarioId(10L)).thenReturn(false);

        assertDoesNotThrow(() -> funcionarioService.deletar(10L));

        verify(funcionarioRepository, times(1)).deleteById(10L);
    }

    @Test
    void deletar_ExceptionFuncionarioNaoEncontrado() {
        when(funcionarioRepository.existsById(10L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            funcionarioService.deletar(10L);
        });

        assertEquals("Funcionário não encontrado para deleção com o ID: 10", exception.getMessage());
        verify(funcionarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletar_ExceptionFuncionarioComServicoAssociado() {
        when(funcionarioRepository.existsById(10L)).thenReturn(true);
        when(servicoRepository.existsByVeterinarioId(10L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            funcionarioService.deletar(10L);
        });

        assertEquals("O funcionário tem um serviço associado e não pode ser excluído.", exception.getMessage());
        verify(funcionarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorNome_Sucesso() {
        when(funcionarioRepository.findByNomeContainingIgnoreCase("João")).thenReturn(Collections.singletonList(funcionario));
        when(modelMapper.map(funcionario, FuncionarioResponseDTO.class)).thenReturn(responseDTO);

        List<FuncionarioResponseDTO> resultado = funcionarioService.buscarPorNome("João");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dr. João", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findByNomeContainingIgnoreCase("João");
    }
}
