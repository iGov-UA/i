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

    private static final int DEPTH_CALL_STACK_SLF4J = 7;
    private static final int DEPTH_CALL_STACK_IGOV  = 9;

    private final org.slf4j.Logger log;
    private final Set<Consumer> consumers;

    private String msg;
    private final Set<Customize> actions = new LinkedHashSet<>();
    private Log task;


    private LoggerImpl(Class<?> clazz, Set<Consumer> consumers) {
        this.log = LoggerFactory.getLogger(clazz);
        this.consumers = consumers;
    }



    public String trace(String msg, Object ... args) {
        return trace(fullMsg(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String debug(String msg, Object... args) {
        return debug(fullMsg(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String info(String msg, Object ... args) {
        return info(fullMsg(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String warn(String msg, Object ... args) {
        return warn(fullMsg(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String error(String msg, Object ... args) {
        return error(fullMsg(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String error(String msg, Exception exp) {
        return error(msg, exp, DEPTH_CALL_STACK_SLF4J);
    }

    public String error(Exception exp, String msg, Object ... args) {
        return error(fullMsg(msg, args), exp, DEPTH_CALL_STACK_SLF4J);
    }



    private String trace(String msg, int stackDepth) {
        return newRecord(msg, ()-> log.trace(getMethodName(stackDepth)+" "+msg));
    }

    private String debug(String msg, int stackDepth) {
        return newRecord(msg, ()-> log.debug(getMethodName(stackDepth)+" "+msg));
    }

    private String info (String msg, int stackDepth) {
        return newRecord(msg, ()-> log.info(getMethodName(stackDepth)+" "+msg));
    }

    private String warn (String msg, int stackDepth) {
        return newRecord(msg, ()-> log.warn(getMethodName(stackDepth)+" "+msg));
    }

    private String error(String msg, int stackDepth) {
        return newRecord(msg, ()-> log.error(getMethodName(stackDepth)+" "+msg));
    }

    private String error(String msg, Exception exp, int stackDepth) {
        return newRecord(msg, ()-> log.error(getMethodName(stackDepth)+" "+msg, exp));
    }



    public LogResponse errorHTTP(int status, String header, String msg) {
        return new LogResponseImpl(status, header, msg, emptyList());
    }


    public Logger Trace(String msg) {
        this.msg = msg;
        task = ()-> trace(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger Debug(String msg) {
        this.msg = msg;
        task = ()-> debug(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger Info(String msg) {
        this.msg = msg;
        task = ()-> info(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger Warn(String msg) {
        this.msg = msg;
        task = ()-> warn(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger Error(String msg, Exception exp) {
        this.msg = msg;
        task = ()-> error(this.msg, exp, DEPTH_CALL_STACK_IGOV);
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
        task.doRecord();
        return msg;
    }


    /**
     * Add to current message a new piece of message
     **/
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




    /**
     * Get the method name for a depth in call stack.
     */
    private static String getMethodName(int depth)
    {
        return Thread.currentThread()
                .getStackTrace()[depth]
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