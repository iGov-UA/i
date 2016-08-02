/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.config;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
@Repository()
public class ConfigDaoImpl extends GenericEntityDaoAnalytic<Long, Config> implements ConfigDao  {
    
    private static final Logger log = Logger.getLogger(ConfigDaoImpl.class);

    protected ConfigDaoImpl() {
        super(Config.class);
    }
}
