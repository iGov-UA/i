package org.igov.service.business.action.execute.old;

import org.activiti.engine.impl.util.json.JSONArray;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.action.execute.item.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dpekach on 28.11.16.
 */
@Service
@Transactional
public class ActionExecuteOldServiceImpl implements ActionExecuteOldService {

    @Autowired
    ActionExecuteDAO actionExecuteDAO;

    @Autowired
    ActionExecuteOldDAO actionExecuteOldDAO;

    @Override
    public Long setActionExecuteOld(Long nID_ActionExecuteStatus, DateTime oDateMake, DateTime oDateEdit, Integer nTry, String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn) {
        ActionExecuteOld actionExecuteOld = new ActionExecuteOld();
        ActionExecuteStatus aes = new ActionExecuteStatus();
        aes.setId(nID_ActionExecuteStatus);
        actionExecuteOld.setActionExecuteStatus(aes);

        actionExecuteOld.setoDateMake(oDateMake);
        actionExecuteOld.setoDateEdit(oDateEdit);
        actionExecuteOld.setnTry(nTry);
        actionExecuteOld.setsObject(sObject);
        actionExecuteOld.setsMethod(sMethod);
        actionExecuteOld.setSoRequest(soRequest);
        actionExecuteOld.setSmParam(smParam);
        actionExecuteOld.setsReturn(sReturn);

        actionExecuteOldDAO.getSessionForService().saveOrUpdate(actionExecuteOld);
        return actionExecuteOld.getId();
    }

    @Override
    public List<ActionExecuteOld> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        return getActionExecuteOldListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    }

    private List<ActionExecuteOld> getActionExecuteOldListByCriteria(Integer nRowsMax, String sMethodMask, String asID_Status,
                                                                     Integer nTryMax, Long nID) {
        Criteria criteria = actionExecuteOldDAO.getSessionForService().createCriteria(ActionExecuteOld.class);
        criteria.setMaxResults(nRowsMax);
        if (nTryMax != null) {
            criteria.add(Restrictions.le("nTry", nTryMax));
        }
        if (nID != null) {
            criteria.add(Restrictions.eq("id", nID));
        }
        if (asID_Status != null) {
            JSONArray statuses = new JSONArray(asID_Status);
            for (int i = 0; i < statuses.length(); i++) {
                criteria.add(Restrictions.in("nID_ActionExecuteStatus", (Object[]) statuses.get(i)));
            }
        }
        if (sMethodMask != null) {
            if (sMethodMask.contains("*")) {
                criteria.add(Restrictions.like("sMethod", sMethodMask.replace("*", "%")));
            } else {
                criteria.add(Restrictions.eq("sMethod", sMethodMask));
            }
        }
        List<ActionExecuteOld> resList = criteria.list();
        return resList;
    }


    @Override
    public void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) {
        List<ActionExecuteOld> resList = getActionExecuteOldListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
        if (resList.size() > 0) {
            for (ActionExecuteOld actionExecuteOld : resList) {
                ActionExecute actionExecute = new ActionExecute();

                actionExecute.setActionExecuteStatus(actionExecuteOld.getActionExecuteStatus());
                actionExecute.setoDateMake(actionExecuteOld.getoDateMake());
                actionExecute.setoDateEdit(actionExecuteOld.getoDateEdit());
                actionExecute.setnTry(actionExecuteOld.getnTry());
                actionExecute.setsObject(actionExecuteOld.getsObject());
                actionExecute.setsMethod(actionExecuteOld.getsMethod());
                actionExecute.setSoRequest(actionExecuteOld.getSoRequest());
                actionExecute.setsReturn(actionExecuteOld.getsReturn());
                actionExecute.setSmParam(actionExecuteOld.getSmParam());

                actionExecuteDAO.saveOrUpdate(actionExecute);
                actionExecuteOldDAO.getSessionForService().delete(actionExecuteOld);
            }
        }
    }
}
