package org.igov.service.business.action.task.listener.doc;

import java.io.InputStream;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.dfs.DfsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks_Listener implements TaskListener {
    
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks_Listener.class);
     
    private Expression sTaskProcessDefinition;
            
    private Expression sID_Attachment;
            
    private Expression sBodyDocument;
            
    private Expression sLoginAuthor;
     
    @Autowired
    private TaskService taskService;

    @Override
    public void notify(DelegateTask delegateTask) {
        try{
            String sTaskProcessDefinition_Value = 
                getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution());
            String sID_Attachment_Value = 
                getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution());
            String sBodyDocument_Value = 
                getStringFromFieldExpression(this.sBodyDocument, delegateTask.getExecution());
            String sLoginAuthor_Value = 
                getStringFromFieldExpression(this.sLoginAuthor, delegateTask.getExecution());
 
            LOG.info("SetTasks listener data: sTaskProcessDefinition_Value: " 
                + sTaskProcessDefinition_Value + " sID_Attachment_Value: " + sID_Attachment_Value + " sBodyDocument_Value: " +
                sBodyDocument_Value + " sLoginAuthor_Value: " + sLoginAuthor_Value);
 
            InputStream json_Content = taskService.getAttachmentContent(sID_Attachment_Value);
 
            LOG.info("json_Content: " + json_Content);
        }
         catch (Exception ex){
             LOG.error("SetTasks listener throws an error: " + ex.toString());
        }
    }
}