package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.*;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.igov.model.subject.*;
import org.igov.model.subject.SubjectHumanRoleDao;
import org.igov.model.subject.organ.*;
import org.igov.service.business.subject.SubjectActionKVEDService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import org.igov.io.GeneralConfig;
import org.igov.service.business.subject.SubjectService;

import static org.igov.util.ToolJS.getCalculatedFormulaValue;

@Controller
@Api(tags = {"SubjectController - субъекты  и смежные сущности"})
@RequestMapping(value = "/subject")
public class SubjectController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectController.class);

    private static final String JSON_TYPE = "Accept=application/json";

    @Autowired
    public GeneralConfig generalConfig;
    
    
    @Autowired
    private SubjectOrganJoinTaxDao subjectOrganJoinTaxDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectHumanDao subjectHumanDao;
    
    @Autowired
    private SubjectHumanRoleDao subjectHumanRoleDao;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Autowired
    private SubjectContactDao subjectContactDao;

    @Autowired
    private SubjectOrganJoinAttributeDao subjectOrganJoinAttributeDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private SubjectAccountDao subjectAccountDao;

    @Autowired
    private SubjectAccountTypeDao subjectAccountTypeDao;

    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private SubjectActionKVEDService subjectActionKVEDService;

    /**
     * получение субъекта, если таков найден, или добавление субъекта в
     * противном случае
     */
    @ApiOperation(value = "Получение субъекта", notes = "##### SubjectController - Субъекты  и смежные сущности. Получение субъекта #####\n\n"
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
            + "\n```json\n"
            + "{\n"
            + "    \"nID\":150,\n"
            + "    \"sID\":\"34125265377\",\n"
            + "    \"sLabel\":null,\n"
            + "    \"sLabelShort\":null\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/syncSubject", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public @ResponseBody
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
        } else {
            throw new ActivitiObjectNotFoundException(
                    "RequestParam not found! You should add nID or  sINN or sINN, "
                    + "or (nID_SubjectHumanIdType + sCode_Subject) or sOKPO param!", Subject.class);
        }
        if (subject == null) {
            throw new ActivitiObjectNotFoundException(
                    String.format("Subject not found and not created! nID = %s sINN = %s, nID_SubjectHumanIdType = %s, "
                            + "sCode_Subject = %s sOKPO = %s", nID, sINN, nID_SubjectHumanIdType, sCode_Subject, sOKPO),
                    Subject.class);
        }
        httpResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        return subject;
    }

    @ApiOperation(value = "/saveSubjectHuman", notes = "##### SubjectController - Субъекты  и смежные сущности. Нет описания #####\n\n")
    @RequestMapping(value = "/setSubjectHuman", method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    SubjectHuman setSubject(@RequestBody SubjectHuman subjectHuman) {
        return subjectHumanDao.saveOrUpdateHuman(subjectHuman);
    }

    @ApiOperation(value = "/setSubjectOrgan", notes = "##### SubjectController - Субъекты  и смежные сущности. Нет описания #####\n\n")
    @RequestMapping(value = "/setSubjectOrgan", method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    SubjectOrgan setSubject(@RequestBody SubjectOrgan subjectOrgan) {
        return subjectOrganDao.saveOrUpdateSubjectOrgan(subjectOrgan);
    }

    @ApiOperation(value = "/getSubjectHuman", notes = "##### SubjectController - Субъекты  и смежные сущности. Получение объекта SubjectHuman по номеру #####\n\n"
            + "HTTP Context: http://server:port/wf/service/subject/getSubjectHuman\n\n\n"
            + "Если объект с заданным номером на найден - возвращается код 404\n"
            + "Примеры:\n\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectHuman?nID_Subject=1\n\n"
            + "Response\n"
            + "\n```json\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Record not found")})
    @RequestMapping(value = "/getSubjectHuman", method = RequestMethod.GET)
    public @ResponseBody
    SubjectHuman getSubjectHuman(@ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject) throws CommonServiceException {
        return getSubjectHuman_(nID_Subject);
    }

    private SubjectHuman getSubjectHuman_(Long nID_Subject) throws CommonServiceException {
        try {
          Optional<SubjectHuman> subjectHuman = subjectHumanDao.findById(nID_Subject);
        if (subjectHuman.isPresent()) {
            return subjectHuman.get();
        } else {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Record not found",
                    HttpStatus.NOT_FOUND);
        }  
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "/getSubjectOrgan", notes = "##### SubjectController - Субъекты  и смежные сущности. Получение объекта SubjectOrgan по номеру #####\n\n"
            + "HTTP Context: http://server:port/wf/service/subject/getSubjectOrgan\n\n\n"
            + "Если объект с заданным номером на найден - возвращается код 404\n"
            + "Примеры:\n\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectOrgan?nID_Subject=1\n\n"
            + "Response\n"
            + "\n```json\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Record not found")})
    @RequestMapping(value = "/getSubjectOrgan", method = RequestMethod.GET)
    public @ResponseBody
    SubjectOrgan getSubjectOrgan(@ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject) throws CommonServiceException {
        return getSubjectOrgan_(nID_Subject);
    }

    private SubjectOrgan getSubjectOrgan_(Long nID_Subject) throws CommonServiceException {
        Optional<SubjectOrgan> subjectOrgan = subjectOrganDao.findById(nID_Subject);
        if (subjectOrgan.isPresent()) {
            return subjectOrgan.get();
        } else {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Record not found",
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Отдает массив объектов сущности
     *
     * @param nIdSubjectOrganJoin ID региональной таможни
     * @return список таможенных органов, которые принаджлежат к соответсвующему
     * региону
     */
    @ApiOperation(value = "Возвратить весь список таможенных органов", notes = "##### SubjectController - Субъекты  и смежные сущности. Возвратить весь список таможенных органов #####\n\n"
            + "HTTP Context: https://server:port/wf/service/subject/getSubjectOrganJoinTax\n\n\n"
            + "Возвращает весь список таможенных органов (залит справочник согласно Державна фіскальна служба України. Офіційний портал)\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/subject/getSubjectOrganJoinTax\n")
    @RequestMapping(value = "/getSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    @ResponseBody
    public List<SubjectOrganJoinTax> getSubjectOrganJoinTax(@ApiParam(value = "ID региональной таможни", required = false) @RequestParam(value = "nID_SubjectOrganJoin", required = false) Integer nIdSubjectOrganJoin) {
        if (nIdSubjectOrganJoin == null) {
            return subjectOrganJoinTaxDao.findAll();
        } else {
            return subjectOrganJoinTaxDao.findAllBy("nIdSubjectOrganJoin", nIdSubjectOrganJoin);
        }
    }

    /**
     * Апдейтит элемент(если задан один из уникальных-ключей) или вставляет
     * (если не задан nID), и отдает экземпляр нового объекта параметры.
     *
     * @param nId (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param nIdSubjectOrganJoin (опциональный)
     * @param sIdUA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sNameUA (опциональный, если nID задан и по нему найдена запись)
     * @return SubjectOrganJoinTax object
     */
    @ApiOperation(value = "Изменить cубьект-орган, или вставить", notes = "##### SubjectController - Субъекты  и смежные сущности. Изменить cубьект-орган, или вставить #####\n\n"
            + "HTTP Context: https://server:port/wf/service/subject/setSubjectOrganJoinTax\n\n\n"
            + "Апдейтит элемент (если задан один из уникальных ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта.\n\n"
            + "Если нет ни одного параметра возвращает ошибку 403. All args are null! Если есть один из уникальных ключей,"
            + " но запись не найдена -- ошибка 403. Record not found! Если кидать \"новую\" запись с одним из уже существующих ключей sID_UA "
            + "-- то обновится существующая запись по ключу sID_UA, если будет дублироваться другой ключ -- ошибка 403. "
            + "Could not execute statement (из-за уникальных констрейнтов)\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "All args are null")})
    @RequestMapping(value = "/setSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    @ResponseBody
    public SubjectOrganJoinTax setSubjectOrganJoinTax(@ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nId,
            @ApiParam(value = "номер-ИД субьекта-органа джоина", required = false) @RequestParam(value = "nID_SubjectOrganJoin", required = false) Integer nIdSubjectOrganJoin,
            @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "sID_UA", required = false) String sIdUA,
            @ApiParam(value = "название на украинском (строка до 190 символов)", required = false) @RequestParam(value = "sName_UA", required = false) String sNameUA) {
        return subjectOrganJoinTaxDao.setSubjectOrganJoinTax(nId, nIdSubjectOrganJoin, sIdUA, sNameUA);
    }

    /**
     * Удаляет элемент(по ОБЯЗАТЕЛЬНО заданому одому из уникальных-ключей)
     * параметры
     *
     * @param nId (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sIdUA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     */
    @ApiOperation(value = "Удалить cубьект-орган, или вставить", notes = "##### SubjectController - Субъекты  и смежные сущности. Удалить cубьект-орган, или вставить #####\n\n"
            + "HTTP Context: https://server:port/wf/service/subject/removeSubjectOrganJoinTax\n\n\n"
            + "Удаляет обьект по одному из двух ключей (nID, sID_UA) или кидает ошибку 403. Record not found!.\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Record not found")})
    @RequestMapping(value = "/removeSubjectOrganJoinTax",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    @ResponseBody
    public void removeSubjectOrganJoinTax(@ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String sIdUA) {
        subjectOrganJoinTaxDao.removeByKey(nId, sIdUA);
    }

    @ApiOperation(value = "Получает весь массив объектов джоинов субьектов-органа п.2 (либо всех либо в рамках заданных в запросе nID_Region, nID_City или sID_UA)", notes = "##### SubjectController - Субъекты  и смежные сущности. Получает весь массив объектов п.2 (либо всех либо в рамках заданных в запросе nID_Region, nID_City или sID_UA) #####\n\n"
            + "Параметры:\n\n"
            + "Пример ответа:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nID_SubjectOrgan\":32343  // nID - ИД-номер автоитеррируемый (уникальный, обязательный) (long)\n"
            + "    ,\"sNameUa\":\"Українська мова\"  // sNameUa - ИД-строка <200 символов\n"
            + "    ,\"sNameRu\":\"Русский язык\"  // sNameRu - строка <200 символов\n"
            + "    ,\"sID_Privat\":\"12345\"  // sID_Privat - ИД-строка ключ-частный <60 символов //опциональный\n"
            + "    ,\"sID_Public\":\"130501\"  // sID_Public - строка ключ-публичный <60 символов\n"
            + "    ,\"sGeoLongitude\":\"15.232312\"  // sGeoLongitude - строка долготы //опциональный\n"
            + "    ,\"sGeoLatitude\":\"23.234231\"  // sGeoLatitude - строка широты //опциональный\n"
            + "    ,\"nID_Region\":11  // nID_Region - ИД-номер //опциональный\n"
            + "    ,\"nID_City\":33  // nID_City - ИД-номер //опциональный\n"
            + "    ,\"sID_UA\":\"1\"  // sID_UA - ИД-строка кода классификатора КОАТУУ //опциональный\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Пример:\n\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectOrganJoins?nID_SubjectOrgan=1&sID_UA=1\n")
    @RequestMapping(value = "/getSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public @ResponseBody
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

    @ApiOperation(value = "Получает весь массив атрибутов джоина субьекта-органа", notes = "##### SubjectController - Субъекты  и смежные сущности. Получает весь массив атрибутов джоина субьекта-органа #####\n\n")
    @RequestMapping(value = "/getSubjectOrganJoinAttributes",
            method = RequestMethod.POST,
            headers = {"Accept=application/json"})
    public @ResponseBody
    List<SubjectOrganJoinAttribute> getAllSubjectOrganJoinAttributes(
            @ApiParam(value = "ИД-номер Джоина Субьекта-органа", required = true) @RequestParam(value = "nID", required = true) Long nID,
            @ApiParam(value = "Карта кастомніх атрибутов", required = true) @RequestBody String smAttributeCustom
    ) {
        LOG.info("getAllSubjectOrganJoinAttributes smAttributeCustom: {}", smAttributeCustom);
        LOG.info("nID: {}", nID);
        //LOG.info("(nID_SubjectOrganJoin={},smAttributeCustom(short)={})", nID, sCut(50, smAttributeCustom));
        //oLogBig_Controller.info("(nID_SubjectOrganJoin={},smAttributeCustom(short)={})", nID, smAttributeCustom);
        Map<String, String> mAttributeCustom = JsonRestUtils.readObject(smAttributeCustom, Map.class);
        //LOG.info("(mAttributeCustom(cuted)={}", sCut(50, mAttributeCustom.toString()));
        //oLogBig_Controller.info("(mAttributeCustom(cuted)={}", mAttributeCustom.toString());

        Map<String, Object> mAttributeReturn = new HashMap();
        List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute = subjectOrganJoinAttributeDao.getSubjectOrganJoinAttributesByParent(nID);
        List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute_Return = new LinkedList();
        if (aSubjectOrganJoinAttribute == null) {
            aSubjectOrganJoinAttribute = new LinkedList();
        }

        for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
            String sValue = oSubjectOrganJoinAttribute.getValue();
            if (sValue == null || !sValue.startsWith("=")) {
                //oSubjectOrganJoin.addAttribute(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), sValue);
            }
        }

        for (Map.Entry<String, ?> oAttributeCustom : mAttributeCustom.entrySet()) {
            //try{
            //}catch(){
            //}
            if (oAttributeCustom.getValue() instanceof String) {
                String sValue = oAttributeCustom.getValue().toString();
                if (sValue == null || !sValue.startsWith("=")) {
                    //oSubjectOrganJoin.addAttribute(oAttributeCustom.getKey(), oAttributeCustom.getValue());
                    //aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                    mAttributeReturn.put(oAttributeCustom.getKey(), sValue);
                }
            }
        }

        for (Map.Entry<String, String> oAttributeCustom : mAttributeCustom.entrySet()) {
            if (oAttributeCustom.getValue() instanceof String) {
                String sValue = oAttributeCustom.getValue();
                String sName = oAttributeCustom.getKey();
                if (sValue != null && sValue.startsWith("=")) {
                    sValue = getCalculatedFormulaValue(sValue, mAttributeReturn);
                    //oSubjectOrganJoin.addAttribute(sName, sValue);
                    SubjectOrganJoinAttribute oSubjectOrganJoinAttribute = new SubjectOrganJoinAttribute();
                    oSubjectOrganJoinAttribute.setSubjectOrganJoinId(nID);
                    oSubjectOrganJoinAttribute.setName(sName);
                    oSubjectOrganJoinAttribute.setValue(sValue);
                    aSubjectOrganJoinAttribute_Return.add(oSubjectOrganJoinAttribute);
                    mAttributeReturn.put(sName, sValue);
                }
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

        //LOG.info("(mAttributeReturn(cuted)={}", sCut(50, mAttributeReturn.toString()));
        //oLogBig_Controller.info("(mAttributeReturn(cuted)={}", mAttributeReturn.toString());
        return aSubjectOrganJoinAttribute_Return;
    }

    @ApiOperation(value = "Добавить/обновить массив объектов", notes = "##### SubjectController - Субъекты  и смежные сущности. Добавить/обновить массив объектов п.2 (сопоставляя по ИД, и связывая новые с nID_Region, nID_City или sID_UA, по совпадению их названий) #####\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectOrganJoin?nID_SubjectOrgan=1&sNameRu=Днепр.РОВД\n")
    @RequestMapping(value = "/setSubjectOrganJoin",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public @ResponseBody
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

    @ApiOperation(value = "Удаление массива объектов п.2 (находя их по ИД)", notes = "##### SubjectController - Субъекты  и смежные сущности. Удаление массива объектов п.2 (находя их по ИД) #####\n\n"
            + "Пример: \n"
            + "https://test.igov.org.ua/wf/service/subject/removeSubjectOrganJoins?nID_SubjectOrgan=1&asID_Public=130505,130506,130507,130508\n")
    @RequestMapping(value = "/removeSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public @ResponseBody
    void removeSubjectOrganJoins(
            @ApiParam(value = "ИД-номер ", required = true) @RequestParam(value = "nID_SubjectOrgan") Long organID,
            @ApiParam(value = "массив ИД-номеров (в урл-е) (например [3423,52354,62356,63434])", required = true) @RequestParam(value = "asID_Public") String[] publicIDs) {

        subjectOrganDao.removeSubjectOrganJoin(organID, publicIDs);
    }

    /**
     * @param nID nID сервера.
     */
    @ApiOperation(value = "Получение информации о сервере", notes = "##### SubjectController - Субъекты  и смежные сущности. Получение информации о сервере #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/subject/getServer?nID=nID\n\n\n"
            + "возвращает json представление сущности Server, которая содержит информацию о сервере.\n\n"
            + "Примеры:\n"
            + "https://test.region.igov.org.ua/wf/service/subject/getServer?nID=0\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID\": \"Common_Region\",\n"
            + "    \"sType\": \"Region\",\n"
            + "    \"sURL_Alpha\": \"https://test.region.igov.org.ua/wf\",\n"
            + "    \"sURL_Beta\": \"https://test-version.region.igov.org.ua/wf\",\n"
            + "    \"sURL_Omega\": \"https://master-version.region.igov.org.ua/wf\",\n"
            + "    \"sURL\": \"https://region.igov.org.ua/wf\",\n"
            + "    \"nID\": 0\n"
            + "}\n"
            + "\n```\n"
            + "https://test.region.igov.org.ua/wf/service/subject/getServer?nID=-1\n"
            + "Ответ:\n"
            + "HTTP Status: 500 (internal server error)\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"BUSINESS_ERR\",\n"
            + "    \"message\": \"Record not found\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getServer", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getService(@ApiParam(value = "nID сервера", required = true) @RequestParam(value = "nID") Integer nID) throws RecordNotFoundException {
        
        nID = generalConfig.getServerId(nID);
        Optional<Server> serverOpt = serverDao.findById(Long.valueOf(nID));
        if (!serverOpt.isPresent()) {
            throw new RecordNotFoundException();
        }

        return JsonRestUtils.toJsonResponse(serverOpt.get());
    }

    /**
     *
     * @param snID_Subject - Строка ИД субъекта (автоматически добавляемые
     * параметр, опциональный)
     * @param sMailTo - Строка адрес электронной почты (опциональный)
     * @param sPhone - Строка номер телефона (опциональный)
     * @return - Возвращает список синхронизированных контактов
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Синхронизация контактов", notes = "#### Пример: \n"
            + "https://test.igov.org.ua/wf/service/subject/syncContacts?snID_Subject=2&sMailTo=test44@gmail.com&sPhone=654 \n"
            + "Ответ: \n"
            + "HTTP Status: 200 \n"
            + "\n```json\n"
            + "["
            + "{"
            + "\"subject\":{ \n"
            + "\"sID\":\"2872618515\",\n"
            + "\"sLabel\":\"Белявцев Владимир Владимирович\", \n"
            + "\"sLabelShort\":\"Белявцев В. В.\", \n"
            + "\"nID\":2 \n"
            + "},"
            + "\"subjectContactType\": \n"
            + "{ \n"
            + "\"sName_EN\":\"Email\", \n"
            + "\"sName_UA\":\"Електрона адреса\", \n"
            + "\"sName_RU\":\"Электнонный адрес\", \n"
            + "\"nID\":1 \n"
            + "}, \n"
            + "\"sValue\":\"test44@gmail.com\", \n"
            + "\"sDate\":\"2016-02-23 19:57:56.279\", \n"
            + "\"nID\":1003 \n"
            + "}, \n"
            + "{ \n"
            + "\"subject\": {\n"
            + "\"sID\":\"2872618515\", \n"
            + "\"sLabel\":\"Белявцев Владимир Владимирович\", \n"
            + "\"sLabelShort\":\"Белявцев В. В.\", \n"
            + "\"nID\":2 \n"
            + "}, \n"
            + "\"subjectContactType\": { \n"
            + "\"sName_EN\":\"Phone\", \n"
            + "\"sName_UA\":\"Телефон\", \n"
            + "\"sName_RU\":\"Телефон\", \n"
            + "\"nID\":0 \n"
            + "}, \n"
            + "\"sValue\":\"654\", \n"
            + "\"sDate\":\"2016-02-23 19:58:41.325\", \n"
            + "\"nID\":1002"
            + "} \n"
            + "] \n"
            + "\n```\n")
    @RequestMapping(value = "/syncContacts", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity syncContacts(
            @ApiParam(value = "Строка ИД субъекта (автоматически добавляемые параметр, опциональный)", required = false) @RequestParam(value = "snID_Subject", required = false) String snID_Subject,
            @ApiParam(value = "Строка адрес электронной почты", required = false) @RequestParam(value = "sMailTo", required = false) String sMailTo,
            @ApiParam(value = "Строка номер телефона", required = false) @RequestParam(value = "sPhone", required = false) String sPhone
    ) throws CommonServiceException {
        LOG.info("(Вход в contactsService sMailTo {}, snID_Subject {}, sPhone {})", sMailTo, snID_Subject, sPhone);
        List<SubjectContact> listContacts = subjectService.syncContactsService(snID_Subject, sMailTo, sPhone);

        return JsonRestUtils.toJsonResponse(listContacts);
    }

    @ApiOperation(value = "Получение аккаунтов субъектов", notes = "Возвращает список аккаунтов субъектов, согласно заданных параметров. "
            + "Обязательно должен быть задан один из параметров: **ID субъекта** или **Логин**. Если заданы оба, **Логин** игнорируется\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectAccounts?nID_Subject=1&nID_Server=1\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "	   \"sLogin\": \"Логин01\",\n"
            + "	   \"sNote\": \"Примечание_1\",\n"
            + "	   \"subjectAccountType\":\n"
            + "	   {\n"
            + "	     \"sID\": \"iGov\",\n"
            + "	     \"sNote\": \"iGov\",\n"
            + "	     \"nID\": 1\n"
            + "	   },\n"
            + "	   \"nID_Server\": 1,\n"
            + "	   \"nID_Subject\": 1,\n"
            + "	   \"nID\": 1\n"
            + "	 },\n"
            + "	 {\n"
            + "	   \"sLogin\": \"Логин01\",\n"
            + "	   \"sNote\": \"Примечание_2\",\n"
            + "	   \"subjectAccountType\":\n"
            + "	     {\n"
            + "	       \"sID\": \"PD\",\n"
            + "	       \"sNote\": \"PD\",\n"
            + "	       \"nID\": 2\n"
            + "	       },\n"
            + "    \"nID_Server\": 1,\n"
            + "	   \"nID_Subject\": 1,\n"
            + "	   \"nID\": 2\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Пример с ошибкой: не указаны обязательные параметры:\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectAccounts\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\"code\":\"BUSINESS_ERR\",\"message\":\"Error! Parameters nID_Subject and  sLogin is null\"}\n"
            + "\n```\n"
            + "Пример, если записи не найдены:\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectAccounts?nID_Subject=1&nID_Server=100\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"code\":\"BUSINESS_ERR\",\n"
            + "	 \"message\":\"Record not found\"\n"
            + "}\n"
            + "\n```\n"
    )
    @RequestMapping(value = "/getSubjectAccounts", method = RequestMethod.GET, headers = {JSON_TYPE})
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Ошибка в параметрах"),
        @ApiResponse(code = 404, message = "Record not found")
    })
    public @ResponseBody
    List<SubjectAccount> getSubjectAccounts(
            @ApiParam(value = "ID субъекта ( необходим, если не указан Логин )", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Логин ( необходим, если, не указан ID субъекта )", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "ID сервера", required = false) @RequestParam(value = "nID_Server", required = false) Long nID_Server,
            @ApiParam(value = "ID типа аккаунта", required = false) @RequestParam(value = "nID_SubjectAccountType", required = false) Long nID_SubjectAccountType
    ) throws CommonServiceException {

        List<SubjectAccount> subjectAccounts = null;

        // Один из параметров должен быть задан обязательно
        if (nID_Subject == null && sLogin == null) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Error! Parameters nID_Subject and  sLogin is null",
                    HttpStatus.BAD_REQUEST);
        }

        subjectAccounts = subjectAccountDao.findSubjectAccounts(nID_Subject, sLogin, nID_Server, nID_SubjectAccountType);
        if (subjectAccounts == null || subjectAccounts.size() == 0) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Record not found",
                    HttpStatus.NOT_FOUND);
        }

        return subjectAccounts;
    }

    @ApiOperation(value = "Добавление/обновление аккаунта субъекта", notes = "Добавляет или обновляет аккаунт субъекта в зависимости от того задан или нет параметр **nID**. "
            + "В случае успешного выполнения возвращается измененная или добавленная сущность.\n"
            + "Пример добавления записи:\n"
            + "**(Важно: обязательно измените любой параметр (не sNote), иначе получите ошибку ограничения уникальности записи)**\n\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectAccount?nID_Subject=1&sLogin=mylog&nID_Server=55&nID_SubjectAccountType=2&sNote=Заметка_про_что-то\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"sLogin\": \"mylog\",\n"
            + "  \"sNote\": \"Заметка про что-то\",\n"
            + "  \"subjectAccountType\": {\n"
            + "    \"sID\": \"PD\",\n"
            + "    \"sNote\": \"PD\",\n"
            + "    \"nID\": 2\n"
            + "  },\n"
            + "  \"nID_Server\": 5,\n"
            + "  \"nID_Subject\": 1,\n"
            + "  \"nID\": 103\n"
            + "}\n"
            + "\n```\n"
            + "Пример обновления записи:\n\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectAccount?nID=1&sNote=Заметка_обновленная\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"sLogin\": \"Логин01\",\n"
            + "  \"sNote\": \"Заметка обновленная\",\n"
            + "  \"subjectAccountType\": {\n"
            + "    \"sID\": \"iGov\",\n"
            + "    \"sNote\": \"iGov\",\n"
            + "    \"nID\": 1\n"
            + "  },\n"
            + "  \"nID_Server\": 1,\n"
            + "  \"nID_Subject\": 1,\n"
            + "  \"nID\": 1\n"
            + "}\n"
            + "\n```\n"
            + "Пример ошибочного добавления записи ( не заданы обязательные поля ):\n\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectAccount\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"code\": \"BUSINESS_ERR\",\n"
            + "  \"message\": \"Ошибка! При добавлении обязательны параметры: nID_Subject, sLogin, nID_Server, nID_SubjectAccountType\"\n"
            + "}\n"
            + "\n```\n"
            + "Пример ошибочного добавления записи ( нарушение уникальности аккаунта ):\n\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectAccount?nID_Subject=1&sLogin=Логин01&nID_Server=1&nID_SubjectAccountType=1\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"code\": \"SYSTEM_ERR\",\n"
            + "  \"message\": \"could not execute statement; SQL [n/a]; constraint [SubjectAccount_sLogin_nID_SubjectAccountType_nID_Server_nID_key]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\"\n"
            + "}\n"
            + "\n```\n"
            + "Пример ошибочного обновление записи ( не существующий аккаунт ):\n\n"
            + "https://test.igov.org.ua/wf/service/subject/setSubjectAccount?nID=777\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"code\": \"BUSINESS_ERR\",\n"
            + "  \"message\": \"Entity with id=777 does not exist\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/setSubjectAccount", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    SubjectAccount setSubjectAccount(
            @ApiParam(value = "ID аккаунта ( если не задан, то запись добавляется )", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ID субъекта ( необходим, при добавлении )", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Логин ( необходим, при добавлении )", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "ID сервера ( необходим, при добавлении )", required = false) @RequestParam(value = "nID_Server", required = false) Long nID_Server,
            @ApiParam(value = "ID типа аккаунта ( необходим, при добавлении )", required = false) @RequestParam(value = "nID_SubjectAccountType", required = false) Long nID_SubjectAccountType,
            @ApiParam(value = "Строка нотации", required = false) @RequestParam(value = "sNote", required = false) String sNote
    ) throws CommonServiceException {

        // Если запись добавляется, то должны быть указаны все обязательные параметры
        if (nID == null && (nID_Subject == null || sLogin == null || nID_Server == null || nID_SubjectAccountType == null)) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Ошибка! При добавлении обязательны параметры: nID_Subject, sLogin, nID_Server, nID_SubjectAccountType",
                    HttpStatus.BAD_REQUEST);
        }

        SubjectAccount subjectAccountRet = null;
        try {
            subjectAccountRet = subjectAccountDao.setSubjectAccount(nID, nID_Subject, sLogin, nID_Server, nID_SubjectAccountType, sNote);
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage() + ". Нарушение уникальности аккаунта", HttpStatus.BAD_REQUEST);
        }

        return subjectAccountRet;
    }

    private Map<String, Subject> getSubject(String saLogin, Long nID_SubjectAccountType, Long nID_Server) {
        Map<String, Subject> subjects = new HashMap();
        Long nID_Subject;
        Subject subject;
        if (saLogin != null) {
            Set<String> asLogin = JsonRestUtils.readObject(saLogin, Set.class);
            LOG.info("asLogin: " + asLogin);
            for (String login : asLogin) {
                List<SubjectAccount> subjectAccounts = subjectAccountDao.findSubjectAccounts(null, login, nID_Server, nID_SubjectAccountType);
                if (subjectAccounts != null && !subjectAccounts.isEmpty()) {
                    nID_Subject = subjectAccounts.get(0).getnID_Subject();
                    subject = subjectDao.getSubject(nID_Subject);
                    List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);
                    LOG.info("subjectContacts: " + subjectContacts);
                    subject.setaSubjectAccountContact(subjectContacts);
                    subjects.put(login, subject);
                }
            }
        }
        return subjects;
    }

    @ApiOperation(value = "Получение полного набора данных по субъектам", notes = "Получаем полный набор данных по субъектам. "
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/getSubjectsBy\n\n"
            + "что-бы протестировать эту чать кода надо 1) запустить проэкт 2)ввести дефолтные парольи логин из 'нашего хозяйства' "
            + " 3)ввести в адресную строку типа этой (без слешей)http://localhost:8080/service/subject/getSubjectsBy?nID_Server=0&saAccount=[\"Barmaley\",\"GrekD\"] 4)ввести вторые логин и пароль из 'нашего хозяйства' "
            + "Ответ:\n")
    @RequestMapping(value = "/getSubjectsBy", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    Map<String, List<NewSubjectAccount>> getSubjectsBy(
            @ApiParam(value = "Массив с логинами чиновников в виде json", required = false) @RequestParam(value = "saAccount", required = true) String saAccount,
            @ApiParam(value = "Ид сервера", required = false) @RequestParam(value = "nID_Server", required = false) Long nID_Server,
            @ApiParam(value = "Не показывать подробности про организации и чиновников", required = false, defaultValue = "false") @RequestParam(value = "bSkipDetails", required = false, defaultValue = "false") boolean bSkipDetails,
            @ApiParam(value = "Массив с типами аакаунтов  в виде json", required = false) @RequestParam(value = "nID_SubjectAccountType", required = false, defaultValue = "1") Long nID_SubjectAccountType) throws CommonServiceException {

        Map<String, List<NewSubjectAccount>> result = new HashMap<>();
        SubjectAccountType subjectAccountType = subjectAccountTypeDao.findByIdExpected(nID_SubjectAccountType);
        if(nID_Server!=null){
            nID_Server = Long.valueOf(generalConfig.getServerId(Integer.valueOf(nID_Server+""))+"");
        }
        
        if (subjectAccountType == null) {
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Error! SubjectAccountType not found for id=" + nID_SubjectAccountType, HttpStatus.NOT_FOUND);
        } else {
            result.put("aSubjectAccount", getSubjectBy(saAccount, subjectAccountType.getId(), nID_Server, bSkipDetails));
            return result;
        }
    }

    private List<NewSubjectAccount> getSubjectBy(String saLogin, Long nID_SubjectAccountType, Long nID_Server, boolean bSkipDetails) {
        List<NewSubjectAccount> newSubjectSet = new ArrayList<>();
        Long nID_Subject;
        //LOG.info("nID_Subject: " + nID_Subject);
        Subject subject;
//         LOG.info("subject: " + subject);
        LOG.info("saLogin: " + saLogin);
        if (saLogin != null) {
            Set<String> asLogin = JsonRestUtils.readObject(saLogin, Set.class);
            LOG.info("saLogin: " + saLogin);
            for (String login : asLogin) {
                LOG.info("login: "+login+" nID_Server: "+nID_Server+" nID_SubjectAccountType: "+nID_SubjectAccountType);
                List<SubjectAccount> subjectAccounts = subjectAccountDao.findSubjectAccounts(null, login, nID_Server, nID_SubjectAccountType);
                LOG.info("login: "+login+" nID_Server: "+nID_Server+" nID_SubjectAccountType: "+nID_SubjectAccountType);
                LOG.info("subjectAccounts: " + subjectAccounts);
                for (SubjectAccount subjectAccount : subjectAccounts) {
                    LOG.info("subjectAccount: "+subjectAccount);
                }
                if (subjectAccounts != null && !subjectAccounts.isEmpty()) {
                    for (SubjectAccount subjectAccount : subjectAccounts) {
                        nID_Subject = subjectAccount.getnID_Subject();
                        
                        LOG.info("nID_Subject: " + nID_Subject);
                        subject = subjectDao.getSubject(nID_Subject);
                        List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);
                        LOG.info("subjectContacts: " + subjectContacts);
                        subject.setaSubjectAccountContact(subjectContacts);
                        SubjectHuman subjectHuman = subjectHumanDao.getSubjectHuman(subject);
                        SubjectOrgan subjectOrgan = subjectOrganDao.getSubjectOrgan(subject);
                        newSubjectSet.add(NewSubjectAccount.getNewSubjectAccount(bSkipDetails, subject, login, subjectHuman, subjectOrgan));
                    }
                }
            }
        }
        return newSubjectSet;
    }

    @ApiOperation(value = "Получение данных из справочника КВЕД", notes = "Получаем данные из справочника КВЕД. "
            + "Пример:\n"
            + "https://alpha.test.igov.org.ua/wf/service/subject/getActionKVED?sFind=рибальство\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sID\": \"03.11\",\n"
            + "    \"sNote\": \"Морське рибальство \",\n"
            + "    \"nID\": 36\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"03.12\",\n"
            + "    \"sNote\": \"Прісноводне рибальство \",\n"
            + "    \"nID\": 37\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getActionKVED", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    List<SubjectActionKVED> getActionKVED(
            @ApiParam(value = "sFind - кретерий поиска в sID или sNote (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sFind", required = true) String sFind)
        	    throws CommonServiceException {
	return subjectActionKVEDService.getSubjectActionKVED(sFind); 
    }

    @ApiOperation(value = "Получение данных из справочника КВЕД", notes = "Получаем данные из справочника КВЕД. "
            + "Пример:\n"
            + "https://alpha.test.igov.org.ua/wf/service/subject/getActionKVED2?sID=03&sNote=Прісноводне\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sID\": \"03.12\",\n"
            + "    \"sNote\": \"Прісноводне рибальство \",\n"
            + "    \"nID\": 37\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"03.22\",\n"
            + "    \"sNote\": \"Прісноводне рибництво (аквакультура) \",\n"
            + "    \"nID\": 39\n"
            + "  }\n"
            + "]\n"            
            + "\n```\n")
    @RequestMapping(value = "/getActionKVED2", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    List<SubjectActionKVED> getActionKVED2(
            @ApiParam(value = "sID - кретерий поиска в sID (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sID", required = false) String sID,
            @ApiParam(value = "sNote - кретерий поиска в sNote (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sNote", required = false) String sNote)
        	    throws CommonServiceException {
	return subjectActionKVEDService.getSubjectActionKVED(sID, sNote); 
    }
    @ApiOperation(value = "Предоставление сабджекту роли",notes = "Предоставление сабджекту роли")
    @RequestMapping(value = "/setSubjectHumanRole", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    String setSubjectHumanRole(
            @ApiParam(value = "nID_SubjectHuman", required = true) @RequestParam(value = "nID_SubjectHuman", required = true) Long nID_SubjectHuman,
            @ApiParam(value = "nID_SubjectHumanRole", required = true) @RequestParam(value = "nID_SubjectHumanRole", required = true) Long nID_SubjectHumanRole)
            throws CommonServiceException {
       return subjectService.setSubjectHumanRole(nID_SubjectHuman, nID_SubjectHumanRole);
        
    }
     public String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
