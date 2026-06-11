package br.com.miguelalves.credit_analysis_api.request.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreateCreditRequestRequest;

public class CreditRequestConstants {

    public static final UUID CREDIT_REQUEST_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID COMPANY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final CreateCreditRequestRequest CREATE_CREDIT_REQUEST_REQUEST = new CreateCreditRequestRequest(
            COMPANY_ID,
            new BigDecimal("100000.00"),
            new BigDecimal("500000.00"));

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

    public static CreditRequest createCreditRequest() {
        return CreditRequest.create(
                createCompany(),
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00"));
    }

    private CreditRequestConstants() {
    }
}
