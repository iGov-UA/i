package org.igov.service.business.action.task.systemtask.doc;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
				generalConfig.sURL_AuthSID_PB() + "?lang=UA");
		
		LOG.info("Retrieved session ID:" + sessionId);
		Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue);

		JSONObject json = new JSONObject();
		json.putAll( urkDocRequest );
		
		LOG.info("Created urk doc request object:" + json.toJSONString());

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", "Bearer " + sessionId);
        headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
        headers.set("Content-Type", "application/json; charset=utf-8");
        
        String resp = new RestRequest().post(generalConfig.getsUkrDocServerAddress(), json.toJSONString(), 
        		null, StandardCharsets.UTF_8, String.class, headers);

        LOG.info("Ukrdoc response:" + resp);
        org.activiti.engine.impl.util.json.JSONObject respJson = new org.activiti.engine.impl.util.json.JSONObject(resp);
        Object details = respJson.get("details");
        
        if (details != null){
        	String documentId = ((org.activiti.engine.impl.util.json.JSONObject)details).get("id") + ":" + 
					((org.activiti.engine.impl.util.json.JSONObject)details).get("year");
        	runtimeService.setVariable(execution.getProcessInstanceId(), UKRDOC_ID_DOCUMENT_VARIABLE_NAME, documentId);
            LOG.info("Set variable to runtime process:{}", documentId);
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
