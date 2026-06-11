package br.com.miguelalves.credit_analysis_api.integration.exception;

public class BrasilApiException extends RuntimeException {

    public BrasilApiException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
