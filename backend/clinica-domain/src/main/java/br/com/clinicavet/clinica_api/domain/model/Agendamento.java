package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento; // << CRIAR ESTE ENUM
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @Setter
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    @Setter
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @Setter
    private Cliente cliente;

    @Column(name = "data_hora_agendamento", nullable = false)
    @Setter
    private LocalDateTime dataHoraAgendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private EnumAgendamento status = EnumAgendamento.AGENDADO;

    @Column(length = 255)
    @Setter
    private String observacoes;

    // Métodos de Regra de Negócio (DDD)

    public void associarClienteEAnimal(Cliente cliente, Animal animal) {
        if (cliente == null || animal == null) {
            throw new IllegalArgumentException("Cliente e Animal não podem ser nulos.");
        }
        if (animal.getCliente() == null || !animal.getCliente().getId().equals(cliente.getId())) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("O animal informado não pertence ao cliente especificado.");
        }
        this.cliente = cliente;
        this.animal = animal;
    }

    public void cancelar() {
        if (this.status == EnumAgendamento.REALIZADO) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Não é possível cancelar um agendamento já realizado.");
        }
        this.status = EnumAgendamento.CANCELADO;
    }

    public void confirmar() {
        if (this.status != EnumAgendamento.AGENDADO) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Apenas agendamentos com status AGENDADO podem ser confirmados.");
        }
        this.status = EnumAgendamento.CONFIRMADO;
    }

    public void realizar() {
        if (this.status == EnumAgendamento.CANCELADO) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Não é possível realizar um agendamento cancelado.");
        }
        this.status = EnumAgendamento.REALIZADO;
    }

    public void atualizarDados(LocalDateTime dataHora, String observacoes) {
        if (this.status == EnumAgendamento.CANCELADO || this.status == EnumAgendamento.REALIZADO) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Não é possível alterar um agendamento com status " + this.status);
        }
        this.dataHoraAgendamento = dataHora;
        this.observacoes = observacoes;
    }
}