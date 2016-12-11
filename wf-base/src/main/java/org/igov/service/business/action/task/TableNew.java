package org.igov.service.business.action.task;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author YmNIK
 */
public class TableNew {   

    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private ActionTaskService oActionTaskService;
    
    
    private String sFileName = "";
    private String description = "";
    private String type = "";
    private String id = "";
    
    public void getAttachment(String taskId, String attachmentId, Integer nFile){
    
    // Получаем по задаче ид процесса
        HistoricTaskInstance historicTaskInstanceQuery = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        String processInstanceId = historicTaskInstanceQuery
                .getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(
                    "ProcessInstanceId for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }

        Attachment attachmentRequested = oActionTaskService.getAttachment(attachmentId, nFile, processInstanceId);
        this.sFileName = attachmentRequested.getName();
        this.description = attachmentRequested.getDescription();
        this.type = attachmentRequested.getType();

        this.id = attachmentRequested.getId();
    
    }
    
    
    public String getName(){
        return this.sFileName;
        }
    public String getDescription(){
        return this.description;
    }
    public String getType(){
        return this.type;
    }
    public String getId(){
        return this.id;
    }
}
