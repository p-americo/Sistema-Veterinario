package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioUpdateDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.CargoRepository;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.PessoaRepository;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
import br.com.clinicavet.clinica_api.application.service.FuncionarioService;
import br.com.clinicavet.clinica_api.application.event.FuncionarioCriadoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FuncionarioServiceImplement extends GenericCrudServiceImplement<Funcionario, Long, FuncionarioRequestDTO, FuncionarioResponseDTO> implements  FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final ModelMapper modelMapper;
    private final ServicoRepository servicoRepository;
    private final PessoaRepository pessoaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FuncionarioServiceImplement(FuncionarioRepository funcionarioRepository, CargoRepository cargoRepository, ModelMapper modelMapper, ServicoRepository servicoRepository,  PessoaRepository pessoaRepository, ApplicationEventPublisher eventPublisher) {
        super(funcionarioRepository, modelMapper);
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
        this.modelMapper = modelMapper;
        this.servicoRepository = servicoRepository;
        this.pessoaRepository = pessoaRepository;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public FuncionarioResponseDTO criar(FuncionarioRequestDTO requestDTO) {
        Cargo cargo = cargoRepository.findById(requestDTO.getCargoId())
                .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado com o ID: " + requestDTO.getCargoId()));

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
        novoFuncionario.alterarCargo(cargo, requestDTO.getCrmv());

        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);
        // Employee is a type of Pessoa, so we can create a user for them
        eventPublisher.publishEvent(new FuncionarioCriadoEvent(requestDTO, funcionarioSalvo));
        return modelMapper.map(funcionarioSalvo, FuncionarioResponseDTO.class);
    }



    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarVeterinarios() {
        return funcionarioRepository.findByCargoEnum(EnumCargo.VETERINARIO)
                .stream()
                .map(func -> modelMapper.map(func, FuncionarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public FuncionarioResponseDTO atualizar(Long id, FuncionarioUpdateDTO dto) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new DataIntegrityViolationException("Funcionário não encontrado para atualização com o ID: " + id));

        // Validação de CPF/email duplicado
        if (dto.getCpf() != null && !dto.getCpf().equals(funcionarioExistente.getCpf()) && pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(funcionarioExistente.getEmail()) && pessoaRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema");
        }
        if (dto.getCrmv() != null && !dto.getCrmv().equals(funcionarioExistente.getCrmv()) && funcionarioRepository.existsByCrmv(dto.getCrmv())) {
            throw new DataIntegrityViolationException("CRMV já cadastrado no sistema.");
        }

        modelMapper.map(dto, funcionarioExistente);

        if (dto.getCargoId() != null || dto.getCrmv() != null) {
            Cargo cargoAtual = funcionarioExistente.getCargo();
            if (dto.getCargoId() != null && !dto.getCargoId().equals(cargoAtual.getId())) {
                cargoAtual = cargoRepository.findById(dto.getCargoId())
                        .orElseThrow(() -> new DataIntegrityViolationException("Novo cargo não encontrado com o ID: " + dto.getCargoId()));
            }
            String crmvAtual = dto.getCrmv() != null ? dto.getCrmv() : funcionarioExistente.getCrmv();
            funcionarioExistente.alterarCargo(cargoAtual, crmvAtual);
        }

        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionarioExistente);
        return modelMapper.map(funcionarioAtualizado, FuncionarioResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new NoSuchElementException("Funcionário não encontrado para deleção com o ID: " + id);
        }

        if (servicoRepository.existsByVeterinarioId(id)) {
            throw new IllegalStateException("O funcionário tem um serviço associado e não pode ser excluído.");
        }
        funcionarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorNome(String nome) {
        return funcionarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(func -> modelMapper.map(func, FuncionarioResponseDTO.class))
                .collect(Collectors.toList());
    }
}