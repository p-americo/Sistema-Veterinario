package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    // Verifica se cpf já é existente

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}