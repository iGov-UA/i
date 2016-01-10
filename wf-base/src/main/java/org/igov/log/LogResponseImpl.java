package org.igov.log;

import java.util.Collection;

/**
 * @author dgroup
 * @since 10.01.2016
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
}
