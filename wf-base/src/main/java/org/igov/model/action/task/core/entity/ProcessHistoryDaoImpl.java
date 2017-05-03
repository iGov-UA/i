/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.action.task.core.entity;

import org.hibernate.Query;
import org.igov.model.core.GenericEntityDao;
import org.igov.util.db.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class ProcessHistoryDaoImpl extends GenericEntityDao<Long, ProcessHistory> implements ProcessHistoryDao {

    protected ProcessHistoryDaoImpl() {
        super(ProcessHistory.class);
    }

    @Override
    @Transactional
    public int removeOldProcess(String removeOldProcessQueryValue, String sID_Process_Def, String sDateFinishAt, String sDateFinishTo) {
        Query query = new QueryBuilder(getSession()).append(removeOldProcessQueryValue).toSQLQuery();
        return query.executeUpdate();
    }
}
