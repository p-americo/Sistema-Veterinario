package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.dto.ClienteUpdateDTO;
import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.model.Cliente;
import br.com.clinicavet.clinica_api.repository.ClienteRepository;
import br.com.clinicavet.clinica_api.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.service.Interface.ClienteService;
import br.com.clinicavet.clinica_api.service.Interface.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImplement implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final PessoaRepository pessoaRepository;
    private final UsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService;


    public ClienteServiceImplement(ClienteRepository clienteRepository, ModelMapper modelMapper, PessoaRepository pessoaRepository, UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.pessoaRepository = pessoaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) {

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
            usuarioService.criarUsuarioCliente(clienteRequestDTO, clienteSalvo);

        return modelMapper.map(novoCliente, ClienteResponseDTO.class);
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

    @Transactional
    public void deletarCliente(Long id) {

        if (!clienteRepository.existsById(id)) {
            throw new NoSuchElementException("Cliente não encontrado para deleção com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    }
