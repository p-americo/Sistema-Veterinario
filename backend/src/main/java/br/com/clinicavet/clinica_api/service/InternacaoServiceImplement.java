package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.model.Animal;
import br.com.clinicavet.clinica_api.model.Internacao;
import br.com.clinicavet.clinica_api.model.enums.EnumInternacaoStatus; // Importe o Enum
import br.com.clinicavet.clinica_api.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.service.Interface.InternacaoServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Importe para usar na alta
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InternacaoServiceImplement implements InternacaoServiceInterface {

    private final InternacaoRepository repository;
    private final AnimalRepository animalRepository;
    private final ModelMapper mapper;

    public InternacaoServiceImplement(InternacaoRepository repository, AnimalRepository animalRepository, ModelMapper mapper) {
        this.repository = repository;
        this.animalRepository = animalRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public InternacaoResponseDTO criarInternacao(InternacaoRequestDTO dto) {
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com o ID: " + dto.getAnimalId()));

        Internacao internacao = mapper.map(dto, Internacao.class);
        internacao.setId(null);
        internacao.setAnimal(animal);

        internacao.setStatus(EnumInternacaoStatus.ATIVA);

        Internacao salva = repository.save(internacao);

        InternacaoResponseDTO response = mapper.map(salva, InternacaoResponseDTO.class);
        response.setNomeAnimal(salva.getAnimal().getNome());
        response.setAnimalId(animal.getId());

        return mapToResponseDTO(salva, false);
    }


    @Transactional(readOnly = true)
    public InternacaoResponseDTO buscarInternacaoAtivaPorAnimalId(Long animalId) {

        List<Internacao> internacoes = repository.findByAnimalIdAndStatus(animalId, EnumInternacaoStatus.ATIVA);


        if (internacoes.isEmpty()) {
            throw new NoSuchElementException("Nenhuma internação ativa encontrada para o animal ID: " + animalId);
        }

        Internacao internacao = internacoes.get(0);

        return mapToResponseDTO(internacao, true);
    }

    @Transactional
    public InternacaoResponseDTO darAltaInternacao(Long id) {
        Internacao internacao = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Internação não encontrada"));

        internacao.setStatus(EnumInternacaoStatus.ALTA);
        internacao.setDataSaida(LocalDateTime.now());

        Internacao atualizada = repository.save(internacao);
        return mapToResponseDTO(atualizada, false);
    }

    @Override
    @Transactional
    public InternacaoResponseDTO atualizarInternacao(Long id, InternacaoRequestDTO dto) {
        Internacao existente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Internação não encontrada"));

        mapper.map(dto, existente);

        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado"));

        existente.setAnimal(animal);

        Internacao atualizada = repository.save(existente);
        return mapToResponseDTO(atualizada, true);
    }

    @Override
    public InternacaoResponseDTO buscarPorId(Long id) {
        Internacao internacao = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Internação não encontrada"));

        return mapToResponseDTO(internacao, true);
    }

    @Override
    public List<InternacaoResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(internacao -> mapToResponseDTO(internacao, false))
                .collect(Collectors.toList());
    }

    @Override
    public void deletarInternacao(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Internação não encontrada");
        }
        repository.deleteById(id);
    }

    private InternacaoResponseDTO mapToResponseDTO(Internacao internacao, boolean incluirDiarias) {
        InternacaoResponseDTO dto = mapper.map(internacao, InternacaoResponseDTO.class);
        if (internacao.getAnimal() != null) {
            dto.setAnimalId(internacao.getAnimal().getId());
            dto.setNomeAnimal(internacao.getAnimal().getNome());
        }

        if (incluirDiarias && internacao.getDiarias() != null) {
            List<DiariaResponseDTO> diariasDTO = internacao.getDiarias().stream()
                    .map(diaria -> {
                        DiariaResponseDTO diariaDTO = mapper.map(diaria, DiariaResponseDTO.class);
                        if (diaria.getMedicamentos() != null) {
                            List<AdministracaoMedicamentoResponseDTO> medicamentosDTO = diaria.getMedicamentos().stream()
                                    .map(medicamentoAdmin -> {
                                        AdministracaoMedicamentoResponseDTO medDTO = new AdministracaoMedicamentoResponseDTO();
                                        medDTO.setId(medicamentoAdmin.getId());
                                        medDTO.setDosagem(medicamentoAdmin.getDosagem());
                                        medDTO.setDataHora(medicamentoAdmin.getDataHora());
                                        medDTO.setQuantidadeAdministrada(medicamentoAdmin.getQuantidadeAdministrada());

                                        if (medicamentoAdmin.getMedicamento() != null) {
                                            medDTO.setMedicamentoId(medicamentoAdmin.getMedicamento().getId());

                                            if (medicamentoAdmin.getMedicamento().getProduto() != null) {
                                                medDTO.setNomeMedicamento(medicamentoAdmin.getMedicamento().getProduto().getNome());
                                            }
                                        }
                                        if (medicamentoAdmin.getFuncionarioExecutor() != null) {
                                            medDTO.setFuncionarioExecutorId(medicamentoAdmin.getFuncionarioExecutor().getId());
                                            medDTO.setNomeFuncionarioExecutor(medicamentoAdmin.getFuncionarioExecutor().getNome());
                                        }
                                        medDTO.setEntradaProntuarioId(diaria.getId());

                                        return medDTO;
                                    })
                                    .collect(Collectors.toList());
                            diariaDTO.setMedicamentos(medicamentosDTO);
                        } else {
                            diariaDTO.setMedicamentos(Collections.emptyList());
                        }
                        return diariaDTO;
                    })
                    .collect(Collectors.toList());
            dto.setDiarias(diariasDTO);
        } else {
            dto.setDiarias(Collections.emptyList());
        }
        return dto;
    }
}