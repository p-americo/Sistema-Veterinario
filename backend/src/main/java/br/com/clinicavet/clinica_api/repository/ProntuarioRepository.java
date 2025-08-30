package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
    
    Optional<Prontuario> findByAnimalId(Long animalId);
    
    boolean existsByAnimalId(Long animalId);
    
    @Query("SELECT p FROM Prontuario p LEFT JOIN FETCH p.registros WHERE p.id = :id")
    Optional<Prontuario> findByIdWithRegistros(@Param("id") Long id);
    
    @Query("SELECT p FROM Prontuario p LEFT JOIN FETCH p.animal WHERE p.animal.id = :animalId")
    Optional<Prontuario> findByAnimalIdWithAnimal(@Param("animalId") Long animalId);
}