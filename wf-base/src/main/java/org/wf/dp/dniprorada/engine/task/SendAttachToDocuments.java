package org.wf.dp.dniprorada.engine.task;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wf.dp.dniprorada.rest.HttpRequester;
import org.wf.dp.dniprorada.util.DocumentTypeUtil;
import org.wf.dp.dniprorada.util.GeneralConfig;

/**
 * @author a.skosyr
 */
@Component("SendAttachToDocuments")
public class SendAttachToDocuments implements JavaDelegate {

    private final static Logger log = LoggerFactory.getLogger(SendAttachToDocuments.class);

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    public Expression nID_Attach;
    public Expression sName;
    public Expression nID_DocumentType;
    
    @Autowired
    private GeneralConfig generalConfig;
    
    @Autowired
    private HttpRequester httpRequester;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
    	log.info(String.format("Processing SendAttachToDocuments for the process ID %s", oExecution.getProcessInstanceId()));
    	
    	Object oIDSubject = runtimeService.getVariable(oExecution.getProcessInstanceId(), "nID_Subject");
    	String nID_Attach = getStringFromFieldExpression(this.nID_Attach, oExecution);
        String sName = getStringFromFieldExpression(this.sName, oExecution);
        String nID_DocumentType = getStringFromFieldExpression(this.nID_DocumentType, oExecution);
    	
		log.info(String.format("Retrieved next values from the parameters of system task %s %s %s %s",
						oIDSubject, nID_Attach, sName, nID_DocumentType));
        
		if (nID_Attach != null){
			Attachment oAttachment = taskService.getAttachment(nID_Attach);
			String sDocumentContentType = oAttachment.getType();
			DocumentTypeUtil.init(generalConfig, httpRequester);
			String nIdDocumentContentType = DocumentTypeUtil.getDocumentTypeIdByName(sDocumentContentType);
			String sSubjectName_Upload = "";
			
			String processInstanceId = oExecution.getProcessInstanceId();
			
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
			if (tasks != null && !tasks.isEmpty()){
				log.info(String.format("Found %s active tasks for the process instance %s", tasks.size(), processInstanceId));
				sSubjectName_Upload = tasks.get(0).getAssignee();
			} else {
				log.info(String.format("There are no active tasks for the process instance %s", processInstanceId));				
			}
			
			sendDocument(oAttachment, nIdDocumentContentType, sSubjectName_Upload, oIDSubject, nID_DocumentType);
		} else {
	    	log.warn("nID_Attach is empty. Breaking execution of the task");
		}
    }

    private void sendDocument(Attachment oAttachment,
			String nIdDocumentContentType, String sSubjectName_Upload, Object oIDSubject, String nID_DocumentType) {
		String sFileExtension = StringUtils.substringAfterLast(oAttachment.getName(), ".");
		String sName = StringUtils.substringBeforeLast(oAttachment.getName(), ".");

		String URI = "/wf/service/services/setDocument";
		
		Map<String, String> params = new HashMap<String, String>(); 
		if (oIDSubject != null){
			params.put("nID_Subject", (String) oIDSubject);
		}
		params.put("sID_Subject_Upload", "1");
		params.put("nID_Subject", nIdDocumentContentType);
		params.put("sSubjectName_Upload", sSubjectName_Upload);
		params.put("sName", sName);
		params.put("sFileExtension", sFileExtension);
		params.put("nID_DocumentType", nID_DocumentType);
		params.put("nID_DocumentContentType", nIdDocumentContentType);
		InputStream oInputStream = taskService.getAttachmentContent(oAttachment.getId());
		params.put("oFile", oDataSource.get);
		 
		String res = httpRequester.post(generalConfig.sHostCentral() + URI, params);
		log.info("Response from setDocument method:" + res);
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
