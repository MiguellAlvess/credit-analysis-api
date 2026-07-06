package br.com.miguelalves.credit_analysis_api.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI creditAnalysisOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Credit Analysis API")
                        .description("API para análise e aprovação de crédito empresarial")
                        .version("1.0.0"));
    }
}
