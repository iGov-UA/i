package org.igov.model.action.event;

import org.igov.model.core.EntityDao;
import org.igov.model.document.Document;

import java.io.IOException;
import java.util.List;

public interface HistoryEventDao extends EntityDao<HistoryEvent> {

    public HistoryEvent getHistoryEvent(Long id);

    public List<HistoryEvent> getHistoryEvents(Long nID_Subject, boolean bGrouped);

    public Long setHistoryEvent(Long nID_Subject, Long nID_HistoryEventType,
            String sEventName_Custom, String sMessage, Long nID_HistoryEvent_Service, Long nID_Document  ) throws IOException;
    
}
