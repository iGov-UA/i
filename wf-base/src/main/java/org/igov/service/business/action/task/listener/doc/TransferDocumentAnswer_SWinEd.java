/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.dfs.DfsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author olga
 */
@Component("TransferDocumentAnswer_SWinEd")
public class TransferDocumentAnswer_SWinEd implements TaskListener {
       
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(TransferDocumentAnswer_SWinEd.class);

    private Expression sINN;

    @Autowired
    private DfsService dfsService;
    
    @Autowired
    private TaskService taskService;
    

    @Override
    public void notify(DelegateTask delegateTask) {
        LOG.info("!!!");
        String sINN_Value = getStringFromFieldExpression(this.sINN, delegateTask.getExecution());
        String asID_Attach_Dfs = dfsService.getAnswer(delegateTask.getId(), delegateTask.getProcessInstanceId(), sINN_Value);
        LOG.info("!!!as: " + asID_Attach_Dfs); //хочу словить налпоинтер. это так и нужно!!! не убирать
        taskService.complete(delegateTask.getId());
    }

}
