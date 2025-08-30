package br.com.clinicavet.clinica_api.service.auth;

import br.com.clinicavet.clinica_api.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // This method is used by Spring Security to load user details by username
    // Instead of using the repository, cant read (Usuario), UserDetailsService is implemented
    // Receives only the login from the AuthenticationManager (UsernamePasswordAuthenticationToken)
    // And uses the login to search the user in the database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByLogin(username)
                // Load user (key with hash, roles, permissions)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
