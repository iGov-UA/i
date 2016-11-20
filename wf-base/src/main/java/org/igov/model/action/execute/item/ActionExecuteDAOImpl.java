package org.igov.model.action.execute.item;

import org.igov.model.core.GenericEntityDao;
import org.igov.service.business.action.execute.ActionExecuteService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ActionExecuteDAOImpl extends GenericEntityDao<Long, ActionExecute> implements ActionExecuteDAO {

    @Autowired
    ActionExecuteOldDAO actionExecuteOldDAO;

    @Autowired
    ActionExecuteService actionExecuteService;

    private static final Logger LOG = LoggerFactory.getLogger(ActionExecuteDAOImpl.class);

    protected ActionExecuteDAOImpl() {
        super(ActionExecute.class);
    }

    @Override
    public ActionExecute getActionExecute(Long id) {
        return findById(id).orNull();
    }

    @Override
    public List<ActionExecute> getAllActionExecutes() {
        return findAll();
    }

    @Transactional
    @Override
    public ActionExecute setActionExecute(Long nID_ActionExecuteStatus,
            DateTime oDateMake, DateTime oDateEdit, Integer nTry,
            String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn) {
        return actionExecuteService.setActionExecute(nID_ActionExecuteStatus, oDateMake, oDateEdit, nTry, sObject, sMethod,
                soRequest, smParam, sReturn, getSession());
    }

    @Transactional
    @Override
    public List<ActionExecute> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        return getActionExecuteListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    }

    @Override
    @Transactional
    public void moveActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        List<ActionExecute> actionExecuteList = getActionExecuteListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
        if (actionExecuteList.size() > 0) {
            for (ActionExecute actionExecute : actionExecuteList) {
                moveActionExecute(actionExecute);
            }
        }
    }

    //anti-pattern Public Morozoff
    @Transactional
    private List<ActionExecute> getActionExecuteListByCriteria(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        return actionExecuteService.getActionExecuteListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID, getSession());
    }

    /**
     *
     * @param actionExecute
     */
    @Transactional
    @Override
    public void moveActionExecute(ActionExecute actionExecute) {
        actionExecuteService.moveActionExecute(actionExecute, getSession());
    }
}
