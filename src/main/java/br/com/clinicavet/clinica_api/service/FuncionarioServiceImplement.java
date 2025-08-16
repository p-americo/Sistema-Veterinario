package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.model.Cargo;
import br.com.clinicavet.clinica_api.model.Funcionario;
import br.com.clinicavet.clinica_api.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.repository.CargoRepository;
import br.com.clinicavet.clinica_api.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.repository.ServicoRepository;
import br.com.clinicavet.clinica_api.service.Interface.FuncionarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FuncionarioServiceImplement implements FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final ModelMapper modelMapper;
    private final ServicoRepository servicoRepository;
    private final PessoaRepository pessoaRepository;

    public FuncionarioServiceImplement(FuncionarioRepository funcionarioRepository, CargoRepository cargoRepository, ModelMapper modelMapper, ServicoRepository servicoRepository,  PessoaRepository pessoaRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
        this.modelMapper = modelMapper;
        this.servicoRepository = servicoRepository;
        this.pessoaRepository = pessoaRepository;
    }


    @Transactional
    public FuncionarioResponseDTO criarFuncionario(FuncionarioRequestDTO requestDTO) {
        Cargo cargo = cargoRepository.findById(requestDTO.getCargoId())
                .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado com o ID: " + requestDTO.getCargoId()));

        if (cargo.getCargo() == EnumCargo.VETERINARIO) {
            if (requestDTO.getCrmv() == null || requestDTO.getCrmv().isBlank()) {
                throw new  DataIntegrityViolationException("CRMV é obrigatório para o cargo de Veterinário.");
            }
        } else {
            if (requestDTO.getCrmv() != null && !requestDTO.getCrmv().isBlank()) {
                throw new  DataIntegrityViolationException("CRMV só pode ser preenchido para o cargo de Veterinário.");
            }
        }
        if (pessoaRepository.existsByCpf(requestDTO.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if (pessoaRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }
        if (requestDTO.getCrmv() != null && !requestDTO.getCrmv().isBlank() && funcionarioRepository.existsByCrmv(requestDTO.getCrmv())) {
            throw new DataIntegrityViolationException("CRMV já cadastrado no sistema.");
        }

        Funcionario novoFuncionario = modelMapper.map(requestDTO, Funcionario.class);
        novoFuncionario.setId(null);
        novoFuncionario.setCargo(cargo);

        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);
        return modelMapper.map(funcionarioSalvo, FuncionarioResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorId(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado com o ID: " + id));
        return modelMapper.map(funcionario, FuncionarioResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(func -> modelMapper.map(func, FuncionarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarVeterinarios() {
        return funcionarioRepository.findByCargoEnum(EnumCargo.VETERINARIO)
                .stream()
                .map(func -> modelMapper.map(func, FuncionarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public FuncionarioResponseDTO atualizarFuncionario(Long id, FuncionarioRequestDTO requestDTO) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new DataIntegrityViolationException("Funcionário não encontrado para atualização com o ID: " + id));
        modelMapper.map(requestDTO, funcionarioExistente);

        if (requestDTO.getCargoId() != null) {
            if (!requestDTO.getCargoId().equals(funcionarioExistente.getCargo().getId())) {
                Cargo novoCargo = cargoRepository.findById(requestDTO.getCargoId())
                        .orElseThrow(() -> new DataIntegrityViolationException("Novo cargo não encontrado com o ID: " + requestDTO.getCargoId()));
                funcionarioExistente.setCargo(novoCargo);
                if (novoCargo.getCargo() != EnumCargo.VETERINARIO) {
                    funcionarioExistente.setCrmv(null);
                }
            }
        }
        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionarioExistente);
        return modelMapper.map(funcionarioAtualizado, FuncionarioResponseDTO.class);
    }

    @Transactional
    public void deletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new NoSuchElementException("Funcionário não encontrado para deleção com o ID: " + id);
        }

        if (servicoRepository.existsByVeterinarioId(id)) {
            throw new IllegalStateException("O funcionário tem um serviço associado e não pode ser excluído.");
        }
        funcionarioRepository.deleteById(id);
    }
}