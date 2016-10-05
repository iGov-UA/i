package org.igov.service.business.action.task.systemtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.document.DocumentContentTypeUtil;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityInsedeCover;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 * @author a.skosyr
 */
@Component("SendAttachToDocuments")
public class SendAttachToDocuments implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(SendAttachToDocuments.class);
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

    @Autowired
    private HttpEntityInsedeCover oHttpEntityInsedeCover;
    
    
    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
    	LOG.info(String.format("Processing SendAttachToDocuments for the process ID %s", oExecution.getProcessInstanceId()));
    	
    	Object oIDSubject = runtimeService.getVariable(oExecution.getProcessInstanceId(), "nID_Subject");
    	String nID_Attach = getStringFromFieldExpression(this.nID_Attach, oExecution);
        String sName = getStringFromFieldExpression(this.sName, oExecution);
        String nID_DocumentType = getStringFromFieldExpression(this.nID_DocumentType, oExecution);
    	
		LOG.info(String.format("Retrieved next values from the parameters of system task %s %s %s %s",
						oIDSubject, nID_Attach, sName, nID_DocumentType));
        
		if (nID_Attach != null){
			String sID_AttachmentTrimmed = nID_Attach.replaceAll("^\"|\"$", "");
            LOG.info("(sID_AttachmentTrimmed={})", sID_AttachmentTrimmed);
			Attachment oAttachment = taskService.getAttachment(sID_AttachmentTrimmed);
			if (oAttachment == null){ 
				List<Attachment> attachmentLists = oExecution.getEngineServices().getTaskService()
		                .getProcessInstanceAttachments(oExecution.getProcessInstanceId());
				if (attachmentLists != null){
					LOG.info("Received {} attachment for the process instance", attachmentLists.size());
					for (Attachment attachment : attachmentLists){
						if (attachment.getId().equals(nID_Attach)){
							oAttachment = attachment;
						}
					}
				}
			}
			if (oAttachment == null){
				LOG.info("There are no attachments to send. Exiting from service task");
				return;
			}
			taskService.getAttachmentContent(oAttachment.getId());
			String sDocumentContentType = oAttachment.getType();
			DocumentContentTypeUtil.init(generalConfig, httpRequester);
			String nIdDocumentContentType = DocumentContentTypeUtil.getDocumentContentTypeIdByName(sDocumentContentType);
			String sSubjectName_Upload = "";
			
			String processInstanceId = oExecution.getProcessInstanceId();
			
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
			if (tasks != null && !tasks.isEmpty()){
				sSubjectName_Upload = tasks.get(0).getAssignee();
				LOG.info(String.format("Found %s active tasks for the process instance %s with assignee %s", tasks.size(), processInstanceId, sSubjectName_Upload));
			} else {
				LOG.info(String.format("There are no active tasks for the process instance %s", processInstanceId));				
			}
			
			sendDocument(oAttachment, nIdDocumentContentType, sSubjectName_Upload, oIDSubject, nID_DocumentType);
		} else {
	    	LOG.warn("nID_Attach is empty. Breaking execution of the task");
		}
    }

    private void sendDocument(final Attachment oAttachment,
			String nIdDocumentContentType, String sSubjectName_Upload, Object oIDSubject, String nID_DocumentType) {
		String sFileExtension = StringUtils.substringAfterLast(oAttachment.getName(), ".");
		String sName = StringUtils.substringBeforeLast(oAttachment.getName(), ".");

		InputStream oInputStream = taskService.getAttachmentContent(oAttachment.getId());
		
		MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<String, Object>();
		if (oIDSubject != null){
			mParam.add("nID_Subject", Long.valueOf(oIDSubject.toString()));
		}
		mParam.add("sID_Subject_Upload", "1");
		mParam.add("sSubjectName_Upload", sSubjectName_Upload);
		mParam.add("sName", sName);
		mParam.add("sFileExtension", sFileExtension);
		mParam.add("nID_DocumentType", nID_DocumentType);
		mParam.add("nID_DocumentContentType", nIdDocumentContentType);
		try {
			byte[] inputStreamBytes = IOUtils.toByteArray(oInputStream);
			if (inputStreamBytes != null){
				LOG.info("Loaded {} bytes as attachment", inputStreamBytes.length );
				mParam.add("oFile", new ByteArrayResource(inputStreamBytes){
	
					@Override
					public String getFilename() {
						return oAttachment.getName();
					}
					
				});
			} else {
				LOG.info("attachment byte array is null");
			}
		} catch (IOException oException) {
			LOG.error("Error: {}, occured while adding file as a parameter", oException.getMessage());
			LOG.debug("FAIL:", oException);
		}
		// Post
		
                String sURL = generalConfig.getSelfHostCentral() + "/wf/service/document/setDocumentFile";
                Long nReturn = oHttpEntityInsedeCover.nReturn_RequestPost_ByMap(sURL, mParam);
		LOG.info("nReturn={}", nReturn);
                
                /*
		String sUser = generalConfig.getAuthLogin();
        String sPassword = generalConfig.getAuthPassword();
        String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + sAuth);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(parts, headers);
		
		RestTemplate template = new RestTemplate();
		LOG.info("Calling URL with parametes {}|{}", generalConfig.getSelfHostCentral() + URI, parts);
		Long result = template.postForObject(generalConfig.getSelfHostCentral() + URI, httpEntity, Long.class);
		*/
		LOG.info("Received response from setDocumentFile: {}", nReturn);
	}
    
}
