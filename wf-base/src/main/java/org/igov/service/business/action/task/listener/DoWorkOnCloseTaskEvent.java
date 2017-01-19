/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.event.CloseTaskEvent;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elena
 */
@Component("DoWorkOnCloseTaskEvent")
public class DoWorkOnCloseTaskEvent implements TaskListener {
    
	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(DoWorkOnCloseTaskEvent.class);

    
    private Expression snClosedTaskId;    
     
    
   
    
    @Autowired
    private CloseTaskEvent closeTaskEvent;
    
    
   @Override
    public void notify(DelegateTask delegateTask) {
        
        LOG.info("DoWorkOnCloseTaskEvent start..." + delegateTask.getProcessInstanceId());
        
        
        String snClosedTaskId_Value = "";
        try{
        	snClosedTaskId_Value 
                = (this.snClosedTaskId != null)?getStringFromFieldExpression(this.snClosedTaskId, delegateTask.getExecution()) : "";
        }catch(Exception ex){}
        
               
        LOG.info("snClosedTaskId_Value is" + snClosedTaskId_Value);
            try {
                closeTaskEvent.doWorkOnCloseTaskEvent(true, snClosedTaskId_Value, null);
            } catch (ParseException e) {
                LOG.info("DoWorkOnCloseTaskEvent throws an errorrrrrrrrrrrrr", e);
                throw new RuntimeException(e);
                
            }
        
        }
}
    
    
