
package org.igov.service.controller;

import org.igov.service.business.feedback.FeedBackService;
import org.igov.service.exception.CommonServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(tags = {"ActionFeedBackController — Действия фидбэков"})
@RequestMapping(value = "/action/feedback")
public class ActionFeedBackController {

    private static final String ERROR_CODE = "exception in feedback-controller!";

    @Autowired
    private FeedBackService feedBackService;



    
    @ApiOperation(value = "Запуск фидбека по процессу")
    @RequestMapping(value = "/runFeedBack", method = RequestMethod.GET)
    @ResponseBody
    public void runFeedBack(@ApiParam(value = "ид процесса", required = true) @RequestParam(value = "snID_Process") String snID_Process)
            throws Exception  {
    	try {
    		feedBackService.runFeedBack(snID_Process);
    	} catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }
    
}
