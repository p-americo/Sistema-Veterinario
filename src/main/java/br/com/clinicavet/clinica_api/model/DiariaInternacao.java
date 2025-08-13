package br.com.clinicavet.clinica_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime; // MUDANÇA AQUI: de LocalDate para LocalDateTime
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diaria_internacao")
@Getter
@Setter
public class DiariaInternacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORREÇÃO: Alterado para LocalDateTime para corresponder ao tipo TIMESTAMP do banco
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "observacoes_clinicas", nullable = false, columnDefinition = "TEXT")
    private String observacoesClinicas;

    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "peso_no_dia")
    private BigDecimal pesoNoDia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internacao_id", nullable = false)
    private Internacao internacao;

    @OneToMany(mappedBy = "diaria", cascade = CascadeType.ALL)
    private List<AdministracaoMedicamento> medicamentos = new ArrayList<>();
}
