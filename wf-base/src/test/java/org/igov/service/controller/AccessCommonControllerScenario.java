package org.igov.service.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.aspectj.weaver.TypeFactory;
import org.igov.model.access.vo.AccessRightVO;
import org.igov.model.access.vo.AccessRoleIncludeVO;
import org.igov.model.access.vo.AccessRoleRightVO;
import org.igov.model.access.vo.AccessRoleVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.igov.util.JSON.JsonRestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 23:08
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class AccessCommonControllerScenario {
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetAccessServiceLoginRoles() throws Exception {
        List<AccessRoleVO> roles = getAccessServiceLoginRoles("TestLogin");
        Assert.assertEquals(1, roles.size());
        assertEquals(getAllRights(roles), "TestRight1", "TestRight2");
    }

    @Test
    public void testGetAccessServiceRoleRights() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(1L); // TestRole1
        assertEquals(getAllRights(role), "TestRight1", "TestRight2");

        role = getAccessServiceRoleRights(3L); // TestRole3
        assertEquals(getAllRights(role), "TestRight3");
    }

    @Test
    public void testSetAccessServiceRole() throws Exception {
        final String newRoleName1 = "TestRole1_changed";
        AccessRoleVO role = setAccessServiceRole(1L, newRoleName1);
        Assert.assertEquals(newRoleName1, role.getsName());

        role = getAccessServiceRoleRights(role.getnID());
        Assert.assertEquals(newRoleName1, role.getsName());

        final String newRoleName2 = "AAA";
        role = setAccessServiceRole(null, newRoleName2);
        Assert.assertEquals(newRoleName2, role.getsName());

        role = getAccessServiceRoleRights(role.getnID());
        Assert.assertEquals(newRoleName2, role.getsName());
    }

    @Test
    public void testRemoveAccessServiceRole() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(4L); // TestRole4
        removeAccessServiceRole(role.getnID());
        validateAccessServiceRoleNotFound(role.getnID());
    }

    @Test
    public void testRemoveAccessServiceRight() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(5L); // TestRole5
        Assert.assertEquals(1, getAllRights(role).size());

        Long rightId = role.getaRoleRight().get(0).getoRight().getnID();
        removeAccessServiceRight(rightId);

        role = getAccessServiceRoleRights(role.getnID());
        Assert.assertEquals(0, getAllRights(role).size());
    }

    @Test
    public void testGetAccessServiceLoginRight() throws Exception {

        List<String> services = getAccessServiceLoginRight("TestLogin");
        Assert.assertTrue(services.size() == 1);
        Assert.assertEquals("TestService", services.get(0));

        services = getAccessServiceLoginRight("NonExistedLogin");
        Assert.assertTrue(services.isEmpty());
    }

    @Test
    public void testGetAccessServiceRights() throws Exception {
        List<AccessRightVO> rights = getAccessServiceRights(null, null, null, null);
        Assert.assertTrue(rights.size() > 0);

        String serviceName = "testService2";
        rights = getAccessServiceRights(null, serviceName, null, null);
        Assert.assertEquals(1, rights.size());
        Assert.assertEquals(serviceName, rights.get(0).getsService());

        String method = "get";
        rights = getAccessServiceRights(null, null, method, null);
        Assert.assertEquals(2, rights.size());
        for (AccessRightVO right : rights) {
            Assert.assertTrue(right.getSaMethod().toLowerCase().contains(method.toLowerCase()));
        }
    }

    @Test
    public void testSetAccessServiceLoginRight() throws Exception {

        String newLogin = "NewLogin1";
        String newService = "NewService1";
        String nonExistedBean = "NonExistedBean";

        mockMvc.perform(post("/access/setAccessServiceLoginRight").
                param("sLogin", newLogin).param("sService", newService).param("sHandlerBean", nonExistedBean)).
                andExpect(status().is5xxServerError());

        setAccessServiceLoginRight(newLogin, newService);

        List<String> services = getAccessServiceLoginRight(newLogin);
        Assert.assertTrue(services.size() == 1);
        Assert.assertEquals(newService, services.get(0));
    }

    @Test
    public void testRemoveAccessServiceLoginRight() throws Exception {

        String newLogin = "NewLogin2";
        String newService = "NewService2";

        mockMvc.perform(delete("/access/removeAccessServiceLoginRight").
                param("sLogin", newLogin).param("sService", newService)).
                andExpect(status().isNotModified());

        setAccessServiceLoginRight(newLogin, newService);

        mockMvc.perform(delete("/access/removeAccessServiceLoginRight").
                param("sLogin", newLogin).param("sService", newService)).
                andExpect(status().isOk());

        List<String> services = getAccessServiceLoginRight(newLogin);
        Assert.assertTrue(services.isEmpty());
    }

    @Test
    public void testSetAccessServiceRight() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(3L); // TestRole3
        AccessRightVO oldRight = role.getaRoleRight().get(0).getoRight();

        String newService = oldRight.getsService() + "111";
        AccessRightVO newRight = setAccessServiceRight(oldRight.getnID(), oldRight.getsName(),
                newService, oldRight.getSaMethod(), oldRight.getsHandlerBean());
        Assert.assertEquals(newService, newRight.getsService());

        role = getAccessServiceRoleRights(3L); // TestRole3
        AccessRightVO newRight2 = role.getaRoleRight().get(0).getoRight();
        Assert.assertEquals(newRight, newRight2);
    }

    @Test
    @Ignore
    public void testHasAccessServiceLoginRight() throws Exception {

        String newLogin = "NewLogin3";
        String newService = "NewService3";

        Assert.assertFalse(hasAccessServiceLoginRight(newLogin, newService));

        setAccessServiceLoginRight(newLogin, newService);

        Assert.assertTrue(hasAccessServiceLoginRight(newLogin, newService));
    }

    private void setAccessServiceLoginRight(String sLogin, String sService) throws Exception {
        mockMvc.perform(post("/access/setAccessServiceLoginRight").
                param("sLogin", sLogin).param("sService", sService)).
                andExpect(status().isOk());
    }

    private List<String> getAccessServiceLoginRight(String sLogin) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/getAccessServiceLoginRight").
                param("sLogin", sLogin)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, List.class);
    }

    private boolean hasAccessServiceLoginRight(String sLogin, String sService) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/hasAccessServiceLoginRight").
                param("sLogin", sLogin).param("sService", sService).param("sMethod", "GET")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, Boolean.class);
    }

    private List<AccessRoleVO> getAccessServiceLoginRoles(String sLogin) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/getAccessServiceLoginRoles").
                param("sLogin", sLogin)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, CollectionType.construct(List.class,
                SimpleType.construct(AccessRoleVO.class)));
    }

    private AccessRoleVO getAccessServiceRoleRights(Long nID_AccessServiceRole) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/getAccessServiceRoleRights").
                param("nID_AccessServiceRole", nID_AccessServiceRole.toString())).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return ((List<AccessRoleVO>) JsonRestUtils.readObject(getJsonData, CollectionType.construct(List.class,
                SimpleType.construct(AccessRoleVO.class)))).get(0);
    }

    private void validateAccessServiceRoleNotFound(Long nID_AccessServiceRole) throws Exception {
        mockMvc.perform(get("/access/getAccessServiceRoleRights").
                param("nID_AccessServiceRole", nID_AccessServiceRole.toString())).
                andExpect(status().is4xxClientError());
    }

    private AccessRoleVO setAccessServiceRole(Long nID, String sName) throws Exception {
        String getJsonData = mockMvc.perform(post("/access/setAccessServiceRole").
                param("nID", nID != null ? nID.toString() : null).param("sName", sName)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, AccessRoleVO.class);
    }

    private List<AccessRightVO> getAccessServiceRights(Long nID, String sService, String saMethod,
                                                       String sHandlerBean) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/getAccessServiceRights").
                param("nID", nID != null ? nID.toString() : null).
                param("sService", sService).
                param("saMethod", saMethod).
                param("sHandlerBean", sHandlerBean)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, CollectionType.construct(List.class,
                SimpleType.construct(AccessRightVO.class)));
    }

    private AccessRightVO setAccessServiceRight(Long nID, String sName, String sService, String saMethod,
                                                       String sHandlerBean) throws Exception {
        String getJsonData = mockMvc.perform(post("/access/setAccessServiceRight").
                param("nID", nID != null ? nID.toString() : null).
                param("sName", sName).
                param("sService", sService).
                param("saMethod", saMethod).
                param("sHandlerBean", sHandlerBean)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, AccessRightVO.class);
    }

    public void removeAccessServiceRole(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRole").param("nID", nID.toString())).andExpect(status().isOk());
    }

    public void removeAccessServiceRight(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRight").param("nID", nID.toString())).andExpect(status().isOk());
    }

    private Set<String> getAllRights(List<AccessRoleVO> roles) {
        Set<String> res = new HashSet<>();
        roles.stream().map(this::getAllRights).forEach(res::addAll);
        return res;
    }

    private Set<String> getAllRights(AccessRoleVO role) {
        Set<String> res = new HashSet<>();
        if (role.getaRoleRight() != null) {
            res.addAll(role.getaRoleRight().stream().map(AccessRoleRightVO::getoRight).map(AccessRightVO::getsName).collect(
                    Collectors.toList()));
        }

        if (role.getaRoleRightInclude() != null) {
            role.getaRoleRightInclude().stream().map(AccessRoleIncludeVO::getoRole).map(this::getAllRights).forEach(
                    res::addAll);
        }
        return res;
    }

    private void assertEquals(Set<String> actual, String... expected) {
        Assert.assertEquals(new HashSet<>(Arrays.asList(expected)), actual);
    }

}
