package org.igov.service.business.action.execute.old;

import org.hibernate.Session;
import org.igov.model.action.execute.item.ActionExecuteOld;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by dpekach on 19.11.16.
 */
public interface ActionExecuteServiceOld {
    Long setActionExecuteOld(Long nID_ActionExecuteStatus,
                                      DateTime oDateMake, DateTime oDateEdit, Integer nTry,
                                      String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn, Session session);

    List<ActionExecuteOld> getActionExecuteOldListByCriteria(Integer nRowsMax, String sMethodMask, String asID_Status,
                                                             Integer nTryMax, Long nID, Session session);

    void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax,
                         Long nID, Session session);
}
