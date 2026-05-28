package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Internacao;
import br.com.clinicavet.clinica_api.domain.repository.InternacaoRepository;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SpringDataInternacaoRepository extends JpaRepository<Internacao, Long>, InternacaoRepository {

    @Override
    @EntityGraph(attributePaths = {"animal"})
    Page<Internacao> findAll(Pageable pageable);
    @Override
    @Query("SELECT DISTINCT i FROM Internacao i " + // Adicionado DISTINCT para garantir
            "LEFT JOIN FETCH i.diarias d " +
            "LEFT JOIN FETCH d.medicamentos m " +
            "LEFT JOIN FETCH m.medicamento med " +
            "LEFT JOIN FETCH med.produto p " +
            "LEFT JOIN FETCH m.funcionarioExecutor f " +
            "WHERE i.animal.id = :animalId AND i.status = :status")
    List<Internacao> findByAnimalIdAndStatus(Long animalId, EnumInternacaoStatus status);

    @Override
    @Query("SELECT DISTINCT i FROM Internacao i LEFT JOIN FETCH i.diarias")
    List<Internacao> findAllCompletas();

}
