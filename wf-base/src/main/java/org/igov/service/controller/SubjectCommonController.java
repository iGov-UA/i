package org.igov.service.controller;

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
import java.util.List;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectHuman;
import org.igov.service.business.subject.SubjectService;

@Controller
@Api(tags = {"SubjectCommonController — Организационная иерархия"})
@RequestMapping(value = "/subject")
public class SubjectCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectCommonController.class);

    @Autowired
    private SubjectService oSubjectService;

    @ApiOperation(value = "Получение сабджекта по логину", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/subject/getSubjectByLoginAccount?sLogin=kermit \n"
            + "Ответ: HTTP STATUS 200\n\n"
    )
    @RequestMapping(value = "/getSubjectByLoginAccount", method = RequestMethod.GET)
    @ResponseBody
    public Subject getSubjectByLoginAccount(@ApiParam(value = "логин", required = true) @RequestParam(value = "sLogin") String sLogin)
            throws Exception {
        return oSubjectService.getSubjectByLoginAccount(sLogin);
    }
    
    @ApiOperation(value = "Получение контактов", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/subject/getSubjectContacts?snID_Process_Activiti=34610001&sID_Field=sTableViewed&sSubjectType=Human&nID_SubjectContactType=1 \n"
    )
    @RequestMapping(value = "/getSubjectContacts", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getSubjectContacts(
        @ApiParam(value = "ИД процесса", required = true) @RequestParam(value = "snID_Process_Activiti", required = true) String snID_Process_Activiti,
        @ApiParam(value = "ид поля - из которого мы вытянем или тейблу, или селек, или стрингу", required = true) @RequestParam(value = "sID_Field", required = true) String sID_Field,
        @ApiParam(value = "фильтр по типу Subject'a", required = false) @RequestParam(value = "sSubjectType", required = false) String sSubjectType,
        @ApiParam(value = "тип контакта", required = true) @RequestParam(value = "sSubjectContactType", required = true) String sSubjectContactType
    ) throws Exception {
            
        return oSubjectService.getSubjectContacts(snID_Process_Activiti, sID_Field, sSubjectType, sSubjectContactType);
    }
}
