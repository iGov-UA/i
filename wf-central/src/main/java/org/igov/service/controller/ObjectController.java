package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.object.ObjectEarthTarget;
import org.igov.model.object.ObjectEarthTargetDao;
import org.igov.service.business.object.ObjectCustomsService;
import org.igov.service.exception.CommonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @author grigoriy-romanenko
 */
@Controller
@Api(tags = { "ObjectController -- Обьекты и смежные сущности" })
@RequestMapping(value = "/object")
public class ObjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectController.class);

    @Autowired
    private ObjectEarthTargetDao objectEarthTargetDao;

    @Autowired
    private ObjectCustomsService objectCustomsService;

    @ApiOperation(value = "Получение списка целевых назначений земель, подпадающих под параметры", notes =
            "Пример запроса:\n"
                    + "https://test.igov.org.ua/wf/service/object/getObjectEarthTargets?sID_UA=01.01\n"
                    + "Пример ответа:\n"
                    + "\n```json\n"
                    + "{\n"
                    + "  \"sID_UA\"   : \"01.01\",\n"
                    + "  \"sName_UA\" : \"Для ведення товарного сільськогосподарського виробництва\",\n"
                    + "  \"nID\"      : 1\n"
                    + "}\n"
                    + "\n```\n"
                    + "http://www.neruhomist.biz.ua/classification.html[источник данных]")
    @RequestMapping(value = "/getObjectEarthTargets", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ObjectEarthTarget> getObjectEarthTargets(
            @ApiParam(value = "ид-строка. подразделение в коде КВЦПЗ (уникальный)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-название целевого назначения земель на украинском (уникальный, достаточно чтоб совпала любая часть названия)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA) {
        return objectEarthTargetDao.getObjectEarthTargets(sID_UA, sName_UA);
    }

    @ApiOperation(value = "Получить список объектов ObjectCustoms ", notes = "Пример ответа:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sID_UA\": \"0101\",\n"
            + "    \"sName_UA\": \"Коні, віслюки, мули та лошаки, живі:\",\n"
            + "    \"sMeasure_UA\": \"-\",\n"
            + "    \"nID\": 1\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID_UA\": \"0101 10\",\n"
            + "    \"sName_UA\": \"Коні, віслюки, мули та лошаки, живі:  чистопородні племінні тварини:\",\n"
            + "    \"sMeasure_UA\": \"-\",\n"
            + "    \"nID\": 2\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getObjectCustoms", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<String> getObjectCustoms(
            @ApiParam(value = "строка-ид(опциональный, если другой уникальный ключ задан и по нему найдена запись) (формат 0101 01 01 01)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-ид(опциональный, если другой уникальный ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            HttpServletResponse response) throws CommonServiceException {
        return objectCustomsService.getObjectCustoms(sID_UA, sName_UA, response);
    }

    @ApiOperation(value = "Обновление или вставка новуой записи", notes =
            "Запрос вида /wf/service/object/setObjectCustoms?\n"
                    + "обновление записи происходит в том случае, если есть параметр nID\n"
                    + "и хотя бы один другой параметр: sID_UA, sName_UA или sMeasure_UA;\n"
                    + "вставка записи происходит в том случае, если в метод не передается\n"
                    + "параметр nID, но передаются три других параметра;\n"
                    + "пример возвращаемого объекта:\n"
                    + "\n```json\n"
                    + "{\n"
                    + "  \"sID_UA\": \"0101\",\n"
                    + "  \"sName_UA\": \"Коні, віслюки, мули та лошаки, живі:\",\n"
                    + "  \"sMeasure_UA\": \"-\",\n"
                    + "  \"nID\": 1\n"
                    + "}\n"
                    + "\n```\n"
                    + "при вставке и обновлении поля sID_UA важно, чтобы параметр не имел впереди и позади пробелов, иначе может нарушиться уникальность записи\n"
                    + "при вставке и обновлении поля sName_UA важно брать в кавычки запись, если она содержит \"точку с пробелом\" — \";\"\n"
                    + "- nID         (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
                    + "- sID_UA      (опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)\n"
                    + "- sName_UA    (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
                    + "- sMeasure_UA (опциональный)\n")
    @RequestMapping(value = "/setObjectCustoms", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity setObjectCustoms(
            @ApiParam(value = "строка-ключ(опциональный, если другой уникальный-ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-ключ(опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-ключ(опциональный, если другой уникальный-ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @ApiParam(value = "строка названия мерчанта на украинском", required = false) @RequestParam(value = "sMeasure_UA", required = false) String sMeasure_UA,
            HttpServletResponse response) throws CommonServiceException {
        ResponseEntity responseEntity = null;
        try {
            responseEntity = objectCustomsService.setObjectCustoms(nID, sID_UA, sName_UA, sMeasure_UA, response);
        }catch (UnexpectedRollbackException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "Possible duplicate of object " + e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    @ApiOperation(value = "Удаление записи по уникальному значению nID или sID_UA", notes =
            "Запрос вида /wf/service/object/removeObjectCustoms?;\n\n\n"
                    + "- nID     (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
                    + "- sID_UA  (опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)")
    @RequestMapping(value = "/removeObjectCustoms", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeObjectCustoms(
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            HttpServletResponse response) throws CommonServiceException {
        objectCustomsService.removeObjectCustoms(nID, sID_UA, response);
    }
}

