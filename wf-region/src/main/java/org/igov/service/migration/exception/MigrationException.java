package org.igov.service.migration.exception;

/**
 * Created by dpekach on 18.06.17.
 */
public class MigrationException extends RuntimeException {

    public MigrationException() {
        super();
    }

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }

    protected MigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
