package org.igov.io.log;

/**
 * Each implementation should contain `equals` and `hash code`.
 * Otherwise, you will have a chance to receive hash collision
 *
 * @author dgroup
 * @since  08.01.2016
 */
public interface Consumer {
    void consume(String msg); 
}
