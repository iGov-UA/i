package org.springfox;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@WebAppConfiguration
@EnableWebMvc
@ComponentScan(basePackages = { "org.igov.service.controller", "org.springfox" })
public class CentralSpringfoxConfiguration {
    
}
