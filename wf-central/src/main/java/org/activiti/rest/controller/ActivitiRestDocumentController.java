package org.activiti.rest.controller;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.wf.dp.dniprorada.base.util.JSExpressionUtil;
import org.wf.dp.dniprorada.base.util.JsonRestUtils;
import org.wf.dp.dniprorada.constant.Currency;
import org.wf.dp.dniprorada.constant.HistoryEventMessage;
import org.wf.dp.dniprorada.constant.HistoryEventType;
import org.wf.dp.dniprorada.constant.Language;
import org.wf.dp.dniprorada.dao.*;
import org.wf.dp.dniprorada.liqPay.LiqBuy;
import org.wf.dp.dniprorada.model.*;
import org.wf.dp.dniprorada.model.document.HandlerFactory;
import org.wf.dp.dniprorada.util.BankIDConfig;
import org.wf.dp.dniprorada.util.BankIDUtils;
import org.wf.dp.dniprorada.util.GeneralConfig;
import org.wf.dp.dniprorada.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Controller
@RequestMapping(value = "/services")
public class ActivitiRestDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestDocumentController.class);
    private static final String NO_ACCESS_MESSAGE = "You don't have access!";
    private static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED_ERROR_CODE";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String REASON_HEADER = "Reason";

    @Autowired
    LiqBuy liqBuy;
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    BankIDConfig bankIDConfig;
    @Autowired
    private DocumentDao documentDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Autowired
    private SubjectOrganJoinAttributeDao subjectOrganJoinAttributeDao;

    @Autowired
    private DocumentContentTypeDao documentContentTypeDao;
    @Autowired
    private DocumentTypeDao documentTypeDao;
    @Autowired
    private HistoryEventDao historyEventDao;
    @Autowired
    private HandlerFactory handlerFactory;

    /**
     * получение документа по ид документа
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/getDocument", method = RequestMethod.GET)
    public
    @ResponseBody
    Document getDocument(@RequestParam(value = "nID") Long id,
                         @RequestParam(value = "nID_Subject") long nID_Subject) throws ActivitiRestException {
        Document document = documentDao.getDocument(id);
        if (nID_Subject != document.getSubject().getId()) {
            throw new ActivitiRestException(UNAUTHORIZED_ERROR_CODE,
                    NO_ACCESS_MESSAGE + " Your nID = " + nID_Subject + " Document's Subject's nID = " + document
                            .getSubject().getId());
        } else {
            return document;
        }
    }

    /**
     * получение контента документа по коду доступа,оператору, типу документа и паролю
     * @param accessCode - строковой код доступа к документу
     * @param organID    - номер-�?Д субьекта-органа оператора документа
     * @param docTypeID  - номер-�?Д типа документа (опционально)
     * @param password   - строка-пароль (опционально)
     */
    @RequestMapping(value = "/getDocumentAccessByHandler",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    Document getDocumentAccessByHandler(
            @RequestParam(value = "sCode_DocumentAccess") String accessCode,
            @RequestParam(value = "nID_DocumentOperator_SubjectOrgan") Long organID,
            @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
            @RequestParam(value = "sPass", required = false) String password,
            @RequestParam(value = "nID_Subject", defaultValue = "1") Long nID_Subject
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
            createHistoryEvent(HistoryEventType.GET_DOCUMENT_ACCESS_BY_HANDLER,
                    document.getSubject().getId(), subjectOrganDao.getSubjectOrgan(organID).getName(), null, document);
        } catch (Exception e) {
            LOG.warn("can`t create history event!", e);
        }
        return document;
    }

    /**
     * получение всех операторов(органов) которые имею право доступа к документу
     */
    @RequestMapping(value = "/getDocumentOperators",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<DocumentOperator_SubjectOrgan> getDocumentOperators() {
        return documentDao.getAllOperators();
    }

    /**
     * получение контента документа по ид документа
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/getDocumentContent", method = RequestMethod.GET)
    public
    @ResponseBody
    String getDocumentContent(@RequestParam(value = "nID") Long id,
                              @RequestParam(value = "nID_Subject") long nID_Subject) throws ActivitiRestException {
        Document document = documentDao.getDocument(id);
        if (nID_Subject != document.getSubject().getId()) {
            throw new ActivitiRestException(UNAUTHORIZED_ERROR_CODE, NO_ACCESS_MESSAGE);
        } else {
            return Util.contentByteToString(documentDao.getDocumentContent(document.getContentKey())); // ????
        }
    }

    /**
     * получение документа в виде файла по ид документа
     * @param id ИД-номер документа
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/getDocumentFile", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] getDocumentFile(
            @RequestParam(value = "nID") Long id,
            @RequestParam(value = "nID_Subject") Long nID_Subject,
            HttpServletResponse httpResponse) throws ActivitiRestException {
        Document document = documentDao.getDocument(id);
        if (!nID_Subject.equals(document.getSubject().getId())) {
            throw new ActivitiRestException(UNAUTHORIZED_ERROR_CODE, NO_ACCESS_MESSAGE);
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
     * @param sID строковой ID документа (параметр обязателен)
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя) (параметр опционален)
     * @param organID определяет класс хэндлера который будет обрабатывать запрос (параметр опционален)
     * @param docTypeID определяет тип документа, например 0 - "Квитанція про сплату", 1 - "Довідка про рух по картці (для візових центрів)" (параметр опционален)
     * @param password пароль (параметр опционален)
     */
    @RequestMapping(value = "/getDocumentAbstract", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] getDocumentAbstract(
            @RequestParam(value = "nID_Subject", required = false, defaultValue = "1") Long nID_Subject,
            @RequestParam(value = "sID", required = false) String sID,
            @RequestParam(value = "nID_DocumentOperator_SubjectOrgan", required = false) Long organID,
            @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
            @RequestParam(value = "sPass", required = false) String password,

            HttpServletResponse httpResponse)
            throws ActivitiRestException {

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
            throw new ActivitiRestException(ActivitiExceptionController.SYSTEM_ERROR_CODE,
                    "Can't read document content!");
        }

        httpResponse.setHeader(CONTENT_TYPE_HEADER, document.getContentType() + ";charset=UTF-8");
        httpResponse.setHeader("Content-Disposition", "attachment; filename=" + document.getFile());
        httpResponse.setContentLength(content.length);

        return content;
    }

    /**
     * получение списка загруженных субъектом документов
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Document> getDocuments(
            @RequestParam(value = "nID_Subject") long nID_Subject) {
        return documentDao.getDocuments(nID_Subject);
    }

    /**
     * @param sID_Merchant ид меранта
     * @param sSum сумма оплаты
     * @param oID_Currency валюта
     * @param oLanguage язык
     * @param sDescription описание
     * @param sID_Order ид заказа
     * @param sURL_CallbackStatusNew URL для отправки статуса
     * @param sURL_CallbackPaySuccess URL для отправки ответа
     * @param nID_Subject ид субъекта
     * @param bTest тестовый вызов или нет
     */
    @RequestMapping(value = "/getPayButtonHTML_LiqPay", method = RequestMethod.GET)
    public
    @ResponseBody
    String getPayButtonHTML_LiqPay(
            @RequestParam(value = "sID_Merchant", required = true) String sID_Merchant,
            @RequestParam(value = "sSum", required = true) String sSum,
            @RequestParam(value = "oID_Currency", required = true) Currency oID_Currency,
            @RequestParam(value = "oLanguage", required = true) Language oLanguage,
            @RequestParam(value = "sDescription", required = true) String sDescription,
            @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
            @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess,
            @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
            @RequestParam(value = "bTest", required = true) boolean bTest) throws Exception {

        return liqBuy.getPayButtonHTML_LiqPay(sID_Merchant, sSum,
                oID_Currency, oLanguage, sDescription, sID_Order,
                sURL_CallbackStatusNew, sURL_CallbackStatusNew,
                nID_Subject, true);
    }

    /**
     * сохранение документа
     * @param sID_Subject_Upload ИД-строка субъекта, который загрузил документ
     * @param sSubjectName_Upload строка-название субъекта, который загрузил документ (временный парметр, будет убран)
     * @param sName строка-название документа
     * @param sFile строка-название и расширение файла
     * @param nID_DocumentType ИД-номер типа документа
     * @param documentContentTypeName строка-тип контента документа
     * @param sContent контект в виде строки-обьекта
     * @param nID_Subject ИД-номер субъекта документа (владельца) ????????????????????????????????????
     */
    @RequestMapping(value = "/setDocument", method = RequestMethod.GET)
    public
    @ResponseBody
    Long setDocument(
            @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
            @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
            @RequestParam(value = "sName") String sName,
            @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
            @RequestParam(value = "sDocumentContentType", required = false) String documentContentTypeName,
            @RequestParam(value = "soDocumentContent") String sContent,
            HttpServletRequest request) throws IOException {

        String sFileName = "filename.txt";
        String sFileContentType = "text/plain";
        byte[] aoContent = sContent.getBytes();

        documentContentTypeName =
                request.getHeader(CONTENT_TYPE_HEADER) != null ?
                        request.getHeader("filename") :
                        documentContentTypeName;
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

        Subject subject_Upload = syncSubject_Upload(sID_Subject_Upload);

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
     * @param sID_Subject_Upload ИД-строка субъекта, который загрузил документ
     * @param sSubjectName_Upload строка-название субъекта, который загрузил документ (временный парметр, нужно убрать его)
     * @param sName строка-название документа
     * @param nID_DocumentType ИД-номер типа документа
     * @param sDocumentContentType строка-тип контента документа
     * @param soDocumentContent контент в виде строки-обьекта
     * @param nID_Subject ИД-номер субъекта документа (владельца)????????????????????????????????????
     * @param oFile обьект файла (тип MultipartFile)
     */
    @RequestMapping(value = "/setDocumentFile", method = RequestMethod.POST)
    public
    @ResponseBody
    Long setDocumentFile(
            @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
            @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
            @RequestParam(value = "sName") String sName,
            @RequestParam(value = "sFileExtension", required = false) String sFileExtension,
            @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
            @RequestParam(value = "nID_DocumentContentType", required = false) Long nID_DocumentContentType,
            @RequestParam(value = "oFile", required = false) MultipartFile oFile,
            @RequestParam(value = "file", required = false) MultipartFile oFile2,
            HttpServletRequest request) throws IOException {

        if (oFile == null) {
            oFile = oFile2;
        }

        String sOriginalFileName = oFile.getOriginalFilename();
        LOG.info("sOriginalFileName=" + sOriginalFileName);

        String sOriginalContentType = oFile.getContentType();
        LOG.info("sOriginalContentType=" + sOriginalContentType);

        String sFileName = request.getHeader("filename");
        LOG.info("sFileName(before)=" + sFileName);

        if (sFileName == null || "".equals(sFileName.trim())) {

            LOG.info("sFileExtension=" + sFileExtension);
            if (sFileExtension != null && !sFileExtension.trim().isEmpty()
                    && sOriginalFileName != null && !sOriginalFileName.trim().isEmpty()
                    && sOriginalFileName.endsWith(sFileExtension)) {
                sFileName = sOriginalFileName;
                LOG.info("sOriginalFileName has equal ext! sFileName(all ok)=" + sFileName);
            } else {
                Enumeration<String> a = request.getHeaderNames();
                for (int n = 0; a.hasMoreElements() && n < 100; n++) {
                    String s = a.nextElement();
                    LOG.info("n=" + n + ", s=" + s + ", value=" + request.getHeader(s));
                }
                String fileExp = RedisUtil.getFileExp(sOriginalFileName);
                fileExp = fileExp != null ? fileExp : ".zip.zip";
                fileExp = fileExp.equalsIgnoreCase(sOriginalFileName) ? sFileExtension : fileExp;
                fileExp = fileExp != null ? fileExp.toLowerCase() : ".zip";
                sFileName = sOriginalFileName + (fileExp.startsWith(".") ? "" : ".") + fileExp;
                LOG.info("sFileName(after)=" + sFileName);
            }
        }
        byte[] aoContent = oFile.getBytes();

        Subject subject_Upload = syncSubject_Upload(sID_Subject_Upload);

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
        createHistoryEvent(HistoryEventType.SET_DOCUMENT_INTERNAL,
                nID_Subject, sSubjectName_Upload, nID_Document, null);
        return nID_Document;
    }

    private Subject syncSubject_Upload(String sID_Subject_Upload) {
        Subject subject_Upload = subjectDao.getSubject(sID_Subject_Upload);
        if (subject_Upload == null) {
            subject_Upload = subjectOrganDao.setSubjectOrgan(sID_Subject_Upload).getoSubject();
        }
        return subject_Upload;
    }

    @RequestMapping(value = "/getSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<SubjectOrganJoin> getAllSubjectOrganJoins(
            @RequestParam(value = "nID_SubjectOrgan") Long nID_SubjectOrgan,
            @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @RequestParam(value = "nID_City", required = false) Long nID_City,
            @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @RequestParam(value = "bIncludeAttributes", required = false, defaultValue = "false") Boolean bIncludeAttributes,
            @RequestParam(value = "mAttributeCustom", required = false) String smAttributeCustom //Map<String, String> mAttributeCustom
    ) {
        
        List<SubjectOrganJoin> aSubjectOrganJoin = subjectOrganDao.findSubjectOrganJoinsBy(nID_SubjectOrgan, nID_Region, nID_City, sID_UA);
        if (bIncludeAttributes == false) {
            return aSubjectOrganJoin;
        }
        
        Map<String, String> mAttributeCustom = JsonRestUtils.readObject(smAttributeCustom, Map.class);
        
        Map<String, Object> mAttributeReturn = new HashMap();
        //mAttributeAll.putAll(mAttributeCustom);
        //Map<String, String> jsonData = new HashMap<>();

        for (SubjectOrganJoin oSubjectOrganJoin : aSubjectOrganJoin) {
            mAttributeReturn = new HashMap(mAttributeCustom);
            List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute = subjectOrganJoinAttributeDao.getSubjectOrganJoinAttributes(oSubjectOrganJoin);
            if (aSubjectOrganJoinAttribute != null) {
                oSubjectOrganJoin.addAttributeList(aSubjectOrganJoinAttribute);
                for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
                    if (!oSubjectOrganJoinAttribute.getValue().startsWith("=")) {
                        mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                        //oSubjectOrganJoinAttribute.setValue(getCalculatedFormulaValue(oSubjectOrganJoinAttribute.getValue(), mAttributeReturn));
                    }
                }
                for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
                    if (oSubjectOrganJoinAttribute.getValue().startsWith("=")) {
                        oSubjectOrganJoinAttribute.setValue(getCalculatedFormulaValue(oSubjectOrganJoinAttribute.getValue(), mAttributeReturn));
                        mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                    }
                }
            }
        }
        return aSubjectOrganJoin;
    }

    private String getCalculatedFormulaValue(String sFormulaOriginal, Map<String, Object> mParam) {//String
        String sReturn = null;
        String sFormula=sFormulaOriginal;
        if(sFormula==null || "".equals(sFormula.trim())){
                LOG.warn("[getCalculatedFormulaValue](sFormula="+sFormula+",mParam="+mParam+"):");
        }else{
            for (Map.Entry<String, ?> oParam : mParam.entrySet()) {
                String sValue = (String)oParam.getValue();
                sFormula = sFormula.replaceAll("\\Q["+oParam.getKey()+"]\\E",sValue);
            }
            try{
                Map<String, Object> m = new HashMap<String, Object>();
                Object o = new JSExpressionUtil().getObjectResultOfCondition(m, mParam, sFormula); //getResultOfCondition
                sReturn = "" + o;
                LOG.info("[getCalculatedFormulaValue](sFormulaOriginal="+sFormulaOriginal+",sFormula="+sFormula+",mParam="+mParam+",sReturn="+sReturn+"):");
            }catch(Exception oException){
                LOG.error("[getCalculatedFormulaValue](sFormulaOriginal="+sFormulaOriginal+",sFormula="+sFormula+",mParam="+mParam+"):", oException);
            }
        }
        return sReturn;
    }



    @RequestMapping(value = "/setSubjectOrganJoin",
            method = RequestMethod.GET,
            headers = { "Accept=application/json" })
    public
    @ResponseBody
    void setSubjectOrganJoin(
            @RequestParam(value = "nID_SubjectOrgan") Long organID,
            @RequestParam(value = "sNameUa") String nameUA,
            @RequestParam(value = "sNameRu") String nameRU,
            @RequestParam(value = "sID_Privat") String privateID,
            @RequestParam(value = "sID_Public") String publicID,
            @RequestParam(value = "sGeoLongitude") String geoLongitude,
            @RequestParam(value = "sGeoLatitude") String geoLatitude,
            @RequestParam(value = "sID_UA") String uaID,
            @RequestParam(value = "nID_Region", required = false) Long regionID,
            @RequestParam(value = "nID_City", required = false) Long cityID
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

    private void createHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, String sSubjectName_Upload, Long nID_Document,
            Document document) {
        Map<String, String> values = new HashMap<>();
        try {
            Document oDocument = document == null ? documentDao
                    .getDocument(nID_Document) : document;
            values.put(HistoryEventMessage.DOCUMENT_TYPE, oDocument
                    .getDocumentType().getName());
            values.put(HistoryEventMessage.DOCUMENT_NAME, oDocument.getName());
            values.put(HistoryEventMessage.ORGANIZATION_NAME,
                    sSubjectName_Upload);
        } catch (RuntimeException e) {
            LOG.warn("can't get document info!", e);
        }
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, values);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage);
        } catch (IOException e) {
            LOG.error("error during creating HistoryEvent", e);
        }
    }

    @RequestMapping(value = "/removeSubjectOrganJoins",
            method = RequestMethod.GET,
            headers = { "Accept=application/json" })
    public
    @ResponseBody
    void removeSubjectOrganJoins(
            @RequestParam(value = "nID_SubjectOrgan") Long organID,
            @RequestParam(value = "asID_Public") String[] publicIDs) {

        subjectOrganDao.removeSubjectOrganJoin(organID, publicIDs);
    }

    //################ DocumentType services ###################

    /**
     * получение списка всех "нескрытых" типов документов, т.е. у которых поле bHidden=false
     */
    @RequestMapping(value = "/getDocumentTypes", method = RequestMethod.GET)
    public
    @ResponseBody
    List<DocumentType> getDocumentTypes() throws Exception {
        return documentTypeDao.getDocumentTypes();
    }

    /**
     * добавить/изменить запись типа документа
     * @param nID ид записи (число)
     * @param sName название записи (строка)
     * @param bHidden скрывать/не скрывать (при отдаче списка всех записей, булевское, по умолчанию = false)
     */
    @RequestMapping(value = "/setDocumentType", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity setDocumentType(
            @RequestParam(value = "nID") Long nID,
            @RequestParam(value = "sName") String sName,
            @RequestParam(value = "bHidden", required = false) Boolean bHidden) {
        ResponseEntity result;
        try {
            DocumentType documentType = documentTypeDao.setDocumentType(nID, sName, bHidden);
            result = JsonRestUtils.toJsonResponse(documentType);
        } catch (RuntimeException e) {
        	LOG.warn(e.getMessage(), e);
            result = toJsonErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return result;
    }

    private ResponseEntity toJsonErrorResponse(HttpStatus httpStatus, String eMessage) {//?? move to JsonRestUtils
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        headers.setContentType(mediaType);
        headers.set(REASON_HEADER, eMessage);
        return new ResponseEntity<>(headers, httpStatus);
    }

    /**
     * удаление записи по ее ид
     * @param nID ид записи
     */
    @RequestMapping(value = "/removeDocumentType", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeDocumentType(
            @RequestParam(value = "nID") Long nID,
            HttpServletResponse response) {
        try {
            documentTypeDao.removeDocumentType(nID);
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader(REASON_HEADER, e.getMessage());
        }
    }

    //################ DocumentContentType services ###################

    /**
     * получение списка типов контента документов
     */
    @RequestMapping(value = "/getDocumentContentTypes", method = RequestMethod.GET)
    public
    @ResponseBody
    List<DocumentContentType> getDocumentContentTypes() {
        return documentContentTypeDao.getDocumentContentTypes();
    }

    /**
     * добавить/изменить запись типа контента документа
     * @param nID ид записи
     * @param sName название записи
     */
    @RequestMapping(value = "/setDocumentContentType", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity setDocumentContentType(
            @RequestParam(value = "nID") Long nID,
            @RequestParam(value = "sName") String sName) {
        ResponseEntity result;
        try {
            DocumentContentType documentType = documentContentTypeDao.setDocumentContentType(nID, sName);
            result = JsonRestUtils.toJsonResponse(documentType);
        } catch (RuntimeException e) {
        	LOG.warn(e.getMessage(), e);
            result = toJsonErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return result;
    }

    /**
     * удаление записи по ее ид
     * @param nID ид записи
     */
    @RequestMapping(value = "/removeDocumentContentType", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeDocumentContentType(
            @RequestParam(value = "nID") Long nID,
            HttpServletResponse response) {
        try {
            documentContentTypeDao.removeDocumentContentType(nID);
        } catch (RuntimeException e) {
        	LOG.warn(e.getMessage(), e);
            response.setStatus(403);
            response.setHeader(REASON_HEADER, e.getMessage());
        }
    }

}
