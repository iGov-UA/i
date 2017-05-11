package org.igov.service.business.object;

import org.igov.model.object.ObjectCustoms;
import org.igov.model.object.ObjectCustomsDao;
import org.igov.service.controller.ExceptionCommonController;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.igov.service.business.object.ObjectService.*;

/**
 * Created by Dmitriy Glushko on 18.04.17.
 */
@Component("objectCustomsService")
@Service
public class ObjectCustomsService {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectCustomsService.class);

    private static final String[] ERROR_MESSAGE = new String[] {
            "it must be set at least one parameter to execute this service: sID_UA, sName_UA",
            "sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)",
            "length sName_UA is more than 2000",
            "at least some parameters need to execute this service: nID, sID_UA, sName_UA",
            "need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object",
            "sMeasure_UA is not correct",
            "nID is the only param, it is necessary else sID_UA or/and sName_UA or/and sMeasure_UA",
            "at least one parameter need to execute this service: nID, sID_UA" };
    private static final String[] ERROR_MESSAGE_NO_RECORD = new String[] {
            "Record not found! No such Entity with sID_UA: ",
            "Record not found! No such Entity with sName_UA: " };

    @Autowired
    private ObjectCustomsDao objectCustomsDao;

    public ResponseEntity<String> getObjectCustoms(String sID_UA, String sName_UA, HttpServletResponse response)
            throws CommonServiceException {
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
                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, reason,
                        HttpStatus.NO_CONTENT);
            }
            result = JsonRestUtils.toJsonResponse(pcode_list);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage(),
                    HttpStatus.FORBIDDEN);
        }
        return result;
    }

    public ResponseEntity setObjectCustoms(Long nID, String sID_UA, String sName_UA, String sMeasure_UA,
            HttpServletResponse response) throws CommonServiceException {

        //выполняем проверку наличия аргументов
        if (isArgsNull(nID, sID_UA, sName_UA, sMeasure_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[3]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[3],
                    HttpStatus.FORBIDDEN);
        }
        //если nID не задан, то должны быть заданы другие параметры, чтобы вставить новую запись
        if (nID == null && (sID_UA == null || sName_UA == null || sMeasure_UA == null)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[4]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[4],
                    HttpStatus.FORBIDDEN);
        }

        //если задан sID_UA, но его значение не совпадает с требуемым форматом (вида 0101 01 01 01)
        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[1]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[1],
                    HttpStatus.FORBIDDEN);
        }
        //проверяем допустимую длину символов sName_UA
        if (sName_UA != null && sName_UA.length() > 2000) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[2]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[2],
                    HttpStatus.FORBIDDEN);
        }

        if (sMeasure_UA != null && !isMeasureCorrect(sMeasure_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[5]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[5],
                    HttpStatus.FORBIDDEN);
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
            response.setHeader("Reason", ERROR_MESSAGE[6]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[6],
                    HttpStatus.FORBIDDEN);
        }

        try {
            ObjectCustoms pcode = this.objectCustomsDao.setObjectCustoms(args);
            result = JsonRestUtils.toJsonResponse(pcode);
        } catch (Exception e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage(),
                    HttpStatus.FORBIDDEN);
        }
        return result;
    }

    public void removeObjectCustoms(Long nID, String sID_UA, HttpServletResponse response)
            throws CommonServiceException {

        //проверяем наличие аргументов
        if (isArgsNull(nID, sID_UA)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[7]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[7],
                    HttpStatus.FORBIDDEN);
        }

        //проверяем корректность sID_UA
        if (sID_UA != null && !isMatchSID(sID_UA, sid_pattern1)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", ERROR_MESSAGE[1]);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, ERROR_MESSAGE[1],
                    HttpStatus.FORBIDDEN);
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
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());

            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage(),
                    HttpStatus.FORBIDDEN);
        }
    }
}

