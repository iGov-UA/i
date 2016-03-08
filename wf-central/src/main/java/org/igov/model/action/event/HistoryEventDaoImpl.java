package org.igov.model.action.event;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.Document;
import java.util.ArrayList;

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

    /**
     * ToDo переделать фильтр вывод списка при bGrouped=true на работу через HQL
     * 
     * Получение списка сущностей HistoryEvent по nID субъекта
     * Если bGrouped = false - выбираются все сущности для данного субъекта
     * если bGrouped = true, то в список попадают только уникальные сущности. Если сущности не уникальные, то из них отбирается только
     * одна с самым большим временем в поле sDate
     *  
     * Уникальность сущности определяется путем сравнения полей: oHistoryEvent_Service, oDocument
     * 
     * Алгоритм сравнения сущностей: 
     * - если поля oHistoryEvent_Service=null и oDocument=null- сущности разные 
     * - если oHistoryEvent_Service=null, а oDocument= не null -сравнение идет только по oDocument 
     * - если oHistoryEvent_Service=не null, а oDocument= null - савнение идет только по oHistoryEvent_Service 
     * - если oHistoryEvent_Service=не null и oDocument= не null - савнение идет и по oHistoryEvent_Service и по oDocument
     */
    @Override
    public List<HistoryEvent> getHistoryEvents(Long nID_Subject, boolean bGrouped) {

	List<HistoryEvent> historyEvents = new ArrayList<>();
	if (bGrouped) {

	    List<HistoryEvent> historyEventsNew = findAllByAttributeCriteria("subjectKey", nID_Subject)
		    .addOrder(Order.asc("oHistoryEvent_Service"))
		    .addOrder(Order.asc("oDocument"))
		    .addOrder(Order.desc("date"))
		    .list();

	    HistoryEvent historyEventOld = null;
	    for (HistoryEvent historyEventNew : historyEventsNew) {
		if (compHistoryEvent(historyEventOld, historyEventNew)) {
		    continue;
		} else {
		    historyEventOld = historyEventNew;
		    historyEvents.add(historyEventOld);		    
		}
	    }

	} else {
	    historyEvents = findAllByAttributeCriteria("subjectKey", nID_Subject).addOrder(Order.desc("date")).list();
	}

	if (historyEvents != null) {
	    for (HistoryEvent historyEvent : historyEvents) {
		if (!historyEvent.getHistoryEventTypeKey().equals(0L)) {
		    historyEvent.setEventNameCustom(
			    HistoryEventType.getById(historyEvent.getHistoryEventTypeKey()).getsName());
		}
	    }
	}

	return historyEvents;
    }

    /**
     * Сравнение сущностей HistoryEvent по полям oHistoryEvent_Service,
     * oDocument
     * 
     * Алгоритм сравнения: 
     * - если поля oHistoryEvent_Service=null и oDocument=null- сущности разные 
     * - если oHistoryEvent_Service=null, а oDocument= не null -сравнение идет только по oDocument 
     * - если oHistoryEvent_Service=не null, а oDocument= null - савнение идет только по oHistoryEvent_Service 
     * - если oHistoryEvent_Service=не null и oDocument= не null - савнение идет и по oHistoryEvent_Service и по oDocument
     */
    private boolean compHistoryEvent(HistoryEvent evold, HistoryEvent evnew) {
	if (evold == null ) {
	    return false;
	}
	HistoryEvent_Service hesNew = evnew.getoHistoryEvent_Service();
	Document docNew = evnew.getoDocument();

	if (hesNew == null && docNew == null) {
	    return false;
	}

	HistoryEvent_Service hesOld = evold.getoHistoryEvent_Service();
	Document docOld = evold.getoDocument();

	if (hesNew != null && docNew != null) {
	    if (hesOld == null || docOld == null) {
		return false;
	    }

	    if (hesNew.getId().equals(hesOld.getId()) && docNew.getId().equals(docOld.getId())) {
		return true;
	    } else {
		return false;
	    }
	} else if (hesNew != null) {
	    if ( hesOld != null && hesNew.getId().equals(hesOld.getId())) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    if (docOld != null && docNew.getId().equals(docOld.getId())) {
		return true;
	    } else {
		return false;
	    }
	}
    }

    @Override
    public Long setHistoryEvent(Long nID_Subject, Long nID_HistoryEventType, String sEventName_Custom, String sMessage, Long nID_HistoryEvent_Service, Long nID_Document )
            throws IOException {
        HistoryEvent historyEvent = new HistoryEvent();
        historyEvent.setSubjectKey(nID_Subject);
        historyEvent.setHistoryEventTypeKey(nID_HistoryEventType);
        historyEvent.setEventNameCustom(sEventName_Custom);
        historyEvent.setsMessage(sMessage);
        historyEvent.setDate(new Date());
        historyEvent.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
        historyEvent.setnID_Document(nID_Document);
        return saveOrUpdate(historyEvent).getId();
    }
}
