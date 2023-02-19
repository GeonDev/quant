package com.stock.quant.api.consts;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@Profile({"local", "prod"})
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title(" API")
                .description("");

        OpenAPI openAPI = new OpenAPI()
                .info(info);


        return openAPI;
    }
}
