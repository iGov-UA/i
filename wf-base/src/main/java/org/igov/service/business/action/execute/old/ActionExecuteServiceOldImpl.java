package org.igov.service.business.action.execute.old;

import org.activiti.engine.impl.util.json.JSONArray;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
import org.igov.model.action.execute.item.ActionExecuteOld;
import org.igov.model.action.execute.item.ActionExecuteStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dpekach on 19.11.16.
 */
@Service
public class ActionExecuteServiceOldImpl implements ActionExecuteServiceOld {


    @Autowired
    ActionExecuteDAO actionExecuteDAO;

    @Transactional
    @Override
    public Long setActionExecuteOld(Long nID_ActionExecuteStatus,
                                          DateTime oDateMake, DateTime oDateEdit, Integer nTry,
                                          String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn, Session session) {
        ActionExecuteOld actionExecute = new ActionExecuteOld();
        ActionExecuteStatus aes = new ActionExecuteStatus();
        aes.setId(nID_ActionExecuteStatus);
        actionExecute.setActionExecuteStatus(aes);

        actionExecute.setoDateMake(oDateMake);
        actionExecute.setoDateEdit(oDateEdit);
        actionExecute.setnTry(nTry);
        actionExecute.setsObject(sObject);
        actionExecute.setsMethod(sMethod);
        actionExecute.setSoRequest(soRequest);
        actionExecute.setSmParam(smParam);
        actionExecute.setsReturn(sReturn);

        session.saveOrUpdate(actionExecute);

        return actionExecute.getId();
    }

    @Transactional
    public List<ActionExecuteOld> getActionExecuteOldListByCriteria(Integer nRowsMax, String sMethodMask, String asID_Status,
                                                                    Integer nTryMax, Long nID, Session session) {
        Criteria criteria = session.createCriteria(ActionExecuteOld.class);
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
    @Transactional
    public void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax,
                                     Long nID, Session session) {
        List<ActionExecuteOld> resList = getActionExecuteOldListByCriteria(nRowsMax, sMethodMask, asID_Status, nTryMax, nID, session);
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
                session.delete(actionExecuteOld);
            }
        }
    }
}
