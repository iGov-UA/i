package org.igov.service.business.action.task.listener.doc;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.model.process.ProcessSubject;
import org.igov.service.business.process.ProcessSubjectService;

/**
 *
 * @author Elena
 */
@Component("UpdateStatusTaskTreeAndCloseProcess")
public class UpdateStatusTaskTreeAndCloseProcess implements TaskListener {


	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(UpdateStatusTaskTreeAndCloseProcess.class);

    
    private Expression snID_Process_Activiti;    
    private Expression sID_ProcessSubjectStatus;
    
    
    @Autowired
    private ProcessSubjectService processSubjectService;
    
    
   @Override
    public void notify(DelegateTask delegateTask) {
        
        LOG.info("UpdateStatusTaskTreeAndCloseProcess start..." + delegateTask.getProcessInstanceId());
        
        
        String snID_Process_Activiti_Value = "";
        try{
        	snID_Process_Activiti_Value 
                = (this.snID_Process_Activiti != null)?getStringFromFieldExpression(this.snID_Process_Activiti, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
        String sID_ProcessSubjectStatus_Value = "";
        try{
        	sID_ProcessSubjectStatus_Value = (this.sID_ProcessSubjectStatus != null) ? getStringFromFieldExpression(this.sID_ProcessSubjectStatus, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
        
       
        
      
        LOG.info("snID_Process_Activiti_Value is" + snID_Process_Activiti_Value);
        processSubjectService.UpdateStatusTaskTreeAndCloseProcess(snID_Process_Activiti_Value,sID_ProcessSubjectStatus_Value );
        
        LOG.info("sID_ProcessSubjectStatus_Value is" + sID_ProcessSubjectStatus_Value);
    }
}

