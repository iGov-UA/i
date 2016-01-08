package org.igov.service.controller;

import io.swagger.annotations.*;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.igov.model.SubjectDao;
import org.igov.model.SubjectHumanDao;
import org.igov.model.SubjectOrganDao;
import org.igov.model.Subject;
import org.igov.model.SubjectHuman;
import org.igov.model.SubjectHumanIdType;
import org.igov.model.SubjectOrgan;

import com.google.common.base.Optional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import static org.igov.debug.Log.oLogBig_Controller;
import org.igov.model.SubjectOrganJoin;
import org.igov.model.SubjectOrganJoinAttribute;
import org.igov.model.SubjectOrganJoinAttributeDao;
import org.igov.model.SubjectOrganJoinTax;
import org.igov.model.SubjectOrganJoinTaxDao;
import org.igov.service.controller.ActivitiExceptionController;
import org.igov.service.interceptor.exception.ActivitiRestException;
import static org.igov.util.Util.getCalculatedFormulaValue;
import static org.igov.util.Util.sCut;
import org.igov.util.convert.JsonRestUtils;

@Controller
@Api(tags = { "ActivitiRestSubjectController" }, description = "Работа с субъектами")
@RequestMapping(value = "/subject")
public class SubjectController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectController.class);

    private static final String JSON_TYPE = "Accept=application/json";
    
    @Autowired
    private SubjectOrganJoinTaxDao subjectOrganJoinTaxDao;
    
    
    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectHumanDao subjectHumanDao;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Autowired
    private SubjectOrganJoinAttributeDao subjectOrganJoinAttributeDao;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с субъектами. ";

    private static final String noteSyncSubject = noteController + "Получение субъекта #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/subject/syncSubject\n\n\n"
		+ "Если субъект найден, или добавление субъекта в противном случае\n\n"

		+ "От клиента ожидается ОДИН и только ОДИН параметр из нижеперечисленных\n\n"

		+ "- nID - ИД-номер субъекта\n"
		+ "- sINN - строка-ИНН (субъект - человек)\n"
		+ "- sOKPO - строка-ОКПО (субъек - организация)\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n\n"
		+ "Примеры:\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/syncSubject?sINN=34125265377\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/syncSubject?sOKPO=123\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/syncSubject?nID=1\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"nID\":150,\n"
		+ "    \"sID\":\"34125265377\",\n"
		+ "    \"sLabel\":null,\n"
		+ "    \"sLabelShort\":null\n"
		+ "}\n"
		+ noteCODE;

    private static final String noteSetSubjectHuman = noteController + "Нет описания #####\n\n";
 
    private static final String noteSetSubjectOrgan = noteController + "Нет описания #####\n\n";

    private static final String noteGetSubjectHuman = noteController + "Получение объекта SubjectHuman по номеру #####\n\n"
    		+ "HTTP Context: http://server:port/wf/service/subject/getSubjectHuman\n\n\n"
		+ "Параметр\n\n"
		+ "- nID_Subject - ИД-номер субъекта\n"
		+ "Если объект с заданным номером на найден - возвращается код 404\n"
		+ "Примеры:\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/getSubjectHuman?nID_Subject=1\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "{\"oSubject\":\n"
				+ "\t{\"sID\":\"2872618515\",\n"
				+ "\t\"sLabel\":\"Белявцев Владимир Владимирович\",\n"
				+ "\t\"sLabelShort\":\"Белявцев В. В.\",\n"
				+ "\t\"nID\":2},\n"
		+ "\"sINN\":\"2872618515\",\n"
		+ "\"sSB\":\"314542353425125\",\n"
		+ "\"sPassportSeria\":\"AN\",\n"
		+ "\"sPassportNumber\":\"11223344\",\n"
		+ "\"sFamily\":\"Белявцев\",\n"
		+ "\"sSurname\":\"Владимирович\",\n"
		+ "\"nID\":1,\n"
		+ "\"sName\":\"Владимир\"}\n"
		+ noteCODE;

    
    private static final String noteGetSubjectOrgan = noteController + "Получение объекта SubjectOrgan по номеру #####\n\n"
    		+ "HTTP Context: http://server:port/wf/service/subject/getSubjectOrgan\n\n\n"
		+ "Параметр\n\n"
		+ "- nID_Subject - ИД-номер субъекта\n"
		+ "Если объект с заданным номером на найден - возвращается код 404\n"
		+ "Примеры:\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/getSubjectOrgan?nID_Subject=1\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "{\"oSubject\":\n"
		+ "\t{\"sID\":\"ПАО\",\n"
		+ "\t\"sLabel\":\"ПАО ПриватБанк\",\n"
		+ "\t\"sLabelShort\":\"ПриватБанк\",\n"
		+ "\t\"nID\":1},\n"
		+ "\"sOKPO\":\"093205\",\n"
		+ "\"sFormPrivacy\":\"ПАО\",\n"
		+ "\"sNameFull\":\"Банк ПриватБанк\",\n"
		+ "\"nID\":1,\n"
		+ "\"sName\":\"ПриватБанк\"}\n"
		+ noteCODE;

    private static final String noteGetSubjectOrganJoinTax = noteController + "Возвратить весь список таможенных органов #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/subject/getSubjectOrganJoinTax\n\n\n"
		+ "Возвращает весь список таможенных органов (залит справочник согласно Державна фіскальна служба України. Офіційний портал)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/subject/getSubjectOrganJoinTax\n";

    private static final String noteSetSubjectOrganJoinTax = noteController + "Изменить cубьект-орган, или вставить #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/subject/setSubjectOrganJoinTax\n\n\n"
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

    private static final String noteGetAllSubjectOrganJoins = noteController + "Получает весь массив объектов п.2 (либо всех либо в рамках заданных в запросе nID_Region, nID_City или sID_UA) #####\n\n"
		+ "Параметры:\n\n"
		+ "- nID_SubjectOrgan - ИД-номер (в урл-е)\n"
		+ "- nID_Region - ИД-номер (в урл-е) //опциональный (только если надо задать или задан)\n"
		+ "- nID_City - ИД-номер (в урл-е) //опциональный (только если надо задать или задан)\n"
		+ "- sID_UA - ИД-строка (в урл-е) //опциональный (только если надо задать или задан)\n\n"
		+ "Пример ответа:\n"
		+ noteCODEJSON
		+ "[\n"
		+ "    {\n"
		+ "    	\"nID_SubjectOrgan\":32343  // nID - ИД-номер автоитеррируемый (уникальный, обязательный) (long)\n"
		+ "        ,\"sNameUa\":\"Українська мова\"  // sNameUa - ИД-строка <200 символов\n"
		+ "        ,\"sNameRu\":\"Русский язык\"  // sNameRu - строка <200 символов\n"
		+ "        ,\"sID_Privat\":\"12345\"  // sID_Privat - ИД-строка ключ-частный <60 символов //опциональный\n"
		+ "        ,\"sID_Public\":\"130501\"  // sID_Public - строка ключ-публичный <60 символов\n"
		+ "        ,\"sGeoLongitude\":\"15.232312\"  // sGeoLongitude - строка долготы //опциональный\n"
		+ "        ,\"sGeoLatitude\":\"23.234231\"  // sGeoLatitude - строка широты //опциональный\n"
		+ "        ,\"nID_Region\":11  // nID_Region - ИД-номер //опциональный\n"
		+ "        ,\"nID_City\":33  // nID_City - ИД-номер //опциональный\n"
		+ "        ,\"sID_UA\":\"1\"  // sID_UA - ИД-строка кода классификатора КОАТУУ //опциональный\n"
		+ "    }\n"
		+ "]\n"
		+ noteCODE
		+ "Пример:\n\n"
		+ "https://test.igov.org.ua/wf/service/subject/getSubjectOrganJoins?nID_SubjectOrgan=1&sID_UA=1\n";

    private static final String noteSetSubjectOrganJoin = noteController + "Добавить/обновить массив объектов п.2 (сопоставляя по ИД, и связывая новые с nID_Region, nID_City или sID_UA, по совпадению их названий) #####\n\n"
		+ "- nID_SubjectOrgan - ИД-номер\n"
		+ "- nID //опциональный, если добавление\n"
		+ "- sNameRu //опциональный\n"
		+ "- sNameUa //опциональный\n"
		+ "- sID_Privat //опциональный\n"
		+ "- sID_Public //опциональный, если апдейт\n"
		+ "- sGeoLongitude //опциональный\n"
		+ "- sGeoLatitude //опциональный\n"
		+ "- nID_Region //опциональный\n"
		+ "- nID_City //опциональный\n"
		+ "- sID_UA //опциональный\n\n\n"
		+ "Пример:\n"
		+ "https://test.igov.org.ua/wf/service/services/setSubjectOrganJoin?nID_SubjectOrgan=1&sNameRu=Днепр.РОВД\n";

    private static final String noteRemoveSubjectOrganJoins = noteController + "Удаление массива объектов п.2 (находя их по ИД) #####\n\n"
		+ "- nID_SubjectOrgan - ИД-номер (в урл-е)\n"
		+ "- asID_Public - массив ИД-номеров (в урл-е) (например [3423,52354,62356,63434])\n\n"
		+ "Пример: \n"
		+ "https://test.igov.org.ua/wf/service/services/removeSubjectOrganJoins?nID_SubjectOrgan=1&asID_Public=130505,130506,130507,130508\n";    
    

    /**
     * получение субъекта, если таков найден, или добавление субъекта в противном случае
     */
    @ApiOperation(value = "Получение субъекта", notes = noteSyncSubject )
    @RequestMapping(value = "/syncSubject", method = RequestMethod.GET, headers = { "Accept=application/json" })
    public
    @ResponseBody
    Subject syncSubject(
	    @ApiParam(value = "ИД-номер субъекта", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "строка-ИНН (субъект - человек)", required = false) @RequestParam(required = false) String sINN,
        @ApiParam(value = "номер-ИД типа идентификации субьекта-человека (по умолчанию 0)", required = false)
            @RequestParam(required = false, defaultValue = "0") int nID_SubjectHumanIdType,
        @ApiParam(value = "строка-код, параметр-идентификатора субьекта (без префикса типа)", required = false)
            @RequestParam(required = false) String sCode_Subject,
        @ApiParam(value = "строка-ОКПО (субъек - организация)", required = false) @RequestParam(required = false) String sOKPO,
            HttpServletResponse httpResponse) {

        LOG.info("--- syncSubject ---");
        Subject subject;
        if (nID != null) {
            subject = subjectDao.getSubject(nID);
        } else if (StringUtils.isNotEmpty(sINN)) {
            SubjectHuman oSubjectHuman = subjectHumanDao.getSubjectHuman(sINN);
            if (oSubjectHuman == null) {
                oSubjectHuman = subjectHumanDao.saveSubjectHuman(sINN);
            }
            subject = oSubjectHuman.getoSubject();
        } else if (StringUtils.isNotEmpty(sCode_Subject)) {
            SubjectHumanIdType subjectHumanIdType = SubjectHumanIdType.fromId(nID_SubjectHumanIdType);

            SubjectHuman oSubjectHuman = subjectHumanDao.getSubjectHuman(subjectHumanIdType, sCode_Subject);
            if (oSubjectHuman == null) {
                oSubjectHuman = subjectHumanDao.saveSubjectHuman(subjectHumanIdType, sCode_Subject);
            }
            subject = oSubjectHuman.getoSubject();
        } else if (StringUtils.isNotEmpty(sOKPO)) {
            SubjectOrgan subjectOrgan = subjectOrganDao.getSubjectOrgan(sOKPO);
            if (subjectOrgan == null) {
                subjectOrgan = subjectOrganDao.setSubjectOrgan(sOKPO);
            }
            subject = subjectOrgan.getoSubject();
        }
        else {
            throw new ActivitiObjectNotFoundException(
                    "RequestParam not found! You should add nID or  sINN or sINN, " +
                            "or (nID_SubjectHumanIdType + sCode_Subject) or sOKPO param!", Subject.class);
        }
        if (subject == null) {
            throw new ActivitiObjectNotFoundException(
                    String.format("Subject not found and not created! nID = %s sINN = %s, nID_SubjectHumanIdType = %s, " +
                            "sCode_Subject = %s sOKPO = %s", nID, sINN, nID_SubjectHumanIdType, sCode_Subject, sOKPO),
                    Subject.class);
        }
        httpResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        return subject;
    }

    @ApiOperation(value = "/saveSubjectHuman", notes = noteSetSubjectHuman )
    @RequestMapping(value = "/setSubjectHuman", method = RequestMethod.POST, headers = { "Accept=application/json" })
    public
    @ResponseBody
    SubjectHuman setSubject(@RequestBody SubjectHuman subjectHuman) {
        return subjectHumanDao.saveOrUpdateHuman(subjectHuman);
    }

    @ApiOperation(value = "/setSubjectOrgan", notes = noteSetSubjectOrgan )
    @RequestMapping(value = "/setSubjectOrgan", method = RequestMethod.POST, headers = { "Accept=application/json" })
    public
    @ResponseBody
    SubjectOrgan setSubject(@RequestBody SubjectOrgan subjectOrgan) {
        return subjectOrganDao.saveOrUpdateSubjectOrgan(subjectOrgan);
    }
    
    @ApiOperation(value = "/getSubjectHuman", notes = noteGetSubjectHuman )
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Record not found") } )
    @RequestMapping(value = "/getSubjectHuman", method = RequestMethod.GET)
    public @ResponseBody SubjectHuman getSubjectHuman(@ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject) throws ActivitiRestException {
    	Optional<SubjectHuman> subjectHuman = subjectHumanDao.findById(nID_Subject);
    	if (subjectHuman.isPresent()){
    		return subjectHuman.get();
    	}
    	throw new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                "Record not found",
                HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "/getSubjectOrgan", notes = noteGetSubjectOrgan )
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Record not found") } )
    @RequestMapping(value = "/getSubjectOrgan", method = RequestMethod.GET)
    public @ResponseBody SubjectOrgan getSubjectOrgan(@ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject) throws ActivitiRestException {
    	Optional<SubjectOrgan> subjectOrgan = subjectOrganDao.findById(nID_Subject);
    	if (subjectOrgan.isPresent()){
    		return subjectOrgan.get();
    	}
    	throw new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                "Record not found",
                HttpStatus.NOT_FOUND);
    }
    

    
    
    
    

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


    

    @ApiOperation(value = "Получает весь массив объектов джоинов субьектов-органа п.2 (либо всех либо в рамках заданных в запросе nID_Region, nID_City или sID_UA)", notes = noteGetAllSubjectOrganJoins )
    @RequestMapping(value = "/getSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<SubjectOrganJoin> getAllSubjectOrganJoins(
	    //@ApiParam(value = "ИД-номер Джоина Субьекта-органа", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Субьекта-органа", required = true) @RequestParam(value = "nID_SubjectOrgan") Long nID_SubjectOrgan,
	    @ApiParam(value = "ИД-номер места-региона (deprecated)", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
	    @ApiParam(value = "ИД-номер места-города (deprecated)", required = false) @RequestParam(value = "nID_City", required = false) Long nID_City,
	    @ApiParam(value = "ИД-строка места (унифицировано)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA
    ) {
        
        List<SubjectOrganJoin> aSubjectOrganJoin = subjectOrganDao.findSubjectOrganJoinsBy(nID_SubjectOrgan, nID_Region, nID_City, sID_UA);
        return aSubjectOrganJoin;
    }
    
    @ApiOperation(value = "Получает весь массив итрибутом джоина субьекта-органа", notes = noteGetAllSubjectOrganJoins )
    @RequestMapping(value = "/getSubjectOrganJoinAttributes",
            method = RequestMethod.POST,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<SubjectOrganJoinAttribute> getAllSubjectOrganJoinAttributes(
	    @ApiParam(value = "ИД-номер Джоина Субьекта-органа", required = true) @RequestParam(value = "nID", required = true) Long nID,
	    @ApiParam(value = "Карта кастомніх атрибутов", required = true) @RequestBody String smAttributeCustom
    ) {
        
        LOG.info("(nID_SubjectOrganJoin={},smAttributeCustom(short)={})", nID, sCut(50, smAttributeCustom));
        oLogBig_Controller.info("(nID_SubjectOrganJoin={},smAttributeCustom(short)={})", nID, smAttributeCustom);
        
        Map<String, String> mAttributeCustom = JsonRestUtils.readObject(smAttributeCustom, Map.class);
        LOG.info("(mAttributeCustom(cuted)={}",sCut(50, mAttributeCustom.toString()));
        oLogBig_Controller.info("(mAttributeCustom(cuted)={}",mAttributeCustom.toString());
        
        Map<String, Object> mAttributeReturn = new HashMap();
        List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute = subjectOrganJoinAttributeDao.getSubjectOrganJoinAttributesByParent(nID);
        List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute_Return = new LinkedList();
        if (aSubjectOrganJoinAttribute == null) {
            aSubjectOrganJoinAttribute = new LinkedList();
        }
                
        for (Map.Entry<String, String> oAttributeCustom : mAttributeCustom.entrySet()) {
            String sValue = oAttributeCustom.getValue();
            if (sValue == null || !sValue.startsWith("=")) {
                //oSubjectOrganJoin.addAttribute(oAttributeCustom.getKey(), oAttributeCustom.getValue());
                //aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                mAttributeReturn.put(oAttributeCustom.getKey(), sValue);
            }
        }

        for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
            String sValue = oSubjectOrganJoinAttribute.getValue();
            if (sValue == null || !sValue.startsWith("=")) {
                //oSubjectOrganJoin.addAttribute(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), sValue);
            }
        }

        for (Map.Entry<String, String> oAttributeCustom : mAttributeCustom.entrySet()) {
            String sValue = oAttributeCustom.getValue();
            String sName = oAttributeCustom.getKey();
            if (sValue != null && sValue.startsWith("=")) {
                sValue = getCalculatedFormulaValue(sValue, mAttributeReturn);
                //oSubjectOrganJoin.addAttribute(sName, sValue);
                SubjectOrganJoinAttribute oSubjectOrganJoinAttribute = new SubjectOrganJoinAttribute();
                oSubjectOrganJoinAttribute.setId(nID);
                oSubjectOrganJoinAttribute.setSubjectOrganJoinId(nID);
                oSubjectOrganJoinAttribute.setName(sName);
                oSubjectOrganJoinAttribute.setValue(sValue);
                aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                mAttributeReturn.put(sName, sValue);
            }
        }

        for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
            String sValue = oSubjectOrganJoinAttribute.getValue();
            if (sValue != null && sValue.startsWith("=")) {
                sValue = getCalculatedFormulaValue(sValue, mAttributeReturn);
                oSubjectOrganJoinAttribute.setValue(sValue);
                //oSubjectOrganJoin.addAttribute(oSubjectOrganJoinAttribute.getName(), sValue);
                aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), sValue);
            }
        }

        LOG.info("(mAttributeReturn(cuted)={}",sCut(50, mAttributeReturn.toString()));
        oLogBig_Controller.info("(mAttributeReturn(cuted)={}",mAttributeReturn.toString());
        return aSubjectOrganJoinAttribute_Return;
    }        
    


    @ApiOperation(value = "Добавить/обновить массив объектов", notes = noteSetSubjectOrganJoin )
    @RequestMapping(value = "/setSubjectOrganJoin",
            method = RequestMethod.GET,
            headers = { "Accept=application/json" })
    public
    @ResponseBody
    void setSubjectOrganJoin(
	    @ApiParam(value = "ИД-номер", required = true) @RequestParam(value = "nID_SubjectOrgan") Long organID,
	    @ApiParam(value = "ИД-строка ", required = true) @RequestParam(value = "sNameUa") String nameUA,
	    @ApiParam(value = "строка", required = true) @RequestParam(value = "sNameRu") String nameRU,
	    @ApiParam(value = "ИД-строка ключ-частный <60 символов", required = true) @RequestParam(value = "sID_Privat") String privateID,
	    @ApiParam(value = "строка ключ-публичный <60 символов", required = true) @RequestParam(value = "sID_Public") String publicID,
	    @ApiParam(value = "строка долготы", required = true) @RequestParam(value = "sGeoLongitude") String geoLongitude,
	    @ApiParam(value = "строка широты", required = true) @RequestParam(value = "sGeoLatitude") String geoLatitude,
	    @ApiParam(value = "ИД-строка", required = true) @RequestParam(value = "sID_UA") String uaID,
	    @ApiParam(value = "ИД-номер", required = false) @RequestParam(value = "nID_Region", required = false) Long regionID,
	    @ApiParam(value = "ИД-номер", required = false) @RequestParam(value = "nID_City", required = false) Long cityID
    ) {
        SubjectOrganJoin soj = new SubjectOrganJoin();
        soj.setUaId(uaID);
        soj.setSubjectOrganId(organID);
        soj.setNameUa(nameUA);
        soj.setNameRu(nameRU);
        soj.setPrivatId(privateID);
        soj.setPublicId(publicID);
        soj.setGeoLongitude(geoLongitude);
        soj.setGeoLatitude(geoLatitude);
        soj.setRegionId(regionID);
        soj.setCityId(cityID);
        subjectOrganDao.add(soj);
    }

    @ApiOperation(value = "Удаление массива объектов п.2 (находя их по ИД)", notes = noteRemoveSubjectOrganJoins )
    @RequestMapping(value = "/removeSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = { "Accept=application/json" })
    public
    @ResponseBody
    void removeSubjectOrganJoins(
	    @ApiParam(value = "ИД-номер ", required = true) @RequestParam(value = "nID_SubjectOrgan") Long organID,
	    @ApiParam(value = "массив ИД-номеров (в урл-е) (например [3423,52354,62356,63434])", required = true) @RequestParam(value = "asID_Public") String[] publicIDs) {

        subjectOrganDao.removeSubjectOrganJoin(organID, publicIDs);
    }
    
    
    
}
