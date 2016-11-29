package org.igov.service.business.action.task.systemtask;

import java.util.List;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.task.Task;
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
public class ProcessCountTask implements JavaDelegate, TaskListener {

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

            LOG.info("Looking for a new task to set form properties");
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
            LOG.info("Get {} active tasks for the process", tasks);
            for (Task task : tasks) {
                TaskFormData formData = formService.getTaskFormData(task.getId());
                for (FormProperty formProperty : formData.getFormProperties()) {
                    if (formProperty.getId().equals(S_ID_ORDER_GOV_PUBLIC)) {
                        LOG.info("Found form property with the id " + S_ID_ORDER_GOV_PUBLIC + ". Setting value {}", processCount);
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(processCount);
                        }
                    }
                }
                StartFormData startFormData = formService.getStartFormData(execution.getProcessDefinitionId());
                for (FormProperty formProperty : startFormData.getFormProperties()) {
                    if (formProperty.getId().equals(S_ID_ORDER_GOV_PUBLIC)) {
                        LOG.info("Found start form property with the id " + S_ID_ORDER_GOV_PUBLIC + ". Setting value {}", processCount);
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(processCount);
                        }
                    }
                }
            }
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
