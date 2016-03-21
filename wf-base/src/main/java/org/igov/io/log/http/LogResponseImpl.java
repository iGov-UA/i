package org.igov.io.log.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Objects.hash;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;

/**
 * @author dgroup
 * @since  10.01.2016
 */
@SuppressWarnings("PMD")
public class LogResponseImpl implements LogResponse {

    @JsonProperty
    private final int status;

    @JsonProperty
    private final String header;

    @JsonProperty
    private final String message;

    @JsonProperty
    private final Collection<Object> arguments;

    public LogResponseImpl(int status, String header, String message, Object ...args) {
        this.status = status;
        this.header = header;
        this.message = arrayFormat(message, args).getMessage();
        this.arguments = asList(args);
    }

    public int status() {
        return status;
    }

    public String header() {
        return header;
    }

    public String message() {
        return status +":"+ header +" "+ message;
    }

    public String rawMessage() {
        return message;
    }

    public Collection<Object> arguments() {
        return arguments;
    }

    public int hashCode() {
        return hash(status, header, message, arguments);
    }

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