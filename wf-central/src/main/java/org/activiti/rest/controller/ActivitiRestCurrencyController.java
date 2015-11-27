package org.activiti.rest.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.dao.CurrencyDao;
import org.wf.dp.dniprorada.model.Currency;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
@Controller
@RequestMapping(value = "/services")
public class ActivitiRestCurrencyController {

    private static final Logger LOGGER = Logger.getLogger(ActivitiRestCurrencyController.class);

    @Autowired
    private CurrencyDao currencyDao;

    /**
     * отдает список объектов сущности, подпадающих под критерии параметры.
     *
     * @param sID_UA   (опциональный)
     * @param sName_UA (опциональный)
     * @param sName_EN (опциональный)
     * @return список Currency согласно фильтрам
     */
    @RequestMapping(value = "/getCurrencies", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Currency> getCurrencies(
            @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @RequestParam(value = "sName_EN", required = false) String sName_EN) {
        return currencyDao.getCurrencies(sID_UA, sName_UA, sName_EN);
    }

    /**
     * обновляет элемент (если задан один из уникальных-ключей) или
     * вставляет (если не задан nID), и отдает экземпляр нового объекта.
     *
     * @param nID      (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_UA   (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sName_UA (опциональный, если nID задан и по нему найдена запись)
     * @param sName_EN (опциональный, если nID задан и по нему найдена запись)
     * @return обновленный/вставленный обьект
     */
    @RequestMapping(value = "/setCurrency", method = RequestMethod.GET)
    public
    @ResponseBody
    Currency setCurrency(
            @RequestParam(value = "nID", required = false) Long nID,
            @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @RequestParam(value = "sName_EN", required = false) String sName_EN)
            throws ActivitiRestException {
        try {
            Currency currency = findByKeys(nID, sID_UA);
            if (currency == null) {
                if (sID_UA == null || sName_UA == null || sName_EN == null) {
                    throw new IllegalArgumentException(
                            "Currency by key params was not founded. "
                                    + "Not enough params to insert.");
                }
                currency = new Currency();
                updateCurrencyParams(currency, sID_UA, sName_UA, sName_EN);
            } else {
                updateCurrencyParams(currency, sID_UA, sName_UA, sName_EN);
            }
            return currencyDao.saveOrUpdate(currency);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            throw new ActivitiRestException(
                    "SYSTEM_ERR",
                    e.getMessage(),
                    e,
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     * удаляет элемент (по обязательно заданому одному из уникальных-ключей).
     *
     * @param nID    (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     */
    @RequestMapping(value = "/removeCurrency", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeCurrency(
            @RequestParam(value = "nID", required = false) Long nID,
            @RequestParam(value = "sID_UA", required = false) String sID_UA)
            throws ActivitiRestException {
        try {
            if (nID == null && sID_UA == null) {
                throw new IllegalArgumentException("Key param was not specified");
            }
            if (nID != null && sID_UA != null) {
                throw new IllegalArgumentException("Too many params");
            }
            if (nID != null) {
                currencyDao.delete(nID);
            } else {
                currencyDao.deleteBy("sID_UA", sID_UA);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            throw new ActivitiRestException(
                    "SYSTEM_ERR",
                    e.getMessage(),
                    e,
                    HttpStatus.FORBIDDEN);
        }
    }

    private Currency findByKeys(Long nID, String sID_UA) {
        if (nID != null) {
            return currencyDao.findByIdExpected(nID);
        }
        if (sID_UA != null) {
            return currencyDao.findBy("sID_UA", sID_UA).orNull();
        }
        return null;
    }

    private void updateCurrencyParams(Currency currency,
            String sID_UA, String sName_UA, String sName_EN) {
        if (sID_UA != null) {
            currency.setsID_UA(sID_UA);
        }
        if (sName_UA != null) {
            currency.setsName_UA(sName_UA);
        }
        if (sName_EN != null) {
            currency.setsName_EN(sName_EN);
        }
    }
}
