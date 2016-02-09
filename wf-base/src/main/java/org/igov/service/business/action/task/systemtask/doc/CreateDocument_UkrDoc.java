package org.igov.service.business.action.task.systemtask.doc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("CreateDocument_UkrDoc")
public class CreateDocument_UkrDoc implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);
	
	private Expression sLoginAuthor;
	private Expression sHead;
	private Expression sBody;
	private Expression nID_Pattern;
	
	 @Autowired
	 GeneralConfig generalConfig; 
	
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
		Map<String, Object> urkDocRequest = makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue);

		LOG.info("Created urk doc request object:" + urkDocRequest.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.set("Authorization", "Bearer " + sessionId);
        
        byte[] resp = new RestRequest().post(generalConfig.getsUkrDocServerAddress(), urkDocRequest.toString(), 
        		MediaType.ALL, StandardCharsets.UTF_8, byte[].class, headers);
        
        String response = String.valueOf(resp);
        LOG.info("Response:" + response);
	}

	private Map<String, Object> makeJsonRequestObject(String sHeadValue, String sBodyValue, String sLoginAuthorValue, 
			String nID_PatternValue) {
		Map<String, Object> res = new HashMap<String, Object>();
		
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("name", sHeadValue);
		content.put("text", sHeadValue);
		content.put("paragraphs", new JSONArray());
		content.put("extensions", new LinkedList<Object>());
		
		res.put("content", content);
		
		Map<String, Object> actors = new HashMap<String, Object>();
		actors.put("paragraphs", new LinkedList<Object>());
		actors.put("ratifiers", new JSONArray());
		actors.put("reconcilers", new JSONArray());
		actors.put("addresses", new LinkedList<Object>());
		actors.put("readers", new JSONArray());
		
		Map<String, String> author = new HashMap<String, String>();
		author.put("id", sLoginAuthorValue);
		
		actors.put("author", author);
		
		Map<String, String> template = new HashMap<String, String>();
		author.put("template", nID_PatternValue);
		
		actors.put("details", template);
		
		return res;
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
