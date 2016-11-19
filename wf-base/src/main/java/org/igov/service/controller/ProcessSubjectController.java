
package org.igov.service.controller;

import java.util.List;

import org.igov.model.process.ProcessSubject;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupAndUser;
import org.igov.service.business.process.ProcessSubjectService;
import org.igov.service.business.subject.SubjectGroupService;
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



    
    @ApiOperation(value = "Получение иерархии процессов")
    @RequestMapping(value = "/getProcessSubject", method = RequestMethod.GET)
    @ResponseBody
    public List<ProcessSubject> getProcessSubject(@ApiParam(value = "ид процесса", required = true) @RequestParam(value = "snID_Process_Activiti") String snID_Process_Activiti,
    		 @ApiParam(value = "глубина выборки", required = false) @RequestParam(value = "nDeepLevel", required = false) Long nDeepLevel)
            throws Exception  {
    	List<ProcessSubject> processSubjectResult = null;
    	try {
    		processSubjectResult = processSubjectService.getCatalogProcessSubject(snID_Process_Activiti,nDeepLevel);
    		
    	} catch (Exception e) {
    		 LOG.error("FAIL: ", e);
        }
		return processSubjectResult;
    }
    
}
