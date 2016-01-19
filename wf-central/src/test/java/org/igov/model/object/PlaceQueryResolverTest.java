package org.igov.model.object;

import org.igov.model.object.place.PlaceQueryResolver;
import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author dgroup
 * @since 25.08.2015
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class PlaceQueryResolverTest {

    @Autowired
    private PlaceQueryResolver sqlBuilder;

    @Test
    public void sqlTreeUp() {
        String sqlQuery = sqlBuilder.getTreeUp(479L, null, true);
        assertThat("Expected hierarchical query", sqlQuery, containsString("WITH RECURSIVE"));
        sqlQuery = sqlBuilder.getTreeUp(479L, null, false);
        assertThat(sqlQuery, not(containsString("RECURSIVE")));
    }
}
