package org.igov.log;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerTest {

    @Test
    @Ignore(value = "Last assertion isn't implemented yet")
    public void swapNonExceptionArguments(){
        Integer userId      = 101;
        String name         = "Max";
        String description  = "He is a general user of WF base application";
        String expectedLog  = "Got (user={}, name={}, description={})";

        TestLogsConsumer consumer = new TestLogsConsumer();
        Logger log = Logger.getLog(LoggerTest.class, consumer);

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
        Logger log = Logger.getLog(LoggerTest.class);

        LogResponse response = log.errorHTTP(status, header, msg);

        assertNotNull(response);
        assertEquals("Status is wrong", status, response.status());
        assertEquals("Header is wrong", header, response.header());
        assertEquals("Message is wrong", expectedMsg, response.message());
        assertEquals("Rwa message is wrong", msg, response.rawMessage());
        assertTrue("Arguments aren't empty", response.arguments().size() == 0);
    }
}
