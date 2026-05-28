package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.Medicamento;



public interface MedicamentoService extends GenericCrudService<Medicamento, Long, MedicamentoRequestDTO, MedicamentoResponseDTO> {

    MedicamentoResponseDTO criar(MedicamentoRequestDTO medicamentoRequestDTO);

    MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO medicamentoRequestDTO);

}
