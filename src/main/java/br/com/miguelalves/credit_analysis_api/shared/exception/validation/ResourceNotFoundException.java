package br.com.miguelalves.credit_analysis_api.shared.exception.validation;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
