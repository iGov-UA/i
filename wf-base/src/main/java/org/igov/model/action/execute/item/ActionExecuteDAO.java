package org.igov.model.action.execute.item;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface ActionExecuteDAO extends EntityDao<Long, ActionExecute>, Sessionable {
	
	ActionExecute getActionExecute(Long id);
	List<ActionExecute> getAllActionExecutes();

}
