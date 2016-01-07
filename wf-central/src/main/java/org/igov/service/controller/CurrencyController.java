package org.igov.service.controller;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.CurrencyDao;
import org.igov.model.Currency;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.util.List;
import org.igov.service.interceptor.exception.ActivitiRestException;

/**
 * @author grigoriy-romanenko
 */
@Controller
@Api(tags = { "ActivitiRestCurrencyController" }, description = "Работа с валютами")
@RequestMapping(value = "/services")
public class CurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с валютами. ";

    private static final String noteGetCurrencies = noteController + "Возвращает список валют, подпадающих под параметры #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/getCurrencies\n\n"
		+ "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
		+ "Параметры:\n\n"
		+ "- sID_UA - ИД-номер Код, в украинском классификаторе (уникальный, опциональный)\n"
		+ "- sName_UA - название на украинском (уникальный, опциональный)\n"
		+ "- sName_EN - название на английском (уникальный, опциональный)\n\n\n"
		+ "Пример запроса: https://test.igov.org.ua/wf/service/services/getCurrencies?sID_UA=004\n\n"
		+ "Пример ответа:\n\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"sID_UA\"   : \"004\",\n"
		+ "    \"sName_UA\" : \"Афґані\",\n"
		+ "    \"sName_EN\" : \"Afghani\",\n"
		+ "    \"nID\"      : 1\n"
		+ "}\n"
		+ noteCODE;

    private static final String noteSetCurrency = noteController + "обновляет запись валюты #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/setCurrency\n\n"
		+ "обновляет запись (если задан один из параметров: nID, sID_UA; и по нему найдена запись) или вставляет (если не задан nID), и отдает экземпляр нового объекта\n\n"
		+ "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
		+ "Параметры:\n\n"
		+ "- nID - внутренний ИД-номер (уникальный; опциональный, если sID_UA задан и по нему найдена запись)\n\n"
		+ "- sID_UA - ИД-номер Код, в украинском классификаторе (уникальный; опциональный, если nID задан и по нему найдена запись)\n\n"
		+ "- sName_UA - название на украинском (уникальный; опциональный, если nID задан и по нему найдена запись)\n\n"
		+ "- sName_EN - название на английском (уникальный; опциональный, если nID задан и по нему найдена запись)\n\n"
		+ "Пример добавления записи:\n\n"
		+ "https://test.igov.org.ua/wf/service/services/setCurrency?sID_UA=050&sName_UA=Така&sName_EN=Taka\n\n"
		+ "Пример обновления записи:\n\n"
		+ "https://test.igov.org.ua/wf/service/services/setCurrency?sID_UA=050&sName_UA=Така\n\n";

    private static final String noteRemoveCurrency = noteController + "удаляет элемент по обязательно заданному одному из параметров #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/removeCurrency\n\n"
		+ "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
		+ "Параметры:\n\n"
		+ "- nID - внутренний ИД-номер (уникальный; опциональный, если sID_UA задан и по нему найдена запись)\n"
		+ "- sID_UA - ИД-номер Код, в украинском классификаторе (уникальный; опциональный, если nID задан и по нему найдена запись)\n\n\n"
		+ "Пример запроса:\n"
		+ "https://test.igov.org.ua/wf/service/services/removeCurrency?sID_UA=050\n";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

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
    @ApiOperation(value = "Возвращает список валют, подпадающих под параметры", notes = noteGetCurrencies )
    @RequestMapping(value = "/getCurrencies", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Currency> getCurrencies(
	    @ApiParam(value = "ИД-номер Код, в украинском классификаторе", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
	    @ApiParam(value = "Название на украинском", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
	    @ApiParam(value = "Название на английском", required = false) @RequestParam(value = "sName_EN", required = false) String sName_EN) {
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
    @ApiOperation(value = "обновляет запись валюты", notes = noteSetCurrency )
    @RequestMapping(value = "/setCurrency", method = RequestMethod.GET)
    public
    @ResponseBody
    Currency setCurrency(
	    @ApiParam(value = "внутренний ИД-номер (уникальный; если sID_UA задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Код, в украинском классификаторе (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
	    @ApiParam(value = "название на украинском (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
	    @ApiParam(value = "название на английском (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_EN", required = false) String sName_EN)
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
    @ApiOperation(value = "удаляет элемент по обязательно заданному одному из параметров", notes = noteRemoveCurrency )
    @RequestMapping(value = "/removeCurrency", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeCurrency(
	    @ApiParam(value = "внутренний ИД-номер (уникальный; если sID_UA задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Код, в украинском классификаторе (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA)
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
