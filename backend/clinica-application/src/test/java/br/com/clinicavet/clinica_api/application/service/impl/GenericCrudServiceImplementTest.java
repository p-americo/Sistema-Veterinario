package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.CargoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.CargoResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.ResourceNotFoundException;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GenericCrudServiceImplementTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private ModelMapper modelMapper;

    private GenericCrudServiceImplement<Cargo, Long, CargoRequestDTO, CargoResponseDTO> service;

    private Cargo cargo;
    private CargoRequestDTO requestDTO;
    private CargoResponseDTO responseDTO;

    private static class CargoGenericCrudServiceImplement extends GenericCrudServiceImplement<Cargo, Long, CargoRequestDTO, CargoResponseDTO> {
        CargoGenericCrudServiceImplement(CargoRepository repository, ModelMapper modelMapper) {
            super(repository, modelMapper);
        }
    }

    @BeforeEach
    void setUp() {
        service = new CargoGenericCrudServiceImplement(cargoRepository, modelMapper);

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
    void listarTodos_Sucesso() {
        when(cargoRepository.findAll()).thenReturn(Collections.singletonList(cargo));
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        List<CargoResponseDTO> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EnumCargo.VETERINARIO, resultado.get(0).getCargo());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    void listarTodos_Paginado_Sucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cargo> pageCargo = new PageImpl<>(Collections.singletonList(cargo), pageable, 1);
        when(cargoRepository.findAll(pageable)).thenReturn(pageCargo);
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        Page<CargoResponseDTO> resultado = service.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(EnumCargo.VETERINARIO, resultado.getContent().get(0).getCargo());
        verify(cargoRepository, times(1)).findAll(pageable);
    }

    @Test
    void buscarPorId_Sucesso() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        CargoResponseDTO resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(EnumCargo.VETERINARIO, resultado.getCargo());
        verify(cargoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_ExceptionNaoEncontrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarPorId(1L);
        });

        assertEquals("Cargo não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void criar_Sucesso() {
        when(modelMapper.map(requestDTO, Cargo.class)).thenReturn(cargo);
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        CargoResponseDTO resultado = service.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumCargo.VETERINARIO, resultado.getCargo());
        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    void atualizar_Sucesso() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(modelMapper.map(cargo, CargoResponseDTO.class)).thenReturn(responseDTO);

        CargoResponseDTO resultado = service.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumCargo.VETERINARIO, resultado.getCargo());
        verify(modelMapper, times(1)).map(requestDTO, cargo);
        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    void atualizar_ExceptionNaoEncontrado() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizar(1L, requestDTO);
        });

        assertEquals("Cargo não encontrado para atualização com o ID: 1", exception.getMessage());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    void deletar_Sucesso() {
        when(cargoRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.deletar(1L));

        verify(cargoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ExceptionNaoEncontrado() {
        when(cargoRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.deletar(1L);
        });

        assertEquals("Cargo não encontrado para deleção com o ID: 1", exception.getMessage());
        verify(cargoRepository, never()).deleteById(anyLong());
    }
}
