package br.com.clinicavet.clinica_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prontuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    // Relacionamento um-para-um: Um animal tem um prontuário.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false, unique = true)
    @Setter
    private Animal animal;

    @Column(name = "alergias_conhecidas", columnDefinition = "TEXT")
    @Setter
    private String alergiasConhecidas;

    @Column(name = "condicoes_preexistentes", columnDefinition = "TEXT")
    @Setter
    private String condicoesPreexistentes;

    // Um prontuário tem uma lista de entradas de histórico
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegistroProntuario> registros = new ArrayList<>();

    // Regra de negócio (DDD)
    public void adicionarRegistro(RegistroProntuario registro) {
        if (registro == null) {
            throw new IllegalArgumentException("O registro não pode ser nulo.");
        }
        registro.setProntuario(this);
        this.registros.add(registro);
    }
}