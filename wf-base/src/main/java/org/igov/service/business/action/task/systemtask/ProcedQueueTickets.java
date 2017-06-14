package org.igov.service.business.action.task.systemtask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.StartFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.task.core.AbstractModelTask;

/**
 * @author BW
 */
@Component("fileTaskUpload")
public class ProcedQueueTickets extends AbstractModelTask implements JavaDelegate {
    public static final String BUILDER_ATACH_MODEL_LIST = "builderAtachModel";
    static final transient Logger LOG = LoggerFactory
            .getLogger(ProcedQueueTickets.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        StartFormData oStartformData = oExecution.getEngineServices()
                .getFormService()
                .getStartFormData(oExecution.getProcessDefinitionId());

        scanExecutionOnQueueTickets(oExecution, oStartformData);
    }

}