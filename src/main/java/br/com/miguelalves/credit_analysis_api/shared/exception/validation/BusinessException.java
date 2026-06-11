package br.com.miguelalves.credit_analysis_api.shared.exception.validation;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
