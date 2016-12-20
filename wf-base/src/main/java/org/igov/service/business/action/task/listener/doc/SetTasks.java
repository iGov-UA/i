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
        
        
        String sTaskProcessDefinition_Value = "";
        try{
            sTaskProcessDefinition_Value 
                = (this.sTaskProcessDefinition != null)?getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sID_Attachment_Value = "";
        try{
                sID_Attachment_Value = (this.sID_Attachment != null) ? getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sContent_Value = "";
        try{
            sContent_Value
                = (this.sContent != null) ? getStringFromFieldExpression(this.sContent, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sAutorResolution_Value = "";
        try{
            sAutorResolution_Value = (this.sAutorResolution != null) ? getStringFromFieldExpression(this.sAutorResolution, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sDateExecution_Value = "";
        try{
            sDateExecution_Value = (this.sDateExecution != null) ? getStringFromFieldExpression(this.sDateExecution, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sTypeDoc_Value = "";
        try{
            sTypeDoc_Value = (this.sTypeDoc != null) ? getStringFromFieldExpression(this.sTypeDoc, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sID_Order_GovPublic_Value = "";
        try{
            sID_Order_GovPublic_Value
                = (this.sID_Order_GovPublic != null) ? getStringFromFieldExpression(this.sID_Order_GovPublic, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sDateRegistration_Value = "";
        try{
            sDateRegistration_Value
                = (this.sDateRegistration != null) ? getStringFromFieldExpression(this.sDateRegistration, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sDateDoc_Value = "";
        try{
            sDateDoc_Value
                = (this.sDateDoc != null) ? getStringFromFieldExpression(this.sDateDoc, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sApplicant_Value = "";
        try{
            sApplicant_Value
                = (this.sApplicant != null) ? getStringFromFieldExpression(this.sApplicant, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String snCountAttach_Value = "";
        try{
            snCountAttach_Value
                = (this.nCountAttach != null) ? getStringFromFieldExpression(this.nCountAttach, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sNote_Value = "";
        try{
            sNote_Value
                = (this.sNote != null) ? getStringFromFieldExpression(this.sNote, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sAsUrgently_Value = "";
        try{
            sAsUrgently_Value
                = (this.asUrgently != null) ? getStringFromFieldExpression(this.asUrgently, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sAsTypeResolution_Value = "";
        try{
            sAsTypeResolution_Value
                = (this.asTypeResolution != null) ? getStringFromFieldExpression(this.asTypeResolution, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sTextResolution_Value = "";
        try{
            sTextResolution_Value
                = (this.sTextResolution != null) ? getStringFromFieldExpression(this.sTextResolution, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sDoc1_Value = "";
        try{
            sDoc1_Value = (this.sDoc1 != null) ? getStringFromFieldExpression(this.sDoc1, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sName_SubjectRole_Value = "";
        try{
            sName_SubjectRole_Value = (this.sName_SubjectRole != null) ? getStringFromFieldExpression(this.sName_SubjectRole, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        Map<String, String> mParam = new HashMap<>();
        
        mParam.put("sTaskProcessDefinition", sTaskProcessDefinition_Value);
        mParam.put("sID_Attachment", sID_Attachment_Value);
        mParam.put("sContent", sContent_Value);
        mParam.put("sAutorResolution", sAutorResolution_Value);
        mParam.put("sName_SubjectRole", sName_SubjectRole_Value);
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
