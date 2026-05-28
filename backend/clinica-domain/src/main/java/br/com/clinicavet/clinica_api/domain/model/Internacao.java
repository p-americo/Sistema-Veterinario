package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "internacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @Setter
    private Animal animal;

    @Column(name = "data_entrada", nullable = false)
    @Setter
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    @Setter(lombok.AccessLevel.PROTECTED)
    private LocalDateTime dataSaida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private EnumInternacaoStatus status = EnumInternacaoStatus.ATIVA;

    @OneToMany(mappedBy = "internacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DiariaInternacao> diarias = new HashSet<>();

    // Métodos de Regra de Negócio (DDD)

    public void darAlta() {
        if (this.status == EnumInternacaoStatus.ALTA) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Esta internação já recebeu alta.");
        }
        this.status = EnumInternacaoStatus.ALTA;
        this.dataSaida = LocalDateTime.now();
    }

    public void adicionarDiaria(DiariaInternacao diaria) {
        if (this.status == EnumInternacaoStatus.ALTA) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Não é possível adicionar diárias a uma internação com alta.");
        }
        diaria.setInternacao(this);
        this.diarias.add(diaria);
    }
}
