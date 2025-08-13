package br.com.clinicavet.clinica_api.model;

import br.com.clinicavet.clinica_api.model.enums.EnumServico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumServico tipo;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Funcionario veterinario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
