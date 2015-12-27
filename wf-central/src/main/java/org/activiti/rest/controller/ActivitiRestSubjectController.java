package org.activiti.rest.controller;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wf.dp.dniprorada.dao.SubjectDao;
import org.wf.dp.dniprorada.dao.SubjectHumanDao;
import org.wf.dp.dniprorada.dao.SubjectOrganDao;
import org.wf.dp.dniprorada.model.Subject;
import org.wf.dp.dniprorada.model.SubjectHuman;
import org.wf.dp.dniprorada.model.SubjectOrgan;

import com.google.common.base.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.servlet.http.HttpServletResponse;

@Controller
@Api(tags = { "ActivitiRestSubjectController" }, description = "Работа с субъектами")
@RequestMapping(value = "/subject")
public class ActivitiRestSubjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestSubjectController.class);

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
		+ "https://test.igov.org.ua/wf/service/subject/getSubjectHuman?nID_Subject=34125265377\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "{\n"
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
		+ "https://test.igov.org.ua/wf/service/subject/getSubjectOrgan?nID_Subject=34125265377\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"nID\":150,\n"
		+ "    \"sID\":\"34125265377\",\n"
		+ "    \"sLabel\":null,\n"
		+ "    \"sLabelShort\":null\n"
		+ "}\n"
		+ noteCODE;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectHumanDao subjectHumanDao;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    /**
     * получение субъекта, если таков найден, или добавление субъекта в противном случае
     * @param nID ИД-номер субъекта
     * @param sINN строка-ИНН (субъект - человек)
     * @param sOKPO строка-ОКПО (субъек - организация)
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Получение субъекта", notes = noteSyncSubject )
    @RequestMapping(value = "/syncSubject", method = RequestMethod.GET, headers = { "Accept=application/json" })
    public
    @ResponseBody
    Subject syncSubject(
	    @ApiParam(value = "ИД-номер субъекта", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "строка-ИНН (субъект - человек)", required = false) @RequestParam(required = false) String sINN,
	    @ApiParam(value = "строка-ОКПО (субъек - организация)", required = false) @RequestParam(required = false) String sOKPO,
            HttpServletResponse httpResponse) {

        LOG.info("--- syncSubject ---");
        Subject subject = null;
        if (nID != null) {
            subject = subjectDao.getSubject(nID);
        } else if (StringUtils.isNotEmpty(sINN)) {
            SubjectHuman oSubjectHuman = subjectHumanDao.getSubjectHuman(sINN);
            if (oSubjectHuman == null) {
                oSubjectHuman = subjectHumanDao.setSubjectHuman(sINN);
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
                    "RequestParam not found! You should add nID or sINN or sOKPO param!", Subject.class);
        }
        if (subject == null) {
            throw new ActivitiObjectNotFoundException(
                    String.format("Subject not found! nID = %s sINN = %s sOKPO = %s", nID, sINN, sOKPO), Subject.class);
        }
        httpResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        return subject;
    }

    @ApiOperation(value = "/setSubjectHuman", notes = noteSetSubjectHuman )
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
                "Security Error",
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
                "Security Error",
                HttpStatus.NOT_FOUND);
    }
}
