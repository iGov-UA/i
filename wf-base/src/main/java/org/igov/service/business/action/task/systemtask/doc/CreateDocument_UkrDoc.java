package org.igov.service.business.action.task.systemtask.doc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.form.FormFileType;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component("CreateDocument_UkrDoc")
public class CreateDocument_UkrDoc extends AbstractModelTask implements JavaDelegate, TaskListener {

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
	    @Autowired
	    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
//		String sLoginAuthorValue = getStringFromFieldExpression(this.sLoginAuthor, execution);
//		String sHeadValue = getStringFromFieldExpression(this.sHead, execution);
//		String sBodyValue = getStringFromFieldExpression(this.sBody, execution);
//		String nID_PatternValue = getStringFromFieldExpression(this.nID_Pattern, execution);
//		
//		LOG.info("Parameters of the task sLogin:{}, sHead:{}, sBody:{}, nId_PatternValue:{}", sLoginAuthorValue, sHeadValue,
//				sBodyValue, nID_PatternValue);
//		
//		String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(), 
//				generalConfig.sURL_AuthSID_PB() + "?lang=UA");
//		
//		LOG.info("Retrieved session ID:" + sessionId);
//		
//		FormData oStartFormData = execution.getEngineServices().getFormService()
//                .getStartFormData(execution.getProcessDefinitionId());
//		LOG.info("SCAN:file");
//        List<String> asFieldID = AbstractModelTask.getListFieldCastomTypeFile(oStartFormData);
//        LOG.info("[addAttachmentsToTask]");
//        LOG.info("(asFieldID={})", asFieldID.toString());
//        List<String> asFieldValue = AbstractModelTask.getVariableValues(execution, asFieldID);
//        LOG.info("(asFieldValue={})", asFieldValue.toString());
//        List<String> asFieldName = AbstractModelTask.getListCastomFieldName(oStartFormData);
//        LOG.info("(asFieldName={})", asFieldName.toString());
//        List<List<String>> attachmentsIds = new LinkedList<List<String>>();
//        if (!asFieldValue.isEmpty()) {
//            int n = 0;
//            for (String sKeyRedis : asFieldValue) {
//                LOG.info("(sKeyRedis={})", sKeyRedis);
//                if (sKeyRedis != null && !sKeyRedis.isEmpty() && !"".equals(sKeyRedis.trim()) && !"null"
//                        .equals(sKeyRedis.trim()) && sKeyRedis.length() > 15) {
//                        //String sDescription = asFieldName.get((asFieldName.size() - 1) - n);
//                		String sDescription = asFieldName.get(n);
//                		LOG.info("(sDescription={})", sDescription);
//                		String sID_Field = asFieldID.get(n);
//                		LOG.info("(sID_Field={})", sID_Field);
//
//                        byte[] aByteFile;
//                        ByteArrayMultipartFile oByteArrayMultipartFile = null;
//                        try {
//                            aByteFile = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
//                            oByteArrayMultipartFile = AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory(aByteFile);
//                        } catch (ClassNotFoundException | IOException | RecordInmemoryException e1) {
//                            throw new ActivitiException(e1.getMessage(), e1);
//                        }
//                        if (oByteArrayMultipartFile != null) {
//                            String sFileName = null;
//                            try {
//                                sFileName = new String(oByteArrayMultipartFile.getOriginalFilename().getBytes(),
//                                        "UTF-8");
//                            } catch (java.io.UnsupportedEncodingException oException) {
//                                LOG.error("error on getting sFileName: {}", oException.getMessage());
//                                LOG.debug("FAIL:", oException);
//                                throw new ActivitiException(oException.getMessage(), oException);
//                            }
//                            LOG.info("(sFileName={})", sFileName);
//
//                            //===
//                            InputStream oInputStream = null;
//                            try {
//                                oInputStream = oByteArrayMultipartFile.getInputStream();
//                            } catch (Exception e) {
//                                throw new ActivitiException(e.getMessage(), e);
//                            }
//                            Attachment oAttachment = execution.getEngineServices().getTaskService().createAttachment(
//                                    oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp(),
//                                    execution.getId(), execution.getProcessInstanceId(), sFileName, sDescription,
//                                    oInputStream);
//
//                            if (oAttachment != null) {
//                            	List<String> attachId = new LinkedList<String>();
//                            	attachId.add(oAttachment.getName());
//                            	attachId.add(oAttachment.getId());
//                            	attachId.add(execution.getId());
//                            	LOG.info("Adding attach info to the list {}", attachId);
//                            	attachmentsIds.add(attachId);
//                            	
//                                String nID_Attachment = oAttachment.getId();
//                                //LOG.info("(nID_Attachment={})", nID_Attachment);
//                                LOG.info("Try set variable(sID_Field={}) with the value(nID_Attachment={}), for new attachment...",
//                                        sID_Field, nID_Attachment);
//                                execution.getEngineServices().getRuntimeService()
//                                        .setVariable(execution.getProcessInstanceId(), sID_Field, nID_Attachment);
//                                LOG.info("Finished setting new value for variable with attachment (sID_Field={})",
//                                        sID_Field);
//                            } else {
//                                LOG.error("Can't add attachment to (oTask.getId()={})", execution.getId());
//                            }
//                            //===
//
//                        } else {
//                            LOG.error("oByteArrayMultipartFile==null!  (sKeyRedis={})", sKeyRedis);
//                        }
//                    } else {
//                        LOG.error("asFieldName has nothing! (asFieldName={})", asFieldName);
//                    }
//                n++;
//            }
//        }
//		
//        LOG.info("beginning of addAttachmentsToTask(startformData, task):execution.getProcessDefinitionId()={}",
//        		execution.getProcessDefinitionId());
//		
//		LOG.info("Found attachments for the process {}", attachmentsIds.size());
//		Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue, 
//				new LinkedList<List<String>>(), execution.getId(), generalConfig);
//
//		JSONObject json = new JSONObject();
//		json.putAll( urkDocRequest );
//		
//		LOG.info("Created ukr doc request object:" + json.toJSONString());
//
//        HttpHeaders headers = new HttpHeaders();
//        //headers.set("Authorization", "Bearer " + sessionId);
//        headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
//        headers.set("Content-Type", "application/json; charset=utf-8");
//        
//        String resp = new RestRequest().post(generalConfig.getsUkrDocServerAddress(), json.toJSONString(), 
//        		null, StandardCharsets.UTF_8, String.class, headers);
//
//        LOG.info("Ukrdoc response:" + resp);
//        org.activiti.engine.impl.util.json.JSONObject respJson = new org.activiti.engine.impl.util.json.JSONObject(resp);
//        Object details = respJson.get("details");
//        
//        if (details != null){
//        	String documentId = ((org.activiti.engine.impl.util.json.JSONObject)details).get("id") + ":" + 
//					((org.activiti.engine.impl.util.json.JSONObject)details).get("year");
//        	runtimeService.setVariable(execution.getProcessInstanceId(), UKRDOC_ID_DOCUMENT_VARIABLE_NAME, documentId);
//        	runtimeService.setVariable(execution.getProcessInstanceId(), "sID_Document_UkrDoc", documentId);
//            LOG.info("Set variable to runtime process:{}", documentId);
//            
//            LOG.info("Looking for a new task to set form properties");
//			List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
//			LOG.info("Get {} active tasks for the process", tasks);
//			for (Task task : tasks){
//				TaskFormData formData = formService.getTaskFormData(task.getId());
//				for (FormProperty formProperty : formData.getFormProperties()){
//					if (formProperty.getId().equals("sID_Document_UkrDoc")){
//						LOG.info("Found form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
//						if (formProperty instanceof FormPropertyImpl){
//							((FormPropertyImpl)formProperty).setValue(documentId);
//						}
//					}
//				}
//				StartFormData startFormData = formService.getStartFormData(execution.getId());
//				for (FormProperty formProperty : startFormData.getFormProperties()){
//					if (formProperty.getId().equals("sID_Document_UkrDoc")){
//						LOG.info("Found start form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
//						if (formProperty instanceof FormPropertyImpl){
//							((FormPropertyImpl)formProperty).setValue(documentId);
//						}
//					}
//				}
//			}
//        }
        
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		
		String sLoginAuthorValue = getStringFromFieldExpression(this.sLoginAuthor, execution);
		String sHeadValue = getStringFromFieldExpression(this.sHead, execution);
		String sBodyValue = getStringFromFieldExpression(this.sBody, execution);
		String nID_PatternValue = getStringFromFieldExpression(this.nID_Pattern, execution);
		
