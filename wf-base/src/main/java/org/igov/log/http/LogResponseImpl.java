package org.igov.log.http;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Collection;

import static java.util.Objects.hash;

/**
 * @author dgroup
 * @since  10.01.2016
 */
public class LogResponseImpl implements LogResponse {
    private final int status;
    private final String header;
    private final String message;
    private final Collection<Object> arguments;

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
        return hash(status, header, message, arguments);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LogResponseImpl that = (LogResponseImpl) obj;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(header, that.header)
                .append(message, that.message)
                .append(arguments, that.arguments)
                .isEquals();
    }
}