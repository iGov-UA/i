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
import java.util.List;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteProccess")
public class DeleteProccess implements JavaDelegate {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
        for(ProcessInstance processInstance : processInstances){
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "deprecated");
        } 
    }

}
