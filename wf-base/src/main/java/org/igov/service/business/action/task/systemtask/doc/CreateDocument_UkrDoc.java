package org.igov.service.business.action.task.systemtask.doc;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("CreateDocument_UkrDoc")
public class CreateDocument_UkrDoc implements JavaDelegate {

	public static final String UKRDOC_ID_DOCUMENT_VARIABLE_NAME = "sID_Document";

	private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);
	
	private Expression sLoginAuthor;
	private Expression sHead;
	private Expression sBody;
	private Expression nID_Pattern;
	
	 @Autowired
	 GeneralConfig generalConfig; 
	 
	 @Autowired
	 RuntimeService runtimeService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String sLoginAuthorValue = getStringFromFieldExpression(this.sLoginAuthor, execution);
		String sHeadValue = getStringFromFieldExpression(this.sHead, execution);
		String sBodyValue = getStringFromFieldExpression(this.sBody, execution);
		String nID_PatternValue = getStringFromFieldExpression(this.nID_Pattern, execution);
		
		LOG.info("Parameters of the task sLogin:{}, sHead:{}, sBody:{}, nId_PatternValue:{}", sLoginAuthorValue, sHeadValue,
				sBodyValue, nID_PatternValue);
		
		String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(), 
				generalConfig.sURL_GenerationSID() + "?lang=UA");
		
		LOG.info("Retrieved session ID:" + sessionId);
		Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue);

		LOG.info("Created urk doc request object:" + urkDocRequest.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.set("Authorization", "Bearer " + sessionId);
        
        byte[] resp = new RestRequest().post(generalConfig.getsUkrDocServerAddress(), urkDocRequest.toString(), 
        		MediaType.APPLICATION_JSON, StandardCharsets.UTF_8, byte[].class, headers);
        
        String response = String.valueOf(resp);
        LOG.info("Ukrdoc response:" + response);
        
        runtimeService.setVariable(execution.getProcessInstanceId(), UKRDOC_ID_DOCUMENT_VARIABLE_NAME, response + ":" + Calendar.getInstance().get(Calendar.YEAR));
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
