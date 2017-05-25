/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * @author olga
 */
@Transactional("transactionManagerAnalytic")
@Repository
public class ProcessDaoImpl extends GenericEntityDaoAnalytic<Long, Process> implements ProcessDao {

    private static final Logger log = Logger.getLogger(ProcessDaoImpl.class);

    protected ProcessDaoImpl() {
        super(Process.class);
    }

    @Autowired
    ConfigDao configDao;

    @Override
    public void saveWithConfigBackup(Process process) {
        saveOrUpdate(process);
        configDao.saveOrUpdate(createBackupConfig(process.getoDateStart()));
    }

    private Config createBackupConfig(DateTime startDateTime) {
        Config config = new Config();
        config.setsValue(startDateTime.toString("yyyy-MM-dd HH:mm:ss.ssssss"));
        config.setName("dateLastBackup");
        return config;

    }
}
