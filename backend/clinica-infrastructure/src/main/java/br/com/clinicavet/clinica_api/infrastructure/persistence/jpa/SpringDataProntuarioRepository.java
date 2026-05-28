package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Prontuario;
import br.com.clinicavet.clinica_api.domain.repository.ProntuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SpringDataProntuarioRepository extends JpaRepository<Prontuario, Long>, ProntuarioRepository {
    @Override
    @Query("SELECT p FROM Prontuario p LEFT JOIN FETCH p.registros WHERE p.id = :id")
    Optional<Prontuario> findByIdWithRegistros(@Param("id") Long id);

    @Override
    @Query("SELECT p FROM Prontuario p LEFT JOIN FETCH p.animal WHERE p.animal.id = :animalId")
    Optional<Prontuario> findByAnimalIdWithAnimal(@Param("animalId") Long animalId);

}
