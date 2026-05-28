package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiariaMapper {

    public static DiariaInternacao toEntity(DiariaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        DiariaInternacao entity = new DiariaInternacao();
        entity.setDataHora(dto.getDataHora());
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setObservacoesClinicas(dto.getObservacoesClinicas());
        entity.setPesoNoDia(dto.getPesoNoDia());
        return entity;
    }

    public static DiariaResponseDTO toResponseDTO(DiariaInternacao entity) {
        if (entity == null) {
            return null;
        }
        DiariaResponseDTO dto = new DiariaResponseDTO();
        dto.setId(entity.getId());
        dto.setDataHora(entity.getDataHora());
        dto.setDiagnostico(entity.getDiagnostico());
        dto.setObservacoesClinicas(entity.getObservacoesClinicas());
        dto.setPesoNoDia(entity.getPesoNoDia());
        
        if (entity.getMedicamentos() != null) {
            List<AdministracaoMedicamentoResponseDTO> medicamentosDTO = entity.getMedicamentos().stream()
                    .map(DiariaMapper::toAdministracaoResponseDTO)
                    .collect(Collectors.toList());
            dto.setMedicamentos(medicamentosDTO);
        } else {
            dto.setMedicamentos(new ArrayList<>());
        }
        
        return dto;
    }

    private static AdministracaoMedicamentoResponseDTO toAdministracaoResponseDTO(AdministracaoMedicamento entity) {
        if (entity == null) {
            return null;
        }
        AdministracaoMedicamentoResponseDTO dto = new AdministracaoMedicamentoResponseDTO();
        dto.setId(entity.getId());
        dto.setQuantidadeAdministrada(entity.getQuantidadeAdministrada());
        dto.setDataHora(entity.getDataHora());
        dto.setDosagem(entity.getDosagem());
        
        if (entity.getMedicamento() != null) {
            dto.setMedicamentoId(entity.getMedicamento().getId());
            if (entity.getMedicamento().getProduto() != null) {
                dto.setNomeMedicamento(entity.getMedicamento().getProduto().getNome());
            }
        }
        
        if (entity.getEntradaProntuario() != null) {
            dto.setEntradaProntuarioId(entity.getEntradaProntuario().getId());
        }
        
        if (entity.getDiaria() != null) {
            dto.setDiariaId(entity.getDiaria().getId());
        }
        
        if (entity.getFuncionarioExecutor() != null) {
            dto.setFuncionarioExecutorId(entity.getFuncionarioExecutor().getId());
            dto.setNomeFuncionarioExecutor(entity.getFuncionarioExecutor().getNome());
        }
        
        return dto;
    }
}
