package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.application.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.domain.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.application.service.ClienteService;
import br.com.clinicavet.clinica_api.application.event.ClienteCriadoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImplement extends GenericCrudServiceImplement<Cliente, Long, ClienteRequestDTO, ClienteResponseDTO> implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final PessoaRepository pessoaRepository;
    private final ApplicationEventPublisher eventPublisher;


    public ClienteServiceImplement(ClienteRepository clienteRepository, ModelMapper modelMapper, PessoaRepository pessoaRepository, ApplicationEventPublisher eventPublisher) {
        super(clienteRepository, modelMapper);
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.pessoaRepository = pessoaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO clienteRequestDTO) {

        if (pessoaRepository.existsByCpf(clienteRequestDTO.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if (pessoaRepository.existsByEmail(clienteRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }
            Cliente novoCliente = modelMapper.map(clienteRequestDTO, Cliente.class);
            novoCliente.setDataCadastro(LocalDate.now());
            Cliente clienteSalvo = clienteRepository.save(novoCliente);
            // Client is a type of Pessoa, so we can create a user for them
            eventPublisher.publishEvent(new ClienteCriadoEvent(clienteRequestDTO, clienteSalvo));

        return modelMapper.map(novoCliente, ClienteResponseDTO.class);
    }


    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o CPF: " + cpf));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para atualização com o ID: " + id));

        if (dto.getCpf() != null && !dto.getCpf().equals(clienteExistente.getCpf()) && pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(clienteExistente.getEmail()) && pessoaRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema");
        }

      modelMapper.map(dto, clienteExistente);

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

}
