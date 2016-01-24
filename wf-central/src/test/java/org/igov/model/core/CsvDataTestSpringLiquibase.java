package org.igov.model.core;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * User: goodg_000
 * Date: 13.12.2015
 * Time: 15:30
 */
public class CsvDataTestSpringLiquibase extends SpringLiquibase {

    public class TestSpringResourceOpener extends SpringResourceOpener {

        private static final String FILE_DELIM = "/";
        private static final String CSV_FILE_EXT = ".csv";

        public TestSpringResourceOpener(String parentFile) {
            super(parentFile);
        }

        @Override
        public Set<InputStream> getResourcesAsStream(String path) throws IOException {

            if (dataSubfolder != null && path.endsWith(CSV_FILE_EXT)) {
                int idx = path.lastIndexOf(FILE_DELIM);

                String modifiedPath = path.substring(0, idx) + FILE_DELIM + dataSubfolder + path.substring(idx);
                Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(
                        getResourceLoader()).getResources(modifiedPath);
                if (resources.length > 0 && resources[0].exists()) {
                    return super.getResourcesAsStream(modifiedPath);
                }
            }

            return super.getResourcesAsStream(path);
        }
    }

    private String dataSubfolder;

    public String getDataSubfolder() {
        return dataSubfolder;
    }

    public void setDataSubfolder(String dataSubfolder) {
        this.dataSubfolder = dataSubfolder;
    }

    protected SpringResourceOpener createResourceOpener() {
        return new TestSpringResourceOpener(getChangeLog());
    }
}
