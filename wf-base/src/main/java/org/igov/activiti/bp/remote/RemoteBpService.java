package org.igov.activiti.bp.remote;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.log4j.Logger;
import org.igov.activiti.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
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
    //    @Autowired
    //    private RuntimeService runtimeService;
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
            String uri = "/wf/service/rest/process/setVariable";
            Map<String, String> params = new HashMap<>();
            String url = generalConfig.sHost() + uri;
            params.put("processInstanceId", instanceId);
            params.put("key", key);
            params.put("value", value.toString());
            try {
                String jsonProcessInstance = httpRequester.get(url, params);
            } catch (Exception e) {
                LOG.warn("error!", e);
            }
            //runtimeService.setVariable(instanceId, key, value);
        }
    }

    @Override
    public List<String> getProcessTasks(String processInstanceId) {
        List<String> result = new LinkedList<>();
        String uri = "/wf/service/rest/process/getTasks";
        Map<String, String> params = new HashMap<>();
        String url = generalConfig.sHost() + uri;
        params.put("processInstanceId", processInstanceId);
        try {
            String jsonProcessInstance = httpRequester.get(url, params);
            JSONArray jsonArray = new JSONArray(jsonProcessInstance);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject task = jsonArray.getJSONObject(i);
                result.add(task.get("id").toString());
            }
        } catch (Exception e) {
            LOG.warn("error!", e);
        }
        return result;//taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public void setVariableToActivitiTask(String taskId, String key, Object value) {
        if (value != null && !"null".equals(value.toString())) {
            LOG.info(String.format("set value [%s] to [%s] in task with id=%s", key, value, taskId));
            taskService.setVariable(taskId, key, value);
        }
    }

}
