/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.config;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author olga
 */
@Transactional("transactionManagerAnalytic")
@Repository
public class ConfigDaoImpl extends GenericEntityDaoAnalytic<Long, Config> implements ConfigDao {

    private static final Logger log = Logger.getLogger(ConfigDaoImpl.class);

    protected ConfigDaoImpl() {
        super(Config.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Config findLatestConfig() {
        log.info("Inside findLatestConfig()");
        List<Config> result = getSession().createSQLQuery("select * from \"Config\" ORDER BY \"sValue\" DESC LIMIT 1;")
                .addEntity(Config.class).list();
        return result.get(0);
    }
}
