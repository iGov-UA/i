package org.igov.service.business.action.execute.old;

import org.igov.model.action.execute.item.ActionExecuteOld;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by dpekach on 28.11.16.
 */
public interface ActionExecuteOldService {
    Long setActionExecuteOld(Long nID_ActionExecuteStatus,
                             DateTime oDateMake, DateTime oDateEdit, Integer nTry,
                             String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn);
    List<ActionExecuteOld> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);

    void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax,
                              Long nID);


}
