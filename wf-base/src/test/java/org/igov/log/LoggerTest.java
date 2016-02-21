 package org.igov.log;

import org.igov.log.http.LogResponse;
import org.junit.Ignore;
import org.junit.Test;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.slf4j.helpers.MessageFormatter.format;

import static org.junit.Assert.*;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerTest {
    private static final String SL4J_MARKER = "{}";

    @Test
    @Ignore(value = "Last assertion isn't implemented yet")
    public void swapNonExceptionArguments(){
        Integer userId      = 101;
        String name         = "Max";
        String description  = "He is a general user of WF base application";
        String expectedLog  = "Got (user={}, name={}, description={})";

        TestLogsConsumer consumer = new TestLogsConsumer();
        Logger log = LoggerImpl.getLog(LoggerTest.class, consumer);

        log.info("Got", userId, name, description);

        assertTrue("One record should be present", consumer.logs().size() == 1);
        assertEquals("Pattern aren't match", expectedLog, consumer.firstLogMessage());
    }

    @Test
    public void logErrorHttp(){
        int status      = 404;
        String header   = "header";
        String msg      = "Houston we have a problem";

        String expectedMsg = status +":"+ header +" "+ msg;
        Logger log = LoggerImpl.getLog(LoggerTest.class);

        LogResponse response = log.errorHTTP(status, header, msg);

        assertNotNull(response);
        assertEquals("Status is wrong", status, response.status());
        assertEquals("Header is wrong", header, response.header());
        assertEquals("Message is wrong", expectedMsg, response.message());
        assertEquals("Rwa message is wrong", msg, response.rawMessage());
        assertTrue("Arguments aren't empty", response.arguments().size() == 0);
    }

    @Test
    public void wellFormedString(){
        String msg = "My name is ";
        String person = "Vova";
        String expectedMessage = msg + person;

        Logger log = LoggerImpl.getLog(LoggerTest.class);

        assertEquals(expectedMessage, log.info(msg + SL4J_MARKER, person));
    }

    @Test
    public void wellFormedStringSLF4J (){
        assertEquals("Hi there.",   format("Hi {}.", "there").getMessage() );
        assertEquals("Hi [there].", format("Hi {}.", new Object[]{"there"}).getMessage() );
        assertEquals("Hi there.",   arrayFormat("Hi {}.", new Object[]{"there"}).getMessage() );
    }
}
