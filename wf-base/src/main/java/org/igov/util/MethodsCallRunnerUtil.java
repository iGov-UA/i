package org.igov.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Base64;

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
	
	private static Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public Object registrateMethod(String className, String methodName, Object[] parameters) throws CommonServiceException{
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
			actionExecute.setSoRequest(className);
			actionExecute.setSmParam(parameters!=null?toString(parameters):null);
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
	
	public void runMethod() throws CommonServiceException{
		try{
			ActionExecute actionExecute = actionExecuteDAO.findByIdExpected(1l);
			
			Object ret = null;
			Class<?> c = Class.forName(actionExecute.getSoRequest());
			Object o = springContext.getBean(c);
			
			Object[] parameters = actionExecute.getSmParam()!=null?(Object[]) fromString(actionExecute.getSmParam()):null;
			
			Class<?>[] param_types = new Class<?>[parameters!=null?parameters.length:0];
			if (parameters!=null && parameters.length>0)
				for (int i=0; i< parameters.length; i++)
					param_types[i] = parameters[i].getClass();
			
			Method  method = c.getMethod(actionExecute.getsMethod(), param_types);
			
			if (parameters!= null)
				ret = method.invoke(o, parameters);
			else 
				ret = method.invoke(o);
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
	        LOG.trace("FAIL:", e);
	        throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
	
}
