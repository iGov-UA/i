package org.igov.util;

import java.lang.reflect.Method;

import org.activiti.engine.impl.util.json.JSONArray;
import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
import org.igov.model.action.execute.item.ActionExecuteStatus;
import org.igov.model.action.execute.item.ActionExecuteStatusDAO;
import org.igov.service.exception.CommonServiceException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MethodsCallRunnerUtil {

	@Autowired
	static ActionExecuteDAO actionExecuteDAO;

	@Autowired
	static ActionExecuteStatusDAO actionExecuteStatusDAO;
	
	
	private static final Logger LOG = LoggerFactory.getLogger(MethodsCallRunnerUtil.class);
	
	public static Object runMethod(String className, String methodName, Object[] parameters) throws CommonServiceException{
		try{
			Object ret = null;
			Class<?> c = Class.forName(className);
			Class<?>[] param_types = new Class<?>[parameters.length];
			for (int i=0; i< parameters.length; i++)
				param_types[i] = parameters[i].getClass();
			
			Method  method = c.getMethod(methodName, param_types);
			
			ActionExecute actionExecute = new ActionExecute();
			ActionExecuteStatus actionExecuteStatus = actionExecuteStatusDAO.findByIdExpected(1L);								
			actionExecute.setActionExecuteStatus(actionExecuteStatus);
			actionExecute.setnTry(0);
			actionExecute.setoDateEdit(null);
			actionExecute.setoDateMake(new DateTime());
			actionExecute.setsMethod(methodName);
			actionExecute.setSoRequest(parameters!=null? new JSONArray(parameters).toString() : null);
			actionExecute.setSmParam(null);
			actionExecute.setsReturn(null);
			
			actionExecuteDAO.saveOrUpdate(actionExecute);
			if (parameters!= null)
				ret = method.invoke(c, parameters);
			else 
				ret = method.invoke(c);
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
		
}
