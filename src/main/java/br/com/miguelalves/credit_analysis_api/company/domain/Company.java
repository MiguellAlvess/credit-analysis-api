package br.com.miguelalves.credit_analysis_api.company.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "companies")
public class Company {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private RegistrationStatus registrationStatus;

    @Column(name = "postal_code", nullable = false, length = 8)
    private String postalCode;

    private String city;

    private String state;

    @Column(name = "founded_at")
    private LocalDate foundedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Company() {
    }

    private Company(
            String cnpj,
            String name,
            RegistrationStatus registrationStatus,
            String postalCode,
            String city,
            String state,
            LocalDate foundedAt) {
        this.id = UUID.randomUUID();
        this.cnpj = normalizeCnpj(cnpj);
        this.name = validateName(name);
        this.registrationStatus = registrationStatus;
        this.postalCode = normalizePostalCode(postalCode);
        this.city = city;
        this.state = state;
        this.foundedAt = foundedAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Company create(
            String cnpj,
            String name,
            RegistrationStatus registrationStatus,
            String postalCode,
            String city,
            String state,
            LocalDate foundedAt) {
        return new Company(cnpj, name, registrationStatus, postalCode, city, state, foundedAt);
    }

    public void updateRegistrationData(
            String name,
            RegistrationStatus registrationStatus,
            String postalCode,
            String city,
            String state,
            LocalDate foundedAt) {
        this.name = validateName(name);
        this.registrationStatus = registrationStatus;
        this.postalCode = normalizePostalCode(postalCode);
        this.city = city;
        this.state = state;
        this.foundedAt = foundedAt;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return RegistrationStatus.ACTIVE.equals(this.registrationStatus);
    }

    public boolean isInactive() {
        return !isActive();
    }

    public boolean hasMinimumOperatingTime() {
        if (this.foundedAt == null) {
            return false;
        }
        return Period.between(this.foundedAt, LocalDate.now()).getYears() >= 1;
    }

    public boolean hasMoreThanFiveYearsOfOperation() {
        if (this.foundedAt == null) {
            return false;
        }

        return Period.between(this.foundedAt, LocalDate.now()).getYears() > 5;
    }

    private String normalizeCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ is required");
        }
        String normalized = cnpj.replaceAll("\\D", "");
        if (normalized.length() != 14) {
            throw new IllegalArgumentException("CNPJ must have 14 digits");
        }
        return normalized;
    }

    private String normalizePostalCode(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        String normalized = postalCode.replaceAll("\\D", "");

        if (normalized.length() != 8) {
            throw new IllegalArgumentException("Postal code must have 8 digits");
        }
        return normalized;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
        return name.trim();
    }
}
