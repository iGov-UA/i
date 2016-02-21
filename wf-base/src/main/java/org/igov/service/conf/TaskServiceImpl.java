package org.igov.service.conf;

import java.io.InputStream;
import java.util.Map;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
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

	@Override
	public void setAssignee(String taskId, String userId) {
		LOG.info("TaskServiceImpl Updating assignee of the task");
		super.setAssignee(taskId, userId);
	}

	@Override
	public void complete(String taskId) {
		LOG.info("TaskServiceImpl complete of the task");
		super.complete(taskId);
	}

	@Override
	public void resolveTask(String taskId) {
		LOG.info("TaskServiceImpl resolveTask");
		super.resolveTask(taskId);
	}

	@Override
	public Task newTask() {
		LOG.info("TaskServiceImpl newTask1");
		return super.newTask();
	}

	@Override
	public Task newTask(String taskId) {
		LOG.info("TaskServiceImpl newTask2");
		return super.newTask(taskId);
	}

	@Override
	public void complete(String taskId, Map<String, Object> variables) {
		LOG.info("TaskServiceImpl complete2");
		super.complete(taskId, variables);
	}

	@Override
	public void complete(String taskId, Map<String, Object> variables,
			boolean localScope) {
		LOG.info("TaskServiceImpl complete3");
		super.complete(taskId, variables, localScope);
	}

	@Override
	public void resolveTask(String taskId, Map<String, Object> variables) {
		LOG.info("TaskServiceImpl resolveTask3");
		super.resolveTask(taskId, variables);
	}
	
	

}
