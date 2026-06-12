package br.com.miguelalves.credit_analysis_api.policy.common;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

public class CreditPolicyConstants {

        public static CreditRequest createCreditRequestWithRiskLevel(
                        RiskLevel riskLevel,
                        BigDecimal requestedAmount,
                        BigDecimal annualRevenue) {
                Company company = Company.create(
                                "12345678000195",
                                "Empresa XPTO",
                                RegistrationStatus.ACTIVE,
                                "58400000",
                                "Campina Grande",
                                "PB",
                                LocalDate.of(2018, 1, 1));
                CreditRequest creditRequest = CreditRequest.create(
                                company,
                                requestedAmount,
                                annualRevenue);
                creditRequest.registerScore(600, riskLevel);
                return creditRequest;
        }

        private CreditPolicyConstants() {
        }
}
