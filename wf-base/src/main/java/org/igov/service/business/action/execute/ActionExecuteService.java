package org.igov.service.business.action.execute;

import org.hibernate.Session;
import org.igov.model.action.execute.item.ActionExecute;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by dpekach on 19.11.16.
 */
public interface ActionExecuteService {
    ActionExecute setActionExecute(Long nID_ActionExecuteStatus,
                                   DateTime oDateMake, DateTime oDateEdit, Integer nTry,
                                   String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn, Session session);

    List<ActionExecute> getActionExecuteListByCriteria(Integer nRowsMax, String sMethodMask, String asID_Status,
                                                       Integer nTryMax, Long nID, Session session);

    void moveActionExecute(ActionExecute actionExecute, Session session);
}
