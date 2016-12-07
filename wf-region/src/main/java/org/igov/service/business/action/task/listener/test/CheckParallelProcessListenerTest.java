package org.igov.service.business.action.task.listener.test;

//import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.*;
//import org.activiti.engine.delegate.DelegateTask;
//import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.controller.ExceptionCommonController;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.TaskAlreadyUnboundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Component("chekForCompleteParallelProcessTest")
public class CheckParallelProcessListenerTest implements TaskListener {

    private static final Logger LOG = LoggerFactory.getLogger(CheckParallelProcessListenerTest.class);
    private Expression paramName;
    private Expression checkValue;
    private Expression sErrorMessage;

    //@Autowired
    //private RuntimeService runtimeService;
    @Override
    public void notify(DelegateTask delegateTask) throws IllegalArgumentException{
        DelegateExecution oExecution = delegateTask.getExecution();

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
            throw new IllegalArgumentException("errMsg=" + sErrorMessage);
        }
    }

}
