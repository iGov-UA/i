package org.igov.service.business.action.event;

import java.util.Map;

public interface HistoryEventService {
    String getHistoryEvent(String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server) throws Exception;

    String getHistoryEvent(String sID_Order) throws Exception;

    String updateHistoryEvent(String sID_Process, String sUserTaskName, boolean addAccessKey,
              Map<String, String> params) throws Exception;

    void addHistoryEvent(String sID_process, String sUserTaskName, Map<String, String> params)
            throws Exception;

    String addServiceMessage(Map<String, String> params);
}
