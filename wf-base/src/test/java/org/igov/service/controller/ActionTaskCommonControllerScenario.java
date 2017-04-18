package org.igov.service.controller;


import org.activiti.engine.impl.util.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class ActionTaskCommonControllerScenario {

    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskCommonController.class);

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Ignore
    @Test
    public void shouldSuccessfullyGetStartFormData() throws Exception {

        LOG.info("Start - shouldSuccessfullyGetStartFormData()");

        String getJsonData = mockMvc.perform(post("/action/task/startProcess").
                param("sID_BP", "_test_autotest_task").content("{\n"
                + "  \"properties\" : [{\n"
                + "      \"id\" : \"sParam1\", \n"
                + "      \"value\" : \"test1\"\n"
                + "    }, {\n"
                + "      \"id\" : \"sParam2\", \n"
                + "      \"value\" : \"test2\"\n"
                + "    }]\n"
                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        JSONObject parsedObject = new JSONObject(getJsonData);
        String nID_Task = parsedObject.getString("snID_Process");

        String getJsonDataTwo = mockMvc.perform(get("/action/task/getStartFormData").
                param("nID_Task", nID_Task)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();

        JSONObject parsedObjectTwo = new JSONObject(getJsonDataTwo);
        String sParam1 = parsedObjectTwo.getString("sParam1");
        String sParam2 = parsedObjectTwo.getString("sParam2");
        Assert.assertNotNull(sParam1);
        Assert.assertNotNull(sParam2);
        
        
        LOG.info("Result GetStartFormData - " + getJsonData);
    }

    @Ignore
    @Test
    public void shouldSuccessfullyTasksOfGroups() throws Exception {

        LOG.info("Start - shouldSuccessfullyTasksOfGroups()");

        shouldSuccessfullyPostStartProcessService();
        shouldSuccessfullyGetGroupsService();
    }

    private void shouldSuccessfullyPostStartProcessService() throws Exception {
        final String sUrl = "/action/task/startProcess";
        final String sID_BP = "_test_autotest_task";
        final String sJsonBody = "{\"properties\" :[{\"id\":\"sParam1\",\"value\":\"test1\"},"
                + "{\"id\":\"sParam2\",\"value\":\"test2\"}]}";
        final String sExpectedJson = this.mockMvc.perform(
                post(sUrl)
                        .param("sID_BP", sID_BP)
                        .contentType(MediaType.valueOf(APPLICATION_JSON_CHARSET_UTF_8))
                        .content(sJsonBody)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        Assert.assertNotNull(sExpectedJson);

        LOG.info("Result PostPostStart - " + sExpectedJson);
    }

    private void shouldSuccessfullyGetGroupsService() throws Exception {
        final String sGroup = "sales";
        final String sUrl = "/action/task/groups/" + sGroup;
        final String sExpectedJson = this.mockMvc.perform(get(sUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        Assert.assertNotNull(sExpectedJson);
        // TODO: Check sExpectedJson

        LOG.info("Result GetGroupsService - " + sExpectedJson);
    }
}
