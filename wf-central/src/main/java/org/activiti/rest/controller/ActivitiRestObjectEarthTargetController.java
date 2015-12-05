package org.activiti.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.dao.ObjectEarthTargetDao;
import org.wf.dp.dniprorada.model.ObjectEarthTarget;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
@Controller
@RequestMapping(value = "/services")
public class ActivitiRestObjectEarthTargetController {

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
    @RequestMapping(value = "/getObjectEarthTargets", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ObjectEarthTarget> getObjectEarthTargets(
            @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @RequestParam(value = "sName_UA", required = false) String sName_UA) {
        return objectEarthTargetDao.getObjectEarthTargets(sID_UA, sName_UA);
    }
}
