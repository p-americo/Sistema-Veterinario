package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByNomeIgnoreCase(String nome);
}
