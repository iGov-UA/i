package org.igov.service.business.action.task.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.igov.util.cache.CachedInvocationBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiTaskEventListener implements ActivitiEventListener {

	private CachedInvocationBean cachedInvocationBean;
	
	private static final transient Logger LOG = LoggerFactory.getLogger(ActivitiTaskEventListener.class);
	
	@Override
	public void onEvent(ActivitiEvent event) {
		LOG.info("cachedInvocationBean {}", cachedInvocationBean);
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

	public CachedInvocationBean getCachedInvocationBean() {
		return cachedInvocationBean;
	}

	public void setCachedInvocationBean(CachedInvocationBean cachedInvocationBean) {
		this.cachedInvocationBean = cachedInvocationBean;
	}

}
