package org.igov.service.controller;

import org.igov.model.HistoryEventDao;
import org.igov.model.DocumentType;
import org.igov.model.DocumentOperator_SubjectOrgan;
import org.igov.model.DocumentContentType;
import org.igov.model.Document;
import org.igov.model.DocumentContentTypeDao;
import org.igov.model.DocumentTypeDao;
import org.igov.model.DocumentDao;
import org.igov.model.Subject;
import org.igov.model.SubjectOrganJoin;
import org.igov.model.SubjectOrganJoinAttribute;
import org.igov.model.SubjectOrganDao;
import org.igov.model.SubjectOrganJoinAttributeDao;
import org.igov.model.SubjectDao;
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
import org.igov.util.convert.JSExpressionUtil;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.enums.Currency;
import org.igov.model.enums.HistoryEventMessage;
import org.igov.model.enums.HistoryEventType;
import org.igov.model.enums.Language;
import org.igov.io.liqpay.LiqBuy;
import org.igov.model.HandlerFactory;
import org.igov.io.bankid.BankIDConfig;
import org.igov.io.bankid.BankIDUtils;
import org.igov.io.GeneralConfig;
import org.igov.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.igov.service.controller.ActivitiExceptionController;
import org.igov.service.controller.ActivitiRestException;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Api(tags = { "ActivitiRestDocumentController" }, description = "Работа с документами")
@RequestMapping(value = "/services")
public class ActivitiRestDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestDocumentController.class);
    private static final String NO_ACCESS_MESSAGE = "You don't have access!";
    private static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED_ERROR_CODE";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String REASON_HEADER = "Reason";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с документами. ";

    private static final String noteGetDocument = noteController + "Получение документа по ид документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocument\n\n\n"
		+ "- nID - ИД-номер документа\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocument?nID=1\n\n"
		+ "Response\n\n"
		+ noteCODEJSON
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
		+ noteCODE;

    private static final String noteGetDocumentAccessByHandler = noteController + "Получение контента документа по коду доступа,оператору, типу документа и паролю #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentAccessByHandler\n\n\n"
		+ "- sCode_DocumentAccess - код доступа документа\n"
		+ "- nID_DocumentOperator_SubjectOrgan - код органа(оператора)\n"
		+ "- nID_DocumentType - типа документа (опциональный)\n"
		+ "- sPass - пароль для доступа к документу (опциональный, пока только для документов у которы sCodeType=SMS)\n\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentAccessByHandler?sCode_DocumentAccess=2&nID_DocumentOperator_SubjectOrgan=2&sPass=123&nID_DocumentType=1\n\n"
		+ "Response КОНТЕНТ ДОКУМЕНТА В ВИДЕ СТРОКИ\n";


    private static final String noteGetDocumentOperators = noteController + "Получение всех операторов(органов) которые имею право доступа к документу #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentOperators\n\n\n"
		+ "Примеры: https://test.igov.org.ua/wf/service/services/getDocumentOperators\n\n"
		+ "Response\n\n"
		+ noteCODEJSON
		+ "[\n"
		+ "    {\n"
		+ "        \"nID_SubjectOrgan\": 2,\n"
		+ "        \"sHandlerClass\": \"org.igov.activiti.common.document.DocumentAccessHandler_IGov\",\n"
		+ "        \"nID\": 1,\n"
		+ "        \"sName\": \"iGov\"\n"
		+ "    }\n"
		+ "]\n"
		+ noteCODE;
 
    private static final String noteGetDocumentContent = noteController + "Получение контента документа по ид документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentContent\n\n\n"
		+ "- nID - ИД-номер документа\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentContent?nID=1\n\n"
		+ "Response КОНТЕНТ ДОКУМЕНТА В ВИДЕ СТРОКИ\n";

    private static final String noteGetDocumentFile = noteController + "Получение документа в виде файла по ид документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentFile\n\n\n"
		+ "- nID - ИД-номер документа\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentFile?nID=1\n\n"
		+ "Response ЗАГРУЖЕННЫЙ ФАЙЛ\n";

    private static final String noteGetDocumentAbstract = noteController + "Получение документа в виде файла #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentAbstract\n\n\n"
		+ "- sID - строковой ID документа (параметр обязателен)\n\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя) (параметр опционален)\n"
		+ "- nID_DocumentOperator_SubjectOrgan - определяет класс хэндлера который будет обрабатывать запрос (параметр опционален)\n"
		+ "- nID_DocumentType - определяет тип документа, например 0 - \"Квитанція про сплату\", 1 - \"Довідка про рух по картці (для візових центрів)\" (параметр опционален)\n"
		+ "- sPass - пароль (параметр опционален)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentAbstract?sID=150826SV7733A36E803B\n\n"
		+ "Response ЗАГРУЖЕННЫЙ ФАЙЛ\n";

    private static final String noteGetDocuments = noteController + "Получение списка загруженных субъектом документов #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocuments\n\n\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocuments?nID_Subject=2\n\n"
		+ "Response\n\n"
		+ noteCODEJSON
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
		+ noteCODE;

    private static final String noteGetPayButtonHTML_LiqPay = noteController + "Получение кнопки для оплаты через LiqPay #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/getPayButtonHTML_LiqPay\n\n\n"
		+ "Параметры:\n\n"
		+ "- sID_Merchant - ид меранта\n"
		+ "- sSum - сумма оплаты\n"
		+ "- oID_Currency - валюта\n"
		+ "- oLanguage - язык\n"
		+ "- sDescription - описание\n"
		+ "- sID_Order - ид заказа\n"
		+ "- sURL_CallbackStatusNew - URL для отправки статуса\n"
		+ "- sURL_CallbackPaySuccess - URL для отправки ответа\n"
		+ "- nID_Subject - ид субъекта\n"
		+ "- bTest - тестовый вызов или нет\n\n\n"
		+ "Пример:\n"
		+ "https://test.igov.org.ua/wf/service/services/getPayButtonHTML_LiqPay?sID_Merchant=i10172968078&sSum=55,00&oID_Currency=UAH&oLanguage=RUSSIAN&sDescription=test&sID_Order=12345&sURL_CallbackStatusNew=&sURL_CallbackPaySuccess=&nID_Subject=1&bTest=true\n";

    private static final String noteSetDocument = noteController + "Сохранение документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/setDocument\n\n\n"
		+ "- sID_Subject_Upload - ИД-строка субъекта, который загрузил документ\n"
		+ "- sSubjectName_Upload - строка-название субъекта, который загрузил документ (временный парметр, будет убран)\n"
		+ "- sName - строка-название документа\n"
		+ "- sFile - строка-название и расширение файла\n"
		+ "- nID_DocumentType - ИД-номер типа документа\n"
		+ "- sDocumentContentType - строка-тип контента документа\n"
		+ "- soDocumentContent - контект в виде строки-обьекта\n"
		+ "- nID_Subject - ИД-номер субъекта документа (владельца)\n\n\n"
		+ "Пример:\n"
		+ "https://test.igov.org.ua/wf/service/services/setDocument?sID_Subject_Upload=123&sSubjectName_Upload=Vasia&sName=Pasport&sFile=file.txt&nID_DocumentType=1&sDocumentContentType=application/zip&soDocumentContent=ffffffffffffffffff&nID_Subject=1\n\n"
		+ "Response ИД ДОКУМЕНТА\n";

    private static final String noteSetDocumentFile = noteController + "Сохранение документа в виде файла (контент файла шлется в теле запроса) #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/setDocumentFile\n\n\n"
		+ "- sID_Subject_Upload - ИД-строка субъекта, который загрузил документ\n"
		+ "- sSubjectName_Upload - строка-название субъекта, который загрузил документ (временный парметр, нужно убрать его)\n"
		+ "- sName - строка-название документа\n"
		+ "- nID_DocumentType - ИД-номер типа документа\n"
		+ "- sDocumentContentType - строка-тип контента документа\n"
		+ "- soDocumentContent - контент в виде строки-обьекта\n"
		+ "- nID_Subject - ИД-номер субъекта документа (владельца)\n"
		+ "- oFile - обьект файла (тип MultipartFile)\n\n\n"
		+ "Response ИД ДОКУМЕНТА\n";

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
		+ "https://test.igov.org.ua/wf/service/services/getSubjectOrganJoins?nID_SubjectOrgan=1&sID_UA=1\n";

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

    private static final String noteGetDocumentTypes = noteController + "ТИПЫ ДОКУМЕНТОВ. Получение списка всех \"нескрытых\" типов документов #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentTypes\n\n\n"
		+ "получение списка всех \"нескрытых\" типов документов, т.е. у которых поле bHidden=false\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentTypes\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "[\n"
		+ "    {\"nID\":0,\"sName\":\"Другое\", \"bHidden\":false},\n"
		+ "    {\"nID\":1,\"sName\":\"Справка\", \"bHidden\":false},\n"
		+ "    {\"nID\":2,\"sName\":\"Паспорт\", \"bHidden\":false}\n"
		+ "]\n"
		+ noteCODE;

    private static final String noteSetDocumentType = noteController + "ТИПЫ ДОКУМЕНТОВ. Добавить/изменить запись типа документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/setDocumentType\n\n\n"
		+ "Параметры:\n\n"
		+ "- nID -- ид записи (число)\n"
		+ "- sName -- название записи (строка)\n"
		+ "- bHidden -- скрывать/не скрывать (при отдаче списка всех записей, булевское, по умолчанию = false)\n\n"
		+ "Если запись с ид=nID не будет найдена, то создастся новая запись (с автогенерируемым nID), иначе -- обновится текущая.\n\n"
		+ "примеры:\n\n"
		+ "создать новый тип: https://test.igov.org.ua/wf/service/services/setDocumentType?nID=100&sName=test\n\n"
		+ "ответ: \n"
		+ noteCODEJSON
		+ "{\"nID\":20314,\"sName\":\"test\", , \"bHidden\":false}\n"
		+ noteCODE
		+ "изменить (взять ид из предыдущего ответа): https://test.igov.org.ua/wf/service/services/setDocumentType?nID=20314&sName=test2\n\n"
		+ "ответ: \n"
		+ noteCODEJSON
		+ "{\"nID\":20314,\"sName\":\"test2\", \"bHidden\":false}\n"
		+ noteCODE;

    private static final String noteRemoveDocumentType = noteController + "ТИПЫ ДОКУМЕНТОВ. Удаление записи по ее ид#####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/removeDocumentType\n\n\n"
		+ "Параметры:\n\n"
		+ "- nID -- ид записи\n\n"
		+ "Если запись с ид=nID не будет найдена, то вернется ошибка 403. Record not found, иначе -- запись удалится.\n\n"
		+ "пример: https://test.igov.org.ua/wf/service/services/removeDocumentType?nID=20314\n\n"
		+ "ответ: 200 ok\n";

    private static final String noteGetDocumentContentTypes = noteController + "ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Получение списка типов контента документов #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/getDocumentContentTypes\n\n\n"
		+ "Пример: https://test.igov.org.ua/wf/service/services/getDocumentContentTypes\n\n"
		+ "Response\n"
		+ noteCODEJSON
		+ "[\n"
		+ "    {\"nID\":0,\"sName\":\"application/json\"},\n"
		+ "    {\"nID\":1,\"sName\":\"application/xml\"},\n"
		+ "    {\"nID\":2,\"sName\":\"text/plain\"},\n"
		+ "    {\"nID\":3,\"sName\":\"application/jpg\"}\n"
		+ "]\n"
		+ noteCODE;

    private static final String noteSetDocumentContentType = noteController + "ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Добавить/изменить запись типа контента документа #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/setDocumentContentType\n\n\n"
		+ "Параметры:\n\n"
		+ "- nID -- ид записи\n"
		+ "- sName -- название записи\n\n"
		+ "Если запись с ид=nID не будет найдена, то создастся новая запись (с автогенерируемым nID), иначе -- обновится текущая.\n\n"
		+ "Примеры:\n\n"
		+ "создать новый тип: \n"
		+ "https://test.igov.org.ua/wf/service/services/setDocumentContentType?nID=100&sName=test\n\n"		
		+ "ответ:\n"
		+ noteCODEJSON
		+ "{\"nID\":20311,\"sName\":\"test\"}\n"
		+ noteCODE
		+ "изменить (взять ид из предыдущего ответа): \n"
		+ "https://test.igov.org.ua/wf/service/services/setDocumentContentType?nID=20311&sName=test2\n\n"		
		+ "ответ:\n"
		+ noteCODEJSON
		+ "{\"nID\":20311,\"sName\":\"test2\"}\n"
		+ noteCODE;

    private static final String noteRemoveDocumentContentType = noteController + "ТИПЫ КОНТЕНТА ДОКУМЕНТОВ. Удаление записи по ее ид #####\n\n"
		+ "HTTP Context: http://server:port/wf/service/services/removeDocumentContentType\n\n"
		+ "Параметры\n\n"
		+ "- nID -- ид записи\n\n"
		+ "Если запись с ид=nID не будет найдена, то вернется ошибка 403. Record not found, иначе -- запись удалится.\n\n"
		+ "пример:\n"
		+ "https://test.igov.org.ua/wf/service/services/removeDocumentContentType?nID=20311\n\n"
		+ "ответ: 200 ok\n";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
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
    @ApiOperation(value = "Получение документа по ид документа", notes = noteGetDocument )
    @RequestMapping(value = "/getDocument", method = RequestMethod.GET)
    public
    @ResponseBody
    Document getDocument( @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
	    @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) throws ActivitiRestException {
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
    @ApiOperation(value = "Получение контента документа по коду доступа,оператору, типу документа и паролю", notes = noteGetDocumentAccessByHandler )
    @RequestMapping(value = "/getDocumentAccessByHandler",
            method = RequestMethod.GET,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    Document getDocumentAccessByHandler(
	    @ApiParam(value = "код доступа документа", required = true) @RequestParam(value = "sCode_DocumentAccess") String accessCode,
	    @ApiParam(value = "код органа(оператора)", required = true) @RequestParam(value = "nID_DocumentOperator_SubjectOrgan") Long organID,
	    @ApiParam(value = "типа документа (опциональный)", required = false) @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
	    @ApiParam(value = "пароль для доступа к документу (опциональный, пока только для документов у которы sCodeType=SMS)", required = false) @RequestParam(value = "sPass", required = false) String password,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID_Subject", defaultValue = "1") Long nID_Subject
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
    @ApiOperation(value = "Получение всех операторов(органов) которые имею право доступа к документу", notes = noteGetDocumentOperators )
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
    @ApiOperation(value = "Получение контента документа по ид документа", notes = noteGetDocumentContent )
    @RequestMapping(value = "/getDocumentContent", method = RequestMethod.GET)
    public
    @ResponseBody
    String getDocumentContent(@ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
	    @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) throws ActivitiRestException {
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
    @ApiOperation(value = "Получение документа в виде файла по ид документа", notes = noteGetDocumentFile )
    @RequestMapping(value = "/getDocumentFile", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] getDocumentFile(
	    @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id,
	    @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
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
    @ApiOperation(value = "Получение документа в виде файла", notes = noteGetDocumentAbstract )
    @RequestMapping(value = "/getDocumentAbstract", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] getDocumentAbstract(
	    @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя) ", required = false) @RequestParam(value = "nID_Subject", required = false, defaultValue = "1") Long nID_Subject,
	    @ApiParam(value = "строковой ID документа", required = false) @RequestParam(value = "sID", required = false) String sID,
	    @ApiParam(value = "определяет класс хэндлера который будет обрабатывать запрос", required = false) @RequestParam(value = "nID_DocumentOperator_SubjectOrgan", required = false) Long organID,
	    @ApiParam(value = "определяет тип документа, например 0 - \"Квитанція про сплату\", 1 - \"Довідка про рух по картці (для візових центрів)\"", required = false) @RequestParam(value = "nID_DocumentType", required = false) Long docTypeID,
	    @ApiParam(value = "пароль", required = false) @RequestParam(value = "sPass", required = false) String password,

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
    @ApiOperation(value = "Получение списка загруженных субъектом документов", notes = noteGetDocuments )
    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Document> getDocuments(
	    @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) {
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
    @ApiOperation(value = "Получение кнопки для оплаты через LiqPay", notes = noteGetPayButtonHTML_LiqPay )
    @RequestMapping(value = "/getPayButtonHTML_LiqPay", method = RequestMethod.GET)
    public
    @ResponseBody
    String getPayButtonHTML_LiqPay(
	    @ApiParam(value = "ид мерчанта", required = true) @RequestParam(value = "sID_Merchant", required = true) String sID_Merchant,
	    @ApiParam(value = "сумма оплаты", required = true) @RequestParam(value = "sSum", required = true) String sSum,
	    @ApiParam(value = "валюта", required = true) @RequestParam(value = "oID_Currency", required = true) Currency oID_Currency,
	    @ApiParam(value = "язык", required = true) @RequestParam(value = "oLanguage", required = true) Language oLanguage,
	    @ApiParam(value = "описание", required = true) @RequestParam(value = "sDescription", required = true) String sDescription,
	    @ApiParam(value = "ид заказа", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
	    @ApiParam(value = "URL для отправки статуса", required = false) @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
	    @ApiParam(value = "URL для отправки ответа", required = false) @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess,
	    @ApiParam(value = "ид субъекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
	    @ApiParam(value = "тестовый вызов или нет", required = true) @RequestParam(value = "bTest", required = true) boolean bTest) throws Exception {

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
    @ApiOperation(value = "Сохранение документа", notes = noteSetDocument )
    @RequestMapping(value = "/setDocument", method = RequestMethod.GET)
    public
    @ResponseBody
    Long setDocument(
	    @ApiParam(value = "ИД-номер субъекта документа (владельца)", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
	    @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = true) @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
	    @ApiParam(value = "строка-название субъекта, который загрузил документ", required = true) @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
	    @ApiParam(value = "строка-название документа", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "ИД-номер типа документа", required = true) @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
	    @ApiParam(value = "строка-тип контента документа", required = false) @RequestParam(value = "sDocumentContentType", required = false) String documentContentTypeName,
	    @ApiParam(value = "контект в виде строки-обьекта", required = true) @RequestParam(value = "soDocumentContent") String sContent,
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
    @ApiOperation(value = "Сохранение документа в виде файла (контент файла шлется в теле запроса)", notes = noteSetDocumentFile )
    @RequestMapping(value = "/setDocumentFile", method = RequestMethod.POST)
    public
    @ResponseBody
    Long setDocumentFile(
	    @ApiParam(value = "ИД-номер субъекта документа (владельца)", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
	    @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = true) @RequestParam(value = "sID_Subject_Upload") String sID_Subject_Upload,
	    @ApiParam(value = "sSubjectName_Upload", required = true) @RequestParam(value = "sSubjectName_Upload") String sSubjectName_Upload,
	    @ApiParam(value = "строка-название документа", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sFileExtension", required = false) String sFileExtension,
	    @ApiParam(value = "ИД-номер типа документа", required = true) @RequestParam(value = "nID_DocumentType") Long nID_DocumentType,
	    @ApiParam(value = "строка-тип контента документа", required = false) @RequestParam(value = "nID_DocumentContentType", required = false) Long nID_DocumentContentType,
	    @ApiParam(value = "обьект файла (тип MultipartFile)", required = false) @RequestParam(value = "oFile", required = false) MultipartFile oFile,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "file", required = false) MultipartFile oFile2,
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

    @ApiOperation(value = "Получает весь массив объектов п.2 (либо всех либо в рамках заданных в запросе nID_Region, nID_City или sID_UA)", notes = noteGetAllSubjectOrganJoins )
    @RequestMapping(value = "/getSubjectOrganJoins",
            method = RequestMethod.POST,
            headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<SubjectOrganJoin> getAllSubjectOrganJoins(
	    @ApiParam(value = "ИД-номер Джоина Субьекта-органа", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Субьекта-органа", required = true) @RequestParam(value = "nID_SubjectOrgan") Long nID_SubjectOrgan,
	    @ApiParam(value = "ИД-номер места-региона (deprecated)", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
	    @ApiParam(value = "ИД-номер места-города (deprecated)", required = false) @RequestParam(value = "nID_City", required = false) Long nID_City,
	    @ApiParam(value = "ИД-строка места (унифицировано)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
	    @ApiParam(value = "Включить вівод атрибутов", required = false) @RequestParam(value = "bIncludeAttributes", required = false, defaultValue = "false") Boolean bIncludeAttributes,
	    @ApiParam(value = "Карта кастомніх атрибутов", required = false) @RequestBody String smAttributeCustom //Map<String, String> mAttributeCustom
    ) {
        
        List<SubjectOrganJoin> aSubjectOrganJoin = subjectOrganDao.findSubjectOrganJoinsBy(nID_SubjectOrgan, nID_Region, nID_City, sID_UA);
        /*List<SubjectOrganJoin> aSubjectOrganJoin = new LinkedList();
        if(nID != null){
            aSubjectOrganJoin = subjectOrganDao.findSubjectOrganJoinsBy(nID_SubjectOrgan, nID_Region, nID_City, sID_UA);
        }else{
            SubjectOrganJoin oSubjectOrganJoin = subjectOrganDao.findSubjectOrganJoin(nID);
            aSubjectOrganJoin.add(oSubjectOrganJoin);
        }*/
        if (bIncludeAttributes == false) {
            return aSubjectOrganJoin;
        }
        LOG.info("[getAllSubjectOrganJoins](smAttributeCustom="+smAttributeCustom+",nID_SubjectOrgan="+nID_SubjectOrgan+",sID_UA="+sID_UA+"):...");
        
        Map<String, String> mAttributeCustom = JsonRestUtils.readObject(smAttributeCustom, Map.class);
        LOG.info("[getAllSubjectOrganJoins](mAttributeCustom="+mAttributeCustom+"):");
        
        Map<String, Object> mAttributeReturn = new HashMap();
        //mAttributeAll.putAll(mAttributeCustom);
        //Map<String, String> jsonData = new HashMap<>();
        List<SubjectOrganJoin> aSubjectOrganJoinReturn = new LinkedList();
        for (SubjectOrganJoin oSubjectOrganJoin : aSubjectOrganJoin) {
            /*if(nID != null && nID != oSubjectOrganJoin.getId()){
                //aSubjectOrganJoin.remove(oSubjectOrganJoin);
            }else */
            if(nID == null || (nID != null && (nID+"").equals(oSubjectOrganJoin.getId()+""))){
                mAttributeReturn = new HashMap();
                List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute = subjectOrganJoinAttributeDao.getSubjectOrganJoinAttributes(oSubjectOrganJoin);
                if (aSubjectOrganJoinAttribute != null) {
                    //oSubjectOrganJoin.addAttributeList(aSubjectOrganJoinAttribute);

                    //mAttributeReturn = new HashMap(mAttributeCustom);
                    for (Map.Entry<String, String> oAttributeCustom : mAttributeCustom.entrySet()) {
                        if (!oAttributeCustom.getValue().startsWith("=")) {
                            //oSubjectOrganJoin.addAttribute(oAttributeCustom.getKey(), oAttributeCustom.getValue());
                            mAttributeReturn.put(oAttributeCustom.getKey(), oAttributeCustom.getValue());
                        }
                    }

                    for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
                        if (!oSubjectOrganJoinAttribute.getValue().startsWith("=")) {
                            oSubjectOrganJoin.addAttribute(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                            mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                        }
                    }

                    for (Map.Entry<String, String> oAttributeCustom : mAttributeCustom.entrySet()) {
                        if (oAttributeCustom.getValue().startsWith("=")) {
                            String sValue = getCalculatedFormulaValue(oAttributeCustom.getValue(), mAttributeReturn);
                            oSubjectOrganJoin.addAttribute(oAttributeCustom.getKey(), sValue);
                            mAttributeReturn.put(oAttributeCustom.getKey(), oAttributeCustom.getValue());
                        }
                    }

                    for (SubjectOrganJoinAttribute oSubjectOrganJoinAttribute : aSubjectOrganJoinAttribute) {
                        if (oSubjectOrganJoinAttribute.getValue().startsWith("=")) {
                            String sValue = getCalculatedFormulaValue(oSubjectOrganJoinAttribute.getValue(), mAttributeReturn);
                            //oSubjectOrganJoinAttribute.setValue(sValue);
                            oSubjectOrganJoin.addAttribute(oSubjectOrganJoinAttribute.getName(), sValue);
                            mAttributeReturn.put(oSubjectOrganJoinAttribute.getName(), oSubjectOrganJoinAttribute.getValue());
                        }
                    }


                }
                aSubjectOrganJoinReturn.add(oSubjectOrganJoin);
            }
        }
        LOG.info("[getAllSubjectOrganJoins](mAttributeReturn="+mAttributeReturn+"):");
        return aSubjectOrganJoinReturn;//aSubjectOrganJoin
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
            sFormula=sFormula.substring(1);
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

    //################ DocumentType services ###################

    /**
     * получение списка всех "нескрытых" типов документов, т.е. у которых поле bHidden=false
     */
    @ApiOperation(value = "Получение списка всех \"нескрытых\" типов документов", notes = noteGetDocumentTypes )
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
    @ApiOperation(value = "Добавить/изменить запись типа документа", notes = noteSetDocumentType )
    @RequestMapping(value = "/setDocumentType", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity setDocumentType(
	    @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
	    @ApiParam(value = "название записи", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "скрывать/не скрывать (при отдаче списка всех записей, булевское, по умолчанию = false)", required = false) @RequestParam(value = "bHidden", required = false) Boolean bHidden) {
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
    @ApiOperation(value = "Удаление записи по ее ид", notes = noteRemoveDocumentType )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") } )
    @RequestMapping(value = "/removeDocumentType", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeDocumentType(
	    @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
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
    @ApiOperation(value = "Получение списка типов контента документов", notes = noteGetDocumentContentTypes )
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
    @ApiOperation(value = "Добавить/изменить запись типа контента документа", notes = noteSetDocumentContentType )
    @RequestMapping(value = "/setDocumentContentType", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity setDocumentContentType(
	    @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
	    @ApiParam(value = "название записи", required = true) @RequestParam(value = "sName") String sName) {
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
    @ApiOperation(value = "Удаление записи по ее ид", notes = noteRemoveDocumentContentType )
    @RequestMapping(value = "/removeDocumentContentType", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeDocumentContentType(
	    @ApiParam(value = "ид записи", required = true) @RequestParam(value = "nID") Long nID,
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
