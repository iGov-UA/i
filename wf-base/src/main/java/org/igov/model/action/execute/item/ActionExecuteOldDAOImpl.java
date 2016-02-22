package org.igov.model.action.execute.item;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
public class ActionExecuteOldDAOImpl extends GenericEntityDao<ActionExecuteOld>	implements ActionExecuteOldDAO {

	protected ActionExecuteOldDAOImpl() {
		super(ActionExecuteOld.class);
	}

	@Override
	public ActionExecuteOld getActionExecute(Long id) {
		return findById(id).orNull();
	}

	@Override
	public List<ActionExecuteOld> getAllActionExecutes() {
		return findAll();
	}

	@Override
	public Long setActionExecute(Long nID_ActionExecuteStatus,
			DateTime oDateMake, DateTime oDateEdit, Integer nTry,
			String sMethod, String soRequest, String smParam, String sReturn) {
		ActionExecuteOld actionExecute = new ActionExecuteOld();
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

	@Override
	public List<ActionExecuteOld> getActionExecute(Integer nRowsMax, String sMethodMask, Long asID_Status, Long nTryMax, Long nID) {
		List<ActionExecuteOld> resList = new ArrayList<ActionExecuteOld>();
		
		Criteria criteria = getSession().createCriteria(ActionExecuteOld.class);
		criteria.setMaxResults(nRowsMax);
		if(nTryMax!=null)
			criteria.add(Restrictions.le("nTry", nTryMax));
		if(nID!=null)
			criteria.add(Restrictions.eq("nID", nID));
		if(asID_Status!=null){			
			JSONArray statuses = new JSONArray(asID_Status);			
			for(int i=0;i<statuses.length();i++){
				criteria.add(Restrictions.in("nID_ActionExecuteStatus", (Object[]) statuses.get(i)));
			}
		}
		if(sMethodMask!=null){
			if(sMethodMask.contains("*"))			
				criteria.add(Restrictions.like("sMethod", sMethodMask.replace("*", "%")));
			else
				criteria.add(Restrictions.eq("sMethod", sMethodMask));
		}
		criteria.add(Restrictions.eq("nID", nID));
		resList = criteria.list();
		return resList;
	}
}
