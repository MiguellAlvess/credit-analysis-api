package br.com.miguelalves.credit_analysis_api.request.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREATE_CREDIT_REQUEST_REQUEST;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREDIT_REQUEST_ID;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.createCreditRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.mapper.CreditRequestMapper;
import br.com.miguelalves.credit_analysis_api.request.service.CreditRequestService;

@WebMvcTest(CreditRequestController.class)
class CreditRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditRequestService creditRequestService;

    @Test
    void shouldCreateCreditRequestWithValidData() throws Exception {
        CreditRequestResponse response = CreditRequestMapper.fromCreditRequestToResponse(createCreditRequest());

        when(creditRequestService.createCreditRequest(CREATE_CREDIT_REQUEST_REQUEST))
                .thenReturn(response);

        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CREATE_CREDIT_REQUEST_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value(response.companyName()))
                .andExpect(jsonPath("$.cnpj").value(response.cnpj()))
                .andExpect(jsonPath("$.requestedAmount").value(100000.00))
                .andExpect(jsonPath("$.annualRevenue").value(500000.00))
                .andExpect(jsonPath("$.status").value(response.status().name()));
    }

    @Test
    void shouldGetCreditRequestByIdWhenCreditRequestExists() throws Exception {
        CreditRequestResponse response = CreditRequestMapper.fromCreditRequestToResponse(createCreditRequest());

        when(creditRequestService.getCreditRequestById(CREDIT_REQUEST_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/requests/{id}", CREDIT_REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value(response.companyName()))
                .andExpect(jsonPath("$.cnpj").value(response.cnpj()))
                .andExpect(jsonPath("$.requestedAmount").value(100000.00))
                .andExpect(jsonPath("$.annualRevenue").value(500000.00))
                .andExpect(jsonPath("$.status").value(response.status().name()));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCreditRequestWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "companyId": null,
                          "requestedAmount": 0,
                          "annualRevenue": 0
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
