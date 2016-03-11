package org.activiti.test.bp;

import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Loads all processes and fails in case if some process has error.
 *
 * User: goodg_000
 * Date: 15.12.2015
 * Time: 0:02
 */
public class ActivitiProcessesTest {

    private static final String ACTIVE_SPRING_PROFILES = "spring.profiles.active";

    private String savedSpringProfiles = null;

    @Before
    public void onBefore() {
        savedSpringProfiles = System.getProperty(ACTIVE_SPRING_PROFILES);
    }

    @After
    public void onAfter() {
        if (savedSpringProfiles != null) {
            System.setProperty(ACTIVE_SPRING_PROFILES, savedSpringProfiles);
        }
    }

    //@Ignore
    @Test
    public void testAlphaProcesses() throws Exception {
        System.setProperty(ACTIVE_SPRING_PROFILES, "alpha");
        testProcessEngineCreated();
    }

    //@Ignore
    @Test
    public void testBetaProcesses() throws Exception {
        System.setProperty(ACTIVE_SPRING_PROFILES, "beta");
        testProcessEngineCreated();
    }

    @Test
    public void testProdProcesses() throws Exception {
        System.setProperty(ACTIVE_SPRING_PROFILES, "prod");
        testProcessEngineCreated();
    }

    private void testProcessEngineCreated() throws Exception {
        try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:/activiti-test-context.xml")) {
            Assert.assertTrue(applicationContext.isActive());
            Assert.assertTrue(applicationContext.isRunning());

            ProcessEngineFactoryBean processEngineFactoryBean = applicationContext.getBean(
                    ProcessEngineFactoryBean.class);
            Assert.assertNotNull(processEngineFactoryBean.getObject());
        }
    }

}
