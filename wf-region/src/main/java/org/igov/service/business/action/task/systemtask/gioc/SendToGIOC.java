package org.igov.service.business.action.task.systemtask.gioc;

/**
 *
 * @author skuhtin
 */

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Component("sendToGIOC")
public class SendToGIOC implements JavaDelegate {
    
    private final static Logger LOG = LoggerFactory.getLogger(SendToGIOC.class);
    
    @Autowired
    private GeneralConfig generalConfig;
    
    @Autowired
    private HttpRequester httpRequester;
    
    @Autowired
    private RuntimeService runtimeService;
        
    Expression sLogin; 

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
        LOG.info(String.format("Processing SendToGIOC for the process ID %s", oExecution.getProcessInstanceId()));
        
        String sLogin = getStringFromFieldExpression(this.sLogin, oExecution);
        
        LOG.info("Retrieved variable sLogin from the process instance");
        
        if (sLogin != null) {
            
            String URI = "/wf/service/action/identity/getGroups";
            
            Map<String, String> part = new HashMap<>();
            
            part.put("sLogin", sLogin);
            
            LOG.info("Calling URL with parametes {}|{}", generalConfig.getSelfHostCentral() + URI, part);
            
            String sListOfGroups = null;
            
            try {
		sListOfGroups = httpRequester.getInside(generalConfig.getSelfHostCentral() + URI, part);
		} catch (Exception oException) {
                    LOG.error("Error: {}, Exception occured while calling SendToGIOC method", oException.getMessage());
                    LOG.trace("FAIL:", oException);
		}
            
            runtimeService.setVariable(oExecution.getProcessInstanceId(), "sListOfGroups", sListOfGroups);
            LOG.info("Set variable to runtime process:{}", sListOfGroups);
        }
         
    }
    
    protected String getStringFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}
