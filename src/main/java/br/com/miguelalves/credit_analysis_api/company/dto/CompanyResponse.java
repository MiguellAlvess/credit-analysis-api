package br.com.miguelalves.credit_analysis_api.company.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;

public record CompanyResponse(
                UUID id,
                String cnpj,
                String name,
                RegistrationStatus registrationStatus,
                String postalCode,
                String city,
                String state,
                LocalDate foundedAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
