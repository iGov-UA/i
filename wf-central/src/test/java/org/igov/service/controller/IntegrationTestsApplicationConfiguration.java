package org.igov.service.controller;

import org.activiti.explorer.conf.ApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by diver on 4/12/15.
 */
@EnableWebMvc
@Configuration
@Import(ApplicationConfiguration.class)
@ComponentScan(basePackages = { "org.igov.model" }) //.place
@ImportResource("classpath:mock-beans.xml")
public class IntegrationTestsApplicationConfiguration {

}
