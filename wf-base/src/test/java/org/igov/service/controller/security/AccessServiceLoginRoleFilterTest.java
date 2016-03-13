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
import org.junit.Ignore;
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
public class AccessServiceLoginRoleFilterTest {

    // specified in AccessServiceLoginRole.csv
    private static final String TEST_LOGIN = "TestLogin4";
    private static final String TEST_SERVICE = "testService10";
    private static final String PATTERN_MATCHED_SERVICE1 = "zeroService10";
    private static final String PATTERN_MATCHED_SERVICE2 = "testService42";
    private static final String WRONG_PATTERN_SERVICE1 = "zeroService9";
    private static final String WRONG_PATTERN_SERVICE2 = "test42";

    private static final String WRONG_LOGIN = "WrongUser";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccessServiceLoginRoleFilter accessServiceLoginRoleFilter;

    @Value("${general.auth.login}")
    private String generalAuthLogin;

    @Value("${general.auth.password}")
    private String generalAuthPassword;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext).addFilter(accessServiceLoginRoleFilter)
                .build();
    }

    @Test
    public void testAccessServiceLoginRightFilter_generalUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(generalAuthLogin, generalAuthPassword));

        verifySuccessLogin(TEST_SERVICE);
    }

    @Test
    public void testAccessServiceLoginRightFilter_wrongUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(WRONG_LOGIN, ""));

        verifyForbidden(TEST_SERVICE);
    }

    @Test
    public void testAccessServiceLoginRightFilter_testUser() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_LOGIN, ""));

        verifySuccessLogin(TEST_SERVICE);
        verifySuccessLogin(PATTERN_MATCHED_SERVICE1);
        verifySuccessLogin(PATTERN_MATCHED_SERVICE2);
        verifyForbidden(WRONG_PATTERN_SERVICE1);
        verifyForbidden(WRONG_PATTERN_SERVICE2);
    }

    private void verifySuccessLogin(String testServiceName) throws Exception {
        mockMvc.perform(get(testServiceName)).andExpect(status().isNotFound()); // no such service, it's Ok.
    }

    private void verifyForbidden(String testServiceName) throws Exception {
        mockMvc.perform(get(testServiceName)).andExpect(status().isForbidden());
    }

}
