package migration;

import org.igov.service.migration.MigrationService;
import org.igov.service.migration.MigrationServiceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * Created by dpekach on 01.05.17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:migration-context-test.xml")
public class MigrationServiceTest {

    @Autowired
    MigrationServiceImpl migrationService;
    
    @Test
    public void shouldReturnHistoricTaskList() {

    }
}
