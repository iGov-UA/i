package org.igov.service.business.action.task.listener;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.task.core.AbstractModelTask;

import java.util.LinkedList;
import java.util.List;
import java.text.MessageFormat;
import org.igov.io.GeneralConfig;
import org.igov.io.Log;

/**
 * @author askosyr
 */
@Component("fileTaskInheritance")
public class FileTaskInheritance extends AbstractModelTask implements TaskListener {

    private static final long serialVersionUID = 1L;

    private static final transient Logger LOG = LoggerFactory.getLogger(FileTaskInheritance.class);

    private Expression aFieldInheritedAttachmentID;

    @Autowired
    GeneralConfig generalConfig;
    
    @Override
    public void notify(DelegateTask oTask) {

        DelegateExecution oExecution = oTask.getExecution();

        LOG.info("notify");

        List<Attachment> asID_Attachment_ToAdd = null;
        try {

            String sInheritedAttachmentsIds = getStringFromFieldExpression(this.aFieldInheritedAttachmentID, oExecution);
            LOG.info("(task.getId()={},sInheritedAttachmentsIds(1)={})", oTask.getId(), sInheritedAttachmentsIds);

            if (sInheritedAttachmentsIds == null || "".equals(sInheritedAttachmentsIds.trim())) {
                LOG.error("aFieldInheritedAttachmentID field is not specified!");
                return;
            }

            List<Attachment> attachments = getAttachmentsFromParentTasks(oExecution);

            asID_Attachment_ToAdd = getInheritedAttachmentIdsFromTask(attachments,
                    sInheritedAttachmentsIds);

            addAttachmentsToCurrentTask(asID_Attachment_ToAdd, oTask);
        } catch (Exception oException) {
            LOG.error("FAIL: {}", oException.getMessage());
            LOG.trace("FAIL:", oException);
            new Log(oException, LOG)//this.getClass()
                    ._Case("Activiti_AttachRedisKeyFail")
                    ._Status(Log.LogStatus.ERROR)
                    ._Head("Invalid Redis Key of Attachment")
                    ._Body(oException.getMessage())
                    //._Exception(oException)
//                    ._Param("n", n)
//                    ._Param("sID_Field", sID_Field)
                    ._Param("asID_Attachment_ToAdd", asID_Attachment_ToAdd)
//                    ._Param("sDescription", sDescription)
                    ._Param("sID_Order", generalConfig.getOrderId_ByProcess(oExecution.getProcessInstanceId()))
                    //._Param("oExecution.getProcessInstanceId()", oExecution.getProcessInstanceId())
                    ._Param("oExecution.getProcessDefinitionId()", oExecution.getProcessDefinitionId())
                    ._Param("oTask.getId()", oTask.getId())
                    ._Param("oTask.getName()", oTask.getName())
                    .save()
                    ;
        }

    }

    private void addAttachmentsToCurrentTask(List<Attachment> attachmentsToAdd,
            DelegateTask task) {
        final String METHOD_NAME = "addAttachmentsToCurrentTask(List<Attachment> attachmentsToAdd, DelegateExecution execution)";
        LOG.trace("Entering method '{}'", METHOD_NAME);

        TaskService taskService = task.getExecution().getEngineServices()
                .getTaskService();
        int n = 0;
        for (Attachment attachment : attachmentsToAdd) {
            n++;
            LOG.info("(n={},task.getId()={},task.getExecution().getProcessInstanceId()={},attachment.getName()={},attachment.getName()={},attachment.getDescription()={})"
                    , n, task.getId(), task.getExecution().getProcessInstanceId(),attachment.getName(),attachment.getDescription());
            Attachment newAttachment = taskService.createAttachment(
                    attachment.getType(), task.getId(),
                    task.getExecution().getProcessInstanceId(), attachment.getName(),
                    attachment.getDescription(),
                    taskService.getAttachmentContent(attachment.getId()));
            LOG.info(MessageFormat
                    .format("Created new attachment for the task {0} with ID {1} from the attachment with ID {2}",
                            task.getId(), newAttachment.getId(),
                            attachment.getId()));
        }
        LOG.trace("Exiting method '{}'", METHOD_NAME);
    }

    protected List<Attachment> getInheritedAttachmentIdsFromTask(
            List<Attachment> attachments, String sInheritedAttachmentsIds) {
        final String METHOD_NAME = "getInheritedAttachmentIdsFromTask(List<Attachment> attachments, String sInheritedAttachmentsIds)";
        LOG.trace("Entering method '{}'", METHOD_NAME);
        LOG.info("sInheritedAttachmentsIds={}", sInheritedAttachmentsIds);
        List<Attachment> res = new LinkedList<Attachment>();

        String[] attachIds = sInheritedAttachmentsIds.split(",");
        for (String attachId : attachIds) {
            LOG.info("(attachId={})", attachId);
            int n = 0;
            for (Attachment attachment : attachments) {
                n++;
                LOG.info("(n={},attachment.getId()={})", n, attachment.getId());

                if (attachment.getId().equals(attachId)) {
                    res.add(attachment);
                    LOG.info("Found attachment with ID {}. Adding to the current task", attachId);
                    break;
                }
            }
        }
        LOG.trace("Exiting method '{}'", METHOD_NAME);
        return res;
    }

    protected List<Attachment> getAttachmentsFromParentTasks(DelegateExecution execution) {
        final String METHOD_NAME = "getAttachmentsFromParentTasks(DelegateExecution execution)";
        LOG.trace("Entering method '{}'", METHOD_NAME);

        LOG.info("(execution.getProcessInstanceId()={})", execution.getProcessInstanceId());
        List<Attachment> res = execution.getEngineServices().getTaskService()
                .getProcessInstanceAttachments(execution.getProcessInstanceId());
        LOG.info("(res={})", res);

        LOG.trace("Exiting method '{}'", METHOD_NAME);
        return res;
    }

}