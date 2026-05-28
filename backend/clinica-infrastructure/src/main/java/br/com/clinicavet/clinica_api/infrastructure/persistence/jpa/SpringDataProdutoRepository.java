package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.repository.ProdutoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataProdutoRepository extends JpaRepository<Produto, Long>, ProdutoRepository {
}
