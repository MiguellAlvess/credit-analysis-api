package br.com.miguelalves.credit_analysis_api.company.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "cnpj", nullable = false, unique = true, length = 14))
    private Cnpj cnpj;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private RegistrationStatus registrationStatus;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "postal_code", nullable = false, length = 8))
    private PostalCode postalCode;

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
        this.cnpj = Cnpj.of(cnpj);
        this.name = validateName(name);
        this.registrationStatus = registrationStatus;
        this.postalCode = PostalCode.of(postalCode);
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
        this.postalCode = PostalCode.of(postalCode);
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

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
        return name.trim();
    }
}
