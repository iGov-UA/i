package org.igov.service.conf;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.cmd.DeleteAttachmentCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDeleteAttachmentCmd extends DeleteAttachmentCmd {

    private static final long serialVersionUID = 1L;
    protected transient IBytesDataStorage durableBytesDataStorage;
    private final transient Logger LOG = LoggerFactory.getLogger(MongoDeleteAttachmentCmd.class);

    public MongoDeleteAttachmentCmd(String attachmentId, IBytesDataStorage durableBytesDataStorage) {
        super(attachmentId);
        this.durableBytesDataStorage = durableBytesDataStorage;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        AttachmentEntity attachment = commandContext.getDbSqlSession().selectById(AttachmentEntity.class, attachmentId);

        commandContext.getDbSqlSession().delete(attachment);

        if (attachment.getContentId() != null) {
            String contentId = attachment.getContentId();
            if (contentId.startsWith(MongoCreateAttachmentCmd.MONGO_KEY_PREFIX)) {
                String realKey = StringUtils.substringAfter(contentId, MongoCreateAttachmentCmd.MONGO_KEY_PREFIX);
                boolean removed = durableBytesDataStorage.remove(realKey);
                LOG.info("Removing attachment with ID={} from Mongo DB. Status:{}", realKey, removed);
            } else {
                commandContext.getByteArrayEntityManager().deleteByteArrayById(
                        attachment.getContentId());
            }
        }

        if (attachment.getTaskId() != null) {
            commandContext.getHistoryManager().createAttachmentComment(
                    attachment.getTaskId(), attachment.getProcessInstanceId(),
                    attachment.getName(), false);
        }

        if (commandContext.getProcessEngineConfiguration().getEventDispatcher()
                .isEnabled()) {
            // Forced to fetch the process-instance to associate the right
            // process definition
            String processDefinitionId = null;
            String processInstanceId = attachment.getProcessInstanceId();
            if (attachment.getProcessInstanceId() != null) {
                ExecutionEntity process = commandContext
                        .getExecutionEntityManager().findExecutionById(
                                processInstanceId);
                if (process != null) {
                    processDefinitionId = process.getProcessDefinitionId();
                }
            }
            commandContext
                    .getProcessEngineConfiguration()
                    .getEventDispatcher()
                    .dispatchEvent(
                            ActivitiEventBuilder.createEntityEvent(
                                    ActivitiEventType.ENTITY_DELETED,
                                    attachment, processInstanceId,
                                    processInstanceId, processDefinitionId));
        }
        return null;
    }

}
