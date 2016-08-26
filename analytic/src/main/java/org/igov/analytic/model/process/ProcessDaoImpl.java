/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.igov.util.db.QueryBuilder;
import org.igov.util.db.queryloader.QueryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class ProcessDaoImpl extends GenericEntityDaoAnalytic<Long, Process> implements ProcessDao {

    private static final Logger log = Logger.getLogger(ProcessDaoImpl.class);

    @Autowired
    QueryLoader queryLoader;

    protected ProcessDaoImpl() {
        super(Process.class);
    }

    @Override
    public void removeOldProcess(String sID_Process_Def, String sDateFinishAt, String sDateFinishTo) {
        for (Entry<String, String> removeOldProcessQuery : queryLoader.getRemoveOldProcessQueries().entrySet()) {
            String removeOldProcessQueryValue;
            if (removeOldProcessQuery.getKey().startsWith("update")) {
                removeOldProcessQueryValue = String.format(removeOldProcessQuery.getValue(), sID_Process_Def, sDateFinishAt, sDateFinishTo);
            } else {
                removeOldProcessQueryValue = removeOldProcessQuery.getValue();
            }
            log.info(removeOldProcessQueryValue + " ...");
            Query query = new QueryBuilder(getSession()).append(removeOldProcessQueryValue).toSQLQuery();
            log.info(removeOldProcessQueryValue + " success!");
        }
    }
}
