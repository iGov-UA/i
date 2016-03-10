package org.igov.log;

import org.igov.log.http.LogResponse;
import org.igov.log.http.LogResponseImpl;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public class LoggerImpl implements Logger {

    private static final int CALL_STACK_DEPTH = 3;
    private final org.slf4j.Logger log;
    private final Set<Consumer> consumers;

    private String msg;
    private final Set<Customize> actions = new LinkedHashSet<>();


    private LoggerImpl(Class<?> clazz, Set<Consumer> consumers) {
        this.log = LoggerFactory.getLogger(clazz);
        this.consumers = consumers;
    }



    public String trace(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.trace(withCalledMethod(msg), args));
    }

    public String debug(String msg, Object... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.debug(withCalledMethod(msg), args));
    }

    public String info(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.info(withCalledMethod(msg), args));
    }

    public String warn(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.warn(withCalledMethod(msg), args));
    }

    public String error(String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.error(withCalledMethod(msg), args));
    }

    public String error(String msg, Exception exp) {
        log.error(msg, exp);
        return msg;
    }

    public String error(Exception exp, String msg, Object ... args) {
        return newRecord( fullMsg(msg, args),
            ()-> log.error(withCalledMethod(msg), exp));
    }



    public LogResponse errorHTTP(int status, String header, String msg) {
        return new LogResponseImpl(status, header, msg, emptyList());
    }


    public Logger Trace(String msg) {
        this.msg = msg;
        return this;
    }

    public Logger Debug(String msg) {
        this.msg = msg;
        return this;
    }

    public Logger Info(String msg) {
        this.msg = msg;
        return this;
    }

    public Logger Warn(String msg) {
        this.msg = msg;
        return this;
    }

    public Logger Error(String msg) {
        this.msg = msg;
        return this;
    }

    public Logger Cut(int length) {
        isTrue (length >= 0, "Length can't be a negative.");
        actions.add( e->substring(e, 0, length) );
        return this;
    }


    public String Send() {
        for(Customize action : actions)
            msg = action.customize(msg);
        return msg;
    }


    public Logger P(String parameter, Object value) {
        notNull(parameter, "Parameter key can't be a null");
        msg += " "+ parameter +"="+ value;
        return this;
    }


    public String Params(Object... args) {
        // TODO send msg to appender, notify consumers
        return fullMsg(msg, args);
    }



    String newRecord(String msg, Log log) {
        notNull(msg, "Log pattern cannot be a null");
        notifyConsumers(consumers, msg);
        log.doRecord();
        return msg;
    }


    private static String withCalledMethod(String msg) {
        return getMethodName() + " " + msg;
    }
    /**
     * Get the method name for a depth in call stack.
     */
    private static String getMethodName()
    {
        return Thread.currentThread()
                .getStackTrace()[CALL_STACK_DEPTH]
                .getMethodName();
    }




    public static Logger getLog(Class<?> clazz) {
        return getLog(clazz, Collections.<Consumer>emptySet());
    }

    public static Logger getLog(Class<?> clazz, Consumer consumer) {
        return new LoggerImpl(clazz, singleton(consumer));
    }

    public static Logger getLog(Class<?> clazz, Set<Consumer> consumers) {
        return new LoggerImpl(clazz, consumers);
    }



    /** Encapsulate particular operation (trace, debug, info, etc) */
    private interface Log { void doRecord(); }

    /** Encapsulate particular customization of log message */
    private interface Customize { String customize(String msg); }
}