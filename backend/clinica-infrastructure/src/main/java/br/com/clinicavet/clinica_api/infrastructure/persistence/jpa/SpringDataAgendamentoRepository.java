package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Agendamento;
import br.com.clinicavet.clinica_api.domain.repository.AgendamentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAgendamentoRepository extends JpaRepository<Agendamento, Long>, AgendamentoRepository {
    
    @Override
    @EntityGraph(attributePaths = {"animal", "servico", "cliente"})
    Page<Agendamento> findAll(Pageable pageable);
}
