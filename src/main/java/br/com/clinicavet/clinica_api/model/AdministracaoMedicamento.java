package br.com.clinicavet.clinica_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "administracoes_medicamentos")
@Getter
@Setter
public class AdministracaoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_prontuario_id")
    private RegistroProntuario entradaProntuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_executor_id", nullable = false)
    private Funcionario funcionarioExecutor;

    @Column(nullable = false)
    private BigDecimal quantidadeAdministrada;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String dosagem;

    // CAMPO ADICIONADO PARA CORRIGIR O ERRO
    // Este é o "outro lado" da relação, o dono do mapeamento.
    // Uma administração de medicamento PODE pertencer a uma diária de internação.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaria_internacao_id") // Nome da coluna da chave estrangeira no banco
    private DiariaInternacao diaria;
}
