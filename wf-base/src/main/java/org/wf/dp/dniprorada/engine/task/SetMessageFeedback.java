package org.wf.dp.dniprorada.engine.task;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.wf.dp.dniprorada.liqPay.LiqBuyUtil;
import org.wf.dp.dniprorada.rest.HttpRequester;
import org.wf.dp.dniprorada.util.GeneralConfig;

/**
 * @author a.skosyr
 */
@Component("setMessageFeedback_Indirectly")
public class SetMessageFeedback implements JavaDelegate {

    private final static Logger log = LoggerFactory.getLogger(SetMessageFeedback.class);

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
        
    	log.info(String.format("Processing SetMessageFeedback_Indirectly for the process ID %s", oExecution.getProcessInstanceId()));
    	
    	String nID_Rate_Indirectly = getStringFromFieldExpression(this.nID_Rate_Indirectly, oExecution);
    	String sBody_Indirectly = getStringFromFieldExpression(this.sBody_Indirectly, oExecution);
    	String nID_Protected = getStringFromFieldExpression(this.nID_Protected, oExecution);
    	String nID_Proccess_Feedback = getStringFromFieldExpression(this.nID_Proccess_Feedback, oExecution);
    	    	
		log.info(String.format("Retrieved next variables from the process instance %s %s %s %s",
				nID_Rate_Indirectly, sBody_Indirectly, nID_Protected, nID_Proccess_Feedback));
			
		if (nID_Rate_Indirectly != null && sBody_Indirectly != null){
			saveMessageFeedback((String)nID_Rate_Indirectly, (String)sBody_Indirectly, (String)nID_Protected, (String) nID_Proccess_Feedback);
		}
    }

    private void saveMessageFeedback(String sID_Rate_Indirectly, String sBody_Indirectly, String nID_Protected, String nID_Proccess_Feedback) {

		String URI = "/wf/service/messages/setMessageFeedback_Indirectly";
		
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		
		parts.add("nID_Protected", nID_Protected);
		parts.add("nID_Server", generalConfig.nID_Server());
		parts.add("sBody_Indirectly", sBody_Indirectly);
		parts.add("sID_Rate_Indirectly", sID_Rate_Indirectly);
		parts.add("nID_Proccess_Feedback", nID_Proccess_Feedback);
		// Post
		
		String sUser = generalConfig.sAuthLogin();
        String sPassword = generalConfig.sAuthPassword();
        String sAuth = LiqBuyUtil.base64_encode(sUser + ":" + sPassword);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + sAuth);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(parts, headers);
		
		RestTemplate template = new RestTemplate();
		log.info("Calling URL with parametes" + generalConfig.sHostCentral() + URI + "|" + parts);
		Long result = template.postForObject(generalConfig.sHostCentral() + URI, httpEntity, Long.class);
		
		log.info("Received response from setMessageFeedback_Indirectly:" + result);
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
