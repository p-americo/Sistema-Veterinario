package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.AnimalRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AnimalResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumEspecie;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumPorte;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumSexo;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnimalServiceImplementTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClienteRepository clienteRepository;

    private AnimalServiceImplement animalService;

    private Animal animal;
    private Cliente cliente;
    private AnimalRequestDTO requestDTO;
    private AnimalResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        when(modelMapper.getConfiguration()).thenReturn(mock(Configuration.class));
        animalService = new AnimalServiceImplement(animalRepository, modelMapper, clienteRepository);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        animal = new Animal();
        animal.setId(1L);
        animal.setNome("Rex");
        animal.setEspecie(EnumEspecie.CANINO);
        animal.setPorte(EnumPorte.MEDIO);
        animal.setRaca("Labrador");
        animal.setSexo(EnumSexo.MACHO);
        animal.setCor("Marrom");
        animal.setPeso(20.0);
        animal.setCastrado(true);
        animal.setDataNascimento(LocalDate.of(2020, 1, 1));
        animal.setObservacao("Nenhuma");
        animal.setCliente(cliente);

        requestDTO = new AnimalRequestDTO();
        requestDTO.setNome("Rex");
        requestDTO.setEspecie(EnumEspecie.CANINO);
        requestDTO.setPorte(EnumPorte.MEDIO);
        requestDTO.setRaca("Labrador");
        requestDTO.setSexo(EnumSexo.MACHO);
        requestDTO.setCor("Marrom");
        requestDTO.setPeso(BigDecimal.valueOf(20));
        requestDTO.setCastrado(true);
        requestDTO.setDataNascimento(LocalDate.of(2020, 1, 1));
        requestDTO.setObservacao("Nenhuma");
        requestDTO.setClienteId(1L);

        responseDTO = new AnimalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Rex");
        responseDTO.setEspecie(EnumEspecie.CANINO);
        responseDTO.setPorte(EnumPorte.MEDIO);
        responseDTO.setRaca("Labrador");
        responseDTO.setSexo(EnumSexo.MACHO);
        responseDTO.setCor("Marrom");
        responseDTO.setPeso(BigDecimal.valueOf(20));
    }

    @Test
    void criar_Sucesso() throws Exception {
        MultipartFile arquivoImagem = mock(MultipartFile.class);
        when(arquivoImagem.isEmpty()).thenReturn(true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.criar(requestDTO, arquivoImagem);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNome());
        assertNotNull(resultado.getIdadeFormatada());
        assertNotNull(resultado.getCliente());
        assertEquals(1L, resultado.getCliente().getId());
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    void criar_SucessoSemImagem() throws Exception {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.criar(requestDTO, null);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNome());
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    void criar_SucessoSemDataNascimento() throws Exception {
        requestDTO.setDataNascimento(null);
        animal.setDataNascimento(null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.criar(requestDTO, null);

        assertNotNull(resultado);
        assertEquals("Idade não informada", resultado.getIdadeFormatada());
    }

    @Test
    void criar_ExceptionClienteNaoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            animalService.criar(requestDTO, null);
        });

        assertEquals("Cliente não encontrado com o ID: 1", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void criar_ExceptionAnimalJaCadastradoParaCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(true);
        when(animalRepository.existsByClienteId(requestDTO.getClienteId())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            animalService.criar(requestDTO, null);
        });

        assertEquals("Animal ja cadastrado no mesmo cliente", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void criar_SucessoComImagemValida() throws Exception {
        MultipartFile arquivoImagem = mock(MultipartFile.class);
        when(arquivoImagem.isEmpty()).thenReturn(false);
        when(arquivoImagem.getBytes()).thenReturn(new byte[]{1, 2, 3});

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.criar(requestDTO, arquivoImagem);

        assertNotNull(resultado);
        verify(arquivoImagem, times(1)).getBytes();
    }

    @Test
    void criar_SucessoMesmoNomeOutroCliente() throws Exception {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(true);
        when(animalRepository.existsByClienteId(requestDTO.getClienteId())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.criar(requestDTO, null);

        assertNotNull(resultado);
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    void criar_SucessoSemClienteNoRetorno() throws Exception {
        Animal animalSemCliente = new Animal();
        animalSemCliente.setId(2L);
        animalSemCliente.setNome("Bidu");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(animalRepository.existsByNome(requestDTO.getNome())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animalSemCliente);
        when(modelMapper.map(animalSemCliente, AnimalResponseDTO.class)).thenReturn(new AnimalResponseDTO());

        AnimalResponseDTO resultado = animalService.criar(requestDTO, null);

        assertNotNull(resultado);
        assertNull(resultado.getCliente());
    }

    @Test
    void buscarImagemPorIdAnimal_Sucesso() {
        byte[] imagem = new byte[]{1, 2, 3};
        animal.setImagem(imagem);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        byte[] resultado = animalService.buscarImagemPorIdAnimal(1L);

        assertNotNull(resultado);
        assertArrayEquals(imagem, resultado);
        verify(animalRepository, times(1)).findById(1L);
    }

    @Test
    void buscarImagemPorIdAnimal_ExceptionAnimalNaoEncontrado() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            animalService.buscarImagemPorIdAnimal(1L);
        });

        assertEquals("Animal não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void atualizar_Sucesso() {
        requestDTO.setClienteId(null);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(animal)).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNome());
        verify(modelMapper, times(1)).map(requestDTO, animal);
        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void atualizar_SucessoComTrocaDeCliente() {
        Cliente novoCliente = new Cliente();
        novoCliente.setId(2L);
        novoCliente.setNome("Maria Souza");

        requestDTO.setClienteId(2L);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(novoCliente));
        when(animalRepository.save(animal)).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(novoCliente, animal.getCliente());
        verify(clienteRepository, times(1)).findById(2L);
        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void atualizar_SucessoMesmoCliente() {
        requestDTO.setClienteId(1L);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(animal)).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        verify(clienteRepository, never()).findById(any());
    }

    @Test
    void atualizar_SucessoSemClienteAnterior() {
        animal.setCliente(null);
        Cliente novoCliente = new Cliente();
        novoCliente.setId(5L);
        requestDTO.setClienteId(5L);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(novoCliente));
        when(animalRepository.save(animal)).thenReturn(animal);
        when(modelMapper.map(animal, AnimalResponseDTO.class)).thenReturn(responseDTO);

        AnimalResponseDTO resultado = animalService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(novoCliente, animal.getCliente());
    }

    @Test
    void atualizar_ExceptionAnimalNaoEncontrado() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            animalService.atualizar(1L, requestDTO);
        });

        assertEquals("Animal não encontrado com o ID: 1", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void atualizar_ExceptionNovoClienteNaoEncontrado() {
        requestDTO.setClienteId(2L);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            animalService.atualizar(1L, requestDTO);
        });

        assertEquals("Cliente não encontrado com o ID: 2", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }
}
