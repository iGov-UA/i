package org.igov.io.log;

import org.igov.io.log.http.LogResponse;

import java.util.Set;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

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
    LogResponse errorHTTP(int status, String header, String msg, Object ...params);

    Logger _trace(String msg);
    Logger _debug(String msg);
    Logger _info (String msg);
    Logger _warn (String msg);
    Logger _error(String msg, Exception exp);

    Logger _cut(int length);

    /** Send log message to appender and notify consumers */
    String send();

    /** Add a parameter into log message */
    Logger _p(String parameter, Object value);

    /** Add all parameters to message, send to appender and notify consumers */
    String params(Object ...args);



    /**
     * @return full SLF4J log message, where all `{}` symbols will be replaced to corresponding parameters.
     **/
    default String fullMessage(String msg, Object... args) {
        return arrayFormat(msg, args).getMessage();
    }

    default void notifyConsumers(Set<Consumer> consumers, String msg) {
        for(Consumer consumer : consumers)
            consumer.consume(msg);
    }
}