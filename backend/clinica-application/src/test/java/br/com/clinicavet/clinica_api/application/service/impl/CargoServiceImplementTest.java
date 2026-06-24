package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CargoServiceImplementTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CargoServiceImplement cargoService;

    private Cargo cargo;
    private CargoRequestDTO requestDTO;
    private CargoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cargo = new Cargo();
        cargo.setId(1L);
        cargo.setCargo(EnumCargo.VETERINARIO);

        requestDTO = new CargoRequestDTO();
        requestDTO.setCargo(EnumCargo.VETERINARIO);

        responseDTO = new CargoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCargo(EnumCargo.VETERINARIO);
    }

    @Test
    void criar_Sucesso() {
        when(cargoRepository.findByCargo(requestDTO.getCargo())).thenReturn(Optional.empty());
        when(modelMapper.map(requestDTO, Cargo.class)).thenReturn(cargo);
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        CargoResponseDTO resultado = cargoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumCargo.VETERINARIO, resultado.getCargo());
        verify(cargoRepository, times(1)).findByCargo(requestDTO.getCargo());
        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    void criar_ExceptionCargoJaExiste() {
        when(cargoRepository.findByCargo(requestDTO.getCargo())).thenReturn(Optional.of(cargo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cargoService.criar(requestDTO);
        });

        assertEquals("Este tipo de cargo já existe.", exception.getMessage());
        verify(cargoRepository, times(1)).findByCargo(requestDTO.getCargo());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    void atualizar_Sucesso() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByCargo(requestDTO.getCargo())).thenReturn(Optional.empty());
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        CargoResponseDTO resultado = cargoService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumCargo.VETERINARIO, resultado.getCargo());
        verify(cargoRepository, times(1)).findById(1L);
        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    void atualizar_ExceptionCargoJaEmUso() {
        Cargo outroCargo = new Cargo();
        outroCargo.setId(2L);
        outroCargo.setCargo(EnumCargo.VETERINARIO);

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByCargo(requestDTO.getCargo())).thenReturn(Optional.of(outroCargo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cargoService.atualizar(1L, requestDTO);
        });

        assertEquals("Este tipo de cargo já está em uso por outro registro.", exception.getMessage());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    void atualizar_ExceptionCargoNaoEncontrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            cargoService.atualizar(1L, requestDTO);
        });

        assertEquals("Cargo não encontrado para atualização com o ID: 1", exception.getMessage());
    }

    @Test
    void deletar_Sucesso() {
        when(cargoRepository.existsById(1L)).thenReturn(true);
        when(funcionarioRepository.existsByCargoId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> cargoService.deletar(1L));

        verify(cargoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ExceptionCargoComFuncionarios() {
        when(cargoRepository.existsById(1L)).thenReturn(true);
        when(funcionarioRepository.existsByCargoId(1L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cargoService.deletar(1L);
        });

        assertEquals("Não é possível deletar o cargo, pois existem funcionários associados a ele.", exception.getMessage());
        verify(cargoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletar_ExceptionCargoNaoEncontrado() {
        when(cargoRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            cargoService.deletar(1L);
        });

        assertEquals("Cargo não encontrado para deleção com o ID: 1", exception.getMessage());
    }
}
