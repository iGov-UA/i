package org.igov.io.log;

import com.google.common.collect.Sets;
import org.igov.io.log.http.LogResponse;
import org.igov.io.log.http.LogResponseImpl;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

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


    // SLF4J approach
    public String trace(String msg, Object ... args) {
        return trace(fullMessage(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String debug(String msg, Object... args) {
        return debug(fullMessage(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String info(String msg, Object ... args) {
        return info(fullMessage(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String warn(String msg, Object ... args) {
        return warn(fullMessage(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String error(String msg, Object ... args) {
        return error(fullMessage(msg, args), DEPTH_CALL_STACK_SLF4J);
    }

    public String error(String msg, Exception exp) {
        return error(msg, exp, DEPTH_CALL_STACK_SLF4J);
    }

    public String error(Exception exp, String msg, Object ... args) {
        return error(fullMessage(msg, args), exp, DEPTH_CALL_STACK_SLF4J);
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



    public LogResponse errorHTTP(int status, String header, String msg, Object ...params) {
        return new LogResponseImpl(status, header, msg, params);
    }


    // Igov custom stuff
    public Logger _trace(String msg) {
        this.msg = msg;
        task = ()-> trace(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger _debug(String msg) {
        this.msg = msg;
        task = ()-> debug(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger _info(String msg) {
        this.msg = msg;
        task = ()-> info(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger _warn(String msg) {
        this.msg = msg;
        task = ()-> warn(this.msg, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger _error(String msg, Exception exp) {
        this.msg = msg;
        task = ()-> error(this.msg, exp, DEPTH_CALL_STACK_IGOV);
        return this;
    }

    public Logger _cut(int length) {
        isTrue (length >= 0, "Length can't be a negative.");
        actions.add( e->substring(e, 0, length) );
        return this;
    }


    public String send() {
        for(Customize action : actions)
            msg = action.customize(msg);
        task.doRecord();
        return msg;
    }


    /**
     * Add to current message a new piece of message
     **/
    public Logger _p(String parameter, Object value) {
        notNull(parameter, "Parameter key can't be a null");
        msg += " "+ parameter +"="+ value;
        return this;
    }


    public String params(Object... args) {
        // TODO send msg to appender, notify consumers
        return fullMessage(msg, args);
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




    public static Logger getLog(Class<?> clazz, Set<Consumer> consumers) {
        return new LoggerImpl(clazz, consumers);
    }
    public static Logger getLog(Class<?> clazz, Consumer ... consumers) {
        return getLog(clazz, Sets.newHashSet(consumers));
    }



    /** Encapsulate particular operation (trace, debug, info, etc) */
    private interface Log { void doRecord(); }

    /** Encapsulate particular customization of log message */
    private interface Customize { String customize(String msg); }
}