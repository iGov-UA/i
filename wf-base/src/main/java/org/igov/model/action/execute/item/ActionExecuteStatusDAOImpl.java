package org.igov.model.action.execute.item;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class ActionExecuteStatusDAOImpl extends	GenericEntityDao<ActionExecuteStatus> implements ActionExecuteStatusDAO{

	protected ActionExecuteStatusDAOImpl() {
		super(ActionExecuteStatus.class);
	}

	@Override
	public ActionExecuteStatus getStatus(Long nID) {
		return findById(nID).orNull();
	}

	@Override
	public Long setActionExecuteStatus(String sID, String sName) {
		ActionExecuteStatus actionExecuteStatus = new ActionExecuteStatus();
		actionExecuteStatus.setsID(sID);
		actionExecuteStatus.setsName(sName);
		getSession().saveOrUpdate(actionExecuteStatus);
		return actionExecuteStatus.getId();
	}

}
