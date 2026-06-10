package br.com.miguelalves.credit_analysis_api.decision.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;

public interface CreditDecisionRepository extends JpaRepository<CreditDecision, Long> {
}
