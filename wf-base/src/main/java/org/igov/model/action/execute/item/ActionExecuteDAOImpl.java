package org.igov.model.action.execute.item;

import java.util.List;

import org.igov.model.core.GenericEntityDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
public class ActionExecuteDAOImpl extends GenericEntityDao<ActionExecute> implements ActionExecuteDAO {

	protected ActionExecuteDAOImpl() {
		super(ActionExecute.class);
	}

	@Override
	public ActionExecute getActionExecute(Long id) {
		return findById(id).orNull();
	}

	@Override
	public List<ActionExecute> getAllActionExecutes() {
		return findAll();
	}

	@Override
	public Long setActionExecute(Long nID_ActionExecuteStatus,
			DateTime oDateMake, DateTime oDateEdit, Integer nTry,
			String sMethod, String soRequest, String smParam, String sReturn) {
		ActionExecute actionExecute = new ActionExecute();
		ActionExecuteStatus aes = new ActionExecuteStatus();
        aes.setId(nID_ActionExecuteStatus);
        actionExecute.setActionExecuteStatus(aes);

		actionExecute.setoDateMake(oDateMake);
		actionExecute.setoDateEdit(oDateEdit);
		actionExecute.setnTry(nTry);
		actionExecute.setsMethod(sMethod);
		actionExecute.setSoRequest(soRequest);
		actionExecute.setsReturn(sReturn);
		
		getSession().saveOrUpdate(actionExecute);
		return actionExecute.getId();
	}

}
