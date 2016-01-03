package org.igov.activiti.bp.remote;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.log4j.Logger;
import org.igov.activiti.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
                if (value != null && !"null".equals(value.toString())) {
                    LOG.info(String.format("set [%s] to [%s]", keyValue, value));
                    runtimeService.setVariable(instanceId, keyValue, value);
                }
            }
        } catch (Exception e) {
            LOG.warn("error!", e);
        }
        return jsonProcessInstance;
    }
}
