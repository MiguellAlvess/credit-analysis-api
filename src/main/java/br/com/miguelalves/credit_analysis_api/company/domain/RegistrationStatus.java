package br.com.miguelalves.credit_analysis_api.company.domain;

public enum RegistrationStatus {

    ACTIVE,
    SUSPENDED,
    CLOSED;

    public static RegistrationStatus fromBrasilApi(String status) {
        if (status == null || status.isBlank()) {
            return CLOSED;
        }
        return switch (status.trim().toUpperCase()) {
            case "ATIVA" -> ACTIVE;
            case "SUSPENSA" -> SUSPENDED;
            default -> CLOSED;
        };
    }
}
