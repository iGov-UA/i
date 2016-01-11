package org.igov.service.conf;

import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSpringProcessEngineConfiguration extends org.activiti.spring.SpringProcessEngineConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(MongoSpringProcessEngineConfiguration.class);
	
	protected IBytesDataStorage bytesDataStorage;
	
	public MongoSpringProcessEngineConfiguration() {
		super();
	}

	public void setBytesDataStorage(IBytesDataStorage bytesDataStorage) {
		this.bytesDataStorage = bytesDataStorage;
	}

	@Override
	protected void initServices() {
		taskService = new TaskServiceImpl(this, bytesDataStorage);
		
		super.initServices();
		LOG.info("bytesDataStorage:" + bytesDataStorage + " taskService:" + taskService);
	}
	
	
}
