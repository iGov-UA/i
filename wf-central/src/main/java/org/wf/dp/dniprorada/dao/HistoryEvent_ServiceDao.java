package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.HistoryEvent_Service;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import java.util.List;
import java.util.Map;

public interface HistoryEvent_ServiceDao extends EntityDao<HistoryEvent_Service> {

    HistoryEvent_Service addHistoryEvent_Service(HistoryEvent_Service event_service);

    HistoryEvent_Service updateHistoryEvent_Service(HistoryEvent_Service historyEvent_Service);

    List<Map<String, Long>> getHistoryEvent_ServiceBynID_Service(Long nID_Service);

    HistoryEvent_Service getOrgerByID(String sID_Order) throws CRCInvalidException;

    HistoryEvent_Service getOrgerByProcessID(Long nID_Process, Integer nID_Server);

    HistoryEvent_Service getOrgerByProtectedID(Long nID_Protected, Integer nID_Server) throws CRCInvalidException;
    
    HistoryEvent_Service getLastTaskHistory(Long nID_Subject, Long nID_Service, String sID_UA);
}
