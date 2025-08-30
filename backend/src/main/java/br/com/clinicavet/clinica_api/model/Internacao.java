package br.com.clinicavet.clinica_api.model;

import br.com.clinicavet.clinica_api.model.enums.EnumInternacaoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "internacao")
@Getter
@Setter
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumInternacaoStatus status;
}
