package org.igov.model.action.execute.item;

import org.hibernate.Session;
import org.igov.model.core.EntityDao;

import java.util.List;

public interface ActionExecuteDAO extends EntityDao<Long, ActionExecute> {
	
	ActionExecute getActionExecute(Long id);
	List<ActionExecute> getAllActionExecutes();

    Session getSessionForService();

}
