package org.igov.service.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.igov.service.business.document.DocumentStepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.model.document.DocumentStepSubjectRight;


@Controller
@Api(tags = {"DocumentCommonController -- Проверки наложености ЭЦП по строкам-подписантам"})
@RequestMapping(value = "/common/document")

public class DocumentCommonController {

	@Autowired
	private DocumentStepService documentStepService;

	private static final Logger LOG = LoggerFactory.getLogger(DocumentCommonController.class);

	@ApiOperation(value = "проверка подписано ли ЕЦП")
	@RequestMapping(value = "/isDocumentAllSigned", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody Map<String, Boolean> isDocumentAllSigned(
			@ApiParam(value = "ИД процесс-активити", required = false) @RequestParam(required = false, value = "nID_Process") String nID_Process,
			@ApiParam(value = "Логин подписанта", required = false) @RequestParam(required = false, value = "sLogin") String sLogin,
			@ApiParam(value = "Ключ шага документа", required = false) @RequestParam(required = false, value = "sKey_Step") String sKey_Step,
			HttpServletResponse httpResponse) throws Exception {

		LOG.info("snID_Process_Activiti: " + nID_Process);
		LOG.info("sLogin: " + sLogin);
		LOG.info("sKey_Step: " + sKey_Step);

		return documentStepService.isDocumentAllSigned(nID_Process, sLogin, sKey_Step);

	}
        
        @ApiOperation(value = "Клонирование документа")
	@RequestMapping(value = "/cloneDocumentStepSubject", method = RequestMethod.GET)
	@Transactional
	public DocumentStepSubjectRight cloneDocumentStepSubject(
			@ApiParam(value = "ИД процесс-активити", required = false) @RequestParam(required = false, value = "snID_Process_Activiti") String snID_Process_Activiti,
			@ApiParam(value = "группа", required = false) @RequestParam(required = false, value = "sKey_GroupPostfix") String sKey_GroupPostfix,
			@ApiParam(value = "новая группа", required = false) @RequestParam(required = false, value = "sKey_GroupPostfix_New") String sKey_GroupPostfix_New) throws Exception {

		LOG.info("snID_Process_Activiti in cloneDocumentStepSubject: {}", snID_Process_Activiti);
		LOG.info("sKey_GroupPostfix in cloneDocumentStepSubject: {}", sKey_GroupPostfix);
		LOG.info("sKey_GroupPostfix_New in cloneDocumentStepSubject: {}", sKey_GroupPostfix_New);

		return documentStepService.cloneDocumentStepSubject(snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New);

	}
}