		LOG.info("Parameters of the task sLogin:{}, sHead:{}, sBody:{}, nId_PatternValue:{}", sLoginAuthorValue, sHeadValue,
				sBodyValue, nID_PatternValue);
		
		List<Attachment> attach1 = taskService.getProcessInstanceAttachments(delegateTask.getProcessInstanceId());

		List<Attachment> attach2 = taskService.getTaskAttachments(delegateTask.getId());
		
		LOG.info("Found attachments for the process {}: {}", attach1 != null ? attach1.size() : 0, attach2 != null ? attach2.size() : 0);
		
		String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(), 
				generalConfig.sURL_AuthSID_PB() + "?lang=UA");
		
		LOG.info("Retrieved session ID:" + sessionId);
		
		List<Attachment> attachments = new LinkedList<Attachment>();
		
        DelegateExecution oExecution = delegateTask.getExecution();
        // получить группу бп
        Set<IdentityLink> identityLink = delegateTask.getCandidates();
        // получить User группы
        List<User> aUser = oExecution.getEngineServices().getIdentityService()
                .createUserQuery()
                .memberOfGroup(identityLink.iterator().next().getGroupId())
                .list();

        LOG.info("Finding any assigned user-member of group. (aUser={})", aUser);
        if (aUser == null || aUser.size() == 0 || aUser.get(0) == null || aUser.get(0).getId() == null) {
            //TODO  what to do if no user?
        } else {
            // setAuthenticatedUserId первого попавщегося
            //TODO Shall we implement some logic for user selection.
            oExecution.getEngineServices().getIdentityService().setAuthenticatedUserId(aUser.get(0).getId());
            // получить информацию по стартовой форме бп
            FormData oStartFormData = oExecution.getEngineServices().getFormService()
                    .getStartFormData(oExecution.getProcessDefinitionId());
            LOG.info("beginning of addAttachmentsToTask(startformData, task):execution.getProcessDefinitionId()={}",
                    oExecution.getProcessDefinitionId());
            attachments = addAttachmentsToTask(oStartFormData, delegateTask);
        }
		
