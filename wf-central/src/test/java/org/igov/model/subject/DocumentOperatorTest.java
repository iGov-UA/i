package org.igov.model.subject;

import org.igov.model.document.DocumentOperator_SubjectOrgan;
import org.igov.model.document.access.DocumentAccess;
import org.igov.model.document.Document;
import net.sf.brunneng.jom.annotations.Skip;
import org.igov.model.document.Document;
import org.igov.model.document.access.DocumentAccess;
import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.igov.model.document.DocumentDao;
import org.igov.service.business.document.access.handler.DocumentAccessHandler;
import org.igov.service.business.document.access.handler.DocumentAccessHandler;
import org.igov.service.business.document.access.handler.DocumentAccessHandler_IGov;
import org.igov.service.business.document.access.handler.DocumentAccessHandler_IGov;
import org.igov.model.document.DocumentDao;
import org.igov.service.business.document.access.handler.HandlerFactory;
import org.igov.service.business.document.access.handler.HandlerFactory;
import org.igov.service.exception.HandlerNotFoundException;
import org.igov.service.exception.HandlerNotFoundException;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author dgroup
 * @since 28.06.15
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class DocumentOperatorTest {
    public static final Long DUMMY_OPERATOR_ID = 2L;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private HandlerFactory handlerFactory;

    @Test
    public void notNull() {
        assertNotNull(documentDao); // just test that Spring DI is working :)
    }

    @Ignore
    @Test
    public void buildHandlerForDummyOperator() {
        DocumentOperator_SubjectOrgan operator =
                documentDao.getOperator(DUMMY_OPERATOR_ID);

        assertNotNull("Operator not found", operator);

        DocumentAccessHandler handler = handlerFactory.buildHandlerFor(operator);
        assertNotNull("Unable to build handler", handler);
        assertTrue("Incorrect handler type", handler instanceof DocumentAccessHandler_IGov);

        DocumentAccess access = handler.setAccessCode("1").getAccess();
        assertNotNull("DocumentAccess not found", access);

        Document doc = documentDao.getDocument(access.getId());
        assertNotNull("Document not found", doc);
        assertNotNull("Document name is empty", doc.getName());
    }

    @Test(expected = HandlerNotFoundException.class)
    public void tryToBuildNonExistentHandler() {
        DocumentOperator_SubjectOrgan operator = documentDao.getOperator(DUMMY_OPERATOR_ID);
        operator.setsHandlerClass("non existent class name");
        handlerFactory.buildHandlerFor(operator);
        fail("Expected exception was missing");
    }

    @Test
    public void oneOperatorShouldBePresent() {
        assertTrue(1 <= documentDao.getAllOperators().size());
    }
}