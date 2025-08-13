package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.DiariaInternacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiariaRepository extends JpaRepository<DiariaInternacao, Long> {
    List<DiariaInternacao> findByInternacaoId(Long internacaoId);
}
