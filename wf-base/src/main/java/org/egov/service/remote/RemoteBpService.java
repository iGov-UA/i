package org.egov.service.remote;

import org.apache.log4j.Logger;
import org.egov.service.BpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wf.dp.dniprorada.rest.HttpRequester;
import org.wf.dp.dniprorada.util.GeneralConfig;

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

    @Override
    public String startProcessInstanceByKey(String key, Map<String, Object> variables) throws Exception {
        String URI_START_PROCESS = "/wf/service/rest/start-process/%s";
        String url = generalConfig.sHost() + String.format(URI_START_PROCESS, key);
        LOG.info("Getting URL with parameters: " + url + ":" + variables);
        Map<String, String> params = new HashMap<>();
        for (String keyValue : variables.keySet()) {
            Object value = variables.get(keyValue);
            params.put(keyValue, value == null ? null : value.toString());
        }
        String jsonProcessInstance = httpRequester.get(url, params);
        LOG.info("jsonProcessInstance=" + jsonProcessInstance);
        return jsonProcessInstance;
    }
}
