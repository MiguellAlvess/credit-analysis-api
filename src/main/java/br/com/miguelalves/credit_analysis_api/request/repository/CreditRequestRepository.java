package br.com.miguelalves.credit_analysis_api.request.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, UUID> {

}
