package org.igov.service.controller;

import org.igov.service.interceptor.exception.ActivitiRestException;
import com.google.gwt.editor.client.Editor.Ignore;
import org.igov.debug.Log;
import org.igov.debug.Log.LogStatus;
import org.igov.service.entity.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.util.convert.JsonRestUtils;

/**
 * Created by diver on 4/6/15.
 */
@Controller
@ControllerAdvice
public class ActivitiExceptionController {

    public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERR";
    public static final String BUSINESS_ERROR_CODE = "BUSINESS_ERR";
    private static final Logger oLog = LoggerFactory.getLogger(ActivitiExceptionController.class);

    @ExceptionHandler(value = ActivitiRestException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchActivitiRestException(ActivitiRestException exception) {
        oLog.error("REST API Exception: " + exception.getMessage(), exception);
        new Log(this.getClass(), exception)
                ._Head("REST API Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(exception.getHttpStatus().value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(exception.getHttpStatus(),
                new ErrorResponse(exception.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchRuntimeException(RuntimeException exception) {
        oLog.error("REST System Exception: " + exception.getMessage(), exception);
        new Log(this.getClass(), exception)
                ._Head("REST System Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(SYSTEM_ERROR_CODE, exception.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public
    @ResponseBody
    ResponseEntity<String> catchException(Exception exception) {
        oLog.error("REST Exception: " + exception.getMessage(), exception);
        new Log(this.getClass(), exception)
                ._Head("REST Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(BUSINESS_ERROR_CODE, exception.getMessage()));
    }

    @Ignore
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        oLog.error("REST Wrong Input Parameters Exception: " + exception.getMessage(), exception);
        new Log(this.getClass(), exception)
                ._Head("REST Wrong Input Parameters Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.BAD_REQUEST.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.BAD_REQUEST,
                new ErrorResponse(BUSINESS_ERROR_CODE, exception.getMessage()));
    }

    @Ignore
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        oLog.error("REST Wrong Input Body Exception: " + exception.getMessage(), exception);
        new Log(this.getClass(), exception)
                ._Head("REST Wrong Input Body Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.BAD_REQUEST.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.BAD_REQUEST,
                new ErrorResponse(BUSINESS_ERROR_CODE, exception.getMessage()));
    }
}
