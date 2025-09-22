package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.model.Medicamento;



public interface MedicamentoService extends BaseService<Medicamento, Long, MedicamentoRequestDTO, MedicamentoResponseDTO> {

    MedicamentoResponseDTO criar(MedicamentoRequestDTO medicamentoRequestDTO);

    MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO medicamentoRequestDTO);

}
