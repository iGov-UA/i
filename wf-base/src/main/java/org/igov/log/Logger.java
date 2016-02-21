package org.igov.log;

import org.igov.log.http.LogResponse;
import org.igov.log.http.LogResponseImpl;

import java.util.Arrays;

import static org.apache.commons.lang3.Validate.notBlank;

/**
 * @author dgroup
 * @since  21.02.16
 */
public interface Logger {

    String trace(String msg, Object ... args);
    String info (String msg, Object ... args);
    String warn (String msg, Object ... args);
    String error(Exception exp, String msg, Object ... args);

    public default LogResponse errorHTTP(int status, String header, String msg, Object... args) {
        // TODO Check status
        notBlank(msg, "Message should be not blank");

        return new LogResponseImpl(status, header, msg, Arrays.asList(args));
    }
}
