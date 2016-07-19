/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.analytic.access.AccessGroup;
import org.igov.model.analytic.access.AccessUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.analytic.process.Process;
import org.igov.model.analytic.process.ProcessTask;
import org.igov.model.analytic.attribute.Attribute;
import org.igov.model.analytic.attribute.Attribute_StingShort;
import org.igov.model.analytic.process.ProcessDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author olga
 */
@Controller
@Api(tags = {"ProcessController - процессы и задачи"})
@RequestMapping(value = "/analytic/process")
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    private static final String JSON_TYPE = "Accept=application/json";
    
    @Autowired
    private ProcessDao processDao;

    @ApiOperation(value = "/setProcess", notes = "##### Process - сохранение процесса #####\n\n")
    @RequestMapping(value = "/setProcess", method = RequestMethod.POST, headers = {JSON_TYPE})
    public @ResponseBody
    Process setSubject(@RequestBody Process oProcess) {
        LOG.info("/setProcess!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        Process process = new Process();
        ProcessTask processTask = new ProcessTask();
        Attribute attribute = new Attribute();
        Attribute_StingShort attribute_StingShort = new Attribute_StingShort();
        AccessGroup accessGroup = new AccessGroup();
        AccessUser accessUser = new AccessUser();
        return process;
    }

    //http://localhost:8080/wf-region/service/analytic/process/getProcess?sID_=1
    @ApiOperation(value = "/getProcess", notes = "##### Process - получение процесса #####\n\n")
    @RequestMapping(value = "/getProcess", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    Process getSubject(@ApiParam(value = "внутренний ид заявки", required = true) @RequestParam(value = "sID_") String sID_,
            @ApiParam(value = "ид источника", required = false) @RequestParam(value = "nID_Source", required = false) Long nID_Source) {
        LOG.info("/getProcess!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        return processDao.findByIdExpected(new Long(1));
    }

}
