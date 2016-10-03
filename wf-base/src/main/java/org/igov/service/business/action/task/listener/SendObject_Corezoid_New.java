package org.igov.service.business.action.task.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.util.json.JSONObject;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.exchange.Corezoid;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;

@Component("SendObject_Corezoid_New")
public class SendObject_Corezoid_New extends Abstract_MailTaskCustom implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(SendObject_Corezoid.class);

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    Corezoid corezoid;

    protected Expression soJSON;

    protected Expression sID_Conveyour;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        String soJSON_Value = this.soJSON.getExpressionText();
        String sID_Conveyour_Value = this.sID_Conveyour.getExpressionText();
        LOG.info("soJSON_Value: " + soJSON_Value + " sID_Conveyour_Value: " + sID_Conveyour_Value);
        String sJSON_Result = replaceTags(soJSON_Value, oExecution);
        LOG.info("sJSON_Result: " + sJSON_Result);
        JSONObject soJSON_Result = new JSONObject(sJSON_Result);
        Map<String, Object> data = new HashMap();
        Iterator it = soJSON_Result.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            data.put(key, soJSON_Result.get(key));
        }
        //data.put("nID_Task", Integer.parseInt(delegateTask.getId()));
        LOG.info("sendToCorezoid data:" + data);
        String result = corezoid.sendToCorezoid(sID_Conveyour_Value,
                generalConfig.getsUser_Corezoid_Gorsovet_Exchange(), 
                generalConfig.getSecretKey_Coreziod_Exchange(), data);
        LOG.info("sendToCorezoid result:" + result);
    }
}
