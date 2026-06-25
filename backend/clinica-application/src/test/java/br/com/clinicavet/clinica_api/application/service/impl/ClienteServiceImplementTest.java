package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.application.event.ClienteCriadoEvent;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.domain.repository.PessoaRepository;
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
public class ClienteServiceImplementTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ClienteServiceImplement clienteService;

    private Cliente cliente;
    private ClienteRequestDTO requestDTO;
    private ClienteResponseDTO responseDTO;
    private ClienteUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@email.com");

        requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("João Silva");
        requestDTO.setCpf("12345678901");
        requestDTO.setEmail("joao@email.com");

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João Silva");
        responseDTO.setCpf("12345678901");
        responseDTO.setEmail("joao@email.com");

        updateDTO = new ClienteUpdateDTO();
        updateDTO.setNome("João S.");
        updateDTO.setCpf("12345678901");
        updateDTO.setEmail("joao.novo@email.com");
    }

    @Test
    void criar_Sucesso() {
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(requestDTO, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertNotNull(cliente.getDataCadastro());
        verify(clienteRepository, times(1)).save(cliente);
        verify(eventPublisher, times(1)).publishEvent(any(ClienteCriadoEvent.class));
    }

    @Test
    void criar_ExceptionCpfJaCadastrado() {
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            clienteService.criar(requestDTO);
        });

        assertEquals("CPF já cadastrado no sistema.", exception.getMessage());
        verify(pessoaRepository, never()).existsByEmail(anyString());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void criar_ExceptionEmailJaCadastrado() {
        when(pessoaRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
        when(pessoaRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            clienteService.criar(requestDTO);
        });

        assertEquals("Email já cadastrado no sistema.", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void buscarPorNome_Sucesso() {
        when(clienteRepository.findByNomeContainingIgnoreCase("João")).thenReturn(Collections.singletonList(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        List<ClienteResponseDTO> resultado = clienteService.buscarPorNome("João");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(clienteRepository, times(1)).findByNomeContainingIgnoreCase("João");
    }

    @Test
    void buscarPorCpf_Sucesso() {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.buscarPorCpf("12345678901");

        assertNotNull(resultado);
        assertEquals("12345678901", resultado.getCpf());
        verify(clienteRepository, times(1)).findByCpf("12345678901");
    }

    @Test
    void buscarPorCpf_ExceptionNaoEncontrado() {
        when(clienteRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            clienteService.buscarPorCpf("00000000000");
        });

        assertEquals("Cliente não encontrado com o CPF: 00000000000", exception.getMessage());
    }

    @Test
    void atualizarCliente_Sucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pessoaRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, updateDTO);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void atualizarCliente_ExceptionCpfJaCadastrado() {
        updateDTO.setCpf("09876543210");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pessoaRepository.existsByCpf(updateDTO.getCpf())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            clienteService.atualizarCliente(1L, updateDTO);
        });

        assertEquals("CPF já cadastrado no sistema.", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_ExceptionEmailJaCadastrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pessoaRepository.existsByEmail(updateDTO.getEmail())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            clienteService.atualizarCliente(1L, updateDTO);
        });

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_SucessoSemAlterarCpfEEmail() {
        updateDTO.setCpf(null);
        updateDTO.setEmail(null);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, updateDTO);

        assertNotNull(resultado);
        verify(pessoaRepository, never()).existsByCpf(any());
        verify(pessoaRepository, never()).existsByEmail(any());
    }

    @Test
    void atualizarCliente_SucessoComEmailIgualAoExistente() {
        updateDTO.setEmail(cliente.getEmail());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, updateDTO);

        assertNotNull(resultado);
        verify(pessoaRepository, never()).existsByEmail(any());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void atualizarCliente_SucessoComCpfEEmailDiferentesSemConflito() {
        updateDTO.setCpf("11122233344");
        updateDTO.setEmail("novo@email.com");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pessoaRepository.existsByCpf("11122233344")).thenReturn(false);
        when(pessoaRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, updateDTO);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void atualizarCliente_ExceptionNaoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            clienteService.atualizarCliente(1L, updateDTO);
        });

        assertEquals("Cliente não encontrado para atualização com o ID: 1", exception.getMessage());
    }
}
