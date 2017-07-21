package org.igov.service.processUtil;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.exception.CRCInvalidException;
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

    @Autowired
    private ActionTaskService oActionTaskService;

    public Map<String, Object> getmID_TaskAndProcess(Long nID_Order, String nID_Process) throws CRCInvalidException {

        if (nID_Order != null) {
            nID_Process = oActionTaskService.getOriginalProcessInstanceId(nID_Order);
        }

        Map<String, Object> resulMap = new HashMap<>();

        resulMap.put("nID_Process", nID_Process);
        String nID_Task_Active = null;
        List<Task> aTask = taskService.createTaskQuery().processInstanceId(nID_Process).active().list();
        if (aTask.size() > 0) {
            nID_Task_Active = aTask.get(0).getId();
        }
        resulMap.put("nID_Task_Active", nID_Task_Active);

        try {

            List<HistoricTaskInstance> aHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(nID_Process).orderByHistoricTaskInstanceEndTime().desc().list();

            if (resulMap.get("nID_Task_Active") == null) {
                resulMap.put("nID_Task_HistoryLast", aHistoricTaskInstance.get(0).getId());
            } else {
                if (aHistoricTaskInstance.size() > 1) {
                    resulMap.put("nID_Task_HistoryLast", aHistoricTaskInstance.get(1).getId());
                } else {
                    resulMap.put("nID_Task_HistoryLast", null);
                }
            }

        } catch (Exception ex) {
            resulMap.put("nID_Task_HistoryLast", null);
        }

        return resulMap;
    }

}
