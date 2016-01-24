package org.igov.service.exception;

/**
 * User: goodg_000
 * Date: 19.08.2015
 * Time: 21:06
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException() {
        super("Record not found");
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}
