package org.igov.io.log;

import org.igov.io.log.http.LogResponse;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author dgroup
 * @since 08.01.2016
 */
public class LoggerTest {

    @Test
    public void logErrorHttp() {
        int status = 404;
        String header = "header";
        String msg = "Houston we have a problem";

        String expectedMsg = status + ":" + header + " " + msg;
        Logger log = LoggerIgov.getLog(LoggerTest.class);

        LogResponse response = log.errorHTTP(status, header, msg);

        assertNotNull(response);
        assertEquals("Status is wrong", status, response.status());
        assertEquals("Header is wrong", header, response.header());
        assertEquals("Message is wrong", expectedMsg, response.message());
        assertEquals("Raw message is wrong", msg, response.rawMessage());
        assertTrue("Arguments aren't empty", response.arguments().size() == 0);
    }

    @Test
    public void logErrorHttpWithParams() {
        String msgPattern = "User id={}, name={}";
        Object[] params = {100, "Tom"};
        String fullMsg = arrayFormat(msgPattern, params).getMessage();
        String httpHeader = "length=500;type=text";
        int httpStatus = 500; // Shit happens, mate
        String expectedMsg = httpStatus + ":" + httpHeader + " User id=100, name=Tom";

        Logger log = LoggerIgov.getLog(LoggerTest.class);
        LogResponse resp = log.errorHTTP(httpStatus, httpHeader, msgPattern,  /* vararg */ params);

        assertNotNull(resp);
        assertEquals("Status is wrong", httpStatus, resp.status());
        assertEquals("Header is wrong", httpHeader, resp.header());
        assertEquals("Message is wrong", expectedMsg, resp.message());
        assertEquals("Raw message is wrong", fullMsg, resp.rawMessage());
        assertThat("Arguments aren't empty", resp.arguments(), hasSize(params.length));
    }

    @Test
    @SuppressWarnings("PMD") // i know that there is a duplication of String, thank you, Cap (PMD)
    public void wellFormedStringSLF4J() {
        assertEquals("Hi there.", format("Hi {}.", "there").getMessage());
        assertEquals("Hi [there].", format("Hi {}.", new Object[]{"there"}).getMessage());
        assertEquals("Hi there.", arrayFormat("Hi {}.", new Object[]{"there"}).getMessage());

        Logger log = LoggerIgov.getLog(LoggerTest.class);
        assertEquals("Hi there.", log.fullMessage("Hi {}.", "there"));
    }

    @Test
    public void generalLogOperation() {
        Logger log = LoggerIgov.getLog(LoggerTest.class);

        String msg = log._trace("Got")
                ._p("userId", 200)
                ._p("name", "Tom")
                .send();

        assertEquals("Got userId=200 name=Tom", msg);
    }

    @Test
    public void slf4jApproach() {
        Logger log = LoggerIgov.getLog(LoggerTest.class);
        String msg = log.trace("Hi `{}`. My name is {} and i'll save you!", "Earth", "Doctor");
        assertEquals("Hi `Earth`. My name is Doctor and i'll save you!", msg);
    }


    @Test
    public void cut() {
        // Prerequisites
        String text = "User name=Daniel";
        int maxLength = text.length() - 3;
        assertEquals("User name=Dan", substring(text, 0, maxLength));

        // Cut last 3 symbols
        Logger log = LoggerIgov.getLog(LoggerTest.class);
        String msg = log._trace("User")
                ._p("name", "Daniel")    // User name=Daniel
                ._cut(maxLength)
                .send();

        assertThat("Last 3 symbols were cut from log message",
                msg, equalTo("User name=Dan"));
    }

    @Test
    public void consumerSLF4J() {
        TestLogsConsumer consumer1 = new TestLogsConsumer();
        TestLogsConsumer consumer2 = new TestLogsConsumer();
        TestLogsConsumer consumer3 = new TestLogsConsumer();
        Logger log = LoggerIgov.getLog(LoggerTest.class, consumer1, consumer2, consumer3);

        List<String> theLogs = asList(
                log.info("Life is {}.", "good"),
                log.warn("Are you sure? {}!", "Yes")
        );

        assertEquals(consumer1.firstLogMessage(), "Life is good.");

        assertThat("First consumer got all logs", consumer1.logs(), hasSize(2));
        assertThat("Second consumer got all logs", consumer2.logs(), hasSize(2));
        assertThat("Third consumer got all logs", consumer3.logs(), hasSize(2));

        assertEquals(consumer1.logs(), theLogs);
        assertEquals(consumer2.logs(), theLogs);
        assertEquals(consumer3.logs(), theLogs);
    }

    @Test
    public void consumerIgov() {
        TestLogsConsumer consumer1 = new TestLogsConsumer();
        TestLogsConsumer consumer2 = new TestLogsConsumer();
        Logger log = LoggerIgov.getLog(LoggerTest.class, consumer1, consumer2);

        List<String> theLogs = asList(
                log._info("Got")._p("id", 100)._p("name", "Tom").send(),
                log._warn("Processing was finished")._p("result", "success").send()
        );

        assertThat("First consumer contains 2 log records", consumer1.logs(), hasSize(2));
        assertThat("First consumer contains all logs", consumer1.logs(), equalTo(theLogs));
        assertThat("First log record from the first consumer is correct",
                consumer1.firstLogMessage(), equalTo("Got id=100 name=Tom"));

        assertThat("Second consumer contains 2 log records", consumer2.logs(), hasSize(2));
        assertThat("Second consumer contains all logs", consumer2.logs(), equalTo(theLogs));
    }
}