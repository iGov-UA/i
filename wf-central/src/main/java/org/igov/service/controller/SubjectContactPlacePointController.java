package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.subject.SubjectContactPlacePoint;
import org.igov.service.business.subject.SubjectContactPlacePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(description = "Работа с адресами", tags = "SubjectContactPlacePoint")
@RequestMapping(value = "/subject")
public class SubjectContactPlacePointController {
    @Autowired
    private SubjectContactPlacePointService oSubjectContactPlacePointService;

    @ApiOperation(value = "Добавление адреса")
    @RequestMapping(value = "/setSubjectContactPlacePoint", method = RequestMethod.POST)
    public @ResponseBody
    SubjectContactPlacePoint setSubjectContactPlacePoint(
            @ApiParam(value = "nID_Subject", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "nID_SubjectContactPlacePointType", required = true) @RequestParam(value = "nID_SubjectContactPlacePointType") Long nID_SubjectContactPlacePointType,
            @ApiParam(value = "nID_Place", required = true) @RequestParam(value = "nID_Place") Long nID_Place,
            @ApiParam(value = "nID_Place_Region", required = true) @RequestParam(value = "nID_Place_Region") Long nID_Place_Region,
            @ApiParam(value = "nID_PlaceBranch", required = true) @RequestParam(value = "nID_PlaceBranch") Long nID_PlaceBranch,
            @ApiParam(value = "nID_PlaceBuild", required = true) @RequestParam(value = "nID_PlaceBuild") Long nID_PlaceBuild,
            @ApiParam(value = "nID_PlaceBuildPart", required = true) @RequestParam(value = "nID_PlaceBuildPart") Long nID_PlaceBuildPart,
            @ApiParam(value = "nID_PlaceBuildPartCell", required = true) @RequestParam(value = "nID_PlaceBuildPartCell") Long nID_PlaceBuildPartCell) {
        return oSubjectContactPlacePointService.setSubjectContactPlacePoint(nID_Subject, nID_SubjectContactPlacePointType,
                nID_Place, nID_Place_Region, nID_PlaceBranch, nID_PlaceBuild, nID_PlaceBuildPart, nID_PlaceBuildPartCell);
    }

    @ApiOperation(value = "Удаление адреса")
    @RequestMapping(value = "/removeSubjectContactPlacePoint", method = RequestMethod.DELETE)
    public @ResponseBody
    void removeSubjectContactPlacePoint(
            @ApiParam(value = "nID", required = true) @RequestParam(value = "nID") Long nID) {
        oSubjectContactPlacePointService.removeSubjectContactPlacePoint(nID);
    }

    @ApiOperation(value = "Получение адреса")
    @RequestMapping(value = "/getSubjectContactPlacePoint", method = RequestMethod.GET)
    public @ResponseBody
    SubjectContactPlacePoint getSubjectContactPlacePoint(
            @ApiParam(value = "nID", required = true) @RequestParam(value = "nID") Long nID) {
        return oSubjectContactPlacePointService.getSubjectContactPlacePoint(nID);
    }
}
