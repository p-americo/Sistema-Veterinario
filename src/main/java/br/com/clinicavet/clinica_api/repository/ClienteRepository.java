package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);
    
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}
