package org.igov.activiti.listener;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.activiti.common.AbstractModelTask;

import java.util.LinkedList;
import java.util.List;
import java.text.MessageFormat;

/**
 * @author askosyr
 */
@Component("fileTaskInheritance")
public class FileTaskInheritance extends AbstractModelTask implements TaskListener {

    private static final long serialVersionUID = 1L;

    private static final transient Logger oLog = LoggerFactory.getLogger(FileTaskInheritance.class);

    private Expression aFieldInheritedAttachmentID;

    @Override
    public void notify(DelegateTask task) {

        DelegateExecution execution = task.getExecution();

        oLog.info("[notify]Util.replacePatterns:Ok(skiped)");

        try {

            String sInheritedAttachmentsIds = getStringFromFieldExpression(this.aFieldInheritedAttachmentID, execution);
            oLog.info("sInheritedAttachmentsIds(0)=" + sInheritedAttachmentsIds);

            if (sInheritedAttachmentsIds == null || "".equals(sInheritedAttachmentsIds.trim())) {
                oLog.error("aFieldInheritedAttachmentID field is not specified");
                return;
            }

            oLog.info("sInheritedAttachmentsIds(1)=" + sInheritedAttachmentsIds);
            oLog.info("task.getId()" + task.getId());

            List<Attachment> attachments = getAttachmentsFromParentTasks(execution);

            List<Attachment> attachmentsToAdd = getInheritedAttachmentIdsFromTask(attachments,
                    sInheritedAttachmentsIds);

            addAttachmentsToCurrentTask(attachmentsToAdd, task);
        } catch (Exception oException) {
            oLog.error("[notify]", oException);
        }

    }

    private void addAttachmentsToCurrentTask(List<Attachment> attachmentsToAdd,
            DelegateTask task) {
        final String METHOD_NAME = "addAttachmentsToCurrentTask(List<Attachment> attachmentsToAdd, DelegateExecution execution)";
        oLog.trace("Entering method " + METHOD_NAME);

        TaskService taskService = task.getExecution().getEngineServices()
                .getTaskService();
        int n = 0;
        for (Attachment attachment : attachmentsToAdd) {
            n++;
            oLog.info("[addAttachmentsToCurrentTask](n=" + n + "):task.getId()" + task.getId());
            oLog.info("[addAttachmentsToCurrentTask](n=" + n + "):task.getExecution().getProcessInstanceId()" + task
                    .getExecution().getProcessInstanceId());
            oLog.info("[addAttachmentsToCurrentTask](n=" + n + "):attachment.getName()" + attachment.getName());
            oLog.info("[addAttachmentsToCurrentTask](n=" + n + "):attachment.getDescription()" + attachment
                    .getDescription());
            Attachment newAttachment = taskService.createAttachment(
                    attachment.getType(), task.getId(),
                    task.getExecution().getProcessInstanceId(), attachment.getName(),
                    attachment.getDescription(),
                    taskService.getAttachmentContent(attachment.getId()));
            oLog.info(MessageFormat
                    .format("Created new attachment for the task {0} with ID {1} from the attachment with ID {2}",
                            task.getId(), newAttachment.getId(),
                            attachment.getId()));
        }
        oLog.trace("Exiting method " + METHOD_NAME);
    }

    protected List<Attachment> getInheritedAttachmentIdsFromTask(
            List<Attachment> attachments, String sInheritedAttachmentsIds) {
        final String METHOD_NAME = "getInheritedAttachmentIdsFromTask(List<Attachment> attachments, String sInheritedAttachmentsIds)";
        oLog.trace("Entering method " + METHOD_NAME);
        oLog.info("sInheritedAttachmentsIds:" + sInheritedAttachmentsIds);
        List<Attachment> res = new LinkedList<Attachment>();

        String[] attachIds = sInheritedAttachmentsIds.split(",");
        for (String attachId : attachIds) {
            oLog.info("[getInheritedAttachmentIdsFromTask]:attachId" + attachId);
            int n = 0;
            for (Attachment attachment : attachments) {
                n++;
                oLog.info("[getInheritedAttachmentIdsFromTask](n=" + n + "):attachment.getId()" + attachment.getId());

                if (attachment.getId().equals(attachId)) {
                    res.add(attachment);
                    oLog.info(MessageFormat.format("Found attachment with ID {0}. Adding to the current task", attachId));
                    break;
                }
            }
        }
        oLog.trace("Exiting method " + METHOD_NAME);
        return res;
    }

    protected List<Attachment> getAttachmentsFromParentTasks(DelegateExecution execution) {
        final String METHOD_NAME = "getAttachmentsFromParentTasks(DelegateExecution execution)";
        oLog.trace("Entering method " + METHOD_NAME);

        oLog.info("execution.getProcessInstanceId():" + execution.getProcessInstanceId());
        List<Attachment> res = execution.getEngineServices().getTaskService()
                .getProcessInstanceAttachments(execution.getProcessInstanceId());
        oLog.info("res:" + res);

        oLog.trace("Exiting method " + METHOD_NAME);
        return res;
    }

}