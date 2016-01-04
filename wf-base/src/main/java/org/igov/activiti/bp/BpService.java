package org.igov.activiti.bp;

import java.util.List;
import java.util.Map;

/**
 * @author OlgaPrylypko
 * @since 22.12.2015
 */

public interface BpService {

    String startProcessInstanceByKey(String key, Map<String, Object> variables) throws Exception;

    void setVariableToProcessInstance(String instanceId, String key, Object value);

    List<String> getProcessTasks(String processInstanceId);

    void setVariableToActivitiTask(String taskId, String key, Object value);
}
