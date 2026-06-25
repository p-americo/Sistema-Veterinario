package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.DiariaService;
import br.com.clinicavet.clinica_api.application.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaMapper;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiariaServiceImplement implements DiariaService {

    private final InternacaoRepository internacaoRepository;

    public DiariaServiceImplement(InternacaoRepository internacaoRepository) {
        this.internacaoRepository = internacaoRepository;
    }

    @Override
    @Transactional
    public DiariaResponseDTO criar(DiariaRequestDTO dto) {
        Internacao internacao = internacaoRepository.findById(dto.getInternacaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Internação não encontrada com o ID: " + dto.getInternacaoId()));

        DiariaInternacao diaria = DiariaMapper.toEntity(dto);
        internacao.adicionarDiaria(diaria);
        // saveAndFlush força o INSERT imediatamente (IDENTITY), garantindo que o id
        // esteja disponível na cópia gerenciada antes de filtrar a coleção.
        Internacao salva = internacaoRepository.saveAndFlush(internacao);

        return salva.getDiarias().stream()
                .filter(d -> dto.getDataHora().equals(d.getDataHora())
                        && dto.getDiagnostico().equals(d.getDiagnostico())
                        && dto.getObservacoesClinicas().equals(d.getObservacoesClinicas()))
                .findFirst()
                .map(DiariaMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Erro ao recuperar a diária criada."));
    }

    @Override
    @Transactional
    public DiariaResponseDTO atualizar(Long id, DiariaRequestDTO dto) {
        DiariaInternacao diaria = internacaoRepository.findDiariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada com o ID: " + id));

        diaria.setDataHora(dto.getDataHora());
        diaria.setDiagnostico(dto.getDiagnostico());
        diaria.setObservacoesClinicas(dto.getObservacoesClinicas());
        diaria.setPesoNoDia(dto.getPesoNoDia());

        internacaoRepository.save(diaria.getInternacao());
        return DiariaMapper.toResponseDTO(diaria);
    }

    @Override
    @Transactional(readOnly = true)
    public DiariaResponseDTO buscarPorId(Long id) {
        DiariaInternacao diaria = internacaoRepository.findDiariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada com o ID: " + id));
        return DiariaMapper.toResponseDTO(diaria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiariaResponseDTO> listarTodos(Pageable pageable) {
        return internacaoRepository.findAllDiarias(pageable)
                .map(DiariaMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiariaResponseDTO> listarPorInternacao(Long internacaoId) {
        return internacaoRepository.findDiariasByInternacaoId(internacaoId).stream()
                .map(DiariaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        DiariaInternacao diaria = internacaoRepository.findDiariaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada com o ID: " + id));

        Internacao internacao = diaria.getInternacao();
        if (internacao != null) {
            internacao.getDiarias().remove(diaria);
            diaria.setInternacao(null);
            internacaoRepository.save(internacao);
        }
    }
}
