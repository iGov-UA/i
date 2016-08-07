package org.igov.service.controller;

import org.igov.model.action.task.core.entity.ErrorResponse;
import org.igov.util.JSON.JsonRestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Used to display exceptions happened in controllers during launch of controller tests.
 * User: goodg_000
 * Date: 26.07.2016
 * Time: 23:23
 */
//@ControllerAdvice
public class ExceptionTracingTestControllerAdvice {

    @ExceptionHandler
    ResponseEntity<String> catchException(Exception oException) {
        oException.printStackTrace();
        return JsonRestUtils.toJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                new ErrorResponse(null, oException.getMessage()));
    }
}
