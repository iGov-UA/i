package org.igov.activiti.bp.remote;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.igov.activiti.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author OlgaPrylypko
 * @since 22.12.2015
 */

@Service
public class RemoteBpService implements BpService {
    private static final Logger LOG = Logger.getLogger(RemoteBpService.class);

    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Override
    public String startProcessInstanceByKey(String key, Map<String, Object> variables) throws Exception {
        String uriStartProcess = "/wf/service/rest/start-process/%s";
        String url = generalConfig.sHost() + String.format(uriStartProcess, key);
        LOG.info("Getting URL with parameters: " + url + ":" + variables);
        Map<String, String> params = new HashMap<>();
        params.put("sParams", new JSONObject(variables).toString());
        String jsonProcessInstance = httpRequester.get(url, params);
        LOG.info("jsonProcessInstance=" + jsonProcessInstance);
        try {
            String instanceId = "" + new JSONObject(jsonProcessInstance).get("id");
            LOG.info("instanceId=" + instanceId);
            for (String keyValue : variables.keySet()) {
                Object value = variables.get(keyValue);
                setVariableToProcessInstance(instanceId, keyValue, value);
            }
        } catch (Exception e) {
            LOG.warn("error!", e);
        }
        return jsonProcessInstance;
    }

    @Override
    public void setVariableToProcessInstance(String instanceId, String key, Object value) {
        if (value != null && !"null".equals(value.toString())) {
            LOG.info(String.format("set value [%s] to [%s] in process with id=%s", key, value, instanceId));
            runtimeService.setVariable(instanceId, key, value);
        }
    }

    @Override
    public List<Task> getProcessTasks(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public void setVariableToActivitiTask(String taskId, String key, Object value) {
        if (value != null && !"null".equals(value.toString())) {
            LOG.info(String.format("set value [%s] to [%s] in task with id=%s", key, value, taskId));
            taskService.setVariable(taskId, key, value);
        }
    }

}
