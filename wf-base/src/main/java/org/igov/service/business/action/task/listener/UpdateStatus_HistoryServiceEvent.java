/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.service.business.action.event.HistoryEventService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author olga
 */
@Component("updateStatus_HistoryServiceEvent")
public class UpdateStatus_HistoryServiceEvent implements JavaDelegate {
    
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(UpdateStatus_HistoryServiceEvent.class);
    
    @Autowired
    HistoryEventService historyEventService;
    
    @Autowired
    GeneralConfig generalConfig;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Map<String, String> mParam = new HashMap<>();
        String sID_order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
        mParam.put("nID_StatusType", String.valueOf(HistoryEvent_Service_StatusType.CLOSED.getnID()));
        
        try {
            LOG.info("mParam: " + mParam);
            String result = historyEventService.updateHistoryEvent(sID_order, mParam);
            LOG.info("result: " + result);
        } catch (Exception ex) {
            LOG.error("updateStatus_HistoryServiceEvent fals!!!", ex);
        }
        
    }
    
}
