package org.igov.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.ObjectEarthTargetDao;
import org.igov.model.ObjectEarthTarget;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
@Controller
@Api(tags = { "ActivitiRestObjectEarthTargetController" }, description = "ActivitiRestObjectEarthTargetController")
@RequestMapping(value = "/services")
public class ActivitiRestObjectEarthTargetController {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### ActivitiRestObjectEarthTargetController. ";

    private static final String noteGetObjectEarthTargets = noteController + "Получение списка целевых назначений земель, подпадающих под параметры #####\n\n"
        + "Пример запроса:\n"
        + "https://test.igov.org.ua/wf/service/services/getObjectEarthTargets?sID_UA=01.01\n\n\n"
        + "Пример ответа:\n"
        + noteCODEJSON
        + "{\n"
        + "  \"sID_UA\"   : \"01.01\",\n"
        + "  \"sName_UA\" : \"Для ведення товарного сільськогосподарського виробництва\",\n"
        + "  \"nID\"      : 1\n"
        + "}\n"
        + noteCODE
        + "http://www.neruhomist.biz.ua/classification.html[источник данных]";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private ObjectEarthTargetDao objectEarthTargetDao;

    /**
     * возвращает список целевых назначений земель, подпадающих под параметры
     <br>пример запроса: https://test.igov.org.ua/wf/service/services/getObjectEarthTargets?sID_UA=01.01
     <br>пример ответа:
     <br>{
     <br>   "sID_UA"   : "01.01",
     <br>   "sName_UA" : "Для ведення товарного сільськогосподарського виробництва",
     <br>   "nID"      : 1
     <br>}
     <br><a href="http://www.neruhomist.biz.ua/classification.html">источник данных</a>
     *
     * @param sID_UA   подразделение в коде КВЦПЗ (уникальный; опциональный)
     * @param sName_UA название целевого назначения земель на украинском
     *                 (уникальный; опциональный, достаточно чтоб совпала
     *                 любая часть названия)
     * @return список целевых назначений земель согласно фильтрам
     */
    @ApiOperation(value = "Получение списка целевых назначений земель, подпадающих под параметры", notes = noteGetObjectEarthTargets )
    @RequestMapping(value = "/getObjectEarthTargets", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ObjectEarthTarget> getObjectEarthTargets(
	    @ApiParam(value = "подразделение в коде КВЦПЗ (уникальный)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
	    @ApiParam(value = "название целевого назначения земель на украинском (уникальный, достаточно чтоб совпала любая часть названия)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA) {
        return objectEarthTargetDao.getObjectEarthTargets(sID_UA, sName_UA);
    }
}
