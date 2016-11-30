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
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

@Component("deleteProccess")
public class DeleteProccess implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(DeleteProccess.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    GeneralConfig generalConfig;

    public Expression processDefinitionKey;
    
    private int limitCountRowDeleted = 200000;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processDefinitionKeyValue = getStringFromFieldExpression(this.processDefinitionKey, execution);
        closeProcess(processDefinitionKeyValue);
    }
    
    public void closeProcess(String processDefinitionKeyValue){
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
        int countRowDeleted = 0;
        int index = 0;
        int size = 1000;
        do {
            size = (limitCountRowDeleted < size ? limitCountRowDeleted : size);
            LOG.info("processInstances processInstanceQuery...");
            LOG.info("processInstances processInstanceQuery: index={},size={}", index, size);
            processInstances = processInstanceQuery.listPage(index, size);
            LOG.info("processInstances processInstanceQuery: processInstances.size()={}", processInstances.size());
            for (ProcessInstance processInstance : processInstances) {
                runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "deprecated");
                countRowDeleted++;
            }
            LOG.info("processInstances processInstanceQuery size: " + processInstances.size() + " countRowDeleted: " + countRowDeleted + " success!");
            //index = ++index + size;
        } while (!processInstances.isEmpty() && countRowDeleted <= limitCountRowDeleted);
        LOG.info("FINISHED!!! processInstances processInstanceQuery size: countRowDeleted: " + countRowDeleted);

        //}
    }

    public void setLimitCountRowDeleted(int limitCountRowDeleted) {
        this.limitCountRowDeleted = limitCountRowDeleted;
    }
}
