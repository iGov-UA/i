package org.igov.model;

import org.igov.model.core.EntityDao;

import java.io.IOException;
import java.util.List;
import org.igov.model.enums.HistoryEventType;

public interface HistoryEventDao extends EntityDao<HistoryEvent> {

    public HistoryEvent getHistoryEvent(Long id);

    public List<HistoryEvent> getHistoryEvents(Long nID_Subject);

    public Long setHistoryEvent(Long nID_Subject, Long nID_HistoryEventType,
            String sEventName_Custom, String sMessage) throws IOException;
    
}
