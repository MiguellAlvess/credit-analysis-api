package br.com.miguelalves.credit_analysis_api.shared.exception.dto;

public record DataValidationError(
        String field,
        String message) {
}
