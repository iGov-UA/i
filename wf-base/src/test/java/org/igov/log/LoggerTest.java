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
        assertEquals("Raw message is wrong", msg, response.rawMessage());
        assertTrue("Arguments aren't empty", response.arguments().size() == 0);
    }


    @Test
    public void wellFormedStringSLF4J (){
        assertEquals("Hi there.",   format("Hi {}.", "there").getMessage() );
        assertEquals("Hi [there].", format("Hi {}.", new Object[]{"there"}).getMessage() );
        assertEquals("Hi there.",   arrayFormat("Hi {}.", new Object[]{"there"}).getMessage() );
    }

    @Test
    public void generalLogOperation(){
        Logger log = LoggerImpl.getLog(LoggerTest.class);

        String msg = log.Trace("Got")
            .P("userId", 200)
            .P("name", "Tom")
            .Send();

        assertEquals("Got userId=200 name=Tom", msg);
    }

    @Test
    public void slf4jApproach(){
        Logger log = LoggerImpl.getLog(LoggerTest.class);
        String msg = log.trace("Hi {}. My name is {} and i'll save you!", "Earth", "Doctor");
        assertEquals("Hi Earth. My name is Doctor and i'll save you!", msg);
    }
}