package org.igov.service.conf;

import java.io.InputStream;

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

public class MongoCreateAttachmentCmd extends CreateAttachmentCmd {

	public static final String MONGO_KEY_PREFIX = "Mongo-";
	
	private final Logger LOG = LoggerFactory.getLogger(MongoCreateAttachmentCmd.class);
	protected IBytesDataStorage bytesDataStorage;
	
	public MongoCreateAttachmentCmd(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, InputStream content, String url, IBytesDataStorage durableBytesDataStorage) {
		super(attachmentType, taskId, processInstanceId, attachmentName,
				attachmentDescription, content, url);
		this.bytesDataStorage = durableBytesDataStorage;
	}

	public Attachment execute(CommandContext commandContext) {

	    verifyParameters(commandContext);
	    
	    AttachmentEntity attachment = new AttachmentEntity();
	    attachment.setName(attachmentName);
	    attachment.setDescription(attachmentDescription);
	    attachment.setType(attachmentType);
	    attachment.setTaskId(taskId);
	    attachment.setProcessInstanceId(processInstanceId);
	    attachment.setUrl(url);
	    attachment.setUserId(Authentication.getAuthenticatedUserId());
	    attachment.setTime(commandContext.getProcessEngineConfiguration().getClock().getCurrentTime());
	    attachment.setUserId("kermit");
	    DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
	    dbSqlSession.insert(attachment);
	    
	    if (content != null) {
	      byte[] bytes = IoUtil.readInputStream(content, attachmentName);
	     // ByteArrayEntity byteArray = ByteArrayEntity.createAndInsert(bytes);
	      String attachmentKey = bytesDataStorage.saveData(bytes);
	      attachment.setContentId(MONGO_KEY_PREFIX + attachmentKey);
	      //BW//TEMP!!! attachment.setContent(byteArray);
	      
	      LOG.info("Saved attachment in Mongo DB with the key: {}{}", MONGO_KEY_PREFIX, attachmentKey);
	    }
	    
	    commandContext.getHistoryManager()
	      .createAttachmentComment(taskId, processInstanceId, attachmentName, true);
	    
	    if(commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
	    	// Forced to fetch the process-instance to associate the right process definition
	    	String processDefinitionId = null;
	    	if(attachment.getProcessInstanceId() != null) {
	    		ExecutionEntity process = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);
	    		if(process != null) {
	    			processDefinitionId = process.getProcessDefinitionId();
	    		}
	    	}
	    	
	    	commandContext.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	    			ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_CREATED, attachment, processInstanceId, processInstanceId, processDefinitionId));
	    	commandContext.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	    			ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_INITIALIZED, attachment, processInstanceId, processInstanceId, processDefinitionId));
	    }
	    
	    return attachment;
	  }

	  private void verifyParameters(CommandContext commandContext) {
	    
	  }
	
}
