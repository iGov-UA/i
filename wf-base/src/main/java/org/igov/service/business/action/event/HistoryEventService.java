package org.igov.service.business.action.event;

import java.util.Map;

public interface HistoryEventService {
    String getHistoryEvent(String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server) throws Exception;

    String updateHistoryEvent(
            //Long nID_Process, 
            String sID_Order,
            String sUserTaskName, boolean addAccessKey,
              Map<String, String> params) throws Exception;

    void addHistoryEvent(String sID_Order, String sUserTaskName, Map<String, String> params)
            throws Exception;

    String addServiceMessage(Map<String, String> params);
}
