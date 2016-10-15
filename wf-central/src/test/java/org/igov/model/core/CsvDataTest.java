package org.igov.model.core;

import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Tests that data in csv files for alpha and for prod, are consistent.
 *
 * User: goodg_000
 * Date: 13.12.2015
 * Time: 14:39
 */
public class CsvDataTest {

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

    @Test
    public void testRootCsv() throws IOException {

        System.setProperty(ACTIVE_SPRING_PROFILES, "root");
        testApplicationContextStartedWithoutErrors();
    }

    @Ignore
    @Test
    public void testProdCsv() throws IOException {

        System.setProperty(ACTIVE_SPRING_PROFILES, "prod");
        testApplicationContextStartedWithoutErrors();
    }

    private void testApplicationContextStartedWithoutErrors() {
        try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:/csv-test-context.xml")) {
            Assert.assertTrue(applicationContext.isActive());
            Assert.assertTrue(applicationContext.isRunning());
        }
    }
}
