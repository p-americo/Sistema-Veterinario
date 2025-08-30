package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.LoginDTO; // DTO que criamos anteriormente
import br.com.clinicavet.clinica_api.dto.TokenJWTDTO; // DTO que criamos anteriormente
import br.com.clinicavet.clinica_api.model.Usuario;
import br.com.clinicavet.clinica_api.service.auth.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {


    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenJWTDTO> login(@RequestBody @Valid LoginDTO dto) {

        // Create object for authentication with the login and password received
        // This object will be used by Spring Security to authenticate the user
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getSenha());


        // Used bean AuthenticationManager to authenticate the user
        // This will call the UserDetailsService in (AuthenticationService) to load the user and check the password
        // Compare the password in Bean(passwordEncoder)
        // If the password is correct, it will return an Authentication object
        // If the password is incorrect, it will throw an exception
        var authentication = manager.authenticate(authenticationToken);

        // getPricipal() returns the authenticated user object generic (Usuario)
        // We cast it to Usuario
        var usuario = (Usuario) authentication.getPrincipal();

        var tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenJWTDTO(tokenJWT));
    }
}