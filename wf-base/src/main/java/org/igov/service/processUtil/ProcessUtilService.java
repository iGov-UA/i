package org.igov.service.processUtil;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component("processUtilService")
@Service
public class ProcessUtilService {
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private TaskService taskService;
    
    public Map<String, Object> getmID_TaskAndProcess(String nID_Process){
        
        Map <String, Object> resulMap = new HashMap<>();
        
        resulMap.put("nID_Process", nID_Process);
        
        try{
            resulMap.put("nID_Task_Active", taskService.createTaskQuery().processInstanceId(nID_Process).active().list().get(0).getId());
        }catch (Exception ex){
            resulMap.put("nID_Task_Active", null);
        }
        
        try{
           
            List<HistoricTaskInstance> aHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(nID_Process).orderByHistoricTaskInstanceEndTime().desc().list();
            
            if(resulMap.get("nID_Task_Active") == null){
               resulMap.put("nID_Task_HistoryLast", aHistoricTaskInstance.get(0).getId()); 
            }
            else{
                if(aHistoricTaskInstance.size() > 1){
                    resulMap.put("nID_Task_HistoryLast", aHistoricTaskInstance.get(1).getId());
                }else{
                    resulMap.put("nID_Task_HistoryLast", null);
                }  
            }
            
        }catch (Exception ex){
            resulMap.put("nID_Task_HistoryLast", null);
        }
        
        return resulMap;
    }
    
}
