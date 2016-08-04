package org.igov.util.db;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Required;

/**
 * User: goodg_000
 * Date: 12.07.2016
 * Time: 1:06
 */
public class DbManager {
    private SpringLiquibase liquibase;

    @Required
    public void setLiquibase(SpringLiquibase liquibase) {
        this.liquibase = liquibase;
    }

    /**
     * Recreates db using liquibase.
     */
    public synchronized void recreateDb() {
        boolean oldDropFirst = liquibase.isDropFirst();
        liquibase.setDropFirst(true);

        try {
            liquibase.afterPropertiesSet();
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
        finally {
            liquibase.setDropFirst(oldDropFirst);
        }
    }
}
