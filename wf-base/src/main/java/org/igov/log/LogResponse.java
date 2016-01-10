package org.igov.log;

import java.util.Collection;

/**
 * @author dgroup
 * @since  10.01.2016
 */
public interface LogResponse {
    int status();
    String header();
    String message();
    String rawMessage();
    Collection<Object> arguments();
}