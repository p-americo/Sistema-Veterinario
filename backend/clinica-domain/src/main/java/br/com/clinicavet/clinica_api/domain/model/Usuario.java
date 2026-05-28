package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter(lombok.AccessLevel.PROTECTED)
    private String login; // Armazenará o CPF para clientes ou CRMV para funcionários

    @Column(nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private String senha; // Armazenará o HASH da senha

    @Enumerated(EnumType.STRING)
    @Setter(lombok.AccessLevel.PROTECTED)
    private EnumUsuarioRole role;

    @OneToOne
    @JoinColumn(name = "pessoa_id", referencedColumnName = "id")
    @JsonIgnore
    @Setter(lombok.AccessLevel.PROTECTED)
    private Pessoa pessoa;

    // Regras de Negócio (DDD)

    public static Usuario criarUsuario(String login, String senhaHash, EnumUsuarioRole role, Pessoa pessoa) {
        if (login == null || login.isBlank()) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("O login é obrigatório.");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A senha é obrigatória.");
        }
        if (role == null) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("O perfil de acesso é obrigatório.");
        }
        if (pessoa == null) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A pessoa associada é obrigatória.");
        }
        Usuario usuario = new Usuario();
        usuario.login = login;
        usuario.senha = senhaHash;
        usuario.role = role;
        usuario.pessoa = pessoa;
        return usuario;
    }

    public void alterarSenha(String novaSenhaHash) {
        if (novaSenhaHash == null || novaSenhaHash.isBlank()) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A nova senha não pode ser vazia.");
        }
        this.senha = novaSenhaHash;
    }

    // MÉTODOS DA INTERFACE UserDetails
    // O Spring Security usará estes métodos para gerenciar a autenticação e autorização

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define o "perfil" do usuário (ex: CLIENTE, VETERINARIO)
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}