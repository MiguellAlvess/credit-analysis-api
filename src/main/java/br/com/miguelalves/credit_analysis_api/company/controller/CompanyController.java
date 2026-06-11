package br.com.miguelalves.credit_analysis_api.company.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.credit_analysis_api.company.dto.CompanyResponse;
import br.com.miguelalves.credit_analysis_api.company.dto.CreateCompanyRequest;
import br.com.miguelalves.credit_analysis_api.company.dto.UpdateCompanyRequest;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(@RequestBody @Valid CreateCompanyRequest request) {
        return companyService.createCompany(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse getCompanyById(@PathVariable UUID id) {
        return companyService.getCompanyById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse updateCompany(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCompanyRequest request) {
        return companyService.updateCompany(id, request);
    }
}
