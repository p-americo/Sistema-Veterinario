package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.application.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
import br.com.clinicavet.clinica_api.application.service.ServicoService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicoServiceImplement extends GenericCrudServiceImplement<Servico, Long, ServicoRequestDTO, ServicoResponseDTO> implements ServicoService {

    private final ServicoRepository servicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ModelMapper modelMapper;


    public ServicoServiceImplement(ServicoRepository servicoRepository, FuncionarioRepository funcionarioRepository, ModelMapper modelMapper) {
        super(servicoRepository, modelMapper);
        this.servicoRepository = servicoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public ServicoResponseDTO cadastrar(ServicoRequestDTO dto) {
        Funcionario veterinario = funcionarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado com o ID: " + dto.getVeterinarioId()));

        if (veterinario.getCargo() == null || veterinario.getCargo().getCargo() != EnumCargo.VETERINARIO) {
            throw new IllegalArgumentException("O funcionário com ID " + veterinario.getId() + " não é um veterinário.");
        }

        Servico novoServico = modelMapper.map(dto, Servico.class);

        novoServico.setId(null);

        novoServico.setVeterinario(veterinario);

        Servico servicoSalvo = servicoRepository.save(novoServico);

        return mapEntidadeParaResponseDTO(servicoSalvo);
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, ServicoRequestDTO dto) {
        Servico servicoExistente = servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado para atualização com o ID: " + id));

        Funcionario veterinario = funcionarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado com o ID: " + dto.getVeterinarioId()));

        if (veterinario.getCargo() == null || veterinario.getCargo().getCargo() != EnumCargo.VETERINARIO) {
            throw new IllegalArgumentException("O funcionário com ID " + veterinario.getId() + " não é um veterinário.");
        }

        modelMapper.map(dto, servicoExistente);
        servicoExistente.setVeterinario(veterinario);
        servicoExistente.setId(id);

        Servico servicoAtualizado = servicoRepository.save(servicoExistente);
        return mapEntidadeParaResponseDTO(servicoAtualizado);
    }


    // Método helper privado para centralizar o mapeamento para o ResponseDTO
    private ServicoResponseDTO mapEntidadeParaResponseDTO(Servico servico) {
        ServicoResponseDTO dto = modelMapper.map(servico, ServicoResponseDTO.class);
        if (servico.getVeterinario() != null) {
            // Popula o campo derivado 'nomeVeterinario'
            dto.setNomeVeterinario(servico.getVeterinario().getNome());
        }
        return dto;
    }
}