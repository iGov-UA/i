package org.igov.util;

import org.activiti.engine.impl.util.json.JSONArray;
import org.apache.commons.beanutils.MethodUtils;
import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
import org.igov.model.action.execute.item.ActionExecuteStatusDAO;
import org.igov.service.business.action.execute.ActionExecuteService;
import org.igov.service.exception.CommonServiceException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class MethodsCallRunnerUtil {

	@Autowired
	private ActionExecuteDAO actionExecuteDAO;

	@Autowired
	private ActionExecuteStatusDAO actionExecuteStatusDAO;

	@Autowired
	private ActionExecuteService actionExecuteService;

	
	@Autowired
	private ApplicationContext springContext;
	
	private static final Logger LOG = LoggerFactory.getLogger(MethodsCallRunnerUtil.class);
	
	private static Object fromByteArray(byte[] byteArray){
		Object o = null;
		ObjectInputStream ois = null;
		LOG.info("Entering fromByteArray()");
		try{			
			ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
			o = ois.readObject();
		}catch(Exception e){
			LOG.error("Error during serializing data from byte array!");
			LOG.error(e.getMessage());
		}finally{
			if(ois!=null)
				try {
					ois.close();
				} catch (IOException e) {
					LOG.error("Error during closing InputStream!");
					LOG.error(e.getMessage());
				}
		}
		return o;
	}

	//TODO check implementation for objects serizlization
	private static byte[] toByteArray(Serializable o) throws IOException {
		byte[] res = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			res = baos.toByteArray();
		}catch(Exception e){
			LOG.error("Error during serializing data to byte array!");
			LOG.error(e.getMessage());
		}finally{
			try{
				if(baos!=null)
					baos.close();
				if(oos!=null)
					oos.close();
			}catch(IOException e){
				LOG.error("Error during closing OutputStream!");
				LOG.error(e.getMessage());
			}
		}
		return res;
	}

	public Object registerMethod(String className, String methodName, Object[] parameters) throws CommonServiceException{
		try{			
			LOG.info("in registerMethod");
			Object ret = null;
			Class<?> c = Class.forName(className);
			Object o = null;
			try{
				o = springContext.getBean(c);
			}catch(BeansException e){
				LOG.info("Cant find bean with class name {} in spring context.", className);
				o = c.getDeclaredConstructor().newInstance();
			}
			
			LOG.info("classname -{}", className);
			LOG.info("methodName -{}", methodName);
			
			Class<?>[] param_types = new Class<?>[parameters!=null?parameters.length:0];
			LOG.info("param_types lenght -{}", param_types.length);
			if (parameters!=null && parameters.length>0)
				for (int i=0; i< parameters.length; i++){					
					if(parameters[i] != null){
						LOG.info("parameters[{}]-{}", i, parameters[i]);
						param_types[i] = parameters[i].getClass();
					}else
						param_types[i] = null;
				}
			
			Method  method = getMethod(param_types, c, methodName);
			if (method==null)
				method = c.getMethod(methodName, param_types);
			if(method==null){
				LOG.error("Cant find method {}, in class {}", methodName, className);
				return null;
			} 
			if(!method.isAccessible())
				method.setAccessible(true);

			LOG.info("method is-{}", method!=null?"not null":"null");

			//TODO move to separate method 
			ActionExecute actionExecute = actionExecuteService.setActionExecute(1L, new DateTime(), new DateTime(), 0, className, methodName, parameters!=null?toByteArray(parameters):new byte[0], new JSONArray(parameters).toString(), null);
			actionExecuteDAO.saveOrUpdate(actionExecute);
			
			LOG.info("Method is saved!");
			LOG.info("Action execute object:{}", actionExecute);
			try{
				if (parameters!= null)
					ret = method.invoke(o, parameters);
				else 
					ret = method.invoke(o);
				LOG.info("return is {}",ret!=null?ret:null);

				actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(2L));
				LOG.info("Trying to move actionExecute to old table");
				actionExecuteService.moveActionExecute(actionExecute);
				LOG.info("ActionExecute is moved");

			}catch(InvocationTargetException e){
				actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(4L));
				actionExecute.setnTry(1);
				actionExecuteDAO.saveOrUpdate(actionExecute);
				LOG.info("error during invoke method {}",e);
			}
			
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
            LOG.error("FAIL:", e);
            throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
	
	public Object runMethod(Integer nRowsMax, String sMethodMask, String asID_Status, Integer nTryMax, Long nID) throws CommonServiceException{
		try{
			Object ret = null;
			List<ActionExecute> actionExecuteList = actionExecuteService.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
			LOG.info("actionExecuteList size -{}",actionExecuteList.size());
			for(ActionExecute actionExecute:actionExecuteList){
				Class<?> c = Class.forName(actionExecute.getsObject());
				Object o = springContext.getBean(c);
				
				if(o==null){
					o = c.getDeclaredConstructor().newInstance();
				}
				
				LOG.info("object - {}", o);				

				Object[] parammeters = actionExecute.getSmParam()!=null?(Object[]) fromByteArray(actionExecute.getSoRequest()):null;
				LOG.info("parameters - {}", parammeters);
				
				if (parammeters!= null)					
					LOG.info("parameters size - {}", parammeters.length);
				
				Class<?>[] param_types = new Class<?>[parammeters!=null?parammeters.length:0];
				if (parammeters!=null && parammeters.length>0)
					for (int i=0; i< parammeters.length; i++){
						param_types[i] = parammeters[i].getClass();
						LOG.info("param_type - {}", parammeters[i].getClass().getName());
					}
				
				Method  method = getMethod(param_types, c, actionExecute.getsMethod());
				LOG.info("method - {}", method);
				if (method==null)
					method = c.getMethod(actionExecute.getsMethod(), param_types);
				if(method==null){
					LOG.error("Cant find method {}, in class {}", actionExecute.getsMethod(), actionExecute.getSoRequest());
					return null;
				} 
				if(!method.isAccessible())
					method.setAccessible(true);				
			
				try{
					LOG.info("Trying to invoke method {}", method);
					if (parammeters!= null)
						ret = method.invoke(o, parammeters);
					else 
						ret = method.invoke(o);
					LOG.info("return is {}",ret!=null?ret:null);
					actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(2L));
					actionExecuteService.moveActionExecute(actionExecute);
				}catch(InvocationTargetException e) {
					actionExecute.setActionExecuteStatus(actionExecuteStatusDAO.findByIdExpected(4L));
					actionExecute.setnTry(actionExecute.getnTry()+1);
					actionExecuteDAO.saveOrUpdate(actionExecute);
					LOG.info("error during invoke method {}",e);
				}
			}
			return ret;
		}catch(Exception e){
			LOG.error("FAIL: {}", e.getMessage());
	        LOG.error("FAIL: {}", e);
	        throw new CommonServiceException(404, "Unknown exception: " + e.getMessage());
		}
	}
	
	private Method getMethod(Class<?>[] param_types, Class clazz, String methodName){
        int paramSize = param_types.length;
        Method bestMatch = null;
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0, size = methods.length; i < size ; i++) {
            if (methods[i].getName().equals(methodName)) {
                Class[] methodsParams = methods[i].getParameterTypes();
                int methodParamSize = methodsParams.length;
                if (methodParamSize == paramSize) {          
                    boolean match = true;
                    for (int n = 0 ; n < methodParamSize; n++) {
                    	if (param_types[n]!=null && !MethodUtils.isAssignmentCompatible(methodsParams[n], param_types[n])) {
                            match = false;
                            break;
                        }                    
                    }                    
                }
                bestMatch = methods[i];                		
            }
        }
        return bestMatch;
	}
	
}
