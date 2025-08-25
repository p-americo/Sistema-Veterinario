package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Search per cpf or crmv
    Optional<Usuario> findByLogin(String login);


}
