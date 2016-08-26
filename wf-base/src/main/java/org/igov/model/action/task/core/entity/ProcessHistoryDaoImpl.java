/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.action.task.core.entity;

import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.igov.model.core.GenericEntityDao;
import org.igov.util.db.QueryBuilder;
import org.igov.util.db.queryloader.QueryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class ProcessHistoryDaoImpl extends GenericEntityDao<Long, ProcessHistory> implements ProcessHistoryDao {

    private static final Logger log = Logger.getLogger(ProcessHistoryDaoImpl.class);

    protected ProcessHistoryDaoImpl() {
        super(ProcessHistory.class);
    }

    @Override
    @Transactional
    public void removeOldProcess(String removeOldProcessQueryValue, String sID_Process_Def, String sDateFinishAt, String sDateFinishTo) {
        Query query = new QueryBuilder(getSession()).append(removeOldProcessQueryValue).toSQLQuery();
    }
}
