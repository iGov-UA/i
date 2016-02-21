package org.igov.log.http;

import java.util.Collection;
import java.util.Objects;

/**
 * @author dgroup
 * @since  10.01.2016
 */
public class LogResponseImpl implements LogResponse {
    private int status;
    private String header;
    private String message;
    private Collection<Object> arguments;

    public LogResponseImpl(int status, String header, String message, Collection<Object> arguments) {
        this.status = status;
        this.header = header;
        this.message = message;
        this.arguments = arguments;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String header() {
        return header;
    }

    @Override
    public String message() {
        return status +":"+ header +" "+ message;
    }

    @Override
    public String rawMessage() {
        return message;
    }

    @Override
    public Collection<Object> arguments() {
        return arguments;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, header, message, arguments);
    }
}