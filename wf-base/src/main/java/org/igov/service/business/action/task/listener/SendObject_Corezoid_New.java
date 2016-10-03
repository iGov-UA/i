package org.igov.service.business.action.task.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
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
        //String soJSON_Value = null, sID_Conveyour_Value = null;
        //try {
            String soJSON_Value = getStringFromFieldExpression(this.soJSON, oExecution);
            String sID_Conveyour_Value = getStringFromFieldExpression(this.sID_Conveyour, oExecution);
            LOG.info("soJSON_Value: " + soJSON_Value + " sID_Conveyour: " + sID_Conveyour_Value);
            /*LOG.info("soJSON: " + this.soJSON.getExpressionText()
                    + " sID_Conveyour: " + this.sID_Conveyour.getExpressionText());
            LOG.info("soJSON: " + this.soJSON.getExpressionText()
                    + " sID_Conveyour: " + this.sID_Conveyour.getExpressionText());*/
            getStringFromFieldExpression(this.soJSON, oExecution);
            LOG.info("!!!!!!!!!!!!!!!!!SendObject_Corezoid soJSON_Result: " + soJSON.getExpressionText());
            String sJSON_Result = replaceTags(this.soJSON.getExpressionText(), oExecution);
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
            String result = corezoid.sendToCorezoid(sID_Conveyour_Value, data);
            LOG.info("sendToCorezoid result:" + result);
        //} catch (Exception ex) {
        //    LOG.error("Error SendObject_Corezoid sID_Conveyour_Value: " + sID_Conveyour_Value + " soJSON: " + soJSON_Value, ex);
        //}
    }
}
