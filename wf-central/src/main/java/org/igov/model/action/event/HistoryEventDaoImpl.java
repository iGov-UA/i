package org.igov.model.action.event;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class HistoryEventDaoImpl extends GenericEntityDao<HistoryEvent> implements HistoryEventDao {

    protected HistoryEventDaoImpl() {
        super(HistoryEvent.class);
    }

    @Override
    public HistoryEvent getHistoryEvent(Long id) {
        HistoryEvent historyEvent = findByIdExpected(id);
        if (!historyEvent.getHistoryEventTypeKey().equals(0L)) {
            historyEvent.setEventNameCustom(HistoryEventType.getById(historyEvent.getHistoryEventTypeKey()).getsName());
        }
        return historyEvent;
    }

    @Override
    public List<HistoryEvent> getHistoryEvents(Long nID_Subject, boolean bGrouped ) {

        //List<HistoryEvent> historyEvents = findAllBy("subjectKey", nID_Subject);
        List<HistoryEvent> historyEvents = findAllByAttributeCriteria("subjectKey", nID_Subject)
                .addOrder(Order.desc("date")).list();
        for (HistoryEvent historyEvent : historyEvents) {
            if (!historyEvent.getHistoryEventTypeKey().equals(0L)) {
                historyEvent
                        .setEventNameCustom(HistoryEventType.getById(historyEvent.getHistoryEventTypeKey()).getsName());
            }
        }

        return historyEvents;
    }

    @Override
    public Long setHistoryEvent(Long nID_Subject, Long nID_HistoryEventType, String sEventName_Custom, String sMessage)
            throws IOException {
        HistoryEvent historyEvent = new HistoryEvent();
        historyEvent.setSubjectKey(nID_Subject);
        historyEvent.setHistoryEventTypeKey(nID_HistoryEventType);
        historyEvent.setEventNameCustom(sEventName_Custom);
        historyEvent.setsMessage(sMessage);
        historyEvent.setDate(new Date());
        return saveOrUpdate(historyEvent).getId();
    }
}
