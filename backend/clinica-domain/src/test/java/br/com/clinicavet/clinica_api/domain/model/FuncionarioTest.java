package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    @Test
    void alterarCargo_ParaVeterinarioComCrmvValido_DeveFuncionar() {
        Funcionario funcionario = new Funcionario();
        Cargo cargoVet = new Cargo();
        cargoVet.setCargo(EnumCargo.VETERINARIO);

        funcionario.alterarCargo(cargoVet, "CRMV-SP 12345");

        assertEquals(cargoVet, funcionario.getCargo());
        assertEquals("CRMV-SP 12345", funcionario.getCrmv());
    }

    @Test
    void alterarCargo_ParaVeterinarioSemCrmv_DeveLancarExcecao() {
        Funcionario funcionario = new Funcionario();
        Cargo cargoVet = new Cargo();
        cargoVet.setCargo(EnumCargo.VETERINARIO);

        assertThrows(BusinessRuleException.class, () -> {
            funcionario.alterarCargo(cargoVet, null);
        });

        assertThrows(BusinessRuleException.class, () -> {
            funcionario.alterarCargo(cargoVet, "   ");
        });
    }

    @Test
    void alterarCargo_ParaNaoVeterinarioSemCrmv_DeveFuncionar() {
        Funcionario funcionario = new Funcionario();
        Cargo cargoRec = new Cargo();
        cargoRec.setCargo(EnumCargo.RECEPCIONISTA);

        funcionario.alterarCargo(cargoRec, null);

        assertEquals(cargoRec, funcionario.getCargo());
        assertNull(funcionario.getCrmv());
    }

    @Test
    void alterarCargo_ParaNaoVeterinarioComCrmv_DeveLancarExcecao() {
        Funcionario funcionario = new Funcionario();
        Cargo cargoRec = new Cargo();
        cargoRec.setCargo(EnumCargo.RECEPCIONISTA);

        assertThrows(BusinessRuleException.class, () -> {
            funcionario.alterarCargo(cargoRec, "CRMV-SP 12345");
        });
    }
}
