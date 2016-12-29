package org.igov.service.conf;

import java.io.InputStream;
import org.activiti.engine.TaskService;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.cmd.CreateAttachmentCmd;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.task.Attachment;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MongoCreateAttachment {

	public static final String MONGO_KEY_PREFIX = "Mongo-";
	
        @Autowired
        protected TaskService taskService;

        
	private final Logger LOG = LoggerFactory.getLogger(MongoCreateAttachmentCmd.class);
	protected IBytesDataStorage bytesDataStorage;
	
	public String createAttachment (String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, InputStream content) throws Exception {
            
            String attachmentKey = "";
            if (content != null) {
	      byte[] bytes = IoUtil.readInputStream(content, attachmentName);
	     attachmentKey = bytesDataStorage.saveData(bytes);
             
             if (processInstanceId != null){
              //oRuntimeService.setVariable(processInstanceId, sID_Field, attachment.getId());
             }else{
                 
                 Attachment oAttach = taskService.getAttachment(taskId);
                 processInstanceId = oAttach.getProcessInstanceId();
                 
                 if (processInstanceId == null){
                     throw new RuntimeException("ProcessInstanceId is null");
                 }
             }
	    }
	    return attachmentKey;
	  }
	
}
