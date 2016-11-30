package org.igov.service.business.action.task.systemtask;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.exchange.Corezoid;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;

@Component("SendObject_Corezoid_New")
public class SendObject_Corezoid_New extends Abstract_MailTaskCustom implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(SendObject_Corezoid_New.class);

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    Corezoid corezoid;

    private Expression soData;

    private Expression sID_Conveyour;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        String soData_Value = this.soData.getExpressionText();
        String sID_Conveyour_Value = this.sID_Conveyour.getExpressionText();
        LOG.info("soData_Value: " + soData_Value + " sID_Conveyour_Value: " + sID_Conveyour_Value);
        String soData_Value_Result = replaceTags(soData_Value, oExecution);
        LOG.info("soData_Value_Result: " + soData_Value_Result);
        Map<String, Object> data = new HashMap();
        String[] aDataSplit = soData_Value_Result.split(";;");
        String key, value;
        for (String dataSplit : aDataSplit) {
            String[] keyValue = dataSplit.split("::");
            if (keyValue != null && keyValue.length > 0) {
                key = keyValue[0];
                if (keyValue.length == 1) {
                    value = "";
                } else {
                    value = keyValue[1];
                }
                data.put(key, value);
            }
        }
        LOG.info("sendToCorezoid data:" + data);
        String result = corezoid.sendToCorezoid(sID_Conveyour_Value,
                generalConfig.getsUser_Corezoid_Gorsovet_Exchange(),
                generalConfig.getsSecretKey_Corezoid_Gorsovet_Exchange(), data);
        LOG.info("sendToCorezoid result:" + result);
    }
}
