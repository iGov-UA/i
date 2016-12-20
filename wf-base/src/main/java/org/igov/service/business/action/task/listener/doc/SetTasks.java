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

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sTaskProcessDefinition;    
    private Expression sID_Attachment;
    private Expression sTypeDoc;
    private Expression sID_Order_GovPublic;
    private Expression sDateRegistration;
    private Expression sDateDoc;
    private Expression sApplicant;
    private Expression nCountAttach;
    private Expression asUrgently;
    private Expression sContent;
    private Expression sNote;
    private Expression sDoc1;
    private Expression sAutorResolution;
    private Expression sName_SubjectRole;
    private Expression asTypeResolution;
    private Expression sTextResolution;
    private Expression sDateExecution;
    
    @Autowired
    private ProcessSubjectService processSubjectService;

    @Override
    public void notify(DelegateTask delegateTask) {
        
        LOG.info("SetTasks start..." + delegateTask.getProcessInstanceId());
        
        
        String sTaskProcessDefinition_Value 
                = (this.sTaskProcessDefinition != null)?getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution()) : null;
        
        String sID_Attachment_Value
                = (this.sID_Attachment != null) ? getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution()) : null;
        
        String sContent_Value
                = (this.sContent != null) ? getStringFromFieldExpression(this.sContent, delegateTask.getExecution()) : null;
        
        String sAutorResolution_Value
                = (this.sAutorResolution != null) ? getStringFromFieldExpression(this.sAutorResolution, delegateTask.getExecution()) : null;
        
        String sDateExecution_Value
                = (this.sDateExecution != null) ? getStringFromFieldExpression(this.sDateExecution, delegateTask.getExecution()) : null;
        
        String sTypeDoc_Value
                = (this.sTypeDoc != null) ? getStringFromFieldExpression(this.sTypeDoc, delegateTask.getExecution()) : null;
        
        String sID_Order_GovPublic_Value
                = (this.sID_Order_GovPublic != null) ? getStringFromFieldExpression(this.sID_Order_GovPublic, delegateTask.getExecution()) : null;
        
        String sDateRegistration_Value
                = (this.sDateRegistration != null) ? getStringFromFieldExpression(this.sDateRegistration, delegateTask.getExecution()) : null;
        
        String sDateDoc_Value
                = (this.sDateDoc != null) ? getStringFromFieldExpression(this.sDateDoc, delegateTask.getExecution()) : null;
        
        String sApplicant_Value
                = (this.sApplicant != null) ? getStringFromFieldExpression(this.sApplicant, delegateTask.getExecution()) : null;
        
        String snCountAttach_Value
                = (this.nCountAttach != null) ? getStringFromFieldExpression(this.nCountAttach, delegateTask.getExecution()) : null;
        
        String sNote_Value
                = (this.sNote != null) ? getStringFromFieldExpression(this.sNote, delegateTask.getExecution()) : null;
        
        String sAsUrgently_Value
                = (this.asUrgently != null) ? getStringFromFieldExpression(this.asUrgently, delegateTask.getExecution()) : null;
        
        String sAsTypeResolution_Value
                = (this.asTypeResolution != null) ? getStringFromFieldExpression(this.asTypeResolution, delegateTask.getExecution()) : null;
        
        String sTextResolution_Value
                = (this.sTextResolution != null) ? getStringFromFieldExpression(this.sTextResolution, delegateTask.getExecution()) : null;
        
        String sDoc1_Value
                = (this.sDoc1 != null) ? getStringFromFieldExpression(this.sDoc1, delegateTask.getExecution()) : null;
        
        String sName_SubjectRole
                = (this.sName_SubjectRole != null) ? getStringFromFieldExpression(this.sName_SubjectRole, delegateTask.getExecution()) : null;
              
        Map<String, String> mParam = new HashMap<>();
        
        mParam.put("sTaskProcessDefinition", sTaskProcessDefinition_Value);
        mParam.put("sID_Attachment", sID_Attachment_Value);
        mParam.put("sContent", sContent_Value);
        mParam.put("sAutorResolution", sAutorResolution_Value);
        mParam.put("sName_SubjectRole", sName_SubjectRole);
        mParam.put("sDateExecution", sDateExecution_Value);
        mParam.put("sTypeDoc", sTypeDoc_Value);
        mParam.put("sID_Order_GovPublic", sID_Order_GovPublic_Value);
        mParam.put("sDateRegistration", sDateRegistration_Value);
        mParam.put("sDateDoc", sDateDoc_Value);
        mParam.put("sApplicant", sApplicant_Value);
        mParam.put("nCountAttach", snCountAttach_Value);
        mParam.put("sNote", sNote_Value);
        mParam.put("asUrgently", sAsUrgently_Value);
        mParam.put("asTypeResolution", sAsTypeResolution_Value);
        mParam.put("sTextResolution", sTextResolution_Value);
        mParam.put("sDoc1", sDoc1_Value);

        LOG.info("SetTasks start with param..." + mParam);
        processSubjectService.setProcessSubjects(mParam, delegateTask.getExecution().getId());
                
        LOG.info("SetTasks finished");
    }
}
