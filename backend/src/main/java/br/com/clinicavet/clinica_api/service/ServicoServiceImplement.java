package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.dto.ServicoResponseDTO;
import br.com.clinicavet.clinica_api.model.Funcionario;
import br.com.clinicavet.clinica_api.model.Servico;
import br.com.clinicavet.clinica_api.model.enums.EnumCargo;
import br.com.clinicavet.clinica_api.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.repository.ServicoRepository;
import br.com.clinicavet.clinica_api.service.Interface.ServicoService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoServiceImplement implements ServicoService {

    private final ServicoRepository servicoRepository; // Boa prática: usar 'final'
    private final FuncionarioRepository funcionarioRepository;
    private final ModelMapper modelMapper;


    public ServicoServiceImplement(ServicoRepository servicoRepository, FuncionarioRepository funcionarioRepository, ModelMapper modelMapper) {
        this.servicoRepository = servicoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> buscarTodos() {
        return servicoRepository.findAll()
                .stream()
                .map(func -> modelMapper.map(func, ServicoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServicoResponseDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado com o ID: " + id));
        return mapEntidadeParaResponseDTO(servico);
    }

    // Dentro da classe de serviço para Servico

    @Transactional
    public ServicoResponseDTO cadastrarServico(ServicoRequestDTO dto) {
        Funcionario veterinario = funcionarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado com o ID: " + dto.getVeterinarioId()));

        if (veterinario.getCargo() == null || veterinario.getCargo().getCargo() != EnumCargo.VETERINARIO) {
            throw new IllegalArgumentException("O funcionário com ID " + veterinario.getId() + " não é um veterinário.");
        }

        Servico novoServico = modelMapper.map(dto, Servico.class);

        // Garantia explícita de que é um novo registro
        novoServico.setId(null);

        novoServico.setVeterinario(veterinario);

        Servico servicoSalvo = servicoRepository.save(novoServico);

        return mapEntidadeParaResponseDTO(servicoSalvo);
    }

    @Transactional
    public ServicoResponseDTO atualizarServico(Long id, ServicoRequestDTO dto) {
        Servico servicoExistente = servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado para atualização com o ID: " + id));

        Funcionario veterinario = funcionarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado com o ID: " + dto.getVeterinarioId()));

        // Valida se o funcionário informado é um veterinário
        if (veterinario.getCargo() == null || veterinario.getCargo().getCargo() != EnumCargo.VETERINARIO) {
            throw new IllegalArgumentException("O funcionário com ID " + veterinario.getId() + " não é um veterinário.");
        }

        modelMapper.map(dto, servicoExistente); // Mapeia os campos simples
        servicoExistente.setVeterinario(veterinario); // Reassocia o veterinário
        servicoExistente.setId(id); // Garante que o ID não seja alterado

        Servico servicoAtualizado = servicoRepository.save(servicoExistente);
        return mapEntidadeParaResponseDTO(servicoAtualizado);
    }

    @Transactional
    public void deletarServico(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Serviço não encontrado para deleção com o ID: " + id);
        }
        servicoRepository.deleteById(id);
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