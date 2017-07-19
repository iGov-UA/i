/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.systemtask;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import static org.igov.util.Tool.parseData;

/**
 *
 * @author iDoc-2
 */
@Component("StartProcess")
public class StartProcess extends Abstract_MailTaskCustom implements JavaDelegate {
    
    private final static Logger LOG = LoggerFactory.getLogger(StartProcess.class);

    private Expression soData;
    
    private Expression sID_BP;
    
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution oDelegateExecution) throws Exception {
        String soData_Value = this.soData.getExpressionText();
        String sID_BP_Value = this.sID_BP.getExpressionText();
        LOG.info("soData_Value before: " + soData_Value);
        String soData_Value_Result = replaceTags(soData_Value, oDelegateExecution);
        LOG.info("soData_Value after: " + soData_Value_Result);
        Map<String, Object> data = parseData(soData_Value_Result);
        Map<String, Object> mDataWithoutNull = new HashMap<>();
        
        LOG.info("data {}", data);
        
        for(String key : data.keySet()){
            if(data.get(key)!= null && !((String)data.get(key)).equals("null")){
               mDataWithoutNull.put(key, data.get(key)); 
            }
        }
        
        LOG.info("data {}", mDataWithoutNull);
        
        runtimeService.startProcessInstanceByKey(sID_BP_Value, mDataWithoutNull);
    }

}
