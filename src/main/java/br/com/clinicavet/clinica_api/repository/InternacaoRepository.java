package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Internacao;
import br.com.clinicavet.clinica_api.model.enums.EnumInternacaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternacaoRepository extends JpaRepository<Internacao, Long> {


    Optional<Internacao> findByAnimalIdAndStatus(Long animalId, EnumInternacaoStatus status);

}