package org.igov.model.action.event;

import org.igov.model.core.EntityDao;

import java.io.IOException;
import java.util.List;

public interface HistoryEventDao extends EntityDao<HistoryEvent> {

    public HistoryEvent getHistoryEvent(Long id);

    public List<HistoryEvent> getHistoryEvents(Long nID_Subject);

    public Long setHistoryEvent(Long nID_Subject, Long nID_HistoryEventType,
            String sEventName_Custom, String sMessage) throws IOException;
    
}
