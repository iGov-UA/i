/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.systemtask;

/**
 *
 * @author olya
 */
import java.security.AccessControlException;
import java.util.List;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteProccess")
public class DeleteProccess implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(DeleteProccess.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    GeneralConfig generalConfig;

    public Expression processDefinitionKey;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processDefinitionKeyValue = getStringFromFieldExpression(this.processDefinitionKey, execution);
        //if (generalConfig.isSelfTest()) {
        //List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (processDefinitionKeyValue == null || "".equals(processDefinitionKeyValue.trim())
                || (!generalConfig.isSelfTest() && "all".equals(processDefinitionKeyValue.trim()))) {
            LOG.info("You don't have access for this operation! processDefinitionKeyValue: " + processDefinitionKeyValue);
            throw new AccessControlException("У Вас нет прав на данную операцию!");
        } else if (!"all".equals(processDefinitionKeyValue.trim())) {
            LOG.info("Delete all active proccess with processDefinitionKeyValue: " + processDefinitionKeyValue);
            processInstanceQuery.processDefinitionKey(processDefinitionKeyValue);
        }
        List<ProcessInstance> processInstances;
        do {
            LOG.info("processInstances processInstanceQuery...");
            processInstances = processInstanceQuery.listPage(0, 1000);
            LOG.info("processInstances processInstanceQuery size: " + processInstances.size());
            for (ProcessInstance processInstance : processInstances) {
                runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "deprecated");
            }
        } while (!processInstances.isEmpty());

        //}
    }

    protected String getStringFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}
