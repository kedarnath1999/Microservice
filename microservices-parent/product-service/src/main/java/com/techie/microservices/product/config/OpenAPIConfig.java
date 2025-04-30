package com.techie.microservices.product.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Product Service API")
                    .description("This is the Product Service API")
                    .version("v0.0.1")
                    .license(new License().name("Apache 2.0")))
                .externalDocs( new ExternalDocumentation()
                    .description("This is the Product Service API external documentation")
                    .url("https://github.com/techie-techie/product-service-api"));
    }
}
