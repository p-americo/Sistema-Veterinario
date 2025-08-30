package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.ServicoRequestDTO;
import br.com.clinicavet.clinica_api.dto.ServicoResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServicoService {

    ServicoResponseDTO buscarPorId(Long id);

    List<ServicoResponseDTO> buscarTodos();

    ServicoResponseDTO cadastrarServico(ServicoRequestDTO servicoRequestDTO);

    ServicoResponseDTO atualizarServico(Long id, ServicoRequestDTO update);

    void deletarServico(Long id);




}
