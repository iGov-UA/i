package org.igov.service.controller.security;

import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: goodg_000
 * Date: 21.02.2016
 * Time: 16:37
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@ActiveProfiles("default")
public class AccessServiceLoginRightFilterTest {

    // specified in AccessServiceLoginRight.csv
    private static final String TEST_LOGIN = "TestLogin";
    private static final String TEST_SERVICE = "TestService";

    private static final String WRONG_LOGIN = "WrongUser";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccessServiceLoginRightFilter accessServiceLoginRightFilter;

    @Value("${general.auth.login}")
    private String generalAuthLogin;

    @Value("${general.auth.password}")
    private String generalAuthPassword;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext).addFilter(accessServiceLoginRightFilter)
                .build();
    }

    @Test
    public void testAccessServiceLoginRightFilter_generalUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(generalAuthLogin, generalAuthPassword));

        verifySuccessLogin();
    }

    @Test
    public void testAccessServiceLoginRightFilter_wrongUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(WRONG_LOGIN, ""));

        verifyForbidden();
    }

    @Test
    public void testAccessServiceLoginRightFilter_testUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_LOGIN, ""));

        verifySuccessLogin();
    }

    private void verifySuccessLogin() throws Exception {
        mockMvc.perform(get(TEST_SERVICE)).andExpect(status().isNotFound()); // no such service, it's Ok.
    }

    private void verifyForbidden() throws Exception {
        mockMvc.perform(get(TEST_SERVICE)).andExpect(status().isForbidden());
    }

}
