 package org.igov.io.log;

 import org.igov.io.log.http.LogResponse;
 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.List;

 import static org.apache.commons.lang3.StringUtils.substring;
 import static org.junit.Assert.*;
 import static org.slf4j.helpers.MessageFormatter.arrayFormat;
 import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerTest {

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
    public void logErrorHttpWithParams(){
        String msgPattern   = "User id={}, name={}"; Object [] params = {100, "Tom"};
        String fullMsg      = arrayFormat(msgPattern, params).getMessage();
        String httpHeader   = "length=500;type=text";
        int httpStatus      = 500; // Shit happens, mate
        String expectedMsg  = httpStatus+":"+httpHeader+" User id=100, name=Tom";

        Logger log          = LoggerImpl.getLog(LoggerTest.class);
        LogResponse resp    = log.errorHTTP(httpStatus, httpHeader, msgPattern,  /* vararg */ params);

        assertNotNull(resp);
        assertEquals("Status is wrong", httpStatus, resp.status());
        assertEquals("Header is wrong", httpHeader, resp.header());
        assertEquals("Message is wrong", expectedMsg, resp.message());
        assertEquals("Raw message is wrong", fullMsg, resp.rawMessage());
        assertTrue("Arguments aren't empty", resp.arguments().size() == params.length);
    }

    @Test
    @SuppressWarnings("PMD") // i know that there is a duplication of String, thank you, Cap (PMD)
    public void wellFormedStringSLF4J (){
        assertEquals("Hi there.",   format("Hi {}.", "there").getMessage() );
        assertEquals("Hi [there].", format("Hi {}.", new Object[]{"there"}).getMessage() );
        assertEquals("Hi there.",   arrayFormat("Hi {}.", new Object[]{"there"}).getMessage() );

        Logger log = LoggerImpl.getLog(LoggerTest.class);
        assertEquals("Hi there.",   log.fullMessage("Hi {}.", "there"));
    }

    @Test
    public void generalLogOperation(){
        Logger log = LoggerImpl.getLog(LoggerTest.class);

        String msg = log._trace("Got")
            ._p("userId", 200)
            ._p("name", "Tom")
            .send();

        assertEquals("Got userId=200 name=Tom", msg);
    }

    @Test
    public void slf4jApproach(){
        Logger log = LoggerImpl.getLog(LoggerTest.class);
        String msg = log.trace("Hi `{}`. My name is {} and i'll save you!", "Earth", "Doctor");
        assertEquals("Hi `Earth`. My name is Doctor and i'll save you!", msg);
    }


    @Test
    public void cut(){
        // Prerequisites
        String text= "User name=Daniel";  int maxLength = text.length()-3;
        assertEquals("User name=Dan",   substring(text, 0, maxLength));

        // Cut last 3 symbols
        Logger log = LoggerImpl.getLog(LoggerTest.class);
        String msg = log._trace("User")
            ._p("name", "Daniel")    // User name=Daniel
            ._cut(maxLength)
            .send();

        assertEquals("Cut operation was broken", "User name=Dan", msg);
    }

    @Test
    public void consumerSLF4J(){
        TestLogsConsumer consumer1 = new TestLogsConsumer();
        TestLogsConsumer consumer2 = new TestLogsConsumer();
        TestLogsConsumer consumer3 = new TestLogsConsumer();
        Logger log = LoggerImpl.getLog(LoggerTest.class, consumer1, consumer2, consumer3);

        List<String> theLogs = new ArrayList<>();
        theLogs.add( log.info("Life is {}.", "good")        );
        theLogs.add( log.warn("Are you sure? {}!", "Yes")   );

        assertTrue("First consumer has wrong amount of logs", consumer1.logs().size() == 2);
        assertEquals(consumer1.firstLogMessage(), "Life is good.");

        assertTrue("Second consumer has wrong amount of logs",  consumer2.logs().size() == 2);
        assertTrue("Third consumer has wrong amount of logs",   consumer3.logs().size() == 2);

        assertEquals(consumer1.logs(), theLogs);
        assertEquals(consumer2.logs(), theLogs);
        assertEquals(consumer3.logs(), theLogs);
    }

    @Test
    public void consumerIgov(){
        TestLogsConsumer consumer1 = new TestLogsConsumer();
        TestLogsConsumer consumer2 = new TestLogsConsumer();
        Logger log = LoggerImpl.getLog(LoggerTest.class, consumer1, consumer2);

        List<String> theLogs = new ArrayList<>();
        theLogs.add(
            log._info("Got")._p("id", 100)._p("name", "Tom").send()
        );
        theLogs.add(
            log._warn("Processing was finished")._p("result", "success").send()
        );

        assertTrue("First consumer has wrong amount of logs", consumer1.logs().size() == 2);
        assertEquals(consumer1.firstLogMessage(), "Got id=100 name=Tom");

        assertTrue("Second consumer has wrong amount of logs",  consumer2.logs().size() == 2);

        assertEquals(consumer1.logs(), theLogs);
        assertEquals(consumer2.logs(), theLogs);
    }

}