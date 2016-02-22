package org.igov.service.business.action.task.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.util.cache.CachedInvocationBean;
import org.igov.util.cache.MethodCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivitiTaskEventListener implements ActivitiEventListener {

	private MethodCacheInterceptor methodCacheInterceptor;
	
	private static final transient Logger LOG = LoggerFactory.getLogger(ActivitiTaskEventListener.class);
	
	@Override
	public void onEvent(ActivitiEvent event) {
		LOG.info("event {} ", event.getType());
		if (methodCacheInterceptor != null) {
            methodCacheInterceptor
                    .clearCacheForMethod(CachedInvocationBean.class, "invokeUsingCache", ActionTaskService.GET_ALL_TASK_FOR_USER_CACHE);
            LOG.info("Cleared cache for " + ActionTaskService.GET_ALL_TASK_FOR_USER_CACHE + " method");
            methodCacheInterceptor
            		.clearCacheForMethod(CachedInvocationBean.class, "invokeUsingCache", ActionTaskService.GET_ALL_TICKETS_CACHE);
            LOG.info("Cleared cache for " + ActionTaskService.GET_ALL_TICKETS_CACHE + " method");
        }
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

	public MethodCacheInterceptor getMethodCacheInterceptor() {
		return methodCacheInterceptor;
	}

	public void setMethodCacheInterceptor(
			MethodCacheInterceptor methodCacheInterceptor) {
		this.methodCacheInterceptor = methodCacheInterceptor;
	}

}
