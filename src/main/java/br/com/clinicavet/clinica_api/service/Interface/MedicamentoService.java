package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.MedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.MedicamentoUpdateDTO;
import java.util.List;

/**
 * Interface que define o contrato para os serviços relacionados a Medicamentos.
 * Expõe todas as operações de negócio (CRUD) para a camada de controller.
 */
public interface MedicamentoService {

    /**
     * Cria um novo medicamento no sistema.
     * @param medicamentoRequestDTO DTO com os dados para criação.
     * @return DTO com os dados do medicamento criado.
     */
    MedicamentoResponseDTO criarMedicamento(MedicamentoRequestDTO medicamentoRequestDTO);

    /**
     * Atualiza um medicamento existente.
     * @param id O ID do medicamento a ser atualizado.
     * @param medicamentoUpdateDTO DTO com os dados a serem atualizados.
     * @return DTO com os dados do medicamento após a atualização.
     */
    MedicamentoResponseDTO atualizarMedicamento(Long id, MedicamentoUpdateDTO medicamentoUpdateDTO);

    /**
     * Deleta um medicamento pelo seu ID.
     * @param id O ID do medicamento a ser deletado.
     */
    void deletarMedicamento(Long id);

    /**
     * Busca um medicamento específico pelo seu ID.
     * @param id O ID do medicamento.
     * @return DTO com os dados do medicamento encontrado.
     */
    MedicamentoResponseDTO buscarPorId(Long id);

    /**
     * Lista todos os medicamentos cadastrados no sistema.
     * @return Uma lista de DTOs com os dados dos medicamentos.
     */
    List<MedicamentoResponseDTO> listarTodos();
}
