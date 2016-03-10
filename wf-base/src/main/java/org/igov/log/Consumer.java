package org.igov.log;

/**
 * @author dgroup
 * @since  08.01.2016
 */
public interface Consumer {
    void consume(String msg, Object... args);
}
