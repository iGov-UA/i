package org.igov.service.controller;

import org.igov.service.business.action.ActionEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.action.event.HistoryEventType;
import org.igov.model.document.access.DocumentAccessDao;
import org.igov.model.document.DocumentDao;
import org.igov.model.document.access.AccessURL;
import org.igov.model.document.Document;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectContactType;
import org.igov.model.subject.SubjectContactTypeDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.service.business.document.access.handler.HandlerFactory;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.igov.service.business.document.access.DocumentAccessService;
import org.igov.service.exception.CommonServiceException;

@Api(tags = {"DocumentAccessController"}, description = "Доступы к документам")
@Controller
@RequestMapping(value = "/document/access")
public class DocumentAccessController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentAccessController.class);
    private static final String REASON_HEADER = "Reason";
    private static final String NO_ACCESS_MESSAGE = "You don't have access!";
    private static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED_ERROR_CODE";

    @Autowired
    private DocumentAccessDao documentAccessDao;
    @Autowired
    private DocumentDao documentDao;
    @Autowired
    private HandlerFactory handlerFactory;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Autowired
    ActionEventService actionEventService;
    @Autowired
    DocumentAccessService documentAccessService;

    /**
     * запись на доступ, с генерацией и получением уникальной ссылки на него
     *
     * @param nID_Document ИД-номер документа
     * @param sFIO ФИО, кому доступ
     * @param sTarget цель получения доступа
     * @param sTelephone телефон того, кому доступ предоставляется
     * @param nMS число милисекунд, на которое предоставляется доступ
     * @param sMail эл. почта того, кому доступ предоставляется
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "запись на доступ, с генерацией и получением уникальной ссылки на него", notes = "##### DocumentAccessController - Доступы к документам. Запись на доступ, с генерацией и получением уникальной ссылки на него #####\n\n"
            + "HTTP Context: https://seriver:port/wf/service/document/access/setDocumentLink\n\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
            + "Response\n\n"
            + "\n```json\n"
            + "[  //[0..N]\n"
            + "  {\"name\":\"sURL\",   //[1..1]\n"
            + "    \"value\":\"https://e-gov.org.ua/index#nID_Access=4345&sSecret=JHg3987JHg3987JHg3987\" //[1..1]\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/setDocumentLink", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public @ResponseBody
    AccessURL setDocumentAccessLink(
            @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID_Document") Long nID_Document,
            @ApiParam(value = "ФИО, кому доступ", required = false) @RequestParam(value = "sFIO", required = false) String sFIO,
            @ApiParam(value = "цель получения доступа", required = false) @RequestParam(value = "sTarget", required = false) String sTarget,
            @ApiParam(value = "телефон того, кому доступ предоставляется", required = false) @RequestParam(value = "sTelephone", required = false) String sTelephone,
            @ApiParam(value = "число милисекунд, на которое предоставляется доступ", required = true) @RequestParam(value = "nMS") Long nMS,
            @ApiParam(value = "эл. почта того, кому доступ предоставляется", required = false) @RequestParam(value = "sMail", required = false) String sMail,
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            HttpServletResponse response) throws CommonServiceException {

        documentAccessService.syncContacts(nID_Subject, sMail, sTelephone);
        Document document = documentDao.getDocument(nID_Document);

        if (!nID_Subject.equals(document.getSubject().getId())) {
            throw new CommonServiceException(UNAUTHORIZED_ERROR_CODE, NO_ACCESS_MESSAGE, HttpStatus.UNAUTHORIZED);
        }
       
        
        AccessURL oAccessURL = new AccessURL();
        try {
            oAccessURL.setName("sURL");
            String sValue = documentAccessDao.setDocumentLink(nID_Document, sFIO, sTarget, sTelephone, nMS, sMail);
            oAccessURL.setValue(sValue);

            actionEventService.createHistoryEvent(HistoryEventType.SET_DOCUMENT_ACCESS_LINK,
                    nID_Document, sFIO, sTelephone, nMS, sMail);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader(REASON_HEADER, e.getMessage());
            LOG.error(e.getMessage(), e);
        }
        
        
        
        
       
        return oAccessURL;
    }
   
    /**
     * получение контента документа по коду доступа,оператору, типу документа и
     * паролю
     *
     * @param accessCode - строковой код доступа к документу
     * @param organID - номер-�?Д субьекта-органа оператора документа
     * @param docTypeID - номер-�?Д типа документа (опционально)
     * @param password - строка-пароль (опционально)
     */
    @ApiOperation(value = "Получение контента документа по коду доступа,оператору, типу документа и паролю", notes = "##### DocumentAccessController - Доступы к документам. Получение контента документа по коду доступа,оператору, типу документа и паролю #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/access/getDocumentAccessByHandler\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/access/getDocumentAccessByHandler?sCode_DocumentAccess=2&nID_DocumentOperator_SubjectOrgan=2&sPass=123&nID_DocumentType=1\n\n"
            + "Response КОНТЕНТ ДОКУМЕНТА В ВИДЕ СТРОКИ\n")
    @RequestMapping(value = "/getDocumentAccessByHandler",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public @ResponseBody
    Document getDocumentAccessByHandler(
            @ApiParam(value = "код доступа документа", required = true) @RequestParam(value = "sCode_DocumentAccess") String accessCode,
            @ApiParam(value = "код органа(оператора)", required = true) @RequestParam(value = "nID_DocumentOperator_SubjectOrgan") Long organID,
            @ApiParam(value = "типа документа (опциональный)", required = false) @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
            @ApiParam(value = "пароль для доступа к документу (опциональный, пока только для документов у которы sCodeType=SMS)", required = false) @RequestParam(value = "sPass", required = false) String password,
            @ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject", defaultValue = "1") Long nID_Subject
    ) {

        LOG.info("accessCode = {} ", accessCode);

        Document document = handlerFactory
                .buildHandlerFor(documentDao.getOperator(organID))
                .setDocumentType(docTypeID)
                .setAccessCode(accessCode)
                .setPassword(password)
                .setWithContent(false)
                .setIdSubject(nID_Subject)
                .getDocument();
        try {
            actionEventService.createHistoryEvent(HistoryEventType.GET_DOCUMENT_ACCESS_BY_HANDLER,
                    document.getSubject().getId(), subjectOrganDao.getSubjectOrgan(organID).getName(), null, document);
        } catch (Exception e) {
        	LOG.warn("Error: {}, can`t create history event!", e.getMessage());
        	LOG.trace("FAIL:", e);
        }
        return document;
    }

}
