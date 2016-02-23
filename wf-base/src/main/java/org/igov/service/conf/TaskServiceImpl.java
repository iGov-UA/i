package org.igov.service.conf;

import java.io.InputStream;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.task.Attachment;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServiceImpl extends org.activiti.engine.impl.TaskServiceImpl {

    protected IBytesDataStorage durableBytesDataStorage;
    private final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	public TaskServiceImpl() {
		super();
	}

	public TaskServiceImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
		super(processEngineConfiguration);
	}

	public void setDurableBytesDataStorage(IBytesDataStorage durableBytesDataStorage) {
		this.durableBytesDataStorage = durableBytesDataStorage;
	}

	@Override
	public Attachment createAttachment(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, InputStream content) {
		LOG.info("Calling org.igov.service.conf.TaskServiceImpl.createAttachment(String, String, String, String, String, InputStream). (durableBytesDataStorage={})", durableBytesDataStorage);
		return commandExecutor.execute(new MongoCreateAttachmentCmd(attachmentType, taskId, processInstanceId, attachmentName, attachmentDescription, content, null, durableBytesDataStorage));
	}

	@Override
	public Attachment createAttachment(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, String url) {
		LOG.info("Calling org.igov.service.conf.TaskServiceImpl.createAttachment(String, String, String, String, String, InputStream). (durableBytesDataStorage={})", durableBytesDataStorage);
		return commandExecutor.execute(new MongoCreateAttachmentCmd(attachmentType, taskId, processInstanceId, attachmentName, attachmentDescription, null, url, durableBytesDataStorage));
	}

	@Override
	public InputStream getAttachmentContent(String attachmentId) {
		LOG.info("Calling org.igov.service.conf.TaskServiceImpl.getAttachmentContent(String)");
		return commandExecutor.execute(new MongoGetAttachmentContentCmd(attachmentId, durableBytesDataStorage));
	}

	@Override
	public void deleteAttachment(String attachmentId) {
		LOG.info("Calling org.igov.service.conf.TaskServiceImpl.deleteAttachment(String)");
		commandExecutor.execute(new MongoDeleteAttachmentCmd(attachmentId, durableBytesDataStorage));
	}

}
