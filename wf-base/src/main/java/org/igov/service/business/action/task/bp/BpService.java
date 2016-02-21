package org.igov.service.business.action.task.bp;

import java.util.List;
import java.util.Map;

/**
 * @author OlgaPrylypko
 * 22.12.2015
 */

public interface BpService {

    String startProcessInstanceByKey(Integer nID_Server, String key, Map<String, Object> variables);

    void setVariableToProcessInstance(Integer nID_Server, String instanceId, String key, Object value);

    List<String> getProcessTasks(Integer nID_Server, String processInstanceId);

    void setVariableToActivitiTask(Integer nID_Server, String taskId, String key, Object value);
}
