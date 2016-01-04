package org.igov.service.adapter;

import org.activiti.engine.task.Attachment;
import org.igov.service.entity.AttachmentEntity;
import org.igov.service.entity.AttachmentEntityI;

/**
 * Created by Dmytro Tsapko on 6/5/2015.
 */

public class AttachmentEntityAdapter implements AdapterI<Attachment, AttachmentEntityI> {
    @Override
    public AttachmentEntityI apply(Attachment attachment) {
        AttachmentEntity attachmentEntity = new AttachmentEntity();

        attachmentEntity.setAttachmentId(attachment.getId());
        attachmentEntity.setAttachmentName(attachment.getName());
        attachmentEntity.setAttachmentDescription(attachment.getDescription());
        attachmentEntity.setContentType(attachment.getType());
        attachmentEntity.setTaskId(attachment.getTaskId());
        attachmentEntity.setProcessInstanceId(attachment.getProcessInstanceId());
        attachmentEntity.setContentUrl(attachment.getUrl());
        attachmentEntity.setUserId(attachment.getUserId());
        attachmentEntity.setCreateDate(attachment.getTime());

        return attachmentEntity;
    }
}
