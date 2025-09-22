package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.DiariaRequestDTO;
import br.com.clinicavet.clinica_api.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.model.Internacao;
import br.com.clinicavet.clinica_api.repository.DiariaRepository;
import br.com.clinicavet.clinica_api.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.service.Interface.DiariaService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DiariaServiceImplement extends BaseServiceImplement<DiariaInternacao, Long, DiariaRequestDTO, DiariaResponseDTO> implements DiariaService {

    private final DiariaRepository diariaRepository;
    private final InternacaoRepository internacaoRepository;
    private final ModelMapper mapper;

    public DiariaServiceImplement(DiariaRepository diariaRepository, InternacaoRepository internacaoRepository, ModelMapper mapper) {
        super(diariaRepository, mapper);
        this.diariaRepository = diariaRepository;
        this.internacaoRepository = internacaoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DiariaResponseDTO criar(DiariaRequestDTO dto) {
        Internacao internacao = internacaoRepository.findById(dto.getInternacaoId())
                .orElseThrow(() -> new NoSuchElementException("Internação não encontrada com o ID: " + dto.getInternacaoId()));

        DiariaInternacao diaria = mapper.map(dto, DiariaInternacao.class);

        diaria.setId(null);

        diaria.setInternacao(internacao);


        DiariaInternacao salva = diariaRepository.save(diaria);
        return mapper.map(salva, DiariaResponseDTO.class);
    }


    @Override
    public DiariaResponseDTO atualizar(Long id, DiariaRequestDTO dto) {
        DiariaInternacao diaria = diariaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Diária não encontrada"));
        mapper.map(dto, diaria);
        diaria.getMedicamentos().clear();
        DiariaInternacao salva = diariaRepository.save(diaria);
        return mapper.map(salva, DiariaResponseDTO.class);
    }

    @Override
    public List<DiariaResponseDTO> listarPorInternacao(Long internacaoId) {
        return diariaRepository.findByInternacaoId(internacaoId).stream()
                .map(diaria -> mapper.map(diaria, DiariaResponseDTO.class))
                .collect(Collectors.toList());
    }

}