package org.egov.service;

import com.google.common.collect.ImmutableMap;

/**
 * @author vit@tym.im
 */
public interface HistoryEventService {
    String getHistoryEvent(String nID_Protected) throws Exception;

    void validateHistoryEventToken(Long nID_Protected, String sToken) throws Exception;

    String updateHistoryEvent(String sID_Process, String sID_Status, boolean addAccessKey,
            ImmutableMap.Builder<String, String> params) throws Exception;

    void addHistoryEvent(String sID_Process,
            String taskName, String sProcessInstanceName, String nID_Subject, String snID_Region, String snID_Service,
            String sID_ua) throws Exception;
}
