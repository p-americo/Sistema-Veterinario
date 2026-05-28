package br.com.clinicavet.clinica_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "administracoes_medicamentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdministracaoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private Medicamento medicamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_prontuario_id")
    @Setter(lombok.AccessLevel.PROTECTED)
    private RegistroProntuario entradaProntuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_executor_id", nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private Funcionario funcionarioExecutor;

    @Column(nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private BigDecimal quantidadeAdministrada;

    @Column(nullable = false)
    @Setter(lombok.AccessLevel.PROTECTED)
    private LocalDateTime dataHora;

    @Setter(lombok.AccessLevel.PROTECTED)
    private String dosagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaria_internacao_id")
    @Setter(lombok.AccessLevel.PROTECTED)
    private DiariaInternacao diaria;

    // Métodos de Regra de Negócio (DDD)

    public void associarAoProntuario(RegistroProntuario prontuario) {
        if (prontuario == null) {
            throw new IllegalArgumentException("O registro do prontuário não pode ser nulo.");
        }
        this.entradaProntuario = prontuario;
        if (!prontuario.getMedicamentosAdministrados().contains(this)) {
            prontuario.getMedicamentosAdministrados().add(this);
        }
    }

    public void associarDiaria(DiariaInternacao diaria) {
        if (diaria == null) {
            throw new IllegalArgumentException("A diária de internação não pode ser nula.");
        }
        this.diaria = diaria;
        if (!diaria.getMedicamentos().contains(this)) {
            diaria.getMedicamentos().add(this);
        }
    }

    public void registrarAdministracao(Medicamento medicamento, Funcionario executor, BigDecimal quantidade, LocalDateTime dataHora, String dosagem) {
        if (medicamento == null) {
            throw new IllegalArgumentException("O medicamento é obrigatório.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("O funcionário executor é obrigatório.");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A quantidade administrada deve ser maior que zero.");
        }
        
        if (medicamento.getProduto() != null) {
            medicamento.getProduto().debitarEstoque(quantidade.intValue());
        }

        this.medicamento = medicamento;
        this.funcionarioExecutor = executor;
        this.quantidadeAdministrada = quantidade;
        this.dataHora = dataHora;
        this.dosagem = dosagem;
    }
}
