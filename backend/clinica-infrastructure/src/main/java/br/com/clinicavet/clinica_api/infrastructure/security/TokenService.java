package br.com.clinicavet.clinica_api.infrastructure.security;

import br.com.clinicavet.clinica_api.domain.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-minutes:120}")
    private Integer expirationMinutes;

    private static final String ISSUER = "Clinica Vet API";

    public String gerarToken(Usuario usuario) {
        try {
            log.debug("Gerando token JWT para o usuário '{}'", usuario.getLogin());
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            
            String nomeUsuario = usuario.getPessoa() != null ? usuario.getPessoa().getNome() : null;
            
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getLogin())
                    .withClaim("role", usuario.getRole().name())
                    .withClaim("userId", usuario.getId())
                    .withClaim("nomeUsuario", nomeUsuario)
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            log.error("Erro ao gerar token JWT para o usuário '{}': ", usuario.getLogin(), exception);
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
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.warn("Falha na verificação de token JWT: {}", exception.getMessage());
            throw new JWTVerificationException("Token JWT inválido ou expirado!", exception);
        }
    }

    private Instant dataExpiracao() {
        return Instant.now().plus(Duration.ofMinutes(expirationMinutes));
    }
}
