package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    boolean existsByVeterinarioId(Long veterinarioId);
}
