package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.object.ObjectCustoms;
import org.igov.model.object.ObjectCustomsDao;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.igov.service.business.object.ObjectService.*;

/**
 * Created by Dmitriy Glushko on 18.04.17.
 */

@Controller
@Api(tags = { "ObjectCustomsService -- Обьекты и смежные сущности" })
@RequestMapping(value = "/object")
public class ObjectCustomsService {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectCustomsService.class);

    private static final String[] ERROR_MESSAGE = new String[] {
            "it must be set at least one parameter to execute this service: sID_UA, sName_UA",
            "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)",
            "length sName_UA is more than 2000" };
    private static final String[] ERROR_MESSAGE_NO_RECORD = new String[] {
            "Record not found! No such Entity with sID_UA: ",
            "Record not found! No such Entity with sName_UA: " };

    @Autowired
    private ObjectCustomsDao objectCustomsDao;

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
            HttpServletResponse response
    ) throws CommonServiceException {
        //проверяем наличие аргументов
        if (isArgsNull(sID_UA, sName_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[0]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[0],
                    HttpStatus.FORBIDDEN);
        }
        //если задан sID_UA, но его значение не совпадает с требуемым форматом (вида 0101 01 01 01)
        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[1]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[1],
                    HttpStatus.FORBIDDEN);
        }
        //если sName_UA задан, но больше требуемого числа символов
        if (sName_UA != null && sName_UA.length() > 2000) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[2]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[2],
                    HttpStatus.FORBIDDEN);
        }

        ResponseEntity<String> result = null;
        Map<String, String> args = new HashMap<>();

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
                    reason = ERROR_MESSAGE_NO_RECORD[0] + sID_UA + ", sName_UA: " + sName_UA;
                } else if (sID_UA != null) {
                    reason = ERROR_MESSAGE_NO_RECORD[0] + sID_UA;
                } else if (sName_UA != null) {
                    reason = ERROR_MESSAGE_NO_RECORD[1] + sName_UA;
                }
                response.setHeader("Reason", reason);
                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                        reason, HttpStatus.NO_CONTENT);
            }
            result = JsonRestUtils.toJsonResponse(pcode_list);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return result;
    }
}
