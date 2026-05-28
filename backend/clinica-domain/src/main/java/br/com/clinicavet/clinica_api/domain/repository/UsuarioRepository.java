package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;


import java.util.Optional;

public interface UsuarioRepository extends GenericRepository<Usuario, Long> {

    // Search per cpf or crmv
    // return object "Usuario"
    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);


}
