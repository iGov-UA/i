package org.igov.service.business.action.task.systemtask;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.controller.interceptor.ActionProcessCountUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author askosyr
 *
 */
@Component("ProcessCountTaskListener")
public class ProcessCountTaskListener implements JavaDelegate, TaskListener {

	public static final String S_ID_ORDER_GOV_PUBLIC = "sID_Order_GovPublic";

	private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);
	
	@Autowired
    HttpRequester httpRequester;
	
	@Autowired
    GeneralConfig generalConfig;
	
	@Autowired
    RuntimeService runtimeService;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	FormService formService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		loadProcessCount(execution);
	}

	private void loadProcessCount(DelegateExecution execution) {
		String processCount = getActionProcessCount(execution.getProcessDefinitionId(), null);
		
		if (processCount != null) {
            runtimeService.setVariable(execution.getProcessInstanceId(), S_ID_ORDER_GOV_PUBLIC, processCount);
            LOG.info("Set variable to runtime process:{}", processCount);
        }
	}

	private String getActionProcessCount(String sID_BP, Long nID_Service) {
		int res = ActionProcessCountUtils.callGetActionProcessCount(httpRequester, generalConfig, StringUtils.substringBefore(sID_BP, ":"), nID_Service, null);
		String resStr = String.format("%07d", res);
		LOG.info("Retrieved {} as a result", resStr);
		return resStr;
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		loadProcessCount(delegateTask.getExecution());
	}
}
