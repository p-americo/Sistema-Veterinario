package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoUpdateDTO;
import java.util.List;


public interface MedicamentoService {

    MedicamentoResponseDTO criarMedicamento(MedicamentoRequestDTO medicamentoRequestDTO);

    MedicamentoResponseDTO atualizarMedicamento(Long id, MedicamentoRequestDTO medicamentoRequestDTO);

    void deletarMedicamento(Long id);

    MedicamentoResponseDTO buscarPorId(Long id);

    List<MedicamentoResponseDTO> listarTodos();
}
