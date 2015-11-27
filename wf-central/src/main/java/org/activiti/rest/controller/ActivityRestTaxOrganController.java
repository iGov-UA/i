package org.activiti.rest.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.dao.TaxOrganDao;
import org.wf.dp.dniprorada.model.TaxOrgan;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */

@Controller
@RequestMapping(value = "/services")
public class ActivityRestTaxOrganController {
    private static final Logger LOG = Logger.getLogger(ActivityRestTaxOrganController.class);

    @Autowired
    private TaxOrganDao taxOrganDao;

    /**
     * Отдает массив объектов сущности
     *
     * @return список
     */
    @RequestMapping(value = "/getTaxOrgans")
    @ResponseBody
    public List<TaxOrgan> getTaxOrgans() {
        return taxOrganDao.findAll();
    }

//    /**
//     * @param nID
//     * @param sID_UA
//     * @param sName_UA
//     * @return TaxOrgan обьект
//     */
//    @RequestMapping(value = "/getTaxOrgan")
//    @ResponseBody
//    public TaxOrgan getTaxOrgan(@RequestParam(value = "nID", required = false) Long nID,
//                                @RequestParam(value = "sID_UA", required = false) String sID_UA,
//                                @RequestParam(value = "sName_UA", required = false) String sName_UA) {
//        //TODO
//        return taxOrganDao.getByKey(nID, sID_UA, sName_UA);
//    }

    /**
     * Апдейтит элемент(если задан один из уникальных-ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта параметры.
     *
     * @param nID      (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_UA   (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sName_UA (опциональный, если nID задан и по нему найдена запись)
     * @return TaxOrgan object
     */
    @RequestMapping(value = "/setTaxOrgan")
    @ResponseBody
    public TaxOrgan setTaxOrgan(@RequestParam(value = "nID", required = false) Long nID,
                                @RequestParam(value = "sID_UA", required = false) String sID_UA,
                                @RequestParam(value = "sName_UA", required = false) String sName_UA) {
        return taxOrganDao.setTaxOrgan(nID, sID_UA, sName_UA);
    }

    /**
     * Удаляет элемент(по ОБЯЗАТЕЛЬНО заданому одому из уникальных-ключей) параметры
     *
     * @param nID    (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     */
    @RequestMapping(value = "/removeTaxOrgan")
    @ResponseBody
    public void removeTaxOrgan(@RequestParam(value = "nID", required = false) Long nID,
                               @RequestParam(value = "sID_UA", required = false) String sID_UA) {
        taxOrganDao.removeByKey(nID, sID_UA);
    }


}
