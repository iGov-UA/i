package org.igov.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
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
		byte[] data = Base64.decodeBase64(s);
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
		return Base64.encodeBase64String(baos.toByteArray());
	}

	public Object registrateMethod(String className, String methodName, Object[] parameters) throws CommonServiceException{
		try{			
			LOG.info("in egistrateMethod");
			Object ret = null;
			Class<?> c = Class.forName(className);
			Object o = springContext.getBean(c);
			LOG.info("classname -{}", className);
			LOG.info("methodName -{}", methodName);
			Class<?>[] param_types = new Class<?>[parameters!=null?parameters.length:0];
			if (parameters!=null && parameters.length>0)
				for (int i=0; i< parameters.length; i++)
					param_types[i] = parameters[i].getClass();
			
			Method  method = c.getMethod(methodName, param_types);

			LOG.info("method is-{}", method!=null?"not null":"null");

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
			
			try{
				if (parameters!= null)
					ret = method.invoke(o, parameters);
				else 
					ret = method.invoke(o);
				LOG.info("return is {}",ret!=null?ret:null);

				actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(2l));
				actionExecuteDAO.moveActionExecute(actionExecute);
			}catch(InvocationTargetException e){
				actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(4l));
				actionExecute.setnTry(1);
				actionExecuteDAO.saveOrUpdate(actionExecute);
				LOG.info("error during invoke method {}",e);
			}
			
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
	
	public Object runMethod(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) throws CommonServiceException{
		try{
			Object ret = null;
			List<ActionExecute> actionExecuteLsit = actionExecuteDAO.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
			LOG.info("actionExecuteLsit size -{}",actionExecuteLsit.size());
			for(ActionExecute actionExecute:actionExecuteLsit){
				Class<?> c = Class.forName(actionExecute.getSoRequest());
				Object o = springContext.getBean(c);
				
				Object[] parameters = actionExecute.getSmParam()!=null?(Object[]) fromString(actionExecute.getSmParam()):null;
				
				Class<?>[] param_types = new Class<?>[parameters!=null?parameters.length:0];
				if (parameters!=null && parameters.length>0)
					for (int i=0; i< parameters.length; i++)
						param_types[i] = parameters[i].getClass();
				
				Method  method = c.getMethod(actionExecute.getsMethod(), param_types);
			
				try{
					if (parameters!= null)
						ret = method.invoke(o, parameters);
					else 
						ret = method.invoke(o);
					LOG.info("return is {}",ret!=null?ret:null);
					actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(2l));
					actionExecuteDAO.moveActionExecute(actionExecute);
				}catch(InvocationTargetException e){
					actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(4l));
					actionExecute.setnTry(actionExecute.getnTry()+1);
					actionExecuteDAO.saveOrUpdate(actionExecute);
					LOG.info("error during invoke method {}",e);

				}
			}
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
	        LOG.trace("FAIL:", e);
	        throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
	
}
