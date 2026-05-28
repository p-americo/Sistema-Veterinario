package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.repository.MedicamentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataMedicamentoRepository extends JpaRepository<Medicamento, Long>, MedicamentoRepository {
}
