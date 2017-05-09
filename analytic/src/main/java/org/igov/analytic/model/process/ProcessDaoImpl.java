/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class ProcessDaoImpl extends GenericEntityDaoAnalytic<Long, Process> implements ProcessDao {

    private static final Logger log = Logger.getLogger(ProcessDaoImpl.class);

    protected ProcessDaoImpl() {
        super(Process.class);
    }

    @Override
    public Process findLatestProcess() {
        log.info("Inside findLatestProcess()");
        List<Process> result = getSession().createSQLQuery("select * from \"Process\" ORDER BY \"oDateStart\" DESC LIMIT 1;\n")
                .list();
        result.forEach(entry -> log.info("List of processes: current entry - " + entry.toString()));
        return  result.get(0);
    }
}
