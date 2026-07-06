package br.com.miguelalves.credit_analysis_api.company.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record PostalCode(String value) {

    public PostalCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        value = value.replaceAll("\\D", "");
        if (value.length() != 8) {
            throw new IllegalArgumentException("Postal code must have 8 digits");
        }
    }

    public static PostalCode of(String value) {
        return new PostalCode(value);
    }
}
