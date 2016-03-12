package org.igov.log;

import org.igov.log.http.LogResponse;
import org.slf4j.helpers.MessageFormatter;

import java.util.Set;

/**
 * Igov decorator on SLF4J
 *
 * Such "unusual" api is a requirement. Please don't blame me if you don't like such approach :)
 *
 * Restriction for now:
 *  - you can't disable `method name` in final log message via log4j.properties or logback.xml
 *
 * @author dgroup
 * @since  08.01.2016
 */
public interface Logger {

    // SLF4J approach
    String trace(String msg, Object ...args);
    String debug(String msg, Object ...args);
    String info (String msg, Object ...args);
    String warn (String msg, Object ...args);
    String error(String msg, Object ...args);


    // Igov custom stuff
    LogResponse errorHTTP(int status, String header, String msg);

    Logger Trace(String msg);
    Logger Debug(String msg);
    Logger Info (String msg);
    Logger Warn (String msg);
    Logger Error(String msg, Exception exp);

    Logger Cut(int length);

    /** Send log message to appender and notify consumers */
    String Send();

    /** Add a parameter into log message */
    Logger P(String parameter, Object value);

    /** Add all parameters to message, send to appender and notify consumers */
    String Params(Object ...args);



    /**
     * @return full SLF4J log message, where all `{}` symbols will be replaced to corresponding parameters.
     **/
    default String fullMsg(String msg, Object... args) {
        return MessageFormatter.arrayFormat(msg, args).getMessage();
    }

    default void notifyConsumers(Set<Consumer> consumers, String msg) {
        for(Consumer consumer : consumers)
            consumer.consume(msg);
    }
}