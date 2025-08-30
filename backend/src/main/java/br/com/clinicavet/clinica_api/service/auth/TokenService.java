package br.com.clinicavet.clinica_api.service.auth;

import br.com.clinicavet.clinica_api.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Injeta a chave secreta a partir do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "Clinica Vet API";

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER) // Sender token
                    .withSubject(usuario.getLogin()) // Identify user (login/CPF/CRMV)
                    .withClaim("role", usuario.getRole().name()) // Define the role of the user
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo); // Assign the token with the algorithm ( key secret )
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject(); //
        } catch (JWTVerificationException exception) {

            throw new JWTVerificationException("Token JWT inválido ou expirado!");
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}