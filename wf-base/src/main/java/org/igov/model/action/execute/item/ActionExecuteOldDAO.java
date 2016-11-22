package org.igov.model.action.execute.item;

import java.util.List;

import org.igov.model.core.EntityDao;
import org.joda.time.DateTime;

public interface ActionExecuteOldDAO extends EntityDao<Long, ActionExecuteOld> {
	
	ActionExecuteOld getActionExecute(Long id);
	List<ActionExecuteOld> getAllActionExecutes();
	Long setActionExecuteOld(Long nID_ActionExecuteStatus, DateTime oDateMake, DateTime oDateEdit, Integer nTry, String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn);
	List<ActionExecuteOld> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);
	void moveActionExecuteOld(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);
}
