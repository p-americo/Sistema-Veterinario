package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.ProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.dto.ProntuarioUpdateDTO;
import br.com.clinicavet.clinica_api.model.Animal;
import br.com.clinicavet.clinica_api.model.Prontuario;
import br.com.clinicavet.clinica_api.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.repository.ProntuarioRepository;
import br.com.clinicavet.clinica_api.service.Interface.MedicamentoService;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProntuarioServiceTest {

    private MedicamentoService medicamentoService;
    private ProntuarioRepository prontuarioRepository;
    private AnimalRepository animalRepository;
    private ModelMapper modelMapper;

    // Inject mocks for the service being tested in constructor
    private ProntuarioServiceImplement prontuarioService;

    void criarProntuario_DeveRetornarResponseDTO_QuandoDadosValidos() {
        // 1. Preparar os dados de entrada
        ProntuarioRequestDTO request = new ProntuarioRequestDTO();
        request.setAnimalId(1L);

        Animal animal = new Animal();
        animal.setId(1L);

        Prontuario prontuario = new Prontuario();
        prontuario.setAnimal(animal);

        // 2. Definir o comportamento dos mocks
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(prontuarioRepository.existsByAnimalId(1L)).thenReturn(false);
        when(modelMapper.map(request, Prontuario.class)).thenReturn(prontuario);
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario);
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(new ProntuarioResponseDTO());

        // 3. Executar o método a ser testado
        ProntuarioResponseDTO response = prontuarioService.criarProntuario(request);

        // 4. Verificar o resultado
        assertNotNull(response);
        verify(prontuarioRepository).save(any(Prontuario.class));
    }


    void atualizarProntuario_DeveLancarExcecao_QuandoProntuarioNaoExiste() {
        ProntuarioUpdateDTO updateDTO = new ProntuarioUpdateDTO();
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> prontuarioService.atualizarProntuario(1L, updateDTO));
    }

    void deletarProntuario_DeveDeletar_QuandoExiste() {
        when(prontuarioRepository.existsById(1L)).thenReturn(true);
        prontuarioService.deletarProntuario(1L);
        verify(prontuarioRepository).deleteById(1L);
    }

    void deletarProntuario_DeveLancarExcecao_QuandoNaoExiste() {
        when(prontuarioRepository.existsById(1L)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> prontuarioService.deletarProntuario(1L));
    }

    void buscarPorId_DeveRetornarResponseDTO_QuandoExiste() {
        Prontuario prontuario = new Prontuario();
        prontuario.setId(1L);
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(new ProntuarioResponseDTO());
        ProntuarioResponseDTO response = prontuarioService.buscarPorId(1L);
        assertNotNull(response);
    }

    void buscarPorId_DeveLancarExcecao_QuandoNaoExiste() {
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> prontuarioService.buscarPorId(1L));
    }

    void buscarPorAnimalId_DeveRetornarResponseDTO_QuandoExiste() {
        Prontuario prontuario = new Prontuario();
        prontuario.setId(1L);
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(new ProntuarioResponseDTO());
        ProntuarioResponseDTO response = prontuarioService.buscarPorAnimalId(1L);
        assertNotNull(response);
    }

    void buscarPorAnimalId_DeveLancarExcecao_QuandoNaoExiste() {
        when(prontuarioRepository.findByAnimalId(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> prontuarioService.buscarPorAnimalId(1L));
    }

    void listarTodos_DeveRetornarListaDeDTOs() {
        Prontuario prontuario1 = new Prontuario();
        Prontuario prontuario2 = new Prontuario();
        List<Prontuario> lista = List.of(prontuario1, prontuario2);
        when(prontuarioRepository.findAll()).thenReturn(lista);
        when(modelMapper.map(any(Prontuario.class), eq(ProntuarioResponseDTO.class))).thenReturn(new ProntuarioResponseDTO());
        List<ProntuarioResponseDTO> result = prontuarioService.listarTodos();
        assertEquals(2, result.size());
    }

    void buscarPorIdComRegistros_DeveRetornarResponseDTO_QuandoExiste() {
        Prontuario prontuario = new Prontuario();
        prontuario.setId(1L);
        prontuario.setRegistros(List.of());
        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.of(prontuario));
        when(modelMapper.map(prontuario, ProntuarioResponseDTO.class)).thenReturn(new ProntuarioResponseDTO());
        ProntuarioResponseDTO response = prontuarioService.buscarPorIdComRegistros(1L);
        assertNotNull(response);
    }

    void buscarPorIdComRegistros_DeveLancarExcecao_QuandoNaoExiste() {
        when(prontuarioRepository.findByIdWithRegistros(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> prontuarioService.buscarPorIdComRegistros(1L));
    }
}