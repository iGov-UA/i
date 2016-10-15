package org.igov.service.exception;

/**
 * User: goodg_000
 * Date: 19.08.2015
 * Time: 21:03
 */
public class CRCInvalidException extends Exception {

    public CRCInvalidException() {
        super("CRC Error");
    }

    public CRCInvalidException(String message) {
        super(message);
    }
}
