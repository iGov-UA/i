package org.egov.service;

import com.google.common.collect.ImmutableMap;

/**
 * @author vit@tym.im
 */
public interface HistoryEventService {
    String getHistoryEvent(String sID_Order, Long nID_Protected, Integer nID_Server) throws Exception;

    String updateHistoryEvent(String sID_Process, String sID_Status, boolean addAccessKey,
            ImmutableMap.Builder<String, String> params) throws Exception;

    void addHistoryEvent(String sID_process, String sID_Status, ImmutableMap.Builder<String, String> params)
            throws Exception;
}
