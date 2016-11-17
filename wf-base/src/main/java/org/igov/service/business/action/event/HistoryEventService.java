package org.igov.service.business.action.event;

import java.util.Map;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;

public interface HistoryEventService {
    String getHistoryEvent(String sID_Order) throws Exception;

    String updateHistoryEvent(String sID_Order,String sUserTaskName, boolean addAccessKey, HistoryEvent_Service_StatusType nID_StatusType, Map<String, String> params) throws Exception;

    String updateHistoryEvent(String sID_order, HistoryEvent_Service_StatusType statusType, Map<String, String> mParam) throws Exception;

    void addHistoryEvent(String sID_Order, String sUserTaskName, Map<String, String> params) throws Exception;

    String addServiceMessage(Map<String, String> params) throws Exception;
}
