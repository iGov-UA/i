package org.igov.service.business.action.task.listener.doc;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.rpc.holders.IntHolder;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.axis.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.igov.util.swind.DocumentInData;
import org.igov.util.swind.DocumentType;
import org.igov.util.swind.SWinEDSoapStub;
import org.igov.util.swind.holders.ProcessResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SendDocument_SWinEd")
public class SendDocument_SWinEd extends AbstractModelTask implements TaskListener {

	private static final long serialVersionUID = 1L;
	private final static String SWIN_ED_ANSWER_STATUS_VARIABLE = "sAnswer_SWinEd_Doc";
	private final static String SWIN_ED_ERROR_VARIABLE = "nAnswerError_SWinEd_Doc";
	
    private final static Logger LOG = LoggerFactory.getLogger(SendDocument_SWinEd.class);

    @Autowired
    RuntimeService runtimeService;
    
    @Autowired
    TaskService taskService;
    
    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    FormService formService;
    
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    
    private Expression sSenderEDRPOU;
    private Expression nSenderDept;
    private Expression sEDRPOU;
    private Expression nDept;
    private Expression sDocId;
    private Expression sDocumentData;
    private Expression sOriginalDocId;
    private Expression nTask;
    
    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();

        String sSenderEDRPOUValue = getStringFromFieldExpression(this.sSenderEDRPOU, execution);
        String nSenderDeptValue = getStringFromFieldExpression(this.nSenderDept, execution);
        String sEDRPOUValue = getStringFromFieldExpression(this.sEDRPOU, execution);
        String nDeptValue = getStringFromFieldExpression(this.nDept, execution);
        String sDocIdValue = getStringFromFieldExpression(this.sDocId, execution);
        String sDocumentDataValue = getStringFromFieldExpression(this.sDocumentData, execution);
        String sOriginalDocIdValue = getStringFromFieldExpression(this.sOriginalDocId, execution);
        String nTaskValue = getStringFromFieldExpression(this.nTask, execution);

        LOG.info("Parameters of the SendDocument_SWinEd sSenderEDRPOU:{}, nSenderDept:{}, sEDRPOU:{}, nDept:{}, sDocId:{},"
        		+ "sDocumentData:{} , sOriginalDocId:{}, nTask:{}", sSenderEDRPOUValue, nSenderDeptValue, sEDRPOUValue, nDeptValue,
        		sDocIdValue, sDocumentDataValue, sOriginalDocIdValue, nTaskValue);

        try {
			SWinEDSoapStub stub = new SWinEDSoapStub();
			ProcessResultHolder handler = new ProcessResultHolder();
			IntHolder errorDocIdx = new IntHolder();
			
			List<Attachment> attachments = new LinkedList<Attachment>();

	        List<Attachment> attach1 = taskService.getProcessInstanceAttachments(delegateTask.getProcessInstanceId());
	        if (attach1 != null && !attach1.isEmpty()) {
	            attachments = attach1;
	        }

	        List<Attachment> attach2 = taskService.getTaskAttachments(delegateTask.getId());
	        if (attach2 != null && !attach2.isEmpty()) {
	            attachments = attach2;
	        }

	        LOG.info("Found attachments for the process {}: {}", attach1 != null ? attach1.size() : 0, attach2 != null ? attach2.size() : 0);

	        String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(),
	                generalConfig.sURL_AuthSID_PB() + "?lang=UA");

	        LOG.info("Retrieved session ID:" + sessionId);

	        if (attachments.isEmpty()) {
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
	        }
			
	        int attachmentsSize = attachments.size();
	        
			DocumentInData[] docs = new DocumentInData[attachmentsSize];
			for (int i = 0; i < attachmentsSize; i++){
				Attachment attachment = attachments.get(i);
				LOG.info("Getting attachment's content with id {}", attachment.getId());
				byte[] attachmentContent = oBytesDataInmemoryStorage.getBytes(attachment.getId());
				DocumentInData document = new DocumentInData();
				document.setDept(Integer.valueOf(nDeptValue));
				document.setDocument(Base64.encodeBase64(attachmentContent));
				document.setDocId(sDocIdValue);
				document.setEDRPOU(sEDRPOUValue);
				document.setOriginalDocId(attachment.getId());
				document.setTask(Integer.valueOf(delegateTask.getProcessInstanceId()));
				docs[i] = document;
			}
			stub.post(sSenderEDRPOUValue, Integer.valueOf(nSenderDeptValue), DocumentType.Original, docs, handler, errorDocIdx);
			
			LOG.info("Setting SwinEd status response variable to {} for the process {}", handler.value.getValue(), delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ANSWER_STATUS_VARIABLE, handler.value.getValue());
			LOG.info("Setting SwinEd error code response variable to {} for the process {}", errorDocIdx.value, delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ERROR_VARIABLE, errorDocIdx.value);
			
			LOG.info("Looking for a new task to set form properties");
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
            LOG.info("Get {} active tasks for the process", tasks);
            for (Task task : tasks) {
                TaskFormData formData = formService.getTaskFormData(task.getId());
                for (FormProperty formProperty : formData.getFormProperties()) {
                    if (formProperty.getId().equals(SWIN_ED_ANSWER_STATUS_VARIABLE)) {
                        LOG.info("Found form property with the id " + SWIN_ED_ANSWER_STATUS_VARIABLE + ". Setting value {}", handler.value.getValue());
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(handler.value.getValue());
                        }
                    }
                    if (formProperty.getId().equals(SWIN_ED_ERROR_VARIABLE)) {
                        LOG.info("Found form property with the id " + SWIN_ED_ERROR_VARIABLE + ". Setting value {}", errorDocIdx.value);
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(String.valueOf(errorDocIdx.value));
                        }
                    }
                }
            }
		} catch (AxisFault e) {
			LOG.error("Error occured while constructing a call to SWinEd {}", e.getMessage());
		} catch (NumberFormatException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage());
		} catch (RemoteException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage());
		} catch (RecordInmemoryException e) {
			LOG.error("Error occured while getting attachment's content {}", e.getMessage());
		}
    }

}
