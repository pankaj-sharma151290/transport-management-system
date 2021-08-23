package com.elemica.tms.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    Contact contact = new Contact("Pankaj Sharma", null, "pankaj.sharma151290@gmail.com");

    ApiInfo apiInfo = new ApiInfoBuilder().title("Transport Management System API")
                                          .description("Transport Management System APIs to minimize the transport cost")
                                          .contact(contact).version("1.0.0").build();

    /**
     * Swagger documentation for all the API in given package
     *
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select()
                                                      .apis(RequestHandlerSelectors.basePackage("com.elemica.tms")).paths(PathSelectors.any()).build();
    }

}
