package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Internacao;
import br.com.clinicavet.clinica_api.model.enums.EnumInternacaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternacaoRepository extends JpaRepository<Internacao, Long> {


    @Query("SELECT DISTINCT i FROM Internacao i " + // Adicionado DISTINCT para garantir
            "LEFT JOIN FETCH i.diarias d " +
            "LEFT JOIN FETCH d.medicamentos m " +
            "LEFT JOIN FETCH m.medicamento med " +
            "LEFT JOIN FETCH med.produto p " +
            "LEFT JOIN FETCH m.funcionarioExecutor f " +
            "WHERE i.animal.id = :animalId AND i.status = :status")
    List<Internacao> findByAnimalIdAndStatus(Long animalId, EnumInternacaoStatus status);


    @Query("SELECT DISTINCT i FROM Internacao i LEFT JOIN FETCH i.diarias")
    List<Internacao> findAllCompletas();
}