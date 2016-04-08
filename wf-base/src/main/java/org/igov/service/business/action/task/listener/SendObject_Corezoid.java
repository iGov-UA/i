package org.igov.service.business.action.task.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
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

@Component("SendObject_Corezoid")
public class SendObject_Corezoid extends Abstract_MailTaskCustom implements TaskListener {

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
            LOG.info("delegateTask: getVariable: " + delegateTask.getVariable("sPhoneCall_SubjectContact_Client")
                    + " getVariableLocal: " + delegateTask.getVariableLocal("sPhoneCall_SubjectContact_Client"));
            getVariableValues(delegateTask);
            LOG.info("!!!!!!!!!!!!!!!!!SendObject_Corezoid soJSON_Result: " + soJSON.getExpressionText());
            //String sJSON = getStringFromFieldExpression(this.soJSON, delegateTask.getExecution());
            String sJSON_Result = replaceTags(this.soJSON.getExpressionText(), delegateTask.getExecution());
            LOG.info("sJSON_Result: " + sJSON_Result);
            JSONObject soJSON_Result = new JSONObject(sJSON_Result);
            Map<String, Object> data = new HashMap();
            Iterator it = soJSON_Result.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                data.put(key, soJSON_Result.get(key));
            }
            data.put("nID_Task", Integer.parseInt(delegateTask.getId()));
            LOG.info("sendToCorezoid data:" + data);
            String result = corezoid.sendToCorezoid(sID_Conveyour.getExpressionText(), data);
            LOG.info("sendToCorezoid result:" + result);
        } catch (Exception ex) {
            LOG.error("SendObject_Corezoid soJSON error: " + soJSON.getExpressionText(), ex);
        }
    }

    //временно. убрать
    public static void getVariableValues(DelegateTask delegateTask) {
        TaskFormData data = delegateTask.getExecution().getEngineServices().getFormService().getTaskFormData(delegateTask.getId());
        if (data != null) {
            for (FormProperty property : data.getFormProperties()) {
                String sValue = "";
                String sType = property.getType() != null ? property.getType().getName() : "";
                if (!"enum".equalsIgnoreCase(sType)) {
                    sValue = property.getValue();
                }
                LOG.info("(taskId={}, propertyName={}, sValue={})", delegateTask.getId(), property.getName(), sValue);
            }
        } else {
            LOG.info("TaskFormData for task {} is null. Skipping from processing.", delegateTask.getId());
        }
    }
}
