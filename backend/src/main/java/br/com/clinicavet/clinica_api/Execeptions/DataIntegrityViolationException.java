package br.com.clinicavet.clinica_api.Execeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para ser lançada quando uma regra de integridade de dados é violada,
 * como tentar cadastrar um CPF ou e-mail que já existe.
 * A anotação @ResponseStatus(HttpStatus.CONFLICT) define que, por padrão,
 * esta exceção resultará em um status HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict é um bom status para dados duplicados
public class DataIntegrityViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataIntegrityViolationException(String message) {
        super(message);
    }
}