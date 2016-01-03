package org.igov.service.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.SubjectOrganJoinTaxDao;
import org.igov.model.SubjectOrganJoinTax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */

@Controller
@Api(tags = { "ActivityRestSubjectOrganJoinTaxController" }, description = "Субьекты-органы - Филиалы - Таможенные")
@RequestMapping(value = "/services")
public class ActivityRestSubjectOrganJoinTaxController {
    private static final Logger oLog = Logger.getLogger(ActivityRestSubjectOrganJoinTaxController.class);

    private static final String JSON_TYPE = "Accept=application/json";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Субьекты-органы - Филиалы - Таможенные ";

    private static final String noteGetSubjectOrganJoinTax = noteController + "Возвратить весь список таможенных органов #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/getSubjectOrganJoinTax\n\n\n"
		+ "Возвращает весь список таможенных органов (залит справочник согласно Державна фіскальна служба України. Офіційний портал)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getSubjectOrganJoinTax\n";

    private static final String noteSetSubjectOrganJoinTax = noteController + "Изменить cубьект-орган, или вставить #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/setSubjectOrganJoinTax\n\n\n"
		+ "Апдейтит элемент (если задан один из уникальных ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта.\n\n"
		+ "Параметры:\n\n"
		+ "- nID - ИД-номер, идентификатор записи\n"
		+ "- sID_UA - ИД-номер Код, в Украинском классификаторе (уникальное)\n"
		+ "- sName_UA - название на украинском (строка до 190 символов)\n\n\n"
		+ "Если нет ни одного параметра возвращает ошибку 403. All args are null! Если есть один из уникальных ключей,"
		+ " но запись не найдена -- ошибка 403. Record not found! Если кидать \"новую\" запись с одним из уже существующих ключей sID_UA -- то обновится существующая запись по ключу sID_UA, если будет дублироваться другой ключ -- ошибка 403. Could not execute statement (из-за уникальных констрейнтов)\n";

    private static final String noteRemoveSubjectOrganJoinTax = noteController + "Удалить cубьект-орган, или вставить #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/removeSubjectOrganJoinTax\n\n\n"
		+ "Удаляет обьект по одному из двух ключей (nID, sID_UA) или кидает ошибку 403. Record not found!.\n\n"
		+ "Параметры:\n\n"
		+ "- nID - ИД-номер, идентификатор записи\n"
		+ "- sID_UA - ИД-номер Код, в Украинском классификаторе (уникальное)\n";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private SubjectOrganJoinTaxDao subjectOrganJoinTaxDao;

    /**
     * Отдает массив объектов сущности
     *
     * @param nIdSubjectOrganJoin ID региональной таможни
     * @return список таможенных органов, которые принаджлежат к соответсвующему региону
     */
    @ApiOperation(value = "Возвратить весь список таможенных органов", notes = noteGetSubjectOrganJoinTax )
    @RequestMapping(value = "/getSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    @ResponseBody
    public List<SubjectOrganJoinTax> getSubjectOrganJoinTax(@ApiParam(value = "ID региональной таможни", required = false) @RequestParam(value = "nID_SubjectOrganJoin", required = false) Integer nIdSubjectOrganJoin) {
        if (nIdSubjectOrganJoin == null) {
            return subjectOrganJoinTaxDao.findAll();
        } else {
            return subjectOrganJoinTaxDao.findAllBy("nIdSubjectOrganJoin", nIdSubjectOrganJoin);
        }
    }

    /**
     * Апдейтит элемент(если задан один из уникальных-ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта параметры.
     *
     * @param nId      (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param nIdSubjectOrganJoin (опциональный)
     * @param sIdUA   (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sNameUA (опциональный, если nID задан и по нему найдена запись)
     * @return SubjectOrganJoinTax object
     */
    @ApiOperation(value = "Изменить cубьект-орган, или вставить", notes = noteSetSubjectOrganJoinTax )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "All args are null") })
    @RequestMapping(value = "/setSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    @ResponseBody
    public SubjectOrganJoinTax setSubjectOrganJoinTax(@ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganJoin", required = false) Integer nIdSubjectOrganJoin,
	    @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "sID_UA", required = false) String sIdUA,
	    @ApiParam(value = "название на украинском (строка до 190 символов)", required = false) @RequestParam(value = "sName_UA", required = false) String sNameUA) {
        return subjectOrganJoinTaxDao.setSubjectOrganJoinTax(nId, nIdSubjectOrganJoin, sIdUA, sNameUA);
    }

    /**
     * Удаляет элемент(по ОБЯЗАТЕЛЬНО заданому одому из уникальных-ключей) параметры
     *
     * @param nId    (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sIdUA (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     */
    @ApiOperation(value = "Удалить cубьект-орган, или вставить", notes = noteRemoveSubjectOrganJoinTax )
    @RequestMapping(value = "/removeSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    @ResponseBody
    public void removeSubjectOrganJoinTax(@ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID", required = false) Long nId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String sIdUA) {
        subjectOrganJoinTaxDao.removeByKey(nId, sIdUA);
    }


}
