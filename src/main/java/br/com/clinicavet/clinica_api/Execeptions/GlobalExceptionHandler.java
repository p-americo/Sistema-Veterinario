package br.com.clinicavet.clinica_api.Execeptions;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice // Anotação que torna esta classe um handler de exceções global
public class GlobalExceptionHandler {

    // DTO simples para padronizar as respostas de erro
    public record ErrorResponse(int status, String message) {}

    // Handler para "Recurso não encontrado"
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        ErrorResponse resposta = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    // Handler para violação de integridade/dados duplicados (nosso erro de CPF)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        ErrorResponse resposta = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        // Retorna um status HTTP 409 Conflict, que é apropriado para dados duplicados
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    // Handler para outros erros de validação de negócio que você possa ter
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRules(IllegalArgumentException ex) {
        ErrorResponse resposta = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    // Handler para erros de validação do @Valid nos DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse resposta = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }
}