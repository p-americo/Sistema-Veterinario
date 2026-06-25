package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.AgendamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AgendamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.Agendamento;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import br.com.clinicavet.clinica_api.domain.repository.AgendamentoRepository;
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AgendamentoServiceImplementTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AgendamentoServiceImplement agendamentoService;

    private Agendamento agendamento;
    private Cliente cliente;
    private Animal animal;
    private Servico servico;
    private AgendamentoRequestDTO requestDTO;
    private AgendamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(2L);
        cliente.setNome("João Silva");

        animal = new Animal();
        animal.setId(3L);
        animal.setNome("Rex");
        animal.setCliente(cliente);

        servico = new Servico();
        servico.setId(1L);
        servico.setTipo(EnumServico.CONSULTA);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.associarClienteEAnimal(cliente, animal);
        agendamento.setServico(servico);
        agendamento.setDataHoraAgendamento(LocalDateTime.now().plusDays(1));
        agendamento.setObservacoes("Animal apático");

        requestDTO = new AgendamentoRequestDTO();
        requestDTO.setClienteId(2L);
        requestDTO.setAnimalId(3L);
        requestDTO.setServicoId(1L);
        requestDTO.setDataHoraAgendamento(LocalDateTime.now().plusDays(1));
        requestDTO.setObservacoes("Animal apático");

        responseDTO = new AgendamentoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(EnumAgendamento.AGENDADO);
        responseDTO.setObservacoes("Animal apático");
    }

    @Test
    void criarAgendamento_Sucesso() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(animalRepository.findById(3L)).thenReturn(Optional.of(animal));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.criarAgendamento(requestDTO);

        assertNotNull(resultado);
        assertEquals(EnumAgendamento.AGENDADO, resultado.getStatus());
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    void criarAgendamento_ExceptionClienteNaoEncontrado() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.criarAgendamento(requestDTO);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void criarAgendamento_ExceptionAnimalNaoEncontrado() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(animalRepository.findById(3L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.criarAgendamento(requestDTO);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void criarAgendamento_ExceptionServicoNaoEncontrado() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(animalRepository.findById(3L)).thenReturn(Optional.of(animal));
        when(servicoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.criarAgendamento(requestDTO);
        });

        assertEquals("Serviço não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void criarAgendamento_ExceptionAnimalNaoPertenceAoCliente() {
        Cliente outroCliente = new Cliente();
        outroCliente.setId(99L);
        animal.setCliente(outroCliente);

        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(animalRepository.findById(3L)).thenReturn(Optional.of(animal));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            agendamentoService.criarAgendamento(requestDTO);
        });

        assertEquals("O animal informado não pertence ao cliente especificado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void cancelarAgendamento_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        responseDTO.setStatus(EnumAgendamento.CANCELADO);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.cancelarAgendamento(1L);

        assertNotNull(resultado);
        assertEquals(EnumAgendamento.CANCELADO, agendamento.getStatus());
        assertEquals(EnumAgendamento.CANCELADO, resultado.getStatus());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void cancelarAgendamento_ExceptionNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.cancelarAgendamento(1L);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void cancelarAgendamento_ExceptionJaRealizado() {
        agendamento.realizar();
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            agendamentoService.cancelarAgendamento(1L);
        });

        assertEquals("Não é possível cancelar um agendamento já realizado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void listarPorId_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.listarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(agendamentoRepository, times(1)).findById(1L);
    }

    @Test
    void listarPorId_ExceptionNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.listarPorId(1L);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void listarTodos_Sucesso() {
        when(agendamentoRepository.findAll()).thenReturn(Collections.singletonList(agendamento));
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        List<AgendamentoResponseDTO> resultado = agendamentoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    void confirmarAgendamento_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        responseDTO.setStatus(EnumAgendamento.CONFIRMADO);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.confirmarAgendamento(1L);

        assertNotNull(resultado);
        assertEquals(EnumAgendamento.CONFIRMADO, agendamento.getStatus());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void confirmarAgendamento_ExceptionNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.confirmarAgendamento(1L);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void confirmarAgendamento_ExceptionStatusInvalido() {
        agendamento.cancelar();
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            agendamentoService.confirmarAgendamento(1L);
        });

        assertEquals("Apenas agendamentos com status AGENDADO podem ser confirmados.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarAgendamento_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.atualizarAgendamento(1L, requestDTO);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(1L);
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void atualizarAgendamento_ExceptionAgendamentoNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.atualizarAgendamento(1L, requestDTO);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarAgendamento_ExceptionStatusInvalido() {
        agendamento.cancelar();
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            agendamentoService.atualizarAgendamento(1L, requestDTO);
        });

        assertEquals("Não é possível alterar um agendamento com status CANCELADO", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarAgendamento_TrocaClienteEAnimal_Sucesso() {
        Cliente novoCliente = new Cliente();
        novoCliente.setId(5L);

        Animal novoAnimal = new Animal();
        novoAnimal.setId(6L);
        novoAnimal.setCliente(novoCliente);

        requestDTO.setClienteId(5L);
        requestDTO.setAnimalId(6L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(novoCliente));
        when(animalRepository.findById(6L)).thenReturn(Optional.of(novoAnimal));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.atualizarAgendamento(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(novoCliente, agendamento.getCliente());
        assertEquals(novoAnimal, agendamento.getAnimal());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void atualizarAgendamento_TrocaApenasAnimal_Sucesso() {
        Animal novoAnimal = new Animal();
        novoAnimal.setId(7L);
        novoAnimal.setCliente(cliente);

        requestDTO.setAnimalId(7L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(novoAnimal));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.atualizarAgendamento(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(novoAnimal, agendamento.getAnimal());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void atualizarAgendamento_TrocaCliente_ExceptionClienteNaoEncontrado() {
        requestDTO.setClienteId(5L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.findById(5L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.atualizarAgendamento(1L, requestDTO);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarAgendamento_TrocaAnimal_ExceptionAnimalNaoEncontrado() {
        Cliente novoCliente = new Cliente();
        novoCliente.setId(5L);

        requestDTO.setClienteId(5L);
        requestDTO.setAnimalId(6L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(novoCliente));
        when(animalRepository.findById(6L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.atualizarAgendamento(1L, requestDTO);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void atualizarAgendamento_TrocaServico_Sucesso() {
        Servico novoServico = new Servico();
        novoServico.setId(4L);
        novoServico.setTipo(EnumServico.VACINACAO);

        requestDTO.setServicoId(4L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(servicoRepository.findById(4L)).thenReturn(Optional.of(novoServico));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.atualizarAgendamento(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(novoServico, agendamento.getServico());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void atualizarAgendamento_TrocaServico_ExceptionServicoNaoEncontrado() {
        requestDTO.setServicoId(4L);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(servicoRepository.findById(4L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.atualizarAgendamento(1L, requestDTO);
        });

        assertEquals("Serviço não encontrado", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void realizarAgendamento_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);
        responseDTO.setStatus(EnumAgendamento.REALIZADO);
        when(modelMapper.map(agendamento, AgendamentoResponseDTO.class)).thenReturn(responseDTO);

        AgendamentoResponseDTO resultado = agendamentoService.realizarAgendamento(1L);

        assertNotNull(resultado);
        assertEquals(EnumAgendamento.REALIZADO, agendamento.getStatus());
        verify(agendamentoRepository, times(1)).save(agendamento);
    }

    @Test
    void realizarAgendamento_ExceptionNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.realizarAgendamento(1L);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void realizarAgendamento_ExceptionStatusCancelado() {
        agendamento.cancelar();
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            agendamentoService.realizarAgendamento(1L);
        });

        assertEquals("Não é possível realizar um agendamento cancelado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void deletarAgendamento_Sucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertDoesNotThrow(() -> agendamentoService.deletarAgendamento(1L));

        verify(agendamentoRepository, times(1)).delete(agendamento);
    }

    @Test
    void deletarAgendamento_ExceptionNaoEncontrado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            agendamentoService.deletarAgendamento(1L);
        });

        assertEquals("Agendamento não encontrado com o ID: 1", exception.getMessage());
        verify(agendamentoRepository, never()).delete(any(Agendamento.class));
    }
}
