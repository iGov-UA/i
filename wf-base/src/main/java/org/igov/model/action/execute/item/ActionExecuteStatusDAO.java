package org.igov.model.action.execute.item;

import org.igov.model.core.EntityDao;

public interface ActionExecuteStatusDAO extends EntityDao<ActionExecuteStatus>{
	ActionExecuteStatus getStatus(Long nID);
	Long setActionExecuteStatus(String sID, String sName);
}
