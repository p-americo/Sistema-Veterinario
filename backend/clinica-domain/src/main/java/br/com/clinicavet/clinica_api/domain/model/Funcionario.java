package br.com.clinicavet.clinica_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "pessoa_id")
public class Funcionario extends Pessoa {


    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Cargo cargo;

    @Column(name = "crmv", unique = true)
    @Setter(AccessLevel.PROTECTED)
    private String crmv;

    // Métodos de Regra de Negócio (DDD)

    public void alterarCargo(Cargo novoCargo, String crmv) {
        if (novoCargo == null) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Cargo não pode ser nulo.");
        }
        if (novoCargo.getCargo() == br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo.VETERINARIO) {
            if (crmv == null || crmv.isBlank()) {
                throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("CRMV é obrigatório para o cargo de Veterinário.");
            }
            this.crmv = crmv;
        } else {
            if (crmv != null && !crmv.isBlank()) {
                throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("CRMV só pode ser preenchido para o cargo de Veterinário.");
            }
            this.crmv = null;
        }
        this.cargo = novoCargo;
    }
}