package com.quant.api.config;

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
        Info info = new Info().title("API")
                .description("배치외 수동으로 데이터를 조회 갱신할때 사용하는 API");

        OpenAPI openAPI = new OpenAPI()
                .info(info);


        return openAPI;
    }
}