//		FormData oStartFormData = execution.getEngineServices().getFormService()
//                .getStartFormData(execution.getProcessDefinitionId());
//		LOG.info("SCAN:file");
//        List<String> asFieldID = AbstractModelTask.getListFieldCastomTypeFile(oStartFormData);
//        LOG.info("[addAttachmentsToTask]");
//        LOG.info("(asFieldID={})", asFieldID.toString());
//        List<String> asFieldValue = AbstractModelTask.getVariableValues(execution, asFieldID);
//        LOG.info("(asFieldValue={})", asFieldValue.toString());
//        List<String> asFieldName = AbstractModelTask.getListCastomFieldName(oStartFormData);
//        LOG.info("(asFieldName={})", asFieldName.toString());
//        List<List<String>> attachmentsIds = new LinkedList<List<String>>();
//        if (!asFieldValue.isEmpty()) {
//            int n = 0;
//            for (String sKeyRedis : asFieldValue) {
//                LOG.info("(sKeyRedis={})", sKeyRedis);
//                if (sKeyRedis != null && !sKeyRedis.isEmpty() && !"".equals(sKeyRedis.trim()) && !"null"
//                        .equals(sKeyRedis.trim()) && sKeyRedis.length() > 15) {
//                        //String sDescription = asFieldName.get((asFieldName.size() - 1) - n);
//                		String sDescription = asFieldName.get(n);
//                		LOG.info("(sDescription={})", sDescription);
//                		String sID_Field = asFieldID.get(n);
//                		LOG.info("(sID_Field={})", sID_Field);
//
//                        byte[] aByteFile;
//                        ByteArrayMultipartFile oByteArrayMultipartFile = null;
//                        try {
//                            aByteFile = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
//                            oByteArrayMultipartFile = AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory(aByteFile);
//                        } catch (ClassNotFoundException | IOException | RecordInmemoryException e1) {
//                            throw new ActivitiException(e1.getMessage(), e1);
//                        }
//                        if (oByteArrayMultipartFile != null) {
//                            String sFileName = null;
//                            try {
//                                sFileName = new String(oByteArrayMultipartFile.getOriginalFilename().getBytes(),
//                                        "UTF-8");
//                            } catch (java.io.UnsupportedEncodingException oException) {
//                                LOG.error("error on getting sFileName: {}", oException.getMessage());
//                                LOG.debug("FAIL:", oException);
//                                throw new ActivitiException(oException.getMessage(), oException);
//                            }
//                            LOG.info("(sFileName={})", sFileName);
//
//                            //===
//                            InputStream oInputStream = null;
//                            try {
//                                oInputStream = oByteArrayMultipartFile.getInputStream();
//                            } catch (Exception e) {
//                                throw new ActivitiException(e.getMessage(), e);
//                            }
//                            Attachment oAttachment = execution.getEngineServices().getTaskService().createAttachment(
//                                    oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp(),
//                                    execution.getId(), execution.getProcessInstanceId(), sFileName, sDescription,
//                                    oInputStream);
//
//                            if (oAttachment != null) {
//                            	List<String> attachId = new LinkedList<String>();
//                            	attachId.add(oAttachment.getName());
//                            	attachId.add(oAttachment.getId());
//                            	attachId.add(execution.getId());
//                            	LOG.info("Adding attach info to the list {}", attachId);
//                            	attachmentsIds.add(attachId);
//                            	
//                                String nID_Attachment = oAttachment.getId();
//                                //LOG.info("(nID_Attachment={})", nID_Attachment);
//                                LOG.info("Try set variable(sID_Field={}) with the value(nID_Attachment={}), for new attachment...",
//                                        sID_Field, nID_Attachment);
//                                execution.getEngineServices().getRuntimeService()
//                                        .setVariable(execution.getProcessInstanceId(), sID_Field, nID_Attachment);
//                                LOG.info("Finished setting new value for variable with attachment (sID_Field={})",
//                                        sID_Field);
//                            } else {
//                                LOG.error("Can't add attachment to (oTask.getId()={})", execution.getId());
//                            }
//                            //===
//
//                        } else {
//                            LOG.error("oByteArrayMultipartFile==null!  (sKeyRedis={})", sKeyRedis);
//                        }
//                    } else {
//                        LOG.error("asFieldName has nothing! (asFieldName={})", asFieldName);
//                    }
//                n++;
//            }
//        }
//		
//        LOG.info("beginning of addAttachmentsToTask(startformData, task):execution.getProcessDefinitionId()={}",
//        		execution.getProcessDefinitionId());
//		
//		LOG.info("Found attachments for the process {}", attachmentsIds.size());
		
		List<List<String>> attachmentsIds = new LinkedList<List<String>>();
		if (attachments != null){
			for (Attachment attachment : attachments){
				List<String> attachId = new LinkedList<String>();
		    	attachId.add(attachment.getName());
		    	attachId.add(attachment.getId());
		    	attachId.add(delegateTask.getId());
		    	LOG.info("Adding attach info to the list {}", attachId);
		    	attachmentsIds.add(attachId);
			}
		}
		Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue, 
				attachmentsIds, execution.getId(), generalConfig);

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
	
}
