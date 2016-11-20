package org.igov.model.action.execute.item;

import org.igov.model.core.GenericEntityDao;
import org.igov.service.business.action.execute.old.ActionExecuteServiceOld;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ActionExecuteOldDAOImpl extends GenericEntityDao<Long, ActionExecuteOld> implements ActionExecuteOldDAO {

    @Autowired
    ActionExecuteServiceOld actionExecuteServiceOld;

    protected ActionExecuteOldDAOImpl() {
        super(ActionExecuteOld.class);
    }

    @Override
    public ActionExecuteOld getActionExecute(Long id) {
        return findById(id).orNull();
    }

    @Override
    public List<ActionExecuteOld> getAllActionExecutes() {
        return findAll();
    }

    @Transactional
    @Override
    public Long setActionExecuteOld(Long nID_ActionExecuteStatus,
            DateTime oDateMake, DateTime oDateEdit, Integer nTry,
            String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn) {

        return actionExecuteServiceOld.setActionExecuteOld(nID_ActionExecuteStatus, oDateMake, oDateEdit, nTry, sObject, sMethod,
                soRequest, smParam, sReturn, getSession());
    }

    @Transactional
    @Override
    public List<ActionExecuteOld> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        return actionExecuteServiceOld.getActionExecuteOldListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID, getSession());
    }


    @Override
    @Transactional
    public void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax,
            Long nID) {
        actionExecuteServiceOld.moveActionExecuteOld(nRowsMax, sMethodMask, asID_Status, nTryMax, nID, getSession());
    }
}
