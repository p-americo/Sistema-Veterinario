package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ServicoServiceImplementTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ServicoServiceImplement servicoService;

    private Servico servico;
    private Funcionario veterinario;
    private ServicoRequestDTO requestDTO;
    private ServicoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        Cargo cargoVeterinario = new Cargo();
        cargoVeterinario.setId(1L);
        cargoVeterinario.setCargo(EnumCargo.VETERINARIO);

        veterinario = new Funcionario();
        veterinario.setId(10L);
        veterinario.setNome("Dr. João");
        veterinario.alterarCargo(cargoVeterinario, "CRMV-SP 12345");

        servico = new Servico();
        servico.setId(1L);
        servico.setTipo(EnumServico.CONSULTA);
        servico.setVeterinario(veterinario);
        servico.setValor(BigDecimal.valueOf(150));

        requestDTO = new ServicoRequestDTO();
        requestDTO.setTipo(EnumServico.CONSULTA);
        requestDTO.setVeterinarioId(10L);
        requestDTO.setValor(BigDecimal.valueOf(150));

        responseDTO = new ServicoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTipo(EnumServico.CONSULTA);
        responseDTO.setValor(BigDecimal.valueOf(150));
    }

    @Test
    void cadastrar_Sucesso() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(veterinario));
        when(modelMapper.map(requestDTO, Servico.class)).thenReturn(servico);
        when(servicoRepository.save(servico)).thenReturn(servico);
        when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

        ServicoResponseDTO resultado = servicoService.cadastrar(requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumServico.CONSULTA, resultado.getTipo());
        assertEquals("Dr. João", resultado.getNomeVeterinario());
        assertNull(servico.getId());
        assertEquals(veterinario, servico.getVeterinario());
        verify(servicoRepository, times(1)).save(servico);
    }

    @Test
    void cadastrar_ExceptionVeterinarioNaoEncontrado() {
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            servicoService.cadastrar(requestDTO);
        });

        assertEquals("Veterinário não encontrado com o ID: 10", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void cadastrar_ExceptionFuncionarioNaoEhVeterinario() {
        Cargo cargoRecepcionista = new Cargo();
        cargoRecepcionista.setId(2L);
        cargoRecepcionista.setCargo(EnumCargo.RECEPCIONISTA);

        Funcionario recepcionista = new Funcionario();
        recepcionista.setId(20L);
        recepcionista.alterarCargo(cargoRecepcionista, null);

        requestDTO.setVeterinarioId(20L);
        when(funcionarioRepository.findById(20L)).thenReturn(Optional.of(recepcionista));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicoService.cadastrar(requestDTO);
        });

        assertEquals("O funcionário com ID 20 não é um veterinário.", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void cadastrar_ExceptionFuncionarioComCargoNulo() {
        Funcionario funcionarioSemCargo = new Funcionario();
        funcionarioSemCargo.setId(30L);

        requestDTO.setVeterinarioId(30L);
        when(funcionarioRepository.findById(30L)).thenReturn(Optional.of(funcionarioSemCargo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicoService.cadastrar(requestDTO);
        });

        assertEquals("O funcionário com ID 30 não é um veterinário.", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(veterinario));
        when(servicoRepository.save(servico)).thenReturn(servico);
        when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

        ServicoResponseDTO resultado = servicoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumServico.CONSULTA, resultado.getTipo());
        verify(servicoRepository, times(1)).findById(1L);
        verify(servicoRepository, times(1)).save(servico);
    }

    @Test
    void atualizar_ExceptionServicoNaoEncontrado() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            servicoService.atualizar(1L, requestDTO);
        });

        assertEquals("Serviço não encontrado para atualização com o ID: 1", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void atualizar_ExceptionVeterinarioNaoEncontrado() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(10L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            servicoService.atualizar(1L, requestDTO);
        });

        assertEquals("Veterinário não encontrado com o ID: 10", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void atualizar_ExceptionFuncionarioNaoEhVeterinario() {
        Cargo cargoRecepcionista = new Cargo();
        cargoRecepcionista.setId(2L);
        cargoRecepcionista.setCargo(EnumCargo.RECEPCIONISTA);

        Funcionario recepcionista = new Funcionario();
        recepcionista.setId(20L);
        recepcionista.alterarCargo(cargoRecepcionista, null);

        requestDTO.setVeterinarioId(20L);
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(20L)).thenReturn(Optional.of(recepcionista));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicoService.atualizar(1L, requestDTO);
        });

        assertEquals("O funcionário com ID 20 não é um veterinário.", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void atualizar_ExceptionFuncionarioComCargoNulo() {
        Funcionario funcionarioSemCargo = new Funcionario();
        funcionarioSemCargo.setId(40L);

        requestDTO.setVeterinarioId(40L);
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(40L)).thenReturn(Optional.of(funcionarioSemCargo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicoService.atualizar(1L, requestDTO);
        });

        assertEquals("O funcionário com ID 40 não é um veterinário.", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void mapEntidadeParaResponseDTO_SemVeterinario() throws Exception {
        Servico servicoSemVeterinario = new Servico();
        servicoSemVeterinario.setId(5L);
        ServicoResponseDTO dtoBase = new ServicoResponseDTO();
        when(modelMapper.map(servicoSemVeterinario, ServicoResponseDTO.class)).thenReturn(dtoBase);

        java.lang.reflect.Method method = ServicoServiceImplement.class.getDeclaredMethod("mapEntidadeParaResponseDTO", Servico.class);
        method.setAccessible(true);
        ServicoResponseDTO resultado = (ServicoResponseDTO) method.invoke(servicoService, servicoSemVeterinario);

        assertNotNull(resultado);
        assertNull(resultado.getNomeVeterinario());
    }
}
