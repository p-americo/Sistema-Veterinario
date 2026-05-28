package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.DiariaResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.InternacaoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus; // Importe o Enum
import br.com.clinicavet.clinica_api.domain.repository.AnimalRepository;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.application.service.InternacaoService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Importe para usar na alta
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternacaoServiceImplement extends GenericCrudServiceImplement<Internacao, Long, InternacaoRequestDTO, InternacaoResponseDTO> implements InternacaoService {

    private final InternacaoRepository repository;
    private final AnimalRepository animalRepository;
    private final ModelMapper mapper;

    public InternacaoServiceImplement(InternacaoRepository repository, AnimalRepository animalRepository, ModelMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.animalRepository = animalRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public InternacaoResponseDTO buscarPorId(Long id) {
        Internacao internacao = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Internação não encontrada com o ID: " + id));
        return mapToResponseDTO(internacao, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternacaoResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(internacao -> mapToResponseDTO(internacao, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InternacaoResponseDTO criar(InternacaoRequestDTO dto) {
        log.info("Iniciando internação para o Animal ID: {} em: {}", dto.getAnimalId(), dto.getDataEntrada());
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> {
                    log.warn("Falha ao criar internação: Animal ID {} não encontrado", dto.getAnimalId());
                    return new NoSuchElementException("Animal não encontrado com o ID: " + dto.getAnimalId());
                });

        Internacao internacao = new Internacao();
        internacao.setAnimal(animal);
        internacao.setDataEntrada(dto.getDataEntrada());

        Internacao salva = repository.save(internacao);
        log.info("Internação criada com sucesso com ID: {} e status: {}", salva.getId(), salva.getStatus());

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
        log.info("Registrando alta para a internação ID: {}", id);
        Internacao internacao = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao dar alta: Internação ID {} não encontrada", id);
                    return new NoSuchElementException("Internação não encontrada");
                });

        internacao.darAlta();

        Internacao atualizada = repository.save(internacao);
        log.info("Alta registrada com sucesso para a internação ID: {} em: {}", id, atualizada.getDataSaida());
        return mapToResponseDTO(atualizada, false);
    }

    @Override
    @Transactional
    public InternacaoResponseDTO atualizar(Long id, InternacaoRequestDTO dto) {
        log.info("Atualizando dados da internação ID: {}", id);
        Internacao existente = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao atualizar: Internação ID {} não encontrada", id);
                    return new NoSuchElementException("Internação não encontrada");
                });

        mapper.map(dto, existente);

        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> {
                    log.warn("Falha ao atualizar internação: Animal ID {} não encontrado", dto.getAnimalId());
                    return new NoSuchElementException("Animal não encontrado");
                });

        existente.setAnimal(animal);

        Internacao atualizada = repository.save(existente);
        log.info("Internação ID: {} atualizada com sucesso.", id);
        return mapToResponseDTO(atualizada, true);
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