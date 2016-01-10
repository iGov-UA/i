package org.igov.service.exception;

import org.igov.service.exception.CommonServiceException;
import org.springframework.http.HttpStatus;

/**
 * Created by diver on 4/20/15.
 */
public class FileServiceIOException extends CommonServiceException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FileServiceIOException(Error error, String message) {
        super(error.getErrorCode(), message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    public enum Error {

        REDIS_ERROR("REDERR");

        private String errorCode;

        private Error(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

}
