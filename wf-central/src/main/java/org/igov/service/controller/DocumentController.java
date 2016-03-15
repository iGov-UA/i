package org.igov.service.controller;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFSDBFile;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.IteratorUtils;
import org.igov.io.db.kv.statical.impl.FileStorage;
import org.igov.model.document.*;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.business.subject.SubjectService;
import org.igov.model.subject.Subject;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.model.action.event.HistoryEventType;
import org.igov.service.business.document.access.handler.HandlerFactory;
import org.igov.service.business.access.BankIDConfig;
import org.igov.service.business.access.BankIDUtils;
import org.igov.io.GeneralConfig;
import org.igov.util.ToolWeb;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.igov.service.exception.CommonServiceException;
import static org.igov.util.ToolFS.getFileExp;
import static org.igov.util.JSON.JsonRestUtils.REASON_HEADER;
import static org.igov.util.JSON.JsonRestUtils.toJsonErrorResponse;

@Controller
@Api(tags = {"DocumentController"}, description = "Документы и смежные сущности")
@RequestMapping(value = "/document")
public class DocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    private static final String NO_ACCESS_MESSAGE = "You don't have access!";
    private static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED_ERROR_CODE";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    BankIDConfig bankIDConfig;

    @Autowired
    private DocumentDao documentDao;
    @Autowired
    private DocumentContentTypeDao documentContentTypeDao;
    @Autowired
    private DocumentTypeDao documentTypeDao;
    @Autowired
    private HandlerFactory handlerFactory;
    @Autowired
    ActionEventService actionEventService;

    /**
     * получение документа по ид документа
     *
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Получение документа по ид документа", notes = "##### DocumentController - Документы и смежные сущности. Получение документа по ид документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocument\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocument?nID=1\n\n"
            + "Response\n\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sDate_Upload\":\"2015-01-01\",\n"
            + "    \"sContentType\":\"text/plain\",\n"
            + "    \"contentType\":\"text/plain\",\n"
            + "    \"nID\":1,\n"
            + "    \"sName\":\"Паспорт\",\n"
            + "    \"oDocumentType\":{\"nID\":0,\"sName\":\"Другое\"},\n"
            + "    \"sID_Content\":\"1\",\n"
            + "    \"oDocumentContentType\":{\"nID\":2,\"sName\":\"text/plain\"},\n"
            + "    \"sFile\":\"dd.txt\",\n"
            + "    \"oDate_Upload\":1420063200000,\n"
            + "    \"sID_Subject_Upload\":\"1\",\n"
            + "    \"sSubjectName_Upload\":\"ПриватБанк\",\n"
            + "    \"oSubject_Upload\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\", \"sLabelShort\":\"ПриватБанк\"},\n"
            + "     \"oSubject\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\",\"sLabelShort\":\"ПриватБанк\"}\n"
            + " }\n"
            + "\n```\n")
    @RequestMapping(value = "/getDocument", method = RequestMethod.GET)
    public @ResponseBody
    Document getDocument(@ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) throws CommonServiceException {
        Document document = documentDao.getDocument(id);
        if (nID_Subject != document.getSubject().getId()) {
            throw new CommonServiceException(UNAUTHORIZED_ERROR_CODE,
                    NO_ACCESS_MESSAGE + " Your nID = " + nID_Subject + " Document's Subject's nID = " + document
                    .getSubject().getId());
        } else {
            return document;
        }
    }

    /**
     * получение всех операторов(органов) которые имею право доступа к документу
     */
    @ApiOperation(value = "Получение всех операторов(органов) которые имею право доступа к документу", notes = "##### DocumentController - Документы и смежные сущности. Получение всех операторов(органов) которые имею право доступа к документу #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentOperators\n\n\n"
            + "Примеры: https://test.igov.org.ua/wf/service/document/getDocumentOperators\n\n"
            + "Response\n\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID_SubjectOrgan\": 2,\n"
            + "        \"sHandlerClass\": \"org.igov.service.business.document.access.handler.DocumentAccessHandler_IGov\",\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"iGov\"\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getDocumentOperators",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public @ResponseBody
    List<DocumentOperator_SubjectOrgan> getDocumentOperators() {
        return documentDao.getAllOperators();
    }

    /**
     * получение контента документа по ид документа
     *
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Получение контента документа по ид документа", notes = "##### DocumentController - Документы и смежные сущности. Получение контента документа по ид документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentContent\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocumentContent?nID=1\n\n"
            + "Response КОНТЕНТ ДОКУМЕНТА В ВИДЕ СТРОКИ\n")
    @RequestMapping(value = "/getDocumentContent", method = RequestMethod.GET)
    public @ResponseBody
    String getDocumentContent(@ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) throws CommonServiceException {
        Document document = documentDao.getDocument(id);
        if (nID_Subject != document.getSubject().getId()) {
            throw new CommonServiceException(UNAUTHORIZED_ERROR_CODE, NO_ACCESS_MESSAGE);
        } else {
            return ToolWeb.contentByteToString(documentDao.getDocumentContent(document.getContentKey())); // ????
        }
    }

    /**
     * получение документа в виде файла по ид документа
     *
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Получение документа в виде файла по ид документа", notes = "##### DocumentController - Документы и смежные сущности. Получение документа в виде файла по ид документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentFile\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocumentFile?nID=1\n\n"
            + "Response ЗАГРУЖЕННЫЙ ФАЙЛ\n")
    @RequestMapping(value = "/getDocumentFile", method = RequestMethod.GET)
    public @ResponseBody
    byte[] getDocumentFile(
            @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            HttpServletResponse httpResponse) throws CommonServiceException {
        Document document = documentDao.getDocument(id);
        if (!nID_Subject.equals(document.getSubject().getId())) {
            throw new CommonServiceException(UNAUTHORIZED_ERROR_CODE, NO_ACCESS_MESSAGE);
        }
        byte[] content = documentDao.getDocumentContent(document
                .getContentKey());

        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + document.getFile());

        httpResponse.setHeader(CONTENT_TYPE_HEADER, document.getContentType() + ";charset=UTF-8");
        httpResponse.setContentLength(content.length);
        return content;
    }

    /**
     * получение документа в виде файла
     *
     * @param sID строковой ID документа (параметр обязателен)
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя) (параметр опционален)
     * @param organID определяет класс хэндлера который будет обрабатывать
     * запрос (параметр опционален)
     * @param docTypeID определяет тип документа, например 0 - "Квитанція про
     * сплату", 1 - "Довідка про рух по картці (для візових центрів)" (параметр
     * опционален)
     * @param password пароль (параметр опционален)
     */
    @ApiOperation(value = "Получение документа в виде файла", notes = "##### DocumentController - Документы и смежные сущности. Получение документа в виде файла #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentAbstract\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocumentAbstract?sID=150826SV7733A36E803B\n\n"
            + "Response ЗАГРУЖЕННЫЙ ФАЙЛ\n")
    @RequestMapping(value = "/getDocumentAbstract", method = RequestMethod.GET)
    public @ResponseBody
    byte[] getDocumentAbstract(
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя) ", required = false) @RequestParam(value = "nID_Subject", required = false, defaultValue = "1") Long nID_Subject,
            @ApiParam(value = "строковой ID документа", required = false) @RequestParam(value = "sID", required = false) String sID,
            @ApiParam(value = "определяет класс хэндлера который будет обрабатывать запрос", required = false) @RequestParam(value = "nID_DocumentOperator_SubjectOrgan", required = false) Long organID,
            @ApiParam(value = "определяет тип документа, например 0 - \"Квитанція про сплату\", 1 - \"Довідка про рух по картці (для візових центрів)\"", required = false) @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
            @ApiParam(value = "пароль", required = false) @RequestParam(value = "sPass", required = false) String password,
            HttpServletResponse httpResponse)
            throws CommonServiceException {

        Document document;
        byte[] content;

        try {
            document = handlerFactory
                    .buildHandlerFor(documentDao.getOperator(organID))
                    .setDocumentType(docTypeID)
                    .setAccessCode(sID)
                    .setPassword(password)
                    .setWithContent(true)
                    .setIdSubject(nID_Subject)
                    .getDocument();
            content = document.getFileBody().getBytes();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new CommonServiceException(ExceptionCommonController.SYSTEM_ERROR_CODE,
                    "Can't read document content!");
        }

        httpResponse.setHeader(CONTENT_TYPE_HEADER, document.getContentType() + ";charset=UTF-8");
        httpResponse.setHeader("Content-Disposition", "attachment; filename=" + document.getFile());
        httpResponse.setContentLength(content.length);

        return content;
    }

    /**
     * получение списка загруженных субъектом документов
     *
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Получение списка загруженных субъектом документов", notes = "##### DocumentController - Документы и смежные сущности. Получение списка загруженных субъектом документов #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocuments\n\n\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocuments?nID_Subject=2\n\n"
            + "Response\n\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sDate_Upload\":\"2015-01-01\",\n"
            + "    \"sContentType\":\"text/plain\",\n"
            + "    \"contentType\":\"text/plain\",\n"
            + "    \"nID\":1,\n"
            + "    \"sName\":\"Паспорт\",\n"
            + "    \"oDocumentType\":{\"nID\":0,\"sName\":\"Другое\"},\n"
            + "    \"sID_Content\":\"1\",\n"
            + "    \"oDocumentContentType\":{\"nID\":2,\"sName\":\"text/plain\"},\n"
            + "    \"sFile\":\"dd.txt\",\n"
            + "    \"oDate_Upload\":1420063200000,\n"
            + "    \"sID_Subject_Upload\":\"1\",\n"
            + "    \"sSubjectName_Upload\":\"ПриватБанк\",\n"
            + "    \"oSubject_Upload\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\", \"sLabelShort\":\"ПриватБанк\"},\n"
            + "     \"oSubject\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\",\"sLabelShort\":\"ПриватБанк\"}\n"
            + "  },\n"
            + "  {\n"
            + "    \"sDate_Upload\":\"2015-01-01\",\n"
            + "    \"sContentType\":\"text/plain\",\n"
            + "    \"contentType\":\"text/plain\",\n"
            + "    \"nID\":2,\n"
            + "    \"sName\":\"Паспорт\",\n"
            + "    \"oDocumentType\":{\"nID\":0,\"sName\":\"Другое\"},\n"
            + "    \"sID_Content\":\"2\",\n"
            + "    \"oDocumentContentType\":{\"nID\":2,\"sName\":\"text/plain\"},\n"
            + "    \"sFile\":\"dd.txt\",\n"
            + "    \"oDate_Upload\":1420063200000,\n"
            + "    \"sID_Subject_Upload\":\"1\",\n"
            + "    \"sSubjectName_Upload\":\"ПриватБанк\",\n"
            + "    \"oSubject_Upload\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\", \"sLabelShort\":\"ПриватБанк\"},\n"
            + "     \"oSubject\":{\"nID\":1,\"sID\":\"ПАО\",\"sLabel\":\"ПАО ПриватБанк\",\"sLabelShort\":\"ПриватБанк\"}\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public @ResponseBody
    List<Document> getDocuments(
            @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) {
        return documentDao.getDocuments(nID_Subject);
    }

    /**
     * сохранение документа
     *
     * @param sID_Subject_Upload ИД-строка субъекта, который загрузил документ
     * @param sSubjectName_Upload строка-название субъекта, который загрузил
     * документ (временный парметр, будет убран)
     * @param sName строка-название документа
     * @param sFile строка-название и расширение файла
     * @param nID_DocumentType ИД-номер типа документа
     * @param documentContentTypeName строка-тип контента документа
     * @param sContent контект в виде строки-обьекта
     * @param nID_Subject ИД-номер субъекта документа (владельца)
     * ????????????????????????????????????
     */
    @ApiOperation(value = "Сохранение документа", notes = "##### DocumentController - Документы и смежные сущности. Сохранение документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/setDocument\n\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/document/setDocument?sID_Subject_Upload=123&sSubjectName_Upload=Vasia&sName=Pasport&sFile=file.txt&nID_DocumentType=1&sDocumentContentType=application/zip&soDocumentContent=ffffffffffffffffff&nID_Subject=1\n\n"
            + "Response ИД ДОКУМЕНТА\n")
    @RequestMapping(value = "/setDocument", method = RequestMethod.GET)
    public @ResponseBody
    Long setDocument(
            @ApiParam(value = "ИД-номер субъекта документа (владельца)", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = true) @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
            @ApiParam(value = "строка-название субъекта, который загрузил документ", required = true) @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
            @ApiParam(value = "строка-название документа", required = true) @RequestParam(value = "sName") String sName,
            @ApiParam(value = "ИД-номер типа документа", required = true) @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
            @ApiParam(value = "строка-тип контента документа", required = false) @RequestParam(value = "sDocumentContentType", required = false) String documentContentTypeName,
            @ApiParam(value = "контект в виде строки-обьекта", required = true) @RequestParam(value = "soDocumentContent") String sContent,
            HttpServletRequest request) throws IOException {

        SubjectService oManageSubject = new SubjectService();

        String sFileName = "filename.txt";
        String sFileContentType = "text/plain";
        byte[] aoContent = sContent.getBytes();

        documentContentTypeName
                = request.getHeader(CONTENT_TYPE_HEADER) != null
                        ? request.getHeader("filename")
                        : documentContentTypeName;
        DocumentContentType documentContentType = null;
        if (documentContentTypeName != null) {
            documentContentType = documentContentTypeDao.getDocumentContentType(documentContentTypeName);
            if (documentContentType == null) {
                documentContentType = new DocumentContentType();
                documentContentType.setName(documentContentTypeName);
                documentContentType.setId(documentContentTypeDao.setDocumentContent(documentContentType));
            }
        } else {
            throw new ActivitiObjectNotFoundException(
                    "RequestParam 'nID_DocumentContentType' not found!", DocumentContentType.class);
        }

        Subject subject_Upload = oManageSubject.syncSubject_Upload(sID_Subject_Upload);

        String oSignData = BankIDUtils.checkECP(bankIDConfig.sClientId(), bankIDConfig.sClientSecret(),
                generalConfig.sHostCentral(), aoContent, sName);

        return documentDao.setDocument(
                nID_Subject,
                subject_Upload.getId(),
                sID_Subject_Upload,
                sSubjectName_Upload,
                sName,
                nID_DocumentType,
                documentContentType.getId(),
                sFileName,
                sFileContentType,
                aoContent,
                oSignData);

    }

    /**
     * сохранение документа в виде файла
     *
     * @param sID_Subject_Upload ИД-строка субъекта, который загрузил документ
     * @param sSubjectName_Upload строка-название субъекта, который загрузил
     * документ (временный парметр, нужно убрать его)
     * @param sName строка-название документа
     * @param nID_DocumentType ИД-номер типа документа
     * @param sDocumentContentType строка-тип контента документа
     * @param soDocumentContent контент в виде строки-обьекта
     * @param nID_Subject ИД-номер субъекта документа
     * (владельца)????????????????????????????????????
     * @param oFile обьект файла (тип MultipartFile)
     */
    @ApiOperation(value = "Сохранение документа в виде файла (контент файла шлется в теле запроса)", notes = "##### DocumentController - Документы и смежные сущности. Сохранение документа в виде файла (контент файла шлется в теле запроса) #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/setDocumentFile\n\n\n"
            + "Response ИД ДОКУМЕНТА\n")
    @RequestMapping(value = "/setDocumentFile", method = RequestMethod.POST)
    public @ResponseBody
    Long setDocumentFile(
            @ApiParam(value = "ИД-номер субъекта документа (владельца)", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = true) @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
            @ApiParam(value = "sSubjectName_Upload", required = true) @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
            @ApiParam(value = "строка-название документа", required = true) @RequestParam(value = "sName") String sName,
            @ApiParam(value = "строка-расширения файла", required = false) @RequestParam(value = "sFileExtension", required = false) String sFileExtension,
            @ApiParam(value = "ИД-номер типа документа", required = true) @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
            @ApiParam(value = "строка-тип контента документа", required = false) @RequestParam(value = "nID_DocumentContentType", required = false) Long nID_DocumentContentType,
            @ApiParam(value = "обьект файла (тип MultipartFile)", required = false) @RequestParam(value = "oFile", required = false) MultipartFile oFile,
            @ApiParam(value = "обьект мультипартфайла", required = false) @RequestParam(value = "file", required = false) MultipartFile oFile2,
            HttpServletRequest request) throws IOException {

        SubjectService oManageSubject = new SubjectService();

        if (oFile == null) {
            oFile = oFile2;
        }

        String sOriginalFileName = oFile.getOriginalFilename();
        LOG.info("sOriginalFileName={}", sOriginalFileName);

        String sOriginalContentType = oFile.getContentType();
        LOG.info("sOriginalContentType={}", sOriginalContentType);

        String sFileName = request.getHeader("filename");
        LOG.info("sFileName(before)={}", sFileName);

        if (sFileName == null || "".equals(sFileName.trim())) {

            LOG.info("sFileExtension={}", sFileExtension);
            if (sFileExtension != null && !sFileExtension.trim().isEmpty()
                    && sOriginalFileName != null && !sOriginalFileName.trim().isEmpty()
                    && sOriginalFileName.endsWith(sFileExtension)) {
                sFileName = sOriginalFileName;
                LOG.info("sOriginalFileName has equal ext! sFileName(all ok)={}", sFileName);
            } else {
                Enumeration<String> a = request.getHeaderNames();
                for (int n = 0; a.hasMoreElements() && n < 100; n++) {
                    String s = a.nextElement();
                    LOG.info("(n={}, s={}, value={})", n, s, request.getHeader(s));
                }
                String fileExp = getFileExp(sOriginalFileName);
                fileExp = fileExp != null ? fileExp : ".zip.zip";
                fileExp = fileExp.equalsIgnoreCase(sOriginalFileName) ? sFileExtension : fileExp;
                fileExp = fileExp != null ? fileExp.toLowerCase() : ".zip";
                sFileName = sOriginalFileName + (fileExp.startsWith(".") ? "" : ".") + fileExp;
                LOG.info("sFileName(after)={}", sFileName);
            }
        }
        byte[] aoContent = oFile.getBytes();

        Subject subject_Upload = oManageSubject.syncSubject_Upload(sID_Subject_Upload);

        String soSignData = BankIDUtils.checkECP(bankIDConfig.sClientId(), bankIDConfig.sClientSecret(),
                generalConfig.sHostCentral(), aoContent, sName);

        Long nID_Document = documentDao.setDocument(
                nID_Subject,
                subject_Upload.getId(),
                sID_Subject_Upload,
                sSubjectName_Upload,
                sName,
                nID_DocumentType,
                nID_DocumentContentType,
                sFileName,
                sOriginalContentType,
                aoContent,
                soSignData);
        actionEventService.createHistoryEvent(HistoryEventType.SET_DOCUMENT_INTERNAL,
                nID_Subject, sSubjectName_Upload, nID_Document, null);
        return nID_Document;
    }

    //################ DocumentType services ###################
    /**
     * получение списка всех "нескрытых" типов документов, т.е. у которых поле
     * bHidden=false
     */
    @ApiOperation(value = "Получение списка всех \"нескрытых\" типов документов", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ ДОКУМЕНТОВ. Получение списка всех \"нескрытых\" типов документов #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentTypes\n\n\n"
            + "получение списка всех \"нескрытых\" типов документов, т.е. у которых поле bHidden=false\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocumentTypes\n\n"
            + "Response\n"
            + "\n```json\n"
            + "[\n"
            + "    {\"nID\":0,\"sName\":\"Другое\", \"bHidden\":false},\n"
            + "    {\"nID\":1,\"sName\":\"Справка\", \"bHidden\":false},\n"
            + "    {\"nID\":2,\"sName\":\"Паспорт\", \"bHidden\":false}\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getDocumentTypes", method = RequestMethod.GET)
    public @ResponseBody
    List<DocumentType> getDocumentTypes() throws Exception {
        return documentTypeDao.getDocumentTypes();
    }

    /**
     * добавить/изменить запись типа документа
     *
     * @param nID ид записи (число)
     * @param sName название записи (строка)
     * @param bHidden скрывать/не скрывать (при отдаче списка всех записей,
     * булевское, по умолчанию = false)
     */
    @ApiOperation(value = "Добавить/изменить запись типа документа", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ ДОКУМЕНТОВ. Добавить/изменить запись типа документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/setDocumentType\n\n\n"
            + "Если запись с ид=nID не будет найдена, то создастся новая запись (с автогенерируемым nID), иначе -- обновится текущая.\n\n"
            + "примеры:\n\n"
            + "создать новый тип: https://test.igov.org.ua/wf/service/document/setDocumentType?nID=100&sName=test\n\n"
            + "ответ: \n"
            + "\n```json\n"
            + "{\"nID\":20314,\"sName\":\"test\", , \"bHidden\":false}\n"
            + "\n```\n"
            + "изменить (взять ид из предыдущего ответа): https://test.igov.org.ua/wf/service/document/setDocumentType?nID=20314&sName=test2\n\n"
            + "ответ: \n"
            + "\n```json\n"
            + "{\"nID\":20314,\"sName\":\"test2\", \"bHidden\":false}\n"
            + "\n```\n")
    @RequestMapping(value = "/setDocumentType", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity setDocumentType(
            @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "название записи", required = true) @RequestParam(value = "sName") String sName,
            @ApiParam(value = "скрывать/не скрывать (при отдаче списка всех записей, булевское, по умолчанию = false)", required = false) @RequestParam(value = "bHidden", required = false) Boolean bHidden) {
        ResponseEntity result;
        try {
            DocumentType documentType = documentTypeDao.setDocumentType(nID, sName, bHidden);
            result = JsonRestUtils.toJsonResponse(documentType);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            result = toJsonErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return result;
    }

    /**
     * удаление записи по ее ид
     *
     * @param nID ид записи
     */
    @ApiOperation(value = "Удаление записи по ее ид", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ ДОКУМЕНТОВ. Удаление записи по ее ид#####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/removeDocumentType\n\n\n"
            + "Если запись с ид=nID не будет найдена, то вернется ошибка 403. Record not found, иначе -- запись удалится.\n\n"
            + "пример: https://test.igov.org.ua/wf/service/document/removeDocumentType?nID=20314\n\n"
            + "ответ: 200 ok\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Record not found")})
    @RequestMapping(value = "/removeDocumentType", method = RequestMethod.GET)
    public @ResponseBody
    void removeDocumentType(
            @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
            HttpServletResponse response) {
        try {
            documentTypeDao.removeDocumentType(nID);
        } catch (RuntimeException e) {
            LOG.error("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader(REASON_HEADER, e.getMessage());
        }
    }

    //################ DocumentContentType services ###################
    /**
     * получение списка типов контента документов
     */
    @ApiOperation(value = "Получение списка типов контента документов", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Получение списка типов контента документов #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/getDocumentContentTypes\n\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/document/getDocumentContentTypes\n\n"
            + "Response\n"
            + "\n```json\n"
            + "[\n"
            + "    {\"nID\":0,\"sName\":\"application/json\"},\n"
            + "    {\"nID\":1,\"sName\":\"application/xml\"},\n"
            + "    {\"nID\":2,\"sName\":\"text/plain\"},\n"
            + "    {\"nID\":3,\"sName\":\"application/jpg\"}\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getDocumentContentTypes", method = RequestMethod.GET)
    public @ResponseBody
    List<DocumentContentType> getDocumentContentTypes() {
        return documentContentTypeDao.getDocumentContentTypes();
    }

    /**
     * добавить/изменить запись типа контента документа
     *
     * @param nID ид записи
     * @param sName название записи
     */
    @ApiOperation(value = "Добавить/изменить запись типа контента документа", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Добавить/изменить запись типа контента документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/setDocumentContentType\n\n\n"
            + "Если запись с ид=nID не будет найдена, то создастся новая запись (с автогенерируемым nID), иначе -- обновится текущая.\n\n"
            + "Примеры:\n\n"
            + "создать новый тип: \n"
            + "https://test.igov.org.ua/wf/service/document/setDocumentContentType?nID=100&sName=test\n\n"
            + "ответ:\n"
            + "\n```json\n"
            + "{\"nID\":20311,\"sName\":\"test\"}\n"
            + "\n```\n"
            + "изменить (взять ид из предыдущего ответа): \n"
            + "https://test.igov.org.ua/wf/service/document/setDocumentContentType?nID=20311&sName=test2\n\n"
            + "ответ:\n"
            + "\n```json\n"
            + "{\"nID\":20311,\"sName\":\"test2\"}\n"
            + "\n```\n")
    @RequestMapping(value = "/setDocumentContentType", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity setDocumentContentType(
            @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "название записи", required = true) @RequestParam(value = "sName") String sName) {
        ResponseEntity result;
        try {
            DocumentContentType documentType = documentContentTypeDao.setDocumentContentType(nID, sName);
            result = JsonRestUtils.toJsonResponse(documentType);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            result = toJsonErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return result;
    }

    /**
     * удаление записи по ее ид
     *
     * @param nID ид записи
     */
    @ApiOperation(value = "Удаление записи по ее ид", notes = "##### DocumentController - Документы и смежные сущности. ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Удаление записи по ее ид #####\n\n"
            + "HTTP Context: http://server:port/wf/service/document/removeDocumentContentType\n\n"
            + "Если запись с ид=nID не будет найдена, то вернется ошибка 403. Record not found, иначе -- запись удалится.\n\n"
            + "пример:\n"
            + "https://test.igov.org.ua/wf/service/document/removeDocumentContentType?nID=20311\n\n"
            + "ответ: 200 ok\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Record not found")})
    @RequestMapping(value = "/removeDocumentContentType", method = RequestMethod.GET)
    public @ResponseBody
    void removeDocumentContentType(
            @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
            HttpServletResponse response) {
        try {
            documentContentTypeDao.removeDocumentContentType(nID);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            response.setStatus(403);
            response.setHeader(REASON_HEADER, e.getMessage());
        }
    }

    


    @Autowired
    FileStorage durableFileStorage;

    @Autowired
    private GridFsTemplate oGridFsTemplate;

    @Autowired
    DocumentDaoImpl entityDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private TaskService taskService;

    @ApiOperation(value = "Удалить все записи в mongo, на ключ которых не ссылается ни одна запись"
            + "в сущности Document,  не ссылается ни одна запись в Attach-ах тасок Активити, "
            + "удалить все записи в postgres, по sID_Content которой не найдено"
            + "ни одной записи в mongo", notes = "todo")
    @RequestMapping(value = "/cleanUpInvalidDocuments", method = RequestMethod.POST)
    public @ResponseBody String cleanUpInvalidDocuments() {

		/*  получаем батч ключей из Document
		 	находим все upload файлы, по ключам из document, если их нет удаляем document
		 	получаем батч по ключам, делаем запрос в document, если не находим, удаляем файлы из mongo
		*/

        int deletedFilesFromMongo = 0;
        int deletedFilesFromPostgres = 0;


        final int BATCH_SIZE = 100;


        for(int offset = 0 ;; offset += BATCH_SIZE) {

            Map<String, Attachment> attachments = new HashMap<>();
            List<Task> activityTasks = taskService.createTaskQuery().listPage(offset, BATCH_SIZE);
            for(Task task: activityTasks) {
                List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
                for (Attachment attachment: taskAttachments) {
                    attachments.put(attachment.getId(), attachment);
                }
            }

            Query query = new Query()
                    .addCriteria(Criteria.where("filename").in(attachments));
            List<GridFSDBFile> relatedFiles = oGridFsTemplate.find(query);

            if (activityTasks.isEmpty()) {
                break;
            }
        }


        for (int offset = 0 ;; offset += BATCH_SIZE) {
            List<String> contentKeys = documentDao.getDocumentContentKeys(offset);
            Query query = new Query()
                    .addCriteria(Criteria.where("filename").in(contentKeys));
            List<GridFSDBFile> relatedFiles = oGridFsTemplate.find(query);
            Set<String> relatedKeys = new HashSet<>();
            for (GridFSDBFile gridFSDBFile: relatedFiles) {
                relatedKeys.add(gridFSDBFile.getFilename());
            }

            for (String documentContentKey: contentKeys) {
                if (!relatedKeys.contains(documentContentKey)) {
                    List<Document> toRemoveDocs = documentDao.findAllBy("contentKey", documentContentKey);
                    for (Document toRemove: toRemoveDocs) {
                        //						documentDao.delete(toRemove);
                        LOG.info("[cleanUpInvalidDocuments](deleted Document : {}):", toRemove);
                        deletedFilesFromPostgres++;
                    }
                }
            }

            if (contentKeys.isEmpty()) {
                break;
            }
        }

        DBCollection collection = mongoTemplate.getCollection("fs.files");
        for (int offset = 0 ;; offset += BATCH_SIZE) {
            DBCursor cursor = collection.find(new BasicDBObject()).skip(offset).limit(BATCH_SIZE);
            if (cursor.hasNext()) {

                List<GridFSDBFile> uploadedFiles = IteratorUtils.toList(cursor.iterator(), BATCH_SIZE);
                Map<String, GridFSDBFile> fileByName = new HashMap<>(BATCH_SIZE);
                for (GridFSDBFile file: uploadedFiles) {
                    fileByName.put(file.getFilename(), file);
                }


                List<Document> relatedDocuments = documentDao.findAllBy("contentKey", fileByName.keySet());
                Set<String> existingContentKeys = new HashSet<>();
                for (Document document: relatedDocuments) {
                    existingContentKeys.add(document.getContentKey());
                }

                for(String contentKey: fileByName.keySet()) {
                    if (!existingContentKeys.contains(contentKey)) {
                        //oGridFsTemplate.delete(new Query().addCriteria(Criteria.where("filename").is(contentKey)));
                        LOG.info("[cleanUpInvalidDocuments](deleted uploaded file from mongo: {}):", fileByName.get(contentKey));
                        deletedFilesFromMongo++;
                    }
                }

            } else {
                break;
            }
        }

        JsonObject object = new JsonObject();
        object.addProperty("sRemovedNoSQL", deletedFilesFromMongo);
        object.addProperty("sRemovedSQL", deletedFilesFromPostgres);
        return object.toString();
    }
}
