package br.com.miguelalves.credit_analysis_api.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCnpj(String cnpj);
}
