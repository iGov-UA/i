package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.subject.SubjectContactPlacePointType;
import org.igov.service.business.subject.SubjectContactPlacePointTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(description = "Работа с типами адресов", tags = "SubjectContactPlacePointType")
@RequestMapping(value = "/subject")
public class SubjectContactPlacePointTypeController {

    @Autowired
    private SubjectContactPlacePointTypeService oSubjectContactPlacePointTypeService;


    @ApiOperation(value = "Получение номера помещения")
    @RequestMapping(value = "/getSubjectContactPlacePointType", method = RequestMethod.GET)
    public @ResponseBody
    SubjectContactPlacePointType getPlaceBuildPartCell(
            @ApiParam(value = "nID", required = true) @RequestParam(value = "nID") Long nID) {
        return oSubjectContactPlacePointTypeService.getSubjectContactPlacePointType(nID);
    }

    @ApiOperation(value = "Добавление номера помещения")
    @RequestMapping(value = "/setSubjectContactPlacePointType", method = RequestMethod.POST)
    public @ResponseBody
    SubjectContactPlacePointType setSubjectContactPlacePointType(
            @ApiParam(value = "sName_UA", required = true) @RequestParam(value = "sName_UA") String sName_UA,
            @ApiParam(value = "sName_EN", required = true) @RequestParam(value = "sName_EN") String sName_EN,
            @ApiParam(value = "sName_RU", required = true) @RequestParam(value = "sName_RU") String sName_RU) {
        return oSubjectContactPlacePointTypeService.setSubjectContactPlacePointType(sName_UA, sName_EN, sName_RU);
    }

    @ApiOperation(value = "Удаление номера помещения")
    @RequestMapping(value = "/removeSubjectContactPlacePointType", method = RequestMethod.DELETE)
    public @ResponseBody
    void removeSubjectContactPlacePointType(
            @ApiParam(value = "nID", required = true) @RequestParam(value = "nID") Long nID) {
        oSubjectContactPlacePointTypeService.removeSubjectContactPlacePointType(nID);
    }
}
