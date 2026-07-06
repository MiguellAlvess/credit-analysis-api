package br.com.miguelalves.credit_analysis_api.company.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Cnpj(String value) {

    public Cnpj {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CNPJ is required");
        }
        value = value.replaceAll("\\D", "");
        if (value.length() != 14) {
            throw new IllegalArgumentException("CNPJ must have 14 digits");
        }
    }

    public static Cnpj of(String value) {
        return new Cnpj(value);
    }
}