package org.igov.service.controller;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.junit.Ignore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: goodg_000
 * Date: 21.06.2015
 * Time: 21:10
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class ActionTaskCommonControllerScenario {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}
