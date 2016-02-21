package org.igov.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.object.ObjectEarthTargetDao;
import org.igov.model.object.ObjectEarthTarget;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.igov.model.object.ObjectCustoms;
import org.igov.model.object.ObjectCustomsDao;
import static org.igov.service.business.object.ObjectService.isArgsNull;
import static org.igov.service.business.object.ObjectService.isMatchSID;
import static org.igov.service.business.object.ObjectService.isMeasureCorrect;
import static org.igov.service.business.object.ObjectService.sid_pattern1;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author grigoriy-romanenko
 */
@Controller
@Api(tags = {"ObjectController"}, description = "Обьекты и смежные сущности")
@RequestMapping(value = "/object")
public class ObjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectController.class);

    @Autowired
    private ObjectEarthTargetDao objectEarthTargetDao;

    @Autowired
    private ObjectCustomsDao objectCustomsDao;

    /**
     * возвращает список целевых назначений земель, подпадающих под параметры
     * <br>пример запроса:
     * https://test.igov.org.ua/wf/service/object/getObjectEarthTargets?sID_UA=01.01
     * <br>пример ответа:
     * <br>{
     * <br> "sID_UA" : "01.01",
     * <br> "sName_UA" : "Для ведення товарного сільськогосподарського
     * виробництва",
     * <br> "nID" : 1
     * <br>}
     * <br><a href="http://www.neruhomist.biz.ua/classification.html">источник
     * данных</a>
     *
     * @param sID_UA подразделение в коде КВЦПЗ (уникальный; опциональный)
     * @param sName_UA название целевого назначения земель на украинском
     * (уникальный; опциональный, достаточно чтоб совпала любая часть названия)
     * @return список целевых назначений земель согласно фильтрам
     */
    @ApiOperation(value = "Получение списка целевых назначений земель, подпадающих под параметры", notes = "##### ObjectController - Обьекты и смежные сущности. Получение списка целевых назначений земель, подпадающих под параметры #####\n\n"
            + "Пример запроса:\n"
            + "https://test.igov.org.ua/wf/service/object/getObjectEarthTargets?sID_UA=01.01\n\n\n"
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
    public @ResponseBody
    List<ObjectEarthTarget> getObjectEarthTargets(
            @ApiParam(value = "подразделение в коде КВЦПЗ (уникальный)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "название целевого назначения земель на украинском (уникальный, достаточно чтоб совпала любая часть названия)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA) {
        return objectEarthTargetDao.getObjectEarthTargets(sID_UA, sName_UA);
    }

    /**
     * issue #968 — lesha1980;
     *
     * Запрос вида /wf/service/object/getObjectCustoms?; метод возвращает список
     * объектов ObjectCustoms по аргументам sID_UA и/или sName_UA; пример
     * возвращаемого списка объектов: [{"sID_UA":"0101","sName_UA":"Коні,
     * віслюки, мули та лошаки, живі:","sMeasure_UA":"-","nID":1},
     * {"sID_UA":"0101 10","sName_UA":"Коні, віслюки, мули та лошаки, живі: 
     * чистопородні племінні тварини:","sMeasure_UA":"-","nID":2}]
     *
     *
     * @param sID_UA (опциональный, если другой уникальный ключ задан и по нему
     * найдена запись) (формат 0101 01 01 01)
     * @param sName_UA (опциональный, если другой уникальный ключ задан и по
     * нему найдена запись)
     * @param response
     * @return list — список найденных объектов
     *
     */
    @ApiOperation(value = "Получить список объектов ObjectCustoms ", notes = "##### ObjectController - Обьекты и смежные сущности. Получение списка объектов ObjectCustoms #####\n\n"
            + "issue #968 — lesha1980;\n"
            + "Запрос вида /wf/service/object/getObjectCustoms?\n\n\n"
            + "Параметры\n\n"
            + "- sID_UA      (опциональный, если другой уникальный ключ задан и по нему найдена запись) (формат 0101 01 01 01)\n"
            + "- sName_UA    (опциональный, если другой уникальный ключ задан и по нему найдена запись)\n\n\n"
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
    public @ResponseBody
    ResponseEntity<String> getObjectCustoms(
            @ApiParam(value = "(опциональный, если другой уникальный ключ задан и по нему найдена запись) (формат 0101 01 01 01)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "(опциональный, если другой уникальный ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            HttpServletResponse response
    ) throws CommonServiceException {
        //проверяем наличие аргументов

        if (isArgsNull(sID_UA, sName_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "it must be set at least one parameter to execute this service: sID_UA, sName_UA");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "it must be set at least one parameter to execute this service: sID_UA, sName_UA",
                    HttpStatus.FORBIDDEN
            );

        }

        //если задан sID_UA, но его значение не совпадает с требуемым форматом (вида 0101 01 01 01)
        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)",
                    HttpStatus.FORBIDDEN
            );

        }
        //если sName_UA задан, но больше требуемого числа символов

        if (sName_UA != null && sName_UA.length() > 2000) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "length sName_UA is more than 2000");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "length sName_UA is more than 2000",
                    HttpStatus.FORBIDDEN
            );

        }

        ResponseEntity<String> result = null;
        Map<String, String> args = new HashMap<String, String>();

        //формируем аргументы для getObjectCustoms
        if (sID_UA != null) {
            args.put("sID_UA", sID_UA);
        }
        if (sName_UA != null) {
            args.put("sName_UA", sName_UA);
        }
        try {
            List<ObjectCustoms> pcode_list = this.objectCustomsDao.getObjectCustoms(args);

            //если список пуст передаем no_content
            if (pcode_list.size() == 0) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                String reason = null;
                if (sID_UA != null && sName_UA != null) {
                    reason = "Record not found! No such Entity with sID_UA: " + sID_UA + ", sName_UA: " + sName_UA;
                } else if (sID_UA != null) {
                    reason = "Record not found! No such Entity with sID_UA: " + sID_UA;
                } else if (sName_UA != null) {
                    reason = "Record not found! No such Entity with sName_UA: " + sName_UA;
                }
                response.setHeader("Reason", reason);

                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                        reason,
                        HttpStatus.NO_CONTENT
                );

            }
            result = JsonRestUtils.toJsonResponse(pcode_list);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
            );

        }

        return result;
    }

    /**
     * issue #968 — lesha1980;
     *
     * Запрос вида /wf/service/object/setObjectCustoms?; обновляет или вставляет
     * новую запись; обновление записи происходит в том случае, если есть
     * параметр nID и хотя бы один другой параметр: sID_UA, sName_UA или
     * sMeasure_UA; вставка записи происходит в том случае, если в метод не
     * передается параметр nID, но передаются три других параметра; пример
     * возвращаемого объекта: {"sID_UA":"0101","sName_UA":"Коні, віслюки, мули
     * та лошаки, живі:","sMeasure_UA":"-","nID":1} при вставке и обновлении
     * поля sID_UA важно, чтобы параметр не имел впереди и позади пробелов,
     * иначе может нарушиться уникальность записи при вставке и обновлении поля
     * sName_UA важно брать в кавычки запись, если она содержит "точку с
     * пробелом" — ";"
     *
     * @param nID (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)(формат 0101 01 01 01)
     * @param sName_UA (опциональный, если другой уникальный-ключ задан и по
     * нему найдена запись)
     * @param sMeasure_UA (опциональный)
     * @param response
     * @return ObjectCustoms
     */
    @ApiOperation(value = "Обновление или вставка новуой записи", notes = "##### ObjectController - Обьекты и смежные сущности. Обновить или вставить новую запись #####\n\n"
            + "issue #968 — lesha1980;\n\n"
            + "Запрос вида /wf/service/object/setObjectCustoms?\n\n\n"
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
            + "при вставке и обновлении поля sName_UA важно брать в кавычки запись, если она содержит \"точку с пробелом\" — \";\"\n\n"
            + "- nID         (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
            + "- sID_UA      (опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)\n"
            + "- sName_UA    (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
            + "- sMeasure_UA (опциональный)\n")
    @RequestMapping(value = "/setObjectCustoms", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity setObjectCustoms(
            @ApiParam(value = "(опциональный, если другой уникальный-ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "(опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "(опциональный, если другой уникальный-ключ задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @ApiParam(value = "строка названия мерчанта на украинском", required = false) @RequestParam(value = "sMeasure_UA", required = false) String sMeasure_UA,
            HttpServletResponse response
    ) throws CommonServiceException {
        //выполняем проверку наличия аргументов

        if (isArgsNull(nID, sID_UA, sName_UA, sMeasure_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "at least some parameters need to execute this service: nID, sID_UA, sName_UA");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "at least some parameters need to execute this service: nID, sID_UA, sName_UA",
                    HttpStatus.FORBIDDEN
            );

        }
        //если nID не задан, то должны быть заданы другие параметры, чтобы вставить новую запись

        if (nID == null && (sID_UA == null || sName_UA == null || sMeasure_UA == null)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object",
                    HttpStatus.FORBIDDEN
            );

        }

        //если задан sID_UA, но его значение не совпадает с требуемым форматом (вида 0101 01 01 01) 
        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)",
                    HttpStatus.FORBIDDEN
            );

        }
        //проверяем допустимую длину символов sName_UA

        if (sName_UA != null && sName_UA.length() > 2000) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "length sName_UA is more than 2000");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "length sName_UA is more than 2000",
                    HttpStatus.FORBIDDEN
            );

        }

        if (sMeasure_UA != null && !isMeasureCorrect(sMeasure_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "sMeasure_UA is not correct");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "sMeasure_UA is not correct",
                    HttpStatus.FORBIDDEN
            );

        }

        ResponseEntity<String> result = null;
        Map<String, String> args = new HashMap<String, String>();

        //формируем переменные для setObjectCustoms        
        if (sID_UA != null) {
            args.put("sID_UA", sID_UA);
        }
        if (sName_UA != null) {
            args.put("sName_UA", sName_UA);
        }
        if (sMeasure_UA != null) {
            args.put("sMeasure_UA", sMeasure_UA);
        }
        //если nID — единственный аргумент, то работу не продолжаем, так как для обновления записи нужны еще другие аргументы
        if (nID != null && args.size() >= 1) {
            args.put("nID", nID.toString());
        } else if (args.size() == 0) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "nID is the only param, it is necessary else sID_UA or/and sName_UA or/and sMeasure_UA");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "nID is the only param, it is necessary else sID_UA or/and sName_UA or/and sMeasure_UA",
                    HttpStatus.FORBIDDEN
            );

        }

        try {
            ObjectCustoms pcode = this.objectCustomsDao.setObjectCustoms(args);
            result = JsonRestUtils.toJsonResponse(pcode);
        } catch (Exception e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
            );

        }
        return result;
    }

    /**
     * issue #968 - lesha1980; Запрос вида
     * /wf/service/object/removeObjectCustoms?; удаляет запись по уникальному
     * значению nID или sID_UA;
     *
     * @param nID (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)(формат 0101 01 01 01)
     * @param response
     */
    @ApiOperation(value = "Удаление записи по уникальному значению nID или sID_UA", notes = "##### ObjectController - Обьекты и смежные сущности. Удалить запись по уникальному значению nID или sID_UA #####\n\n"
            + "issue #968 - lesha1980;\n"
            + "Запрос вида /wf/service/object/removeObjectCustoms?;\n\n\n"
            + "- nID     (опциональный, если другой уникальный-ключ задан и по нему найдена запись)\n"
            + "- sID_UA  (опциональный, если другой уникальный-ключ задан и по нему найдена запись)(формат 0101 01 01 01)")
    @RequestMapping(value = "/removeObjectCustoms", method = RequestMethod.GET)
    public @ResponseBody
    void removeObjectCustoms(
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            HttpServletResponse response
    ) throws CommonServiceException {
        //проверяем наличие аргументов

        if (isArgsNull(nID, sID_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "at least one parameter need to execute this service: nID, sID_UA");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "at least one parameter need to execute this service: nID, sID_UA",
                    HttpStatus.FORBIDDEN
            );

        }
        //проверяем корректность sID_UA

        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)");
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)",
                    HttpStatus.FORBIDDEN
            );

        }

        Map<String, String> args = new HashMap<String, String>();

        if (nID != null) {
            args.put("nID", nID.toString());
        }
        if (sID_UA != null) {
            args.put("sID_UA", sID_UA);
        }

        try {
            this.objectCustomsDao.removeObjectCustoms(args);
        } catch (Exception e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
            );

        }

    }

}
