package org.igov.service.business.action.task.listener.doc;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.igov.service.business.process.ProcessSubjectService;
import org.igov.util.Tool;

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sTaskProcessDefinition;
    private Expression sID_Attachment;
    private Expression processDefinitionId;
    private Expression soData;

    @Autowired
    private ProcessSubjectService processSubjectService;

    @Override
    public void notify(DelegateTask delegateTask) {

        LOG.info("SetTasks start..." + delegateTask.getProcessInstanceId());

        String sTaskProcessDefinition_Value = "";
        try {
            sTaskProcessDefinition_Value
                    = (this.sTaskProcessDefinition != null) ? getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("sTaskProcessDefinition: ", ex);
        }

        String sID_Attachment_Value = "";
        try {
            sID_Attachment_Value = (this.sID_Attachment != null) ? getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("sID_Attachment_Value: ", ex);
        }

        String sProcessDefinitionId_Value = "";
        try {
            sProcessDefinitionId_Value = (this.processDefinitionId != null) ? getStringFromFieldExpression(this.processDefinitionId, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("processDefinitionId: ", ex);
        }

        String soData_Value = "";

        try {
            soData_Value = (this.soData != null) ? getStringFromFieldExpression(this.soData, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("soData: ", ex);
        }

        LOG.info("SetTasks start with soData..." + soData_Value);

        Map<String, Object> mParam = new HashMap<>();

        mParam.put("sTaskProcessDefinition", sTaskProcessDefinition_Value);
        mParam.put("sID_Attachment", sID_Attachment_Value);
        mParam.put("processDefinitionId", sProcessDefinitionId_Value);
        mParam.put("soData", soData_Value);
        mParam.putAll(Tool.parseData(soData_Value));

        LOG.info("SetTasks start with param..." + mParam);
        processSubjectService.setProcessSubjects(mParam, delegateTask.getExecution().getId());

        LOG.info("SetTasks finished");
    }
}
