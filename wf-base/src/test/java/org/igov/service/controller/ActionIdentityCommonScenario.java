package org.igov.service.controller;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.igov.service.business.action.task.core.UsersService;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private RequestBuilder requestBuilder;
    private IdentityService identityService;
    private UsersService usersService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        identityService = webApplicationContext.getBean(IdentityService.class);
        usersService = webApplicationContext.getBean(UsersService.class);
    }

    @Test
    public void shouldSuccessfullySetUser() throws Exception {
        String sURL_Template_Set = "/action/identity/setUser";
        String sURL_Template_Get = "/action/identity/getUsers";
        
        String sLogin = "test12345";
        String sFirstName = "test12345";        
        
        String sBody = "{\n"
                + "  \"sPassword\" : \"12345\",\n"
                + "  \"sName\" : \""+sFirstName+"\",\n"
                + "  \"sDescription\" : \"test12345\"  \n"
                + "}";

        requestBuilder = post(sURL_Template_Set)
                .param("sLogin", sLogin).content(sBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        requestBuilder = get(sURL_Template_Get);

        List<Map<String, String>> aResultUsers
                = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);

        Predicate<Map<String, String>> expectedUser = user
                -> sLogin.equals(user.get("sLogin"))                
                && sFirstName.equals(user.get("sFirstName"));
        List<Map<String, String>> aFilteredUsers = aResultUsers.stream()
                .filter(expectedUser)
                .collect(Collectors.toList());

        Assert.assertEquals(1, aFilteredUsers.size());
    }
    
    
    @Test
    public void shouldSuccessfullySetGroup()throws Exception {
        String sURL_Template_Set = "/action/identity/setGroup";
        String sURL_Template_Get = "/action/identity/getGroups";
        String sID = "123456";
        String sName = "new_test_group_shouldSuccessfullySetGroup_name";

        requestBuilder = get(sURL_Template_Set).param("sID", sID).param("sName", sName);
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());

        requestBuilder = get(sURL_Template_Get);
        List<Map<String, String>> aResultGroups
            = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);

        Predicate<Map<String, String>> expectedGroup = group ->
            sID.equals(group.get("id"))
            && sName.equals(group.get("name"));
        List<Map<String, String>> aFilteredGroups = aResultGroups.stream()
            .filter(expectedGroup)
            .collect(Collectors.toList());

        Assert.assertEquals(1, aFilteredGroups.size());
    }

    @Test
    public void shouldSuccessfullyGetUsers() throws Exception {
        String sURL_Template = "/action/identity/getUsers";
        String sParam;
        //check that all users are returned in response to a query with no parameters
        requestBuilder = get(sURL_Template);
        List<Map<String, String>> aUsers =
                JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(
                usersService.getUsersByGroup(null),
                aUsers
        );

        //check that there is only one user returned in response to a query with
        //ID_Group parameter
        sParam = "admin";
        requestBuilder = get(sURL_Template).param("sID_Group", sParam);
        aUsers = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(
                usersService.getUsersByGroup(sParam),
                aUsers
        );
        Assert.assertEquals("kermit", aUsers.get(0).get("sLogin"));

        //check that nothing is returned in response to a query with
        //non-existing group name in ID_Group parameter
        sParam = "someMeaninglessPhrase";
        requestBuilder = get(sURL_Template).param("sID_Group", sParam);
        aUsers = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(0, aUsers.size());
    }

    @Test
    public void shouldSuccessfullyGetGroups() throws Exception {
        String sURL_Template = "/action/identity/getGroups";
        String sParam;
        //check that all groups returned
        requestBuilder = get(sURL_Template);
        List<Group> aGroups
                = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(
                identityService.createGroupQuery().list().size(),
                aGroups.size()
                );

        //check that groups of "kermit" login returned
        sParam = "kermit";
        requestBuilder = get(sURL_Template).param("sLogin", sParam);
        aGroups = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(
                identityService.createGroupQuery().groupMember(sParam).list().size(),
                aGroups.size()
                );

        //check that no groups returned if there is wrong login
        sParam = "00phraseFAIL11";
        requestBuilder = get(sURL_Template).param("sLogin", sParam);
        aGroups = JsonRestUtils.readObject(fetchJSONData(requestBuilder), List.class);
        Assert.assertEquals(
                0,
                aGroups.size());
    }

    private String fetchJSONData(RequestBuilder requestBuilder) throws Exception{
        return mockMvc.perform(requestBuilder).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
    }
}
