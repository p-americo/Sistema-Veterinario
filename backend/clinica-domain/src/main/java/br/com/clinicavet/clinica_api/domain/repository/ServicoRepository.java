package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;


public interface ServicoRepository extends GenericRepository<Servico, Long> {

    boolean existsByVeterinarioId(Long veterinarioId);
}
