package org.springfox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.*;
import static com.google.common.base.Predicates.*;

@Configuration
@EnableSwagger2
@EnableWebMvc
@PropertySource("classpath:springfox.properties")
public class ApplicationSwaggerConfig {
    @Bean
    public UiConfiguration uiConfig() {
	return UiConfiguration.DEFAULT;
    }

    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title("iGov.ua APIs").description("Портал державних послуг").version("1.0")
		.contact("support@igov.ua").termsOfServiceUrl("https://github.com/e-government-ua/i/wiki").build();
    }

    @Bean
    public Docket dockAccessAndAuth() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("AccessAndAuth").apiInfo(apiInfo()).select()
		.paths(pathAccessAndAuth()).build();
    }

    private Predicate<String> pathAccessAndAuth() {
	return or(regex("/access.*"), regex("/auth.*"));
    }

    @Bean
    public Docket dockRest() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("Rest").apiInfo(apiInfo()).select().paths(pathRest())
		.build();
    }

    private Predicate<String> pathRest() {
	return regex("/rest.*");
    }

    @Bean
    public Docket dockServices() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("Services").apiInfo(apiInfo()).select()
		.paths(pathServices()).build();
    }

    private Predicate<String> pathServices() {
	return regex("/services.*");
    }

    @Bean
    public Docket dockOthere() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("Othere").apiInfo(apiInfo()).select()
		.paths(pathOthere()).build();
    }
    private Predicate<String> pathOthere() {
	return or(regex("/escalation.*"), regex("/flow.*"), regex("/merchant.*"), regex("/server.*"),
		regex("/subject.*"), regex("/messages.*"), regex("^/[^/]*$"));
    }

    @Bean
    public Docket dockAll() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("All").apiInfo(apiInfo()).select()
		.paths(PathSelectors.any()).build();
    }
}