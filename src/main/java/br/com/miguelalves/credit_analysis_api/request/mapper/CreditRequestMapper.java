package br.com.miguelalves.credit_analysis_api.request.mapper;

import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;

public class CreditRequestMapper {

    private CreditRequestMapper() {
    }

    public static CreditRequestResponse fromCreditRequestToResponse(CreditRequest creditRequest) {
        return new CreditRequestResponse(
                creditRequest.getId(),
                creditRequest.getCompany().getId(),
                creditRequest.getCompany().getName(),
                creditRequest.getCompany().getCnpj().value(),
                creditRequest.getRequestedAmount(),
                creditRequest.getAnnualRevenue(),
                creditRequest.getScore(),
                creditRequest.getRiskLevel(),
                creditRequest.getApprovedLimit(),
                creditRequest.getStatus(),
                creditRequest.getCreatedAt(),
                creditRequest.getUpdatedAt());
    }
}
