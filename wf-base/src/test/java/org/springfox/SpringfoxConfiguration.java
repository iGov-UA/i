package org.springfox;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@WebAppConfiguration
@EnableWebMvc
@ComponentScan(basePackages = { "org.activiti.rest.controller" }, basePackageClasses = {
		org.springfox.ApplicationSwaggerConfig.class })
public class SpringfoxConfiguration {
}
