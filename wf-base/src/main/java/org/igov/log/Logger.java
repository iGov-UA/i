package org.igov.log;

import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class Logger {

    private static final int CALL_STACK_DEPTH = 3;
    private final org.slf4j.Logger log;
    private final Set<Consumer> consumers;


    private Logger(Class<?> clazz, Set<Consumer> consumers) {
        this.log = LoggerFactory.getLogger(clazz);
        this.consumers = consumers;
    }


    public static Logger getLog(Class<?> clazz) {
        return new Logger(clazz, Collections.<Consumer>emptySet());
    }

    public static Logger getLog(Class<?> clazz, Consumer consumer) {
        return new Logger(clazz, singleton(consumer));
    }

    public static Logger getLog(Class<?> clazz, Set<Consumer> consumers) {
        return new Logger(clazz, consumers);
    }



    public void info(String msg, Object ... args) {
        notNull(msg, "Log pattern cannot be a null");

        if (msg.contains("{}")) {
            // TODO handle annotation on params
            writeInfo(message(msg), args);

        } else
            writeInfo(getMethodName() + msg, args);
    }

    private void writeInfo(String msg, Object ... args){
        notifyConsumers(consumers, msg, args);
        log.info(msg, args);
    }


    private void notifyConsumers(Set<Consumer> consumers, String msg, Object... args) {
        for(Consumer consumer : consumers)
            consumer.consume(msg, args);
    }


    static String message(String msg) {
        return getMethodName() + msg;
    }

    /**
     * Get the method name for a depth in call stack.
     */
    static String getMethodName()
    {
        return Thread.currentThread()
                .getStackTrace()[CALL_STACK_DEPTH]
                .getMethodName();
    }

    public LogResponse errorHTTP(int status, String header, String msg, Object... args) {
        // TODO Check status
        notBlank(msg, "Message should be not blank");

        return new LogResponseImpl(status, header, msg, Arrays.asList(args));
    }
}