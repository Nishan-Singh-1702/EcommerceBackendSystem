package com.Ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirement = new SecurityRequirement().addList("Bearer Authentication");
        return new OpenAPI().info(new Info().title("Spring Boot E-Commerce Backend API")
                        .version("1.0").description("A full spring boot ecommerce backend project")
                        .license(new License().name("Nishan Singh licence").url("http://nishan.com"))
                        .contact(new Contact().name("Nishan Singh").email("nishansingh1914984@gmail.com").url("https://github.com/Nishan-Singh-1702/EcommerceBackendSystem.git")))
                .externalDocs(new ExternalDocumentation().description("Project Documentation").url("http://nishan.com"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",bearerScheme)).addSecurityItem(bearerRequirement);
    }
}
