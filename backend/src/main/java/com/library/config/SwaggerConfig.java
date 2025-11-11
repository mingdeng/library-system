package com.library.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableKnife4j
@OpenAPIDefinition(info = @Info(
        title = "图书馆管理系统 API 文档",
        version = "1.0",
        description = "基于 Spring Boot + Vue 的图书馆管理系统 API 接口文档"
))
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())

                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("图书馆管理系统 API")
                        .version("1.0")
                        .description("图书馆管理系统接口文档")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("开发团队")
                                .email("developer@example.com")));
    }
}

