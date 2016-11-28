package org.igov.service.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.exception.TaskAlreadyUnboundException;

@Component("chekForCompleteParallelProcess")
public class CheckParallelProcessListener implements ExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(CheckParallelProcessListener.class);
    private Expression paramName;
    private Expression checkValue;
    private Expression sErrorMessage;

    @Override
    public void notify(DelegateExecution oExecution) throws TaskAlreadyUnboundException {
        //DelegateExecution oExecution = delegateTask.getExecution();

        String paramName = getStringFromFieldExpression(this.paramName, oExecution);
        LOG.info("paramName: " + paramName);
        String checkValue = getStringFromFieldExpression(this.checkValue, oExecution);
        LOG.info("checkValue: " + checkValue);
        String sErrorMessage = getStringFromFieldExpression(this.sErrorMessage, oExecution);
        LOG.info("sErrorMessage: " + sErrorMessage);
        //String sValFromProcess = (String) runtimeService.getVariable(oExecution.getProcessInstanceId(), paramName);
        String sValFromProcess = (String) oExecution.getVariable(paramName);
        LOG.info("sValFromProcess: " + sValFromProcess);

        if (!checkValue.trim().equals(sValFromProcess.trim())) {
            throw new TaskAlreadyUnboundException("errMsg=" + sErrorMessage);
        }
    }

}
