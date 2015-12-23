package org.springfox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.base.Predicate;

import springfox.documentation.RequestHandler;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import io.swagger.annotations.Api;

import static springfox.documentation.builders.PathSelectors.*;
import static com.google.common.base.Predicates.*;

@Configuration
@EnableSwagger2
@EnableWebMvc
@PropertySource("classpath:springfox.properties")
public class ApplicationSwaggerConfig {
    // При изменении наименования группы, изменить файлы в src/main/asciidoc
    public static final String EGOV_GROUP = "igov";
    
    @Bean
    public UiConfiguration uiConfig() {
	System.out.println(" ApplicationSwaggerConfig start");
	return UiConfiguration.DEFAULT;
    }

    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title("iGov.ua APIs").description("Портал державних послуг").version("1.0")
		.contact("support@igov.ua").termsOfServiceUrl("https://github.com/e-government-ua/i/wiki").build();
    }

    @Bean
    public Docket dockAccessAndAuth() {
	return new Docket(DocumentationType.SWAGGER_2).groupName("Авторизация и Доступ").apiInfo(apiInfo()).select()
		.paths(pathAccessAndAuth()).build();
    }
    private Predicate<String> pathAccessAndAuth() {
	return or(regex("/access.*"), regex("/auth.*"));
    }

    @Bean
    public Docket dockDefault() {
	return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
		.paths(PathSelectors.any()).build();
    }

    @Bean
    public Docket dockEGov() {
	return new Docket(DocumentationType.SWAGGER_2).groupName(EGOV_GROUP).apiInfo(apiInfo()).select()
		.apis(apiEgovRest()).paths(PathSelectors.any()).build();
    }
    private Predicate<RequestHandler> apiEgovRest() {
	return and(RequestHandlerSelectors.withClassAnnotation(Api.class),
		not(RequestHandlerSelectors.withClassAnnotation(ApiIgnore.class)),
		not(RequestHandlerSelectors.withMethodAnnotation(ApiIgnore.class)));
    }
}