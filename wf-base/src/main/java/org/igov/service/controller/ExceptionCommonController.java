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
    ResponseEntity<String> catchCommonServiceException(CommonServiceException exception) {
        int n=0;
        for(StackTraceElement oStackTraceElement : exception.getStackTrace()){
            String sPackage = oStackTraceElement.getClass().getPackage().getName();
            LOG.info("sPackage={}", sPackage);
            if(sPackage!=null && sPackage.startsWith("org.igov")){
                break;
            }
            n++;
        }
        if(n>=exception.getStackTrace().length){
            n=0;
        }
        Class oClass = exception.getStackTrace()[n].getClass();
        String sClass = exception.getStackTrace()[n].getClassName();
        String sFileName = exception.getStackTrace()[n].getFileName();
        String sMethod = exception.getStackTrace()[n].getMethodName();
        //LOG.error("Error:{}. REST API Exception", exception.getMessage());
        LOG.error("Error:{}. REST API Exception (sClass={},sMethod={},sFileName={})", exception.getMessage(),sClass,sMethod,sFileName);
        LOG.trace("FAIL:", exception);
        new Log(oClass!=null?oClass:this.getClass(), exception)//this.getClass()
                ._Head("REST API Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(exception.getHttpStatus().value())
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
        int n=0;
        for(StackTraceElement oStackTraceElement : exception.getStackTrace()){
            String sPackage = oStackTraceElement.getClass().getPackage().getName();
            LOG.info("sPackage={}", sPackage);
            if(sPackage!=null && sPackage.startsWith("org.igov")){
                break;
            }
            n++;
        }
        if(n>=exception.getStackTrace().length){
            n=0;
        }
        Class oClass = exception.getStackTrace()[n].getClass();
        String sClass = exception.getStackTrace()[n].getClassName();
        String sFileName = exception.getStackTrace()[n].getFileName();
        String sMethod = exception.getStackTrace()[n].getMethodName();
        //LOG.error("Error:{}. REST API Exception", exception.getMessage());
        LOG.error("Error:{}. REST System Exception (sClass={},sMethod={},sFileName={})", exception.getMessage(),sClass,sMethod,sFileName);
        LOG.trace("FAIL:", exception);
        new Log(oClass!=null?oClass:this.getClass(), exception)//this.getClass()
        //LOG.error("Error:{}. REST System Exception", exception.getMessage());
        //LOG.trace("FAIL:", exception);
        //new Log(this.getClass(), exception)
                ._Head("REST System Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._Send()
                ;
        if(exception.getMessage() != null && exception.getMessage().contains("act_fk_tskass_task")){
            return JsonRestUtils.toJsonResponse("");
        } else{
            return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(SYSTEM_ERROR_CODE, exception.getMessage()));
        }
    }

    @ExceptionHandler(value = Exception.class)
    public
    @ResponseBody
    ResponseEntity<String> catchException(Exception exception) {
        int n=0;
        for(StackTraceElement oStackTraceElement : exception.getStackTrace()){
            String sPackage = oStackTraceElement.getClass().getPackage().getName();
            LOG.info("sPackage={}", sPackage);
            if(sPackage!=null && sPackage.startsWith("org.igov")){
                break;
            }
            n++;
        }
        if(n>=exception.getStackTrace().length){
            n=0;
        }
        Class oClass = exception.getStackTrace()[n].getClass();
        String sClass = exception.getStackTrace()[n].getClassName();
        String sFileName = exception.getStackTrace()[n].getFileName();
        String sMethod = exception.getStackTrace()[n].getMethodName();
        //LOG.error("Error:{}. REST API Exception", exception.getMessage());
        LOG.error("Error:{}. REST Exception (sClass={},sMethod={},sFileName={})", exception.getMessage(),sClass,sMethod,sFileName);
        LOG.trace("FAIL:", exception);
        new Log(oClass!=null?oClass:this.getClass(), exception)//this.getClass()
        //LOG.error("Error:{}. REST Exception", exception.getMessage());
        //LOG.trace("FAIL:", exception);
        //new Log(this.getClass(), exception)
                ._Head("REST Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(SYSTEM_ERROR_CODE, exception.getMessage()));
    }

    @Ignore
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public
    @ResponseBody
    ResponseEntity<String> catchMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        int n=0;
        for(StackTraceElement oStackTraceElement : exception.getStackTrace()){
            String sPackage = oStackTraceElement.getClass().getPackage().getName();
            LOG.info("sPackage={}", sPackage);
            if(sPackage!=null && sPackage.startsWith("org.igov")){
                break;
            }
            n++;
        }
        if(n>=exception.getStackTrace().length){
            n=0;
        }
        Class oClass = exception.getStackTrace()[n].getClass();
        String sClass = exception.getStackTrace()[n].getClassName();
        String sFileName = exception.getStackTrace()[n].getFileName();
        String sMethod = exception.getStackTrace()[n].getMethodName();
        //LOG.error("Error:{}. REST API Exception", exception.getMessage());
        LOG.error("Error:{}. REST Wrong Input Parameters Exception (sClass={},sMethod={},sFileName={})", exception.getMessage(),sClass,sMethod,sFileName);
        LOG.trace("FAIL:", exception);
        new Log(oClass!=null?oClass:this.getClass(), exception)//this.getClass()
        //LOG.error("Error:{}. REST Wrong Input Parameters Exception", exception.getMessage());
        //LOG.trace("FAIL:", exception);
        //new Log(this.getClass(), exception)
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
        int n=0;
        for(StackTraceElement oStackTraceElement : exception.getStackTrace()){
            String sPackage = oStackTraceElement.getClass().getPackage().getName();
            LOG.info("sPackage={}", sPackage);
            if(sPackage!=null && sPackage.startsWith("org.igov")){
                break;
            }
            n++;
        }
        if(n>=exception.getStackTrace().length){
            n=0;
        }
        Class oClass = exception.getStackTrace()[n].getClass();
        String sClass = exception.getStackTrace()[n].getClassName();
        String sFileName = exception.getStackTrace()[n].getFileName();
        String sMethod = exception.getStackTrace()[n].getMethodName();
        //LOG.error("Error:{}. REST API Exception", exception.getMessage());
        LOG.error("Error:{}. REST Wrong Input Body Exception (sClass={},sMethod={},sFileName={})", exception.getMessage(),sClass,sMethod,sFileName);
        LOG.trace("FAIL:", exception);
        new Log(oClass!=null?oClass:this.getClass(), exception)//this.getClass()
        //LOG.error("Error:{}. REST Wrong Input Body Exception", exception.getMessage());
        //LOG.trace("FAIL:", exception);
        //new Log(this.getClass(), exception)
                ._Head("REST Wrong Input Body Exception")
                ._Status(LogStatus.ERROR)
                ._StatusHTTP(HttpStatus.BAD_REQUEST.value())
                ._Send()
                ;
        return JsonRestUtils.toJsonResponse(HttpStatus.BAD_REQUEST,
                new ErrorResponse(BUSINESS_ERROR_CODE, exception.getMessage()));
    }
}
