package org.igov.log;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerTest {

    @Test @Ignore(value = "Last assertion isn't implemented yet")
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

}
