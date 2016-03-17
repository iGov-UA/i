package org.igov.model.action.event;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.EntityDaoQueriesTest;
import org.igov.model.document.Document;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: kr110666kai
 * 
 * Тест для проверки работы с сущностями HistoryEvent
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/dao-test-context.xml")
public class HistoryEventTest {
    private static final Log LOG = LogFactory.getLog(HistoryEventTest.class);

    @Autowired
    private HistoryEventDao historyEventDao;

    private final static Long testHistoryEvent_nID_Empty = 11L;

    private final static Long testHistoryEvent_nID_Full = 12L;
    private final static Long testHistoryEvent_Service_nID_Full = 2L;
    private final static Long testDoc_nID_Full = 7L;

    private final static Long testSubject_nID = 10L;
    private final static Long testSubject_nID_Set = 11L;

    @Ignore
    @Test
    public void getHistoryEvent() {

	// Проверяем HistoryEvent с данными
	String msgError = "Ошибка получения HistoryEvent по id=" + testHistoryEvent_nID_Full;

	HistoryEvent historyEvent = historyEventDao.findByIdExpected(testHistoryEvent_nID_Full);
	Assert.assertNotNull(msgError, historyEvent);

	HistoryEvent_Service historyEvent_Service = historyEvent.getoHistoryEvent_Service();
	Assert.assertNotNull(msgError, historyEvent_Service);
	Assert.assertEquals(msgError, testHistoryEvent_Service_nID_Full, historyEvent_Service.getId());

	Document document = historyEvent.getoDocument();
	Assert.assertNotNull(msgError, document);
	Assert.assertEquals(msgError, testDoc_nID_Full, document.getId());

	// Проверяем HistoryEvent без данных
	msgError = msgError + testHistoryEvent_nID_Empty;

	historyEvent = historyEventDao.findByIdExpected(testHistoryEvent_nID_Empty);
	Assert.assertNotNull(msgError, historyEvent);

	historyEvent_Service = historyEvent.getoHistoryEvent_Service();
	Assert.assertNull(msgError, historyEvent_Service);

	document = historyEvent.getoDocument();
	Assert.assertNull(msgError, document);

	Long nHistoryEvent_Id = null;
	try {
	    nHistoryEvent_Id = historyEventDao.setHistoryEvent(testSubject_nID_Set, 6L, "event custom set",
		    "message set", null, testDoc_nID_Full);
	    Assert.assertNotNull("Ошибка сохранения данных HistoryEvent", nHistoryEvent_Id);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	historyEvent = historyEventDao.findByIdExpected(nHistoryEvent_Id);
	Assert.assertNotNull("Ошибка сохранения данных HistoryEvent", historyEvent);
	Assert.assertNotNull("Ошибка сохранения данных HistoryEvent", historyEvent.getoDocument());
	Assert.assertEquals(msgError, testDoc_nID_Full, historyEvent.getoDocument().getId());
    }

    // Тест проверяет количество строк попадающих под кретерий
    // если сработал скорее всего, проблема из-за изменений в файле
    // HistoryEvent.csv
    // @Ignore
    @Ignore
    @Test
    public void getHistoryEvents() {
	getHistoryEventsBySubject(testSubject_nID, false, 20);
	getHistoryEventsBySubject(testSubject_nID, true, 11);
    }

    private void getHistoryEventsBySubject(Long nID_Subject, boolean bGrouped, int retCount) {
	List<HistoryEvent> historyEvents = historyEventDao.getHistoryEvents(nID_Subject, bGrouped);
	Assert.assertNotNull("Ошибка получения historyEvents по nID_Subject=" + nID_Subject, historyEvents);
	Assert.assertEquals(String.format("Ошибка получения данных по кретериям: nID_Subject=%d, bGrouped=%b.",
		nID_Subject, bGrouped), retCount, historyEvents.size());

	LOG.info(String.format("Согласно кретериям: nID_Subject=%d, bGrouped=%b, найдено строк %d", nID_Subject,
		bGrouped, historyEvents.size()));

	if (LOG.isDebugEnabled()) {
	    String sHesId;
	    String sDocId;
	    for (HistoryEvent historyEvent : historyEvents) {

		sHesId = "null.null";
		sDocId = "null.null";

		HistoryEvent_Service historyEvent_Service = historyEvent.getoHistoryEvent_Service();
		if (historyEvent_Service != null) {
		    sHesId = historyEvent_Service.getId().toString();
		}

		Document document = historyEvent.getoDocument();
		if (document != null) {
		    sDocId = document.getId().toString();
		}

		LOG.info(String.format(" HistoryEvent.nID=%d, historyEvent_Service.nID=%s, Document.nID=%s, sDate=%s",
			historyEvent.getId(), sHesId, sDocId, historyEvent.getDate()));

	    }
	}
    }

}
