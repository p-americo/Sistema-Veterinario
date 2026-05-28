package br.com.clinicavet.clinica_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_prontuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroProntuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    // Muitas entradas pertencem a um prontuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private Prontuario prontuario;

    // Relacionamento opcional para saber qual agendamento gerou esta entrada
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id")
    @Setter
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    @Setter
    private Funcionario veterinarioResponsavel;

    @Column(name = "data_hora", nullable = false)
    @Setter
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internacao_id") // Permite nulos
    @Setter
    private Internacao internacao;

    @Column(name = "peso_no_dia")
    @Setter
    private BigDecimal pesoNoDia;

    @Column(columnDefinition = "TEXT")
    @Setter
    private String observacoesClinicas; // Exame físico, anamnese

    @Column(columnDefinition = "TEXT")
    @Setter
    private String diagnostico;

    // Cada entrada pode ter uma lista de medicamentos que foram administrados
    @OneToMany(mappedBy = "entradaProntuario", cascade = CascadeType.ALL)
    private List<AdministracaoMedicamento> medicamentosAdministrados = new ArrayList<>();

    // Regra de negócio (DDD)
    public void associarAoProntuario(Prontuario prontuario) {
        if (prontuario == null) {
            throw new IllegalArgumentException("O prontuário associado não pode ser nulo.");
        }
        prontuario.adicionarRegistro(this);
    }
}
