package org.igov.service.business.action.task.systemtask.doc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("GetDocument_UkrDoc")
public class GetDocument_UkrDoc implements JavaDelegate, TaskListener {

	private final static Logger LOG = LoggerFactory.getLogger(GetDocument_UkrDoc.class);
	
	private Expression sID_Document;
	
	 @Autowired
	 GeneralConfig generalConfig; 
	 
	 @Autowired
	 RuntimeService runtimeService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String sID_Document = getStringFromFieldExpression(this.sID_Document, execution);
		
		LOG.info("Parameters of the task sID_Document:{}", sID_Document);
		
		String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(), 
				generalConfig.sURL_AuthSID_PB() + "?lang=UA");
		
		String[] documentIDs = sID_Document.split(":");
		if (documentIDs.length > 1){
			String url = String.format("/%s/%s/content", documentIDs[1], documentIDs[0]);
			
			LOG.info("Retrieved session ID:{} and created URL to request: {}", sessionId, url);
	
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
	        
	        String resp = new RestRequest().get(generalConfig.getsUkrDocServerAddress() + url, MediaType.APPLICATION_JSON, StandardCharsets.UTF_8, String.class, headers);
	
	        LOG.info("Ukrdoc response getDocument:" + resp);
	        org.activiti.engine.impl.util.json.JSONObject respJson = new org.activiti.engine.impl.util.json.JSONObject(resp);
	        Object content = respJson.get("content");
	        
	        if (content != null){
	        	String name = (String) ((org.activiti.engine.impl.util.json.JSONObject)content).get("name");
                        runtimeService.setVariable(execution.getProcessInstanceId(), "sHead_Document", name);
	        	String text = (String) ((org.activiti.engine.impl.util.json.JSONObject)content).get("text");
                        runtimeService.setVariable(execution.getProcessInstanceId(), "sBody_Document", text);
                        List<Map<String, Object>> files = (List<Map<String, Object>>) ((Map)((org.activiti.engine.impl.util.json.JSONObject)content).get("extensions")).get("files");
	        	if(files != null && !files.isEmpty()){
                            for(Map<String, Object> file: files){
                                String view_url = (String)file.get("view_url");
                                resp = new RestRequest().get(generalConfig.getsUkrDocServerAddress() + view_url, MediaType.APPLICATION_JSON, StandardCharsets.UTF_8, String.class, headers);
                                LOG.info("Ukrdoc response getContentFile:" + resp);
                                // который прикреплять в качестве атача к юзертаске, используя значения: "file": "a10300000.jpg", "name": "Приложение", 5) При этом засетить в поле с id=anID_Attach_UkrDoc - список ИД-шников аттачей(через запятую)
                            }
                        
                        }
	        	
	            LOG.info("Set variables to runtime process. name:{} text:{}", name, text);
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

    @Override
    public void notify(DelegateTask dt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
}
