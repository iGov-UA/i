package org.igov.service.business.action.task.systemtask;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.io.web.HttpRequester;
import org.igov.io.GeneralConfig;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 * @author a.skosyr
 */
@Component("setMessageFeedback_Indirectly")
public class SetMessageFeedback implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(SetMessageFeedback.class);

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private GeneralConfig generalConfig;
    
    @Autowired
    private HttpRequester httpRequester;
    
    public Expression nID_Rate_Indirectly;
    public Expression sBody_Indirectly;
    public Expression nID_Protected;
    public Expression nID_Proccess_Feedback;
    
    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
    	LOG.info(String.format("Processing SetMessageFeedback_Indirectly for the process ID %s", oExecution.getProcessInstanceId()));
    	
    	String nID_Rate_Indirectly = getStringFromFieldExpression(this.nID_Rate_Indirectly, oExecution);
    	String sBody_Indirectly = getStringFromFieldExpression(this.sBody_Indirectly, oExecution);
    	String nID_Protected = getStringFromFieldExpression(this.nID_Protected, oExecution);
    	    	
		LOG.info(String.format("Retrieved next variables from the process instance %s %s %s",
				nID_Rate_Indirectly, sBody_Indirectly, nID_Protected));
			
		if (nID_Rate_Indirectly != null && sBody_Indirectly != null){
			saveMessageFeedback((String)nID_Rate_Indirectly, (String)sBody_Indirectly, (String)nID_Protected, (String) oExecution.getProcessInstanceId());
		}
    }

    private void saveMessageFeedback(String sID_Rate_Indirectly, String sBody_Indirectly, String nID_Protected, String nID_Proccess_Feedback) {

		String URI = "/wf/service/subject/message/setMessageFeedback_Indirectly";
		
		Map<String, String> parts = new HashMap<String, String>();
		
		parts.put("nID_Protected", nID_Protected != null ? nID_Protected : "0");
		parts.put("nID_Server", String.valueOf(generalConfig.getSelfServerId()));
		parts.put("sBody_Indirectly", sBody_Indirectly);
		parts.put("sID_Rate_Indirectly", sID_Rate_Indirectly);
		parts.put("nID_Proccess_Feedback", nID_Proccess_Feedback);
		// Post
		
		LOG.info("Calling URL with parametes {}|{}", generalConfig.getSelfHostCentral() + URI, parts);
		
		try {
			httpRequester.getInside(generalConfig.getSelfHostCentral() + URI, parts);
		} catch (Exception oException) {
			LOG.error("Error: {}, Exception occured while calling setMessageFeedback_Indirectly method", oException.getMessage());
            LOG.trace("FAIL:", oException);
		}		
	}
    
}
