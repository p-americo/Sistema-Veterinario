package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.DiariaInternacao;
import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InternacaoRepository extends GenericRepository<Internacao, Long> {
    
    List<Internacao> findByAnimalIdAndStatus(Long animalId, EnumInternacaoStatus status);
    
    List<Internacao> findAllCompletas();

    @Query("SELECT d FROM Internacao i JOIN i.diarias d WHERE d.id = :id")
    Optional<DiariaInternacao> findDiariaById(@Param("id") Long id);

    @Query("SELECT d FROM Internacao i JOIN i.diarias d")
    Page<DiariaInternacao> findAllDiarias(Pageable pageable);

    @Query("SELECT d FROM Internacao i JOIN i.diarias d WHERE i.id = :internacaoId")
    List<DiariaInternacao> findDiariasByInternacaoId(@Param("internacaoId") Long internacaoId);
}