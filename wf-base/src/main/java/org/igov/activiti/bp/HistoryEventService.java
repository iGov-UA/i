package org.igov.activiti.bp;

import java.util.Map;

public interface HistoryEventService {
    String getHistoryEvent(String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server) throws Exception;

    String updateHistoryEvent(String sID_Process, String sID_Status, boolean addAccessKey,
            Map<String, String> params) throws Exception;

    void addHistoryEvent(String sID_process, String sID_Status, Map<String, String> params)
            throws Exception;

    void addServiceMessage(Map<String, String> params);
}
