package org.igov.service.business.action.task.systemtask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONObject;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.exchange.Corezoid;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;

@Component("SendObject_Corezoid")
public class SendObject_Corezoid extends Abstract_MailTaskCustom {

    private final static Logger LOG = LoggerFactory.getLogger(SendObject_Corezoid.class);

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    Corezoid corezoid;

    protected Expression soJSON;
    
    protected Expression sID_Conveyour;

    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            LOG.info("!!!!!!!!!!!!!!!!!SendObject_Corezoid soJSON_Result: " + soJSON.getExpressionText());
            String sJSON = getStringFromFieldExpression(this.soJSON, delegateTask.getExecution());
            String sJSON_Result = replaceTags(sJSON, delegateTask.getExecution());
            LOG.info("sJSON_Result: " + sJSON_Result);
            JSONObject soJSON_Result = new JSONObject(sJSON_Result);
            Map<String, Object> data = new HashMap();
            Iterator it = soJSON_Result.keys();
            while(it.hasNext()){
                String key = (String)it.next();
                data.put(key, soJSON_Result.get(key));
            }
            data.put("nID_Task", Integer.parseInt(delegateTask.getId()));
            LOG.info("sendToCorezoid data:" + data);
            corezoid.sendToCorezoid(sID_Conveyour.getExpressionText(), data);
        } catch (Exception ex) {
            LOG.error("SendObject_Corezoid soJSON: " + soJSON.getExpressionText(), ex);
        }
    }
}
