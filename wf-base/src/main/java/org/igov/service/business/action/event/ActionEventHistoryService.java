/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.event;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.igov.model.action.event.*;
import static org.igov.model.action.event.HistoryEvent_ServiceDaoImpl.DASH;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.util.ToolLuna;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Kovilin
 */
@Service
public class ActionEventHistoryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ActionEventHistoryService.class);
    
    @Autowired
    private HistoryEventDao historyEventDao;
    
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    
    public void setHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, Map<String, String> mParamMessage, Long nID_HistoryEvent_Service, Long nID_Document, String sSubjectInfo) {
        try {
            LOG.info("Method setHistoryEvent started");
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, mParamMessage);
            LOG.info("Creating journal message ended");
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage, nID_HistoryEvent_Service, nID_Document, sSubjectInfo);
        } catch (IOException e) {
            LOG.error("error: {}, during creating HistoryEvent", e.getMessage());
            LOG.trace("FAIL:", e);
        }
    }

    public HistoryEvent_Service addActionStatus_Central(
            String sID_Order,
            Long nID_Subject,
            String sUserTaskName,
            //String sProcessInstanceName,
            Long nID_Service,
            Long nID_ServiceData,
            Long nID_Region,
            String sID_UA,
            String soData,
            String sToken,
            String sHead,
            String sBody,
            Long nID_Proccess_Feedback,
            Long nID_Proccess_Escalation,
            Long nID_StatusType,
            Long nID_HistoryEventType
            
    ) {
        int dash_position = sID_Order.indexOf(DASH);
        int nID_Server = dash_position != -1 ? Integer.parseInt(sID_Order.substring(0, dash_position)) : 0;
        Long nID_Order = Long.valueOf(sID_Order.substring(dash_position + 1));
        Long nID_Process = ToolLuna.getOriginalNumber(nID_Order);

        HistoryEvent_Service oHistoryEvent_Service = new HistoryEvent_Service();
        oHistoryEvent_Service.setnID_Process(nID_Process);
        oHistoryEvent_Service.setsUserTaskName(sUserTaskName);
        oHistoryEvent_Service.setnID_StatusType(nID_StatusType);
        oHistoryEvent_Service.setnID_Subject(nID_Subject);
        oHistoryEvent_Service.setnID_Region(nID_Region);
        oHistoryEvent_Service.setnID_Service(nID_Service);
        oHistoryEvent_Service.setnID_ServiceData(nID_ServiceData);
        oHistoryEvent_Service.setsID_UA(sID_UA);
        oHistoryEvent_Service.setnRate(null);
        oHistoryEvent_Service.setSoData(soData);
        oHistoryEvent_Service.setsToken(sToken);
        //if(sHead==null){
        //    sHead = sProcessInstanceName;
        //}
        oHistoryEvent_Service.setsHead(sHead);
        oHistoryEvent_Service.setsBody(sBody);
        oHistoryEvent_Service.setnID_Server(nID_Server);
        oHistoryEvent_Service.setnID_Proccess_Feedback(nID_Proccess_Feedback);
        oHistoryEvent_Service.setnID_Proccess_Escalation(nID_Proccess_Escalation);
        oHistoryEvent_Service = historyEventServiceDao.addHistoryEvent_Service(oHistoryEvent_Service);
        Map<String, String> mParamMessage = new HashMap<>();
        mParamMessage.put(HistoryEventMessage.SERVICE_NAME, sHead);//sProcessInstanceName
        mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName);
        setHistoryEvent(HistoryEventType.getById(nID_HistoryEventType), nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null, null);
        
        return oHistoryEvent_Service;
    }
}