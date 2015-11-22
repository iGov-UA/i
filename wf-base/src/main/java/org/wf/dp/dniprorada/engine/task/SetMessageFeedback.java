package org.wf.dp.dniprorada.engine.task;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
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
@Component("SetMessageFeedback_Indirectly")
public class SetMessageFeedback implements JavaDelegate {

    private final static Logger log = LoggerFactory.getLogger(SetMessageFeedback.class);

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private GeneralConfig generalConfig;
    
    @Autowired
    private HttpRequester httpRequester;
    
    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
    	log.info(String.format("Processing SetMessageFeedback_Indirectly for the process ID %s", oExecution.getProcessInstanceId()));
    	
    	Object sID_Rate_Indirectly = runtimeService.getVariable(oExecution.getProcessInstanceId(), "sID_Rate_Indirectly");
    	Object sBody_Indirectly = runtimeService.getVariable(oExecution.getProcessInstanceId(), "sBody_Indirectly");
    	Object nID_Protected = runtimeService.getVariable(oExecution.getProcessInstanceId(), "nID_Protected");
    	Object nID_Proccess_Feedback = runtimeService.getVariable(oExecution.getProcessInstanceId(), "nID_Proccess_Feedback");
    	    	
		log.info(String.format("Retrieved next variables from the process instance %s %s %s %s",
				sID_Rate_Indirectly, sBody_Indirectly, nID_Protected, nID_Proccess_Feedback));
			
		if (sID_Rate_Indirectly != null && sBody_Indirectly != null){
			saveMessageFeedback((String)sID_Rate_Indirectly, (String)sBody_Indirectly, (String)nID_Protected, (String) nID_Proccess_Feedback);
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

}
