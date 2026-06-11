package br.com.miguelalves.credit_analysis_api.company.common;

import java.time.LocalDate;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.company.dto.CreateCompanyRequest;
import br.com.miguelalves.credit_analysis_api.company.dto.UpdateCompanyRequest;

public class CompanyConstants {

    public static final UUID COMPANY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final CreateCompanyRequest CREATE_COMPANY_REQUEST = new CreateCompanyRequest(
            "12345678000195",
            "Empresa XPTO",
            RegistrationStatus.ACTIVE,
            "58400000",
            "Campina Grande",
            "PB",
            LocalDate.of(2018, 1, 1));

    public static final UpdateCompanyRequest UPDATE_COMPANY_REQUEST = new UpdateCompanyRequest(
            "Empresa Atualizada",
            RegistrationStatus.SUSPENDED,
            "58000000",
            "João Pessoa",
            "PB",
            LocalDate.of(2019, 1, 1));

    public static Company createCompany() {
        return Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400000",
                "Campina Grande",
                "PB",
                LocalDate.of(2018, 1, 1));
    }

    private CompanyConstants() {
    }
}
