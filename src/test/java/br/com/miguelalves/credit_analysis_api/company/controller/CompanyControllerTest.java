package br.com.miguelalves.credit_analysis_api.company.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.COMPANY_ID;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.CREATE_COMPANY_REQUEST;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.UPDATE_COMPANY_REQUEST;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.dto.CompanyResponse;
import br.com.miguelalves.credit_analysis_api.company.mapper.CompanyMapper;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyService;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private CompanyService companyService;

        @Test
        void shouldCreateCompanyWithValidData() throws Exception {
                CompanyResponse response = CompanyMapper.fromCompanyToResponse(createCompany());

                when(companyService.createCompany(CREATE_COMPANY_REQUEST))
                                .thenReturn(response);

                mockMvc.perform(post("/api/companies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(CREATE_COMPANY_REQUEST)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.cnpj").value(CREATE_COMPANY_REQUEST.cnpj()))
                                .andExpect(jsonPath("$.name").value(CREATE_COMPANY_REQUEST.name()))
                                .andExpect(jsonPath("$.registrationStatus")
                                                .value(CREATE_COMPANY_REQUEST.registrationStatus().name()))
                                .andExpect(jsonPath("$.postalCode").value(CREATE_COMPANY_REQUEST.postalCode()))
                                .andExpect(jsonPath("$.city").value(CREATE_COMPANY_REQUEST.city()))
                                .andExpect(jsonPath("$.state").value(CREATE_COMPANY_REQUEST.state()));
        }

        @Test
        void shouldGetCompanyByIdWhenCompanyExists() throws Exception {
                CompanyResponse response = CompanyMapper.fromCompanyToResponse(createCompany());

                when(companyService.getCompanyById(COMPANY_ID))
                                .thenReturn(response);

                mockMvc.perform(get("/api/companies/{id}", COMPANY_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.cnpj").value(response.cnpj()))
                                .andExpect(jsonPath("$.name").value(response.name()))
                                .andExpect(jsonPath("$.registrationStatus").value(response.registrationStatus().name()))
                                .andExpect(jsonPath("$.postalCode").value(response.postalCode()))
                                .andExpect(jsonPath("$.city").value(response.city()))
                                .andExpect(jsonPath("$.state").value(response.state()));
        }

        @Test
        void shouldUpdateCompanyWithValidData() throws Exception {
                CompanyResponse response = new CompanyResponse(
                                COMPANY_ID,
                                CREATE_COMPANY_REQUEST.cnpj(),
                                UPDATE_COMPANY_REQUEST.name(),
                                UPDATE_COMPANY_REQUEST.registrationStatus(),
                                UPDATE_COMPANY_REQUEST.postalCode(),
                                UPDATE_COMPANY_REQUEST.city(),
                                UPDATE_COMPANY_REQUEST.state(),
                                UPDATE_COMPANY_REQUEST.foundedAt(),
                                createCompany().getCreatedAt(),
                                createCompany().getUpdatedAt());

                when(companyService.updateCompany(COMPANY_ID, UPDATE_COMPANY_REQUEST))
                                .thenReturn(response);

                mockMvc.perform(patch("/api/companies/{id}", COMPANY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UPDATE_COMPANY_REQUEST)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value(UPDATE_COMPANY_REQUEST.name()))
                                .andExpect(jsonPath("$.registrationStatus")
                                                .value(UPDATE_COMPANY_REQUEST.registrationStatus().name()))
                                .andExpect(jsonPath("$.postalCode").value(UPDATE_COMPANY_REQUEST.postalCode()))
                                .andExpect(jsonPath("$.city").value(UPDATE_COMPANY_REQUEST.city()))
                                .andExpect(jsonPath("$.state").value(UPDATE_COMPANY_REQUEST.state()));
        }

        @Test
        void shouldReturnBadRequestWhenCreatingCompanyWithInvalidData() throws Exception {
                mockMvc.perform(post("/api/companies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "cnpj": "",
                                                  "name": "",
                                                  "registrationStatus": null,
                                                  "postalCode": "",
                                                  "city": "Campina Grande",
                                                  "state": "PB",
                                                  "foundedAt": null
                                                }
                                                """))
                                .andExpect(status().isBadRequest());
        }
}
