package org.igov.service.controller;

import com.google.gwt.editor.client.Editor.Ignore;
import org.igov.io.Log;
import org.igov.io.Log.LogStatus;
import org.igov.model.action.task.core.entity.ErrorResponse;
import org.igov.service.exception.*;
import org.igov.util.JSON.JsonRestUtils;
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

/**
 * Created by diver on 4/6/15.
 */
@Controller
@ControllerAdvice
public class ExceptionCommonController {

    public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERR";
    public static final String BUSINESS_ERROR_CODE = "BUSINESS_ERR";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionCommonController.class);

    /* ========= */
    @ExceptionHandler({CRCInvalidException.class, EntityNotFoundException.class, RecordNotFoundException.class, TaskAlreadyUnboundException.class})
    @ResponseBody
    public ResponseEntity<String> handleAccessException(Exception e) throws CommonServiceException {
        return catchCommonServiceException(new CommonServiceException(
                ExceptionCommonController.BUSINESS_ERROR_CODE,
                e.getMessage(), e,
                HttpStatus.FORBIDDEN));
    }
    
    
    
    @ExceptionHandler(value = CommonServiceException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchCommonServiceException(CommonServiceException oException) {
                //LOG.error("Error:{}. REST API Exception", oException.getMessage());
                //LOG.trace("FAIL:", oException);
                new Log(oException, LOG)
                ._Case("REST_API")
                ._Head("REST API Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(oException.getHttpStatus().value())
                ._StatusHTTP(oException.getHttpStatus().value())
                ._LogTrace()
                .save()
                ;
        return JsonRestUtils.toJsonResponse(oException.getHttpStatus(),
                new ErrorResponse(oException.getErrorCode(), oException.getMessage()));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchRuntimeException(RuntimeException oException) {
                //LOG.error("Error:{}. REST System Exception", oException.getMessage());
                //LOG.trace("FAIL:", oException);
                new Log(oException, LOG)
                ._Case("REST_System")
                ._Head("REST System Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._LogTrace()
                .save()
                ;
        if(oException.getMessage() != null && oException.getMessage().contains("act_fk_tskass_task")){
            return JsonRestUtils.toJsonResponse("");
        } else{
            return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(SYSTEM_ERROR_CODE, oException.getMessage()));
        }
    }

    @ExceptionHandler(value = Exception.class)
    public
    @ResponseBody
    ResponseEntity<String> catchException(Exception oException) {
                //LOG.error("Error:{}. REST Exception", oException.getMessage());
                //LOG.trace("FAIL:", oException);
                new Log(oException, LOG)
                ._Case("REST_Unknown")
                ._Head("REST Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._LogTrace()
                .save()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(SYSTEM_ERROR_CODE, oException.getMessage()));
    }

    @Ignore
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchMissingServletRequestParameterException(
            MissingServletRequestParameterException oException) {
                //LOG.error("Error:{}. REST Wrong Input Parameters Exception", oException.getMessage());
                //LOG.trace("FAIL:", oException);
                new Log(oException, LOG)
                ._Case("REST_FailArgs")
                ._Head("REST Wrong Input Parameters Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.BAD_REQUEST.value())
                ._LogTrace()
                .save()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.BAD_REQUEST,
                new ErrorResponse(BUSINESS_ERROR_CODE, oException.getMessage()));
    }

    @Ignore
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchHttpMessageNotReadableException(HttpMessageNotReadableException oException) {
                //LOG.error("Error:{}. REST Wrong Input Body Exception", oException.getMessage());
                //LOG.trace("FAIL:", oException);
                new Log(oException, LOG)
                ._Case("REST_FailBody")
                ._Head("REST Wrong Input Body Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.BAD_REQUEST.value())
                ._LogTrace()
                .save()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.BAD_REQUEST,
                new ErrorResponse(BUSINESS_ERROR_CODE, oException.getMessage()));
    }
}
