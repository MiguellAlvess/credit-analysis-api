package br.com.miguelalves.credit_analysis_api.decision.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecisionType;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;

@DataJpaTest
@ActiveProfiles("it")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditDecisionRepositoryTest {

    @Autowired
    private CreditDecisionRepository creditDecisionRepository;

    @Autowired
    private CreditRequestRepository creditRequestRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldSaveApprovedCreditDecisionWithValidData() {
        Company company = companyRepository.save(createCompany());
        CreditRequest creditRequest = CreditRequest.create(
                company,
                new BigDecimal("200000.00"),
                new BigDecimal("1000000.00"));
        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);

        CreditDecision decisionToSave = CreditDecision.approve(
                savedCreditRequest,
                new BigDecimal("300000.00"),
                "Credit approved");
        CreditDecision savedDecision = creditDecisionRepository.save(decisionToSave);
        Optional<CreditDecision> persistedDecision = creditDecisionRepository.findById(savedDecision.getId());

        assertThat(persistedDecision).isPresent();
        assertThat(persistedDecision.get().getCreditRequest().getId())
                .isEqualTo(savedCreditRequest.getId());
        assertThat(persistedDecision.get().getDecision())
                .isEqualTo(CreditDecisionType.APPROVED);
        assertThat(persistedDecision.get().getApprovedAmount())
                .isEqualByComparingTo(new BigDecimal("300000.00"));
        assertThat(persistedDecision.get().getReason())
                .isEqualTo("Credit approved");
        assertThat(persistedDecision.get().getDecidedAt()).isNotNull();
    }

    @Test
    void shouldFindCreditDecisionByCreditRequestIdWhenDecisionExists() {
        Company company = companyRepository.save(createCompany());
        CreditRequest creditRequest = CreditRequest.create(
                company,
                new BigDecimal("200000.00"),
                new BigDecimal("1000000.00"));
        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);

        CreditDecision decision = CreditDecision.approve(
                savedCreditRequest,
                new BigDecimal("300000.00"),
                "Credit approved");
        creditDecisionRepository.save(decision);
        Optional<CreditDecision> foundDecision = creditDecisionRepository
                .findByCreditRequestId(savedCreditRequest.getId());

        assertThat(foundDecision).isPresent();
        assertThat(foundDecision.get().getCreditRequest().getId())
                .isEqualTo(savedCreditRequest.getId());
        assertThat(foundDecision.get().getDecision())
                .isEqualTo(CreditDecisionType.APPROVED);
    }

    @Test
    void shouldReturnEmptyWhenCreditDecisionDoesNotExistByCreditRequestId() {
        Optional<CreditDecision> foundDecision = creditDecisionRepository.findByCreditRequestId(UUID.randomUUID());

        assertThat(foundDecision).isEmpty();
    }
}
