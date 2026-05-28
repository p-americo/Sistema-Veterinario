package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Pessoa;
import br.com.clinicavet.clinica_api.domain.repository.PessoaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepository {
}
