package br.com.miguelalves.credit_analysis_api.company.domain;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class CompanyTest {

    @Test
    void shouldCreateCompanyWithValidData() {
        Company company = Company.create(
                "12.345.678/0001-95",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2));

        assertThat(company.getId()).isNotNull();
        assertThat(company.getCnpj().value()).isEqualTo("12345678000195");
        assertThat(company.getName()).isEqualTo("Empresa XPTO");
        assertThat(company.getRegistrationStatus()).isEqualTo(RegistrationStatus.ACTIVE);
        assertThat(company.getPostalCode().value()).isEqualTo("58400000");
        assertThat(company.getCity()).isEqualTo("Campina Grande");
        assertThat(company.getState()).isEqualTo("PB");
        assertThat(company.getFoundedAt()).isEqualTo(LocalDate.now().minusYears(2));
        assertThat(company.getCreatedAt()).isNotNull();
        assertThat(company.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenCnpjIsNull() {
        assertThatThrownBy(() -> Company.create(
                null,
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CNPJ is required");
    }

    @Test
    void shouldThrowExceptionWhenCnpjDoesNotHaveFourteenDigits() {
        assertThatThrownBy(() -> Company.create(
                "123",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CNPJ must have 14 digits");
    }

    @Test
    void shouldThrowExceptionWhenPostalCodeIsNull() {
        assertThatThrownBy(() -> Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                null,
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Postal code is required");
    }

    @Test
    void shouldThrowExceptionWhenPostalCodeDoesNotHaveEightDigits() {
        assertThatThrownBy(() -> Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "123",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Postal code must have 8 digits");
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> Company.create(
                "12345678000195",
                " ",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Company name is required");
    }

    @Test
    void shouldReturnTrueWhenCompanyIsActive() {
        Company company = createActiveCompanyFoundedYearsAgo(2);

        assertThat(company.isActive()).isTrue();
    }

    @Test
    void shouldReturnTrueWhenCompanyIsInactive() {
        Company company = Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.SUSPENDED,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(2));

        assertThat(company.isInactive()).isTrue();
    }

    @Test
    void shouldReturnTrueWhenCompanyHasAtLeastOneYearOfOperation() {
        Company company = createActiveCompanyFoundedYearsAgo(1);

        assertThat(company.hasMinimumOperatingTime()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCompanyHasLessThanOneYearOfOperation() {
        Company company = Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusMonths(8));

        assertThat(company.hasMinimumOperatingTime()).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCompanyHasMoreThanFiveYearsOfOperation() {
        Company company = createActiveCompanyFoundedYearsAgo(6);

        assertThat(company.hasMoreThanFiveYearsOfOperation()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCompanyHasFiveYearsOfOperation() {
        Company company = createActiveCompanyFoundedYearsAgo(5);

        assertThat(company.hasMoreThanFiveYearsOfOperation()).isFalse();
    }

    @Test
    void shouldUpdateRegistrationData() {
        Company company = createActiveCompanyFoundedYearsAgo(2);

        company.updateRegistrationData(
                "Empresa Atualizada",
                RegistrationStatus.SUSPENDED,
                "58000-000",
                "João Pessoa",
                "PB",
                LocalDate.now().minusYears(3));

        assertThat(company.getName()).isEqualTo("Empresa Atualizada");
        assertThat(company.getRegistrationStatus()).isEqualTo(RegistrationStatus.SUSPENDED);
        assertThat(company.getPostalCode().value()).isEqualTo("58000000");
        assertThat(company.getCity()).isEqualTo("João Pessoa");
        assertThat(company.getState()).isEqualTo("PB");
        assertThat(company.getFoundedAt()).isEqualTo(LocalDate.now().minusYears(3));
        assertThat(company.getUpdatedAt()).isNotNull();
    }

    private Company createActiveCompanyFoundedYearsAgo(int years) {
        return Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400-000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(years));
    }
}