package org.igov.service.controller;

import org.igov.util.JSON.JsonRestUtils;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class ActionIdentityCommonScenario {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldSuccessfullyGetUsers() throws Exception {
        String sURL_Template = "/action/identity/getUsers";
        RequestBuilder requestBuilder;
        //check that all users are returned in response to a query with no parameters
        requestBuilder = get(sURL_Template);
        List<Map<String, String>> aUsers =
                JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(3, aUsers.size());

        //check that there is only one user returned in response to a query with
        //ID_Group parameter
        requestBuilder = get(sURL_Template).param("sID_Group", "admin");
        aUsers = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(1, aUsers.size());
        Assert.assertEquals("kermit", aUsers.get(0).get("sLogin"));

        //check that nothing is returned in response to a query with
        //non-existing group name in ID_Group parameter
        requestBuilder = get(sURL_Template).param("sID_Group", "someMeaninglessPhrase");
        aUsers = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(0,aUsers.size());
    }

    private String fetchJSONData(RequestBuilder requestBuilder) throws Exception{
        return mockMvc.perform(requestBuilder).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
    }
}
