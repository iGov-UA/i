package org.igov.service.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 *
 * @author cepera
 */
@Component("setCheckVar")
public class SetCheckVarListener implements TaskListener {

    private static final Logger LOG = LoggerFactory.getLogger(SetCheckVarListener.class);
    private Expression paramName;
    private Expression paramVal;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution oExecution = delegateTask.getExecution();

        String paramName = getStringFromFieldExpression(this.paramName, oExecution);
        LOG.info("paramName: " + paramName);
        String paramVal = getStringFromFieldExpression(this.paramVal, oExecution);
        LOG.info("paramVal: " + paramVal);

        oExecution.setVariable(paramName, paramVal);

    }

}
