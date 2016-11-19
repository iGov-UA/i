
package org.igov.service.controller;

import java.util.List;

import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.service.business.process.ProcessSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Api(tags = {"ProcessSubjectController — Иерархия процессов"})
@RequestMapping(value = "/subject/process")
public class ProcessSubjectController {
	
	 private static final Logger LOG = LoggerFactory.getLogger(ProcessSubjectController.class);


    @Autowired
    private ProcessSubjectService processSubjectService;



    
    @ApiOperation(value = "Получение иерархии процессов", notes = "##### Пример:\n"
	        + "https://alpha.test.region.igov.org.ua/wf/service/subject/process/getProcessSubject?snID_Process_Activiti=MJU_Dnipro&nDeepLevel=1 \n")
    @RequestMapping(value = "/getProcessSubject", method = RequestMethod.GET)
    @ResponseBody
    public ProcessSubjectResult getProcessSubject(@ApiParam(value = "ид процесса", required = true) @RequestParam(value = "snID_Process_Activiti") String snID_Process_Activiti,
    		 @ApiParam(value = "глубина выборки", required = false) @RequestParam(value = "nDeepLevel", required = false) Long nDeepLevel)
            throws Exception  {
    	ProcessSubjectResult processSubjectResult = null;
    	try {
    		processSubjectResult = processSubjectService.getCatalogProcessSubject(snID_Process_Activiti,nDeepLevel);
    		
    	} catch (Exception e) {
    		 LOG.error("FAIL: ", e);
        }
		return processSubjectResult;
    }
    
    
    @ApiOperation(value = "Сохранить процесс", notes = "##### Пример:\n"
	        + "https://alpha.test.region.igov.org.ua/wf/service/subject/process/setProcessSubject?snID_Process_Activiti=MJU_Dnipro&sLogin=1&sDatePlan=19-11-2016&nOrder=1 \n")
    @RequestMapping(value = "/setProcessSubject", method = RequestMethod.GET)
    @ResponseBody
    public Long setProcessSubject(@ApiParam(value = "ид процесса", required = true) @RequestParam(value = "snID_Process_Activiti") String snID_Process_Activiti,
    		 @ApiParam(value = "sLogin", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
    		 @ApiParam(value = "sDatePlan", required = false) @RequestParam(value = "sDatePlan", required = false) String sDatePlan,
    		 @ApiParam(value = "nOrder", required = false) @RequestParam(value = "nOrder", required = false) Long nOrder)
            throws Exception  {
    	Long processSubjectResult = null;
    	try {
    		processSubjectResult = processSubjectService.setProcessSubject(snID_Process_Activiti, sLogin, sDatePlan, nOrder);
    		
    	} catch (Exception e) {
    		 LOG.error("FAIL: ", e);
        }
		return processSubjectResult;
    }
    
}
