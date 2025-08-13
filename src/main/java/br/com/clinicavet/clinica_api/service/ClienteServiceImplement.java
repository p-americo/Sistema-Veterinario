package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.model.Cliente;
import br.com.clinicavet.clinica_api.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.service.Interface.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImplement implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final PessoaRepository pessoaRepository;


    public ClienteServiceImplement(ClienteRepository clienteRepository, ModelMapper modelMapper, PessoaRepository pessoaRepository) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) {
        if (pessoaRepository.existsByCpf(clienteRequestDTO.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }

        if(pessoaRepository.existsByEmail(clienteRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema");
        }

        Cliente cliente = modelMapper.map(clienteRequestDTO, Cliente.class);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }


    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o ID: " + id));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateDTO clienteRequestDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para atualização com o ID: " + id));

        if (pessoaRepository.existsByCpf(clienteRequestDTO.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if(pessoaRepository.existsByEmail(clienteRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema");
        }

        modelMapper.map(clienteRequestDTO, clienteExistente);

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    @Transactional
    public void deletarCliente(Long id) {

        if (!clienteRepository.existsById(id)) {
            throw new NoSuchElementException("Cliente não encontrado para deleção com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    }
