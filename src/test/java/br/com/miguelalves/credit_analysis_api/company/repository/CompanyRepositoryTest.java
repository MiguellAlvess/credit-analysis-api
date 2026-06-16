package br.com.miguelalves.credit_analysis_api.company.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;

@DataJpaTest
@ActiveProfiles("it")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldSaveCompanyWithValidData() {
        Company companyToSave = createCompany();

        Company savedCompany = companyRepository.save(companyToSave);

        Optional<Company> persistedCompany = companyRepository.findById(savedCompany.getId());

        assertThat(persistedCompany).isPresent();
        assertThat(persistedCompany.get().getCnpj()).isEqualTo(companyToSave.getCnpj());
        assertThat(persistedCompany.get().getName()).isEqualTo(companyToSave.getName());
        assertThat(persistedCompany.get().getRegistrationStatus())
                .isEqualTo(companyToSave.getRegistrationStatus());
        assertThat(persistedCompany.get().getPostalCode())
                .isEqualTo(companyToSave.getPostalCode());
        assertThat(persistedCompany.get().getCity()).isEqualTo(companyToSave.getCity());
        assertThat(persistedCompany.get().getState()).isEqualTo(companyToSave.getState());
        assertThat(persistedCompany.get().getFoundedAt()).isEqualTo(companyToSave.getFoundedAt());
    }

    @Test
    void shouldFindCompanyByCnpjWhenCompanyExists() {
        Company company = companyRepository.save(createCompany());

        Optional<Company> foundCompany = companyRepository.findByCnpj(company.getCnpj());

        assertThat(foundCompany).isPresent();
        assertThat(foundCompany.get().getId()).isEqualTo(company.getId());
        assertThat(foundCompany.get().getCnpj()).isEqualTo(company.getCnpj());
    }

    @Test
    void shouldReturnEmptyWhenCompanyDoesNotExistByCnpj() {
        Optional<Company> foundCompany = companyRepository.findByCnpj("00000000000000");

        assertThat(foundCompany).isEmpty();
    }
}
