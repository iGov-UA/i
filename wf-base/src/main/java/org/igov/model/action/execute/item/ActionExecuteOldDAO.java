package org.igov.model.action.execute.item;

import java.util.List;

import org.igov.model.core.EntityDao;
import org.joda.time.DateTime;

public interface ActionExecuteOldDAO extends EntityDao<ActionExecuteOld> {
	
	ActionExecuteOld getActionExecute(Long id);
	List<ActionExecuteOld> getAllActionExecutes();
	Long setActionExecute(Long nID_ActionExecuteStatus, DateTime oDateMake, DateTime oDateEdit, Integer nTry, String sMethod, String soRequest, String smParam, String sReturn);
}
