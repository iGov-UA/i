package org.igov.service.controller;

import java.util.List;

import org.igov.model.subject.SubjectGroupAndUser;
import org.igov.model.subject.SubjectGroupResultTree;
import org.igov.service.business.subject.SubjectGroupService;
import org.igov.service.business.subject.SubjectGroupTreeService;
import org.igov.service.business.subject.SubjectRightBPService;
import org.igov.service.business.subject.SubjectRightBPVO;
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
import java.util.Map;
import org.igov.model.subject.SubjectGroup;

@Controller
@Api(tags = {"SubjectGroupController — Организационная иерархия"})
@RequestMapping(value = "/subject/group")
public class SubjectGroupController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectGroupController.class);

    @Autowired
    private SubjectGroupService subjectGroupService;

    @Autowired
    private SubjectGroupTreeService subjectGroupTreeService;

    @Autowired
    SubjectRightBPService subjectRightBPService;

    @ApiOperation(value = "Получение организационной иерархии", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/subject/group/getSubjectGroups?sID_Group_Activiti=MJU_Dnipro&nDeepLevel=1 \n"
            + "Ответ: HTTP STATUS 200\n\n"
            + "{\n"
            + "\"aSubjectGroup\": [\n"
            + "{\n"
            + "\"sID_Group_Activiti\": \"MJU_Dnipro_Top3\",\n"
            + "\"sChain\": \"MJU_Dnipro_\",\n"
            + "\"nID\": 172,\n"
            + "\"sName\": \"Управління державної виконавчої служби-начальник управління\"\n"
            + "	}\n"
            + "	],\n"
            + "aSubjectUser\": [\n"
            + "{\n"
            + "\"sLogin\": \"MJU_common\",\n"
            + "\"sFirstName\": \"мінюст\",\n"
            + "sLastName\": \"тестовий користувач\",\n"
            + "sEmail\":,\n"
            + "sPicture\": \"null\n"
            + "        }\n"
            + "    ]\n"
            + "}\n\n"
            + "\n```\n"
    )
    @RequestMapping(value = "/getSubjectGroups", method = RequestMethod.GET)
    @ResponseBody
    public SubjectGroupAndUser getSubjectGroups(@ApiParam(value = "ид группы", required = true) 
    @RequestParam(value = "sID_Group_Activiti") String sID_Group_Activiti,
            @ApiParam(value = "глубина выборки", required = false) 
            @RequestParam(value = "nDeepLevel", required = false) Long nDeepLevel,
            @ApiParam(value = "текст поиска (искать в ФИО, по наличию вхождения текста в ФИО)", required = false) 
            @RequestParam(value = "sFind", required = false) String sFind)
            throws Exception {
        SubjectGroupAndUser subjectGroupResult = null;
        try {
            subjectGroupResult = subjectGroupService.getCatalogSubjectGroups(sID_Group_Activiti, nDeepLevel, sFind);

        } catch (Exception e) {
            LOG.error("FAIL: ", e);
        }
        return subjectGroupResult;
    }

    @RequestMapping(value = "/getSubjectGroupsTree", method = RequestMethod.GET)
    @ResponseBody
    public SubjectGroupResultTree getSubjectGroupsTree(
            @ApiParam(value = "ид группы", required = true) 
            @RequestParam(value = "sID_Group_Activiti") String sID_Group_Activiti,
            @ApiParam(value = "глубина выборки", required = false) 
            @RequestParam(value = "nDeepLevel", required = false) Long nDeepLevel,
            @ApiParam(value = "текст поиска (искать в ФИО, по наличию вхождения текста в ФИО)", required = false) 
            @RequestParam(value = "sFind", required = false) String sFind,
            @ApiParam(value = "Флаг отображения рутового элемента для всей иерархии (true-отоборажаем, false-нет, по умолчанию yes)", required = false) 
            @RequestParam(value = "bIncludeRoot", required = false) Boolean bIncludeRoot,
            @ApiParam(value = "Ширина выборки", required = false) 
            @RequestParam(value = "nDeepLevelWidth", required = false) Long nDeepLevelWidth,
            @ApiParam(value = "Тип выборки: Organ- иерархия в разрезе органы,  Human -иерархия в разрезе людей, * - иерархия органы+люди", required = false) 
            @RequestParam(value = "sSubjectType", required = false) String sSubjectType)
            throws Exception {
        return subjectGroupTreeService.getCatalogSubjectGroupsTree(sID_Group_Activiti, nDeepLevel, 
                sFind, bIncludeRoot, nDeepLevelWidth, sSubjectType);
    }
    
    @ApiOperation(value = "Получение организационной иерархии вверх")
    @RequestMapping(value = "/getSubjectGroupsTreeUp", method = RequestMethod.GET)
    @ResponseBody
    public List<SubjectGroup> getSubjectGroupsTreeUp(
            @ApiParam(value = "Идентификатор группы", required = true) 
            @RequestParam(value = "sID_Group_Activiti", required = true) String sID_Group_Activiti,
            @ApiParam(value = "глубина выборки", required = false) 
            @RequestParam(value = "nDeepLevel", required = false) Long nDeepLevel,
            @ApiParam(value = "Флаг отображения рутового элемента для всей иерархии (true-отоборажаем, false-нет, по умолчанию yes)", required = false) 
            @RequestParam(value = "bIncludeRoot", required = false) Boolean bIncludeRoot,
            @ApiParam(value = "Тип выборки: Organ- иерархия в разрезе органы,  Human -иерархия в разрезе людей", required = false) 
            @RequestParam(value = "sSubjectType", required = false) String sSubjectType) {
        
        return subjectGroupTreeService.getSubjectGroupsTreeUp(sID_Group_Activiti, sSubjectType);
       
    }

    @RequestMapping(value = "/getBPs_ForReferent", method = RequestMethod.GET)
    @ResponseBody
    public List<SubjectRightBPVO> getBPs_ForReferent(
            @ApiParam(value = "Логин сотрудника", required = false) 
            @RequestParam(required = false, value = "sLogin") String sLogin)
            throws Exception {
        LOG.info("sLogin: ", sLogin);
        List<SubjectRightBPVO> aResSubjectRightBPVO = subjectRightBPService.getBPs_ForReferent(sLogin);
        return aResSubjectRightBPVO;
    }
    
    @RequestMapping(value = "/getBPs_ForExport", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getBPs_ForExport(
            @ApiParam(value = "Логин сотрудника", required = false) 
            @RequestParam(required = false, value = "sLogin") String sLogin)
            throws Exception {
        
        return subjectRightBPService.getBPs_ForExport(sLogin);
    }
    
}
