package org.igov.model.action.execute.item;

import java.util.List;

import org.igov.model.core.EntityDao;
import org.joda.time.DateTime;

public interface ActionExecuteDAO extends EntityDao<Long, ActionExecute> {
	
	ActionExecute getActionExecute(Long id);
	List<ActionExecute> getAllActionExecutes();
	ActionExecute setActionExecute(Long nID_ActionExecuteStatus, DateTime oDateMake, DateTime oDateEdit, Integer nTry, String sObject, String sMethod, byte[] soRequest, String smParam, String sReturn);
	List<ActionExecute> getActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);
	void moveActionExecute(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID);
	void moveActionExecute(ActionExecute actionExecute);
}
