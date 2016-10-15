package org.igov.service.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.aspectj.weaver.TypeFactory;
import org.igov.model.access.vo.*;
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
        List<AccessLoginRoleVO> roles = getAccessServiceLoginRoles("TestLogin1");
        Assert.assertEquals(1, roles.size());
        assertEquals(getAllRights(roles.stream().map(AccessLoginRoleVO::getoRole).collect(Collectors.toList())),
                "TestRight1", "TestRight2");
    }

    @Test
    public void testGetAccessServiceRoleRights() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(1L); // TestRole1
        assertEquals(getAllRights(role), "TestRight1", "TestRight2");

        role = getAccessServiceRoleRights(3L); // TestRole3
        assertEquals(getAllRights(role), "TestRight3");
    }

    @Test
    public void testSetAccessServiceLoginRole() throws Exception {
        final String login = "JJJ";
        final Long roleId = 3L;

        AccessLoginRoleVO loginRole = setAccessServiceLoginRole(null, login, roleId);
        Assert.assertNotNull(loginRole.getnID());
        Assert.assertEquals(login, loginRole.getsLogin());
        Assert.assertEquals(roleId, loginRole.getoRole().getnID());

        List<AccessLoginRoleVO> roles = getAccessServiceLoginRoles(login);
        Assert.assertEquals(1, roles.size());
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
    public void testSetAccessServiceRoleRight() throws Exception {
        final long currentRoleId = 6L;
        AccessRoleVO role = getAccessServiceRoleRights(currentRoleId);
        Assert.assertTrue(getAllRights(role).isEmpty());

        AccessRoleRightVO roleRightVO = setAccessServiceRoleRight(null, role.getnID(), currentRoleId);
        Assert.assertNotNull(roleRightVO.getnID());
        role = getAccessServiceRoleRights(currentRoleId);
        assertEquals(getAllRights(role), "TestRight6");
    }

    @Test
    public void testSetAccessServiceRoleRightInclude() throws Exception {
        final long currentRoleId = 6L;
        AccessRoleVO role = getAccessServiceRoleRights(currentRoleId);
        Assert.assertTrue(role.getaRoleRightInclude() == null);

        final long childRoleId = 7L;
        AccessRoleIncludeVO roleIncludeVO = setAccessServiceRoleRightInclude(null, role.getnID(), childRoleId);
        Assert.assertNotNull(roleIncludeVO.getnID());

        role = getAccessServiceRoleRights(currentRoleId);
        final AccessRoleIncludeVO include = role.getaRoleRightInclude().get(0);
        Assert.assertEquals(roleIncludeVO.getnID(), include.getnID());

        Assert.assertEquals(childRoleId, include.getoRole().getnID().longValue());
    }

    @Test
    public void testRemoveAccessServiceRoleRight() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(8L);
        Assert.assertEquals(1, role.getaRoleRight().size());

        removeAccessServiceRoleRight(role.getaRoleRight().get(0).getnID());

        role = getAccessServiceRoleRights(role.getnID());

        Assert.assertNull(role.getaRoleRight());
    }

    @Test
    public void testRemoveAccessServiceRoleRightInclude() throws Exception {
        AccessRoleVO role = getAccessServiceRoleRights(8L);
        Assert.assertEquals(1, role.getaRoleRightInclude().size());

        removeAccessServiceRoleRightInclude(role.getaRoleRightInclude().get(0).getnID());

        role = getAccessServiceRoleRights(role.getnID());

        Assert.assertNull(role.getaRoleRightInclude());
    }

    @Test
    public void testRemoveAccessServiceLoginRole() throws Exception {

        final String testLogin2 = "TestLogin2";
        Assert.assertEquals(1, getAccessServiceLoginRoles(testLogin2).size());
        removeAccessServiceLoginRole(2L, null, null);
        Assert.assertEquals(0, getAccessServiceLoginRoles(testLogin2).size());


        final String testLogin3 = "TestLogin3";
        Assert.assertEquals(1, getAccessServiceLoginRoles(testLogin3).size());
        removeAccessServiceLoginRole(null, testLogin3, 1L);
        Assert.assertEquals(0, getAccessServiceLoginRoles(testLogin3).size());
    }

    private List<AccessLoginRoleVO> getAccessServiceLoginRoles(String sLogin) throws Exception {
        String getJsonData = mockMvc.perform(get("/access/getAccessServiceLoginRoles").
                param("sLogin", sLogin)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, CollectionType.construct(List.class,
                SimpleType.construct(AccessLoginRoleVO.class)));
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

    private AccessLoginRoleVO setAccessServiceLoginRole(Long nID, String sLogin, Long nID_AccessServiceRole)
            throws Exception {

        String getJsonData = mockMvc.perform(post("/access/setAccessServiceLoginRole").
                param("nID", nID != null ? nID.toString() : null).
                param("sLogin", sLogin).
                param("nID_AccessServiceRole", nID_AccessServiceRole.toString())).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, AccessLoginRoleVO.class);
    }

    private AccessRoleRightVO setAccessServiceRoleRight(Long nID, Long nID_AccessServiceRole, Long nID_AccessServiceRight)
            throws Exception {

        String getJsonData = mockMvc.perform(post("/access/setAccessServiceRoleRight").
                param("nID", nID != null ? nID.toString() : null).
                param("nID_AccessServiceRole", nID_AccessServiceRole.toString()).
                param("nID_AccessServiceRight", nID_AccessServiceRight.toString())).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, AccessRoleRightVO.class);
    }

    private AccessRoleIncludeVO setAccessServiceRoleRightInclude(Long nID, Long nID_AccessServiceRole,
                                                               Long nID_AccessServiceRole_Include) throws Exception {

        String getJsonData = mockMvc.perform(post("/access/setAccessServiceRoleRightInclude").
                param("nID", nID != null ? nID.toString() : null).
                param("nID_AccessServiceRole", nID_AccessServiceRole.toString()).
                param("nID_AccessServiceRole_Include", nID_AccessServiceRole_Include.toString())).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        return JsonRestUtils.readObject(getJsonData, AccessRoleIncludeVO.class);
    }

    public void removeAccessServiceRole(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRole").param("nID", nID.toString())).
                andExpect(status().isOk());
    }

    public void removeAccessServiceRight(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRight").param("nID", nID.toString())).
                andExpect(status().isOk());
    }

    public void removeAccessServiceRoleRight(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRoleRight").param("nID", nID.toString())).
                andExpect(status().isOk());
    }

    public void removeAccessServiceRoleRightInclude(Long nID) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceRoleRightInclude").param("nID", nID.toString())).
                andExpect(status().isOk());
    }

    public void removeAccessServiceLoginRole(Long nID, String sLogin, Long nID_AccessServiceRole) throws Exception {
        mockMvc.perform(post("/access/removeAccessServiceLoginRole").
                param("nID", nID != null ? nID.toString() : null).
                param("sLogin", sLogin).
                param("nID_AccessServiceRole", nID_AccessServiceRole != null ? nID_AccessServiceRole.toString() : null)).
                andExpect(status().isOk());
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
    
    @Ignore
    @Test
    public void testChangePassword_ShouldSuccess() throws Exception
    {
        String sUrl = "/action/task/changePassword";
        String sLoginOwner = "kermit";
        String sPasswordOld = "kermit";
        String sPasswordNew = "kermit1";
        
        String jsonResponse = mockMvc.perform(post(sUrl).
                param("sLoginOwner", sLoginOwner).
                param("sPasswordOld", sPasswordOld).
                param("sPasswordNew", sPasswordNew)).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
    @Ignore
    @Test
    public void testChangePassword_WrongPassword() throws Exception
    {
        String sUrl = "/action/task/changePassword";
        String sLoginOwner = "kermit";
        String sPasswordOld = "kermit45";
        String sPasswordNew = "kermit";
        
        String jsonResponse = mockMvc.perform(post(sUrl).
                param("sLoginOwner", sLoginOwner).
                param("sPasswordOld", sPasswordOld).
                param("sPasswordNew", sPasswordNew)).
                andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

    }
    @Ignore
    @Test
    public void testChangePassword_WrongLogin() throws Exception
    {
        String sUrl = "/action/task/changePassword";
        String sLoginOwner = "kermit45";
        String sPasswordOld = "kermit1";
        String sPasswordNew = "kermit";
        
        String jsonResponse = mockMvc.perform(post(sUrl).
                param("sLoginOwner", sLoginOwner).
                param("sPasswordOld", sPasswordOld).
                param("sPasswordNew", sPasswordNew)).
                andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

    }

}
