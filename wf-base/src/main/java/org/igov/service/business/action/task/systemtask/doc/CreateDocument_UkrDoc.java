package org.igov.service.business.action.task.systemtask.doc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
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
	 
	 @Autowired
	 FormService formService;
	 
	 @Autowired
	 TaskService taskService;
	
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
		List<Attachment> attachments = taskService.getProcessInstanceAttachments(execution.getProcessInstanceId());
		LOG.info("Found attachments for the process {}", attachments != null ? attachments.size() : 0);
		Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue, 
				attachments, execution.getId(), generalConfig);

		JSONObject json = new JSONObject();
		json.putAll( urkDocRequest );
		
		LOG.info("Created ukr doc request object:" + json.toJSONString());

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
        	runtimeService.setVariable(execution.getProcessInstanceId(), "sID_Document_UkrDoc", documentId);
            LOG.info("Set variable to runtime process:{}", documentId);
            
            LOG.info("Looking for a new task to set form properties");
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
			LOG.info("Get {} active tasks for the process", tasks);
			for (Task task : tasks){
				TaskFormData formData = formService.getTaskFormData(task.getId());
				for (FormProperty formProperty : formData.getFormProperties()){
					if (formProperty.getId().equals("sID_Document_UkrDoc")){
						LOG.info("Found form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
						if (formProperty instanceof FormPropertyImpl){
							((FormPropertyImpl)formProperty).setValue(documentId);
						}
					}
				}
				StartFormData startFormData = formService.getStartFormData(execution.getId());
				for (FormProperty formProperty : startFormData.getFormProperties()){
					if (formProperty.getId().equals("sID_Document_UkrDoc")){
						LOG.info("Found start form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
						if (formProperty instanceof FormPropertyImpl){
							((FormPropertyImpl)formProperty).setValue(documentId);
						}
					}
				}
			}
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
