package org.igov.service.business.action.task.listener;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.form.FormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.task.core.AbstractModelTask;

import java.util.List;
import java.util.Set;

/**
 * @author inna
 */
@Component("fileTaskUploadListener")
public class FileTaskUploadListener extends AbstractModelTask implements TaskListener {

    static final transient Logger LOG = LoggerFactory.getLogger(FileTaskUploadListener.class);

    private static final long serialVersionUID = 1L;
    
    //@Autowired
    //private TaskService taskService;

    @Override
    public void notify(DelegateTask oTask) {
    	List<Attachment> attachments = taskService.getProcessInstanceAttachments(oTask.getProcessInstanceId());
		LOG.info("Found attachments for the process {}", attachments != null ? attachments.size() : 0);
		if (attachments != null && attachments.size() > 0){
			LOG.info("Returning from listener as there are {} attachments already assigned to the task", attachments.size());
		}
		
        DelegateExecution oExecution = oTask.getExecution();
        // получить группу бп
        Set<IdentityLink> identityLink = oTask.getCandidates();
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
            addAttachmentsToTask(oStartFormData, oTask);
        }
    }
}
