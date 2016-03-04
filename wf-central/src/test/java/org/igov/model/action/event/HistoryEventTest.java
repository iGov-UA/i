package org.igov.model.action.event;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.EntityDaoQueriesTest;
import org.igov.model.document.Document;
import org.junit.Assert;
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
    private static final Log LOG = LogFactory.getLog(EntityDaoQueriesTest.class);

    @Autowired
    private HistoryEventDao historyEventDao;

    private final static Long testHistoryEvent_nID_Empty = 11L;

    private final static Long testHistoryEvent_nID_Full = 12L;
    private final static Long testHistoryEvent_Service_nID_Full = 2L;
    private final static Long testDoc_nID_Full = 7L;

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
    }

    @Test
    public void getHistoryEvents() {
	String msgError = "Ошибка получения HistoryEvent по id=" + testHistoryEvent_nID_Full;
	List<HistoryEvent> historyEvents = historyEventDao.getHistoryEvents(10L, true);
	Assert.assertNotNull(msgError, historyEvents);
	LOG.info(String.format("Всего попало под кретерии строк %d=", historyEvents.size()));

	if (LOG.isDebugEnabled()) {
	    for (HistoryEvent historyEvent : historyEvents) {
		LOG.info(String.format("  id=%d, subj_id=%d", historyEvent.getId(), historyEvent.getSubjectKey()));

		HistoryEvent_Service historyEvent_Service = historyEvent.getoHistoryEvent_Service();
		if (historyEvent_Service != null) {
		    LOG.info(String.format("  historyEvent_Service_nID=%d", historyEvent_Service.getId()));
		}

		Document document = historyEvent.getoDocument();
		if (historyEvent_Service != null) {
		    LOG.info(String.format("  document_nID=%d", document.getId()));
		}
	    }
	}

    }

}
