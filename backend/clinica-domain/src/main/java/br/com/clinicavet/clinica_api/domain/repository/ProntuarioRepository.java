package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Prontuario;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProntuarioRepository extends GenericRepository<Prontuario, Long> {
    
    Optional<Prontuario> findByAnimalId(Long animalId);
    
    boolean existsByAnimalId(Long animalId);
    
    
    Optional<Prontuario> findByIdWithRegistros(Long id);
    
    
    Optional<Prontuario> findByAnimalIdWithAnimal(Long animalId);
}