package org.igov.log;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerImpl implements Logger {

    private static final int CALL_STACK_DEPTH = 3;
    private final org.slf4j.Logger log;
    private final Set<Consumer> consumers;


    private LoggerImpl(Class<?> clazz, Set<Consumer> consumers) {
        this.log = LoggerFactory.getLogger(clazz);
        this.consumers = consumers;
    }



    public String trace(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            () ->log.trace(withCalledMethod(msg), args));
    }

    public String info(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            () ->log.debug(withCalledMethod(msg), args));
    }

    public String warn(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            () ->log.warn(withCalledMethod(msg), args));
    }

    public String error(Exception exp, String msg, Object ... args) {
        String message = fullMsg(msg, args);
        return newRecord( message,
            () ->log.error(withCalledMethod(message), exp));
    }

    /**
     * This is
     **/
    private String newRecord(String msg, Log log) {
        notNull(msg, "Log pattern cannot be a null");
        notifyConsumers(consumers, msg);
        log.doRecord();
        return msg;
    }


    /**
     * @return full SLF4J log message.
     * All `{}` will be replaced to corresponding parameters.
     **/
    private String fullMsg(String msg, Object[] args) {
        return MessageFormatter.arrayFormat(msg, args).getMessage();
    }


    private String withCalledMethod(String msg) {
        return getMethodName() + " " + msg;
    }





    private static void notifyConsumers(Set<Consumer> consumers, String msg) {
        for(Consumer consumer : consumers)
            consumer.consume(msg);
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




    public static Logger getLog(Class<?> clazz) {
        return new LoggerImpl(clazz, Collections.<Consumer>emptySet());
    }

    public static Logger getLog(Class<?> clazz, Consumer consumer) {
        return new LoggerImpl(clazz, singleton(consumer));
    }

    public static Logger getLog(Class<?> clazz, Set<Consumer> consumers) {
        return new LoggerImpl(clazz, consumers);
    }



    /** Encapsulate particular operation (trace, debug, info, etc) */
    interface Log { void doRecord(); }
}