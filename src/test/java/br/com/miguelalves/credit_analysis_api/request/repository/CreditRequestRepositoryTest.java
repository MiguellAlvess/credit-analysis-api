package br.com.miguelalves.credit_analysis_api.request.repository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

@DataJpaTest
@ActiveProfiles("it")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRequestRepositoryTest {

    @Autowired
    private CreditRequestRepository creditRequestRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldSaveCreditRequestWithValidData() {
        Company company = companyRepository.save(createCompany());
        CreditRequest creditRequestToSave = CreditRequest.create(
                company,
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00"));

        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequestToSave);
        Optional<CreditRequest> persistedCreditRequest = creditRequestRepository.findById(savedCreditRequest.getId());

        assertThat(persistedCreditRequest).isPresent();
        assertThat(persistedCreditRequest.get().getCompany().getId()).isEqualTo(company.getId());
        assertThat(persistedCreditRequest.get().getRequestedAmount())
                .isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(persistedCreditRequest.get().getAnnualRevenue())
                .isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(persistedCreditRequest.get().getStatus())
                .isEqualTo(CreditRequestStatus.PENDING);
    }

    @Test
    void shouldFindCreditRequestByIdWhenCreditRequestExists() {
        Company company = companyRepository.save(createCompany());
        CreditRequest creditRequest = CreditRequest.create(
                company,
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00"));

        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);
        Optional<CreditRequest> foundCreditRequest = creditRequestRepository.findById(savedCreditRequest.getId());

        assertThat(foundCreditRequest).isPresent();
        assertThat(foundCreditRequest.get().getId()).isEqualTo(savedCreditRequest.getId());
        assertThat(foundCreditRequest.get().getCompany().getId()).isEqualTo(company.getId());
    }

    @Test
    void shouldReturnEmptyWhenCreditRequestDoesNotExistById() {
        Optional<CreditRequest> foundCreditRequest = creditRequestRepository.findById(java.util.UUID.randomUUID());

        assertThat(foundCreditRequest).isEmpty();
    }

    @Test
    void shouldPersistScoreRiskLevelApprovedLimitAndStatus() {
        Company company = companyRepository.save(createCompany());
        CreditRequest creditRequest = CreditRequest.create(
                company,
                new BigDecimal("200000.00"),
                new BigDecimal("1000000.00"));
        creditRequest.registerScore(1000, RiskLevel.LOW);
        creditRequest.approve();

        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);
        Optional<CreditRequest> persistedCreditRequest = creditRequestRepository.findById(savedCreditRequest.getId());

        assertThat(persistedCreditRequest).isPresent();
        assertThat(persistedCreditRequest.get().getScore()).isEqualTo(1000);
        assertThat(persistedCreditRequest.get().getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(persistedCreditRequest.get().getApprovedLimit())
                .isEqualByComparingTo(new BigDecimal("300000.00"));
        assertThat(persistedCreditRequest.get().getStatus())
                .isEqualTo(CreditRequestStatus.APPROVED);
    }
}
