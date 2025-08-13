package br.com.clinicavet.clinica_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_prontuario")
@Getter
@Setter
public class RegistroProntuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitas entradas pertencem a um prontuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    // Relacionamento opcional para saber qual agendamento gerou esta entrada
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id")
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Funcionario veterinarioResponsavel;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internacao_id") // Permite nulos
    private Internacao internacao;

    @Column(name = "peso_no_dia")
    private BigDecimal pesoNoDia;

    @Column(columnDefinition = "TEXT")
    private String observacoesClinicas; // Exame físico, anamnese

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    // Cada entrada pode ter uma lista de medicamentos que foram administrados
    @OneToMany(mappedBy = "entradaProntuario", cascade = CascadeType.ALL)
    private List<AdministracaoMedicamento> medicamentosAdministrados = new ArrayList<>();
}
