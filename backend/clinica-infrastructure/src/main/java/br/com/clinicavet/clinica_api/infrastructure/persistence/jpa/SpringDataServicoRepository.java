package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Servico;
import br.com.clinicavet.clinica_api.domain.repository.ServicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataServicoRepository extends JpaRepository<Servico, Long>, ServicoRepository {
}
