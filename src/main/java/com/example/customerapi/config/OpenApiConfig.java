package com.example.customerapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
	
	@Bean
    public OpenAPI customerApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Management API")
                        .description("Spring Boot REST API for managing customers with dynamic tier calculation.")
                        .version("1.1.0")
                        .contact(new Contact().name("Customer API").email("support@example.com"))
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("OpenAPI YAML")
                        .url("/v3/api-docs.yaml"));
    }

}

