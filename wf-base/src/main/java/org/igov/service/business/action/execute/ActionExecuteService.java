package org.igov.service.business.action.execute;

import org.igov.model.action.execute.item.ActionExecute;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by dpekach on 28.11.16.
 */
public interface ActionExecuteService {
    ActionExecute setActionExecute(Long nID_ActionExecuteStatus,
                                   DateTime oDateMake, DateTime oDateEdit, Integer nTry,
                                   String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn);

    List<ActionExecute> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);

    void moveActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);

    void moveActionExecute(ActionExecute actionExecute);
}
