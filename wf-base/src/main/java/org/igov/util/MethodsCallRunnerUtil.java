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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MethodsCallRunnerUtil {

	@Autowired
	private ActionExecuteDAO actionExecuteDAO;

	@Autowired
	private ActionExecuteStatusDAO actionExecuteStatusDAO;
	
	@Autowired
	private ApplicationContext springContext;
	
	private static final Logger LOG = LoggerFactory.getLogger(MethodsCallRunnerUtil.class);
	
	public Object runMethod(String className, String methodName, Object[] parameters) throws CommonServiceException{
		try{			
			Object ret = null;
			Class<?> c = Class.forName(className);
			Object o = springContext.getBean(c);
			
			Class<?>[] param_types = new Class<?>[parameters!=null?parameters.length:0];
			if (parameters!=null && parameters.length>0)
				for (int i=0; i< parameters.length; i++)
					param_types[i] = parameters[i].getClass();
			
			Method  method = c.getMethod(methodName, param_types);
			
			ActionExecute actionExecute = new ActionExecute();
			ActionExecuteStatus actionExecuteStatus = actionExecuteStatusDAO.findByIdExpected(1l);								
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
				ret = method.invoke(o, parameters);
			else 
				ret = method.invoke(o);
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
		
}
