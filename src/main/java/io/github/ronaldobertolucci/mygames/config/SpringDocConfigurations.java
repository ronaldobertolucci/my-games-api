package io.github.ronaldobertolucci.mygames.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer").bearerFormat("JWT")))
                        .info(new Info()
                                .title("My Games API")
                        .description("API REST para gerenciar meus jogos de diversas plataformas e origens")
                        .contact(new Contact()
                                .name("Ronaldo Bertolucci Jr")
                                .email("ronaldobertoluccijr@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/ronaldobertolucci/my-games-api?tab=MIT-1-ov-file")));
    }

}