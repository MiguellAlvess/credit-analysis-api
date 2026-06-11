package br.com.miguelalves.credit_analysis_api.shared.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.miguelalves.credit_analysis_api.shared.exception.dto.DataValidationError;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;
import jakarta.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        var errors = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(
                errors.stream()
                        .map(error -> new DataValidationError(
                                error.getField(),
                                error.getDefaultMessage()))
                        .toList());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(
            ValidationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(
            BusinessException exception) {
        return ResponseEntity.unprocessableEntity().body(exception.getMessage());
    }
}
