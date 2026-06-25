package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Prontuario;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.ProntuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProntuarioServiceImplementTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProntuarioServiceImplement prontuarioService;

    private Animal animal;
    private Prontuario prontuario;
    private ProntuarioRequestDTO requestDTO;
    private ProntuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(1L);
        animal.setNome("Rex");

        prontuario = new Prontuario();
        prontuario.setId(1L);
        prontuario.setAnimal(animal);
        prontuario.setAlergiasConhecidas("Nenhuma");
        prontuario.setCondicoesPreexistentes("Nenhuma");

        requestDTO = new ProntuarioRequestDTO();
        requestDTO.setAnimalId(1L);
        requestDTO.setAlergiasConhecidas("Nenhuma");
        requestDTO.setCondicoesPreexistentes("Nenhuma");

        responseDTO = new ProntuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setAlergiasConhecidas("Nenhuma");
        responseDTO.setCondicoesPreexistentes("Nenhuma");
    }

    @Test
    void criar_Sucesso() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(prontuarioRepository.existsByAnimalId(1L)).thenReturn(false);
        when(modelMapper.map(requestDTO, Prontuario.class)).thenReturn(prontuario);
        when(prontuarioRepository.save(prontuario)).thenReturn(prontuario);
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getAnimalId());
        assertEquals("Rex", resultado.getNomeAnimal());
        assertEquals(animal, prontuario.getAnimal());
        verify(prontuarioRepository, times(1)).save(prontuario);
    }

    @Test
    void criar_ExceptionAnimalNaoEncontrado() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            prontuarioService.criar(requestDTO);
        });

        assertEquals("Animal não encontrado com o ID: 1", exception.getMessage());
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    void criar_ExceptionProntuarioJaExisteParaAnimal() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(prontuarioRepository.existsByAnimalId(1L)).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            prontuarioService.criar(requestDTO);
        });

        assertEquals("Já existe um prontuário para este animal", exception.getMessage());
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    void buscarPorAnimalId_Sucesso() {
        Funcionario veterinario = new Funcionario();
        veterinario.setId(10L);
        veterinario.setNome("Dr. João");

        RegistroProntuario registro = new RegistroProntuario();
        registro.setId(5L);
        registro.setVeterinarioResponsavel(veterinario);
        prontuario.getRegistros().add(registro);

        RegistroProntuarioResponseDTO registroResponseDTO = new RegistroProntuarioResponseDTO();
        registroResponseDTO.setId(5L);

        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(registro, RegistroProntuarioResponseDTO.class)).thenReturn(registroResponseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorAnimalId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getAnimalId());
        assertEquals("Rex", resultado.getNomeAnimal());
        assertNotNull(resultado.getRegistros());
        assertEquals(1, resultado.getRegistros().size());
        assertEquals("Dr. João", resultado.getRegistros().get(0).getNomeVeterinario());
        verify(prontuarioRepository, times(1)).findByAnimalId(1L);
    }

    @Test
    void buscarPorAnimalId_SucessoSemRegistros() {
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorAnimalId(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getRegistros());
        assertTrue(resultado.getRegistros().isEmpty());
        verify(prontuarioRepository, times(1)).findByAnimalId(1L);
    }

    @Test
    void buscarPorAnimalId_ExceptionNaoEncontrado() {
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            prontuarioService.buscarPorAnimalId(1L);
        });

        assertEquals("Prontuário não encontrado para o animal com ID: 1", exception.getMessage());
    }

    @Test
    void buscarPorIdComRegistros_Sucesso() {
        Funcionario veterinario = new Funcionario();
        veterinario.setId(10L);
        veterinario.setNome("Dra. Ana");

        RegistroProntuario registro = new RegistroProntuario();
        registro.setId(7L);
        registro.setVeterinarioResponsavel(veterinario);
        prontuario.getRegistros().add(registro);

        RegistroProntuarioResponseDTO registroResponseDTO = new RegistroProntuarioResponseDTO();
        registroResponseDTO.setId(7L);

        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(registro, RegistroProntuarioResponseDTO.class)).thenReturn(registroResponseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorIdComRegistros(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.getRegistros().size());
        assertEquals("Dra. Ana", resultado.getRegistros().get(0).getNomeVeterinario());
        verify(prontuarioRepository, times(1)).findByIdWithRegistros(1L);
    }

    @Test
    void buscarPorIdComRegistros_SucessoSemRegistros() {
        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorIdComRegistros(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getRegistros());
        assertTrue(resultado.getRegistros().isEmpty());
        verify(prontuarioRepository, times(1)).findByIdWithRegistros(1L);
    }

    @Test
    void buscarPorIdComRegistros_ExceptionNaoEncontrado() {
        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            prontuarioService.buscarPorIdComRegistros(1L);
        });

        assertEquals("Prontuário não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void buscarPorAnimalId_SucessoComRegistrosNulos() {
        Prontuario prontuarioSemRegistros = new Prontuario(1L, animal, "Nenhuma", "Nenhuma", null);
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuarioSemRegistros));
        when(modelMapper.map(prontuarioSemRegistros, ProntuarioResponseDTO.class)).thenReturn(responseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorAnimalId(1L);

        assertNotNull(resultado);
        assertNull(resultado.getRegistros());
    }

    @Test
    void buscarPorAnimalId_SucessoComRegistroSemVeterinario() {
        RegistroProntuario registroSemVet = new RegistroProntuario();
        registroSemVet.setId(8L);
        prontuario.getRegistros().add(registroSemVet);

        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(registroSemVet, RegistroProntuarioResponseDTO.class)).thenReturn(new RegistroProntuarioResponseDTO());

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorAnimalId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.getRegistros().size());
        assertNull(resultado.getRegistros().get(0).getNomeVeterinario());
    }

    @Test
    void buscarPorAnimalId_SucessoSemAnimalAssociado() {
        Prontuario prontuarioSemAnimal = new Prontuario(1L, null, "Nenhuma", "Nenhuma", new ArrayList<>());
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuarioSemAnimal));
        when(modelMapper.map(prontuarioSemAnimal, ProntuarioResponseDTO.class)).thenReturn(new ProntuarioResponseDTO());

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorAnimalId(1L);

        assertNotNull(resultado);
        assertNull(resultado.getNomeAnimal());
    }

    @Test
    void buscarPorIdComRegistros_SucessoComRegistrosNulos() {
        Prontuario prontuarioSemRegistros = new Prontuario(1L, animal, "Nenhuma", "Nenhuma", null);
        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.of(prontuarioSemRegistros));
        when(modelMapper.map(prontuarioSemRegistros, ProntuarioResponseDTO.class)).thenReturn(responseDTO);

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorIdComRegistros(1L);

        assertNotNull(resultado);
        assertNull(resultado.getRegistros());
    }

    @Test
    void buscarPorIdComRegistros_SucessoComRegistroSemVeterinario() {
        RegistroProntuario registroSemVet = new RegistroProntuario();
        registroSemVet.setId(9L);
        prontuario.getRegistros().add(registroSemVet);

        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(registroSemVet, RegistroProntuarioResponseDTO.class)).thenReturn(new RegistroProntuarioResponseDTO());

        ProntuarioResponseDTO resultado = prontuarioService.buscarPorIdComRegistros(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.getRegistros().size());
        assertNull(resultado.getRegistros().get(0).getNomeVeterinario());
    }
}
