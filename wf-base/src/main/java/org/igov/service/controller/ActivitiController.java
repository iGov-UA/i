package org.igov.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import io.swagger.annotations.*;
import liquibase.util.csv.CSVWriter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.activiti.rest.service.api.runtime.process.ExecutionBaseResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.igov.activiti.bp.HistoryEventService;
import org.igov.activiti.common.AbstractModelTask;
import org.igov.activiti.common.BuilderAttachModel;
import org.igov.activiti.common.ByteArrayMultipartFileOld;
import org.igov.activiti.common.ReportField;
import org.igov.activiti.form.QueueDataFormType;
import org.igov.activiti.systemtask.FileTaskUpload;
import org.igov.io.GeneralConfig;
import org.igov.io.bankid.BankIDConfig;
import org.igov.io.bankid.BankIDUtils;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.mail.Mail;
import org.igov.model.flow.FlowSlotTicketDao;
import org.igov.service.adapter.AttachmentEntityAdapter;
import org.igov.service.adapter.ProcDefinitionAdapter;
import org.igov.service.adapter.TaskAssigneeAdapter;
import org.igov.service.entity.*;
import org.igov.service.entity.Process;
import org.igov.service.interceptor.exception.*;
import org.igov.util.EGovStringUtils;
import org.igov.util.SecurityUtils;
import org.igov.util.Util;
import org.igov.util.convert.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.igov.activiti.common.AbstractModelTask.getByteArrayMultipartFileFromRedis;

//import com.google.common.base.Optional;

/**
 * @author BW
 */

@Controller
@Api(tags = { "ActivitiRestApiController" }, description = "Activiti")
@RequestMapping(value = "/rest")
public class ActivitiController extends ExecutionBaseResource {

        public static final String CANCEL_INFO_FIELD = "sCancelInfo";
    private static final int DEFAULT_REPORT_FIELD_SPLITTER = 59;
    private static final Logger LOG = LoggerFactory
            .getLogger(ActivitiController.class);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd:HH-mm-ss", Locale.ENGLISH);
    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE = "\n```\n";
    private static final String noteCODEJSON = "\n```json\n";
    private static final String noteController = "#####  Activiti. ";
    private static final String noteStartProcessByKey = noteController + "Запуск процесса Activiti #####\n\n"
            + "HTTP Context: https://server:port/wf/service/rest/start-process/{key}\n"
            + "- key - Ключ процесса\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/rest/start-process/citizensRequest\n\n"
            + "Response\n"
            + noteCODEJSON
            + "  {\n"
            + "    \"id\":\"31\"\n"
            + "  }\n"
            + noteCODE;
    private static final String noteGetProcessDefinitions =
            noteController + "Загрузка каталога сервисов из Activiti #####\n\n"
                    + "nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n"
                    + "Request:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/process-definitions\n\n"
                    + "Response:\n\n"
                    + noteCODEJSON
                    + "  [\n"
                    + "    {\n"
                    + "      \"id\": \"CivilCardAccountlRequest:1:9\",\n"
                    + "      \"category\": \"http://www.activiti.org/test\",\n"
                    + "      \"name\": \"Видача картки обліку об’єкта торговельного призначення\",\n"
                    + "      \"key\": \"CivilCardAccountlRequest\",\n"
                    + "      \"description\": \"Описание процесса\",\n"
                    + "      \"version\": 1,\n"
                    + "      \"resourceName\": \"dnepr-2.bpmn\",\n"
                    + "      \"deploymentId\": \"1\",\n"
                    + "      \"diagramResourceName\": \"dnepr-2.CivilCardAccountlRequest.png\",\n"
                    + "      \"tenantId\": \"diver\",\n"
                    + "      \"suspended\": true\n"
                    + "    }\n"
                    + "  ]\n"
                    + noteCODE;
    private static final String noteDeleteProcess = noteController + "описания нет #####\n\n";
    private static final String noteDeleteProcessTest = noteController + "описания нет #####\n\n";
    private static final String notePutAttachmentsToRedis = noteController + "описания нет #####\n\n";
    private static final String noteGetAttachmentsFromRedis = noteController + "описания нет #####\n\n";
    private static final String noteGetAttachmentsFromRedisBytes = noteController + "описания нет #####\n\n";
    private static final String noteCheckAttachmentsFromRedisSign =
            noteController + "Проверка ЭЦП на файле хранящемся в Redis #####\n\n"
                    + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/file/check_file_from_redis_sign?sID_File_Redis=sID_File_Redis"
                    + " -- возвращает json объект описывающий ЭЦП файла.\n\n"
                    + "sID_File_Redis - key по которому можно получить файл из хранилища Redis.\n"
                    + "Примеры:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/file/check_file_from_redis_sign?sID_File_Redis=d2993755-70e5-409e-85e5-46ba8ce98e1d\n\n"
                    + "Ответ json описывающий ЭЦП:\n\n"
                    + noteCODEJSON
                    + "{\n"
                    + "  \"state\": \"ok\",\n"
                    + "  \"customer\": {\n"
                    + "    \"inn\": \"1436057000\",\n"
                    + "    \"fullName\": \"Сервіс зберігання сканкопій\",\n"
                    + "    \"signatureData\": {\n"
                    + "      \"name\": \"АЦСК ПАТ КБ «ПРИВАТБАНК»\",\n"
                    + "      \"serialNumber\": \"0D84EDA1BB9381E80400000079DD02004A710800\",\n"
                    + "      \"timestamp\": \"29.10.2015 13:45:33\",\n"
                    + "      \"code\": true,\n"
                    + "      \"desc\": \"ПІДПИС ВІРНИЙ\",\n"
                    + "      \"dateFrom\": \"13.08.2015 11:24:31\",\n"
                    + "      \"dateTo\": \"12.08.2016 23:59:59\",\n"
                    + "      \"sn\": \"UA-14360570-1\"\n"
                    + "    },\n"
                    + "    \"organizations\": [\n"
                    + "      {\n"
                    + "        \"type\": \"edsOwner\",\n"
                    + "        \"name\": \"ПАТ КБ «ПРИВАТБАНК»\",\n"
                    + "        \"mfo\": \"14360570\",\n"
                    + "        \"position\": \"Технологічний сертифікат\",\n"
                    + "        \"ownerDesc\": \"Співробітник банку\",\n"
                    + "        \"address\": {\n"
                    + "          \"type\": \"factual\",\n"
                    + "          \"state\": \"Дніпропетровська\",\n"
                    + "          \"city\": \"Дніпропетровськ\"\n"
                    + "        }\n"
                    + "      },\n"
                    + "      {\n"
                    + "        \"type\": \"edsIsuer\",\n"
                    + "        \"name\": \"ПУБЛІЧНЕ АКЦІОНЕРНЕ ТОВАРИСТВО КОМЕРЦІЙНИЙ БАНК «ПРИВАТБАНК»\",\n"
                    + "        \"unit\": \"АЦСК\",\n"
                    + "        \"address\": {\n"
                    + "          \"type\": \"factual\",\n"
                    + "          \"state\": \"Дніпропетровська\",\n"
                    + "          \"city\": \"Дніпропетровськ\"\n"
                    + "        }\n"
                    + "      }\n"
                    + "    ]\n"
                    + "  }\n"
                    + "}\n"
                    + noteCODE
                    + "Ответ для несуществующего ключа (sID_File_Redis):\n"
                    + noteCODEJSON
                    + "{\"code\":\"SYSTEM_ERR\",\"message\":\"File with sID_File_Redis 'd2993755-70e5-409e-85e5-46ba8ce98e1e' not found.\"}\n\n"
                    + noteCODE
                    + "Ответ для файла который не имеет наложеной ЭЦП:\n\n"
                    + noteCODEJSON
                    + "{}\n"
                    + noteCODE;
    private static final String noteGetAttachmentFromDb =
            noteController + "Загрузки прикрепленного к заявке файла из постоянной базы #####\n\n"
                    + "HTTP Context: https://server:port/wf/service/rest/download_file_from_db?taskId=XXX&attachmentId=XXX&nFile=XXX\n\n"
                    + "- {taskId} - ид задачи\n"
                    + "- {attachmentID} - ID прикрепленного файла\n"
                    + "- {nFile} - порядковый номер прикрепленного файла\n"
                    + "- {nID_Subject} - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
                    + "Пример: https://test.igov.org.ua/wf/service/rest/file/download_file_from_db?taskId=82596&attachmentId=6726532&nFile=7\n";
    private static final String noteCheckAttachSign =
            noteController + "Проверка ЭЦП на атачменте(файл) таски Activiti #####\n\n"
                    + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/file/check_attachment_sign?nID_Task=[nID_Task]&nID_Attach=[nID_Attach] --"
                    + "возвращает json объект описывающий ЭЦП файла-аттачмента.\n\n"
                    + "- nID_Task - id таски Activiti BP\n"
                    + "- nID_Attach - id атачмента приложеного к таске\n"
                    + "Примеры:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/file/check_attachment_sign?nID_Task=7315073&nID_Attach=7315075\n"
                    + "Ответ:\n"
                    + noteCODEJSON
                    + "{\n"
                    + "  \"state\": \"ok\",\n"
                    + "  \"customer\": {\n"
                    + "    \"inn\": \"1436057000\",\n"
                    + "    \"fullName\": \"Сервіс зберігання сканкопій\",\n"
                    + "    \"signatureData\": {\n"
                    + "      \"name\": \"АЦСК ПАТ КБ «ПРИВАТБАНК»\",\n"
                    + "      \"serialNumber\": \"0D84EDA1BB9381E80400000079DD02004A710800\",\n"
                    + "      \"timestamp\": \"29.10.2015 13:45:33\",\n"
                    + "      \"code\": true,\n"
                    + "      \"desc\": \"ПІДПИС ВІРНИЙ\",\n"
                    + "      \"dateFrom\": \"13.08.2015 11:24:31\",\n"
                    + "      \"dateTo\": \"12.08.2016 23:59:59\",\n"
                    + "      \"sn\": \"UA-14360570-1\"\n"
                    + "    },\n"
                    + "    \"organizations\": [\n"
                    + "      {\n"
                    + "        \"type\": \"edsOwner\",\n"
                    + "        \"name\": \"ПАТ КБ «ПРИВАТБАНК»\",\n"
                    + "        \"mfo\": \"14360570\",\n"
                    + "        \"position\": \"Технологічний сертифікат\",\n"
                    + "        \"ownerDesc\": \"Співробітник банку\",\n"
                    + "        \"address\": {\n"
                    + "          \"type\": \"factual\",\n"
                    + "          \"state\": \"Дніпропетровська\",\n"
                    + "          \"city\": \"Дніпропетровськ\"\n"
                    + "        }\n"
                    + "      },\n"
                    + "      {\n"
                    + "        \"type\": \"edsIsuer\",\n"
                    + "        \"name\": \"ПУБЛІЧНЕ АКЦІОНЕРНЕ ТОВАРИСТВО КОМЕРЦІЙНИЙ БАНК «ПРИВАТБАНК»\",\n"
                    + "        \"unit\": \"АЦСК\",\n"
                    + "        \"address\": {\n"
                    + "          \"type\": \"factual\",\n"
                    + "          \"state\": \"Дніпропетровська\",\n"
                    + "          \"city\": \"Дніпропетровськ\"\n"
                    + "        }\n"
                    + "      }\n"
                    + "    ]\n"
                    + "  }\n"
                    + "}\n"
                    + noteCODE
                    + "\nОтвет для несуществующей таски (nID_Task):\n"
                    + noteCODEJSON
                    + "{\"code\":\"SYSTEM_ERR\",\"message\":\"ProcessInstanceId for taskId '7315070' not found.\"}\n"
                    + noteCODE
                    + "\nОтвет для несуществующего атачмента (nID_Attach):\n"
                    + noteCODEJSON
                    + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Attachment for taskId '7315073' not found.\"}\n"
                    + noteCODE
                    + "\nОтвет для атачмента который не имеет наложеной ЭЦП:\n"
                    + noteCODEJSON
                    + "{}\n"
                    + noteCODE;
    private static final String noteGetAttachmentFromDbExecution =
            noteController + "Сервис для получения Attachment из execution #####\n\n";
    private static final String notePutAttachmentsToExecution = noteController + "Activiti #####\n\n"
            + "HTTP Context: http://server:port/wf/service/rest/file/upload_file_as_attachment - Аплоад(upload) и прикрепление файла в виде атачмента к таске Activiti\n\n"
            + "- taskId - ИД-номер таски\n"
            + "- description - описание\n"
            + "- file - в html это имя элемента input типа file - . в HTTP заголовках - Content-Disposition: form-data; name=\"file\" ...\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
            + "Пример: http://test.igov.org.ua/wf/service/rest/file/upload_file_as_attachment?taskId=68&description=ololo\n\n"
            + noteCODEJSON
            + "Ответ без ошибок:\n"
            + "{\n"
            + "  \"taskId\": \"38\",\n"
            + "  \"processInstanceId\": null,\n"
            + "  \"userId\": \"kermit\",\n"
            + "  \"name\": \"jmt.png\",\n"
            + "  \"id\": \"45\",\n"
            + "  \"type\": \"image/png;png\",\n"
            + "  \"description\": \"SomeDocumentDescription\",\n"
            + "  \"time\": 1433539278957,\n"
            + "  \"url\": null\n"
            + "}\n"
            + "\nID созданного attachment - \"id\": \"45\"\n\n"
            + noteCODE
            + "Ответ с ошибкой:\n\n"
            + noteCODEJSON
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Cannot find task with id 384\"}\n"
            + noteCODE;
    private static final String notePutTextAttachmentsToExecution = noteController
            + "Аплоад(upload) и прикрепление текстового файла в виде атачмента к таске Activiti #####\n\n"
            + "HTTP Context: http://server:port/wf/service/rest/file/upload_content_as_attachment - Аплоад(upload) и прикрепление текстового файла в виде атачмента к таске Activiti\n\n"
            + "- nTaskId - ИД-номер таски\n"
            + "- sContentType - MIME тип отправляемого файла (опциоанльно) (значение по умолчанию = \"text/html\")\n"
            + "- sDescription - описание\n"
            + "- sFileName - имя отправляемого файла\n"
            + "Пример: http://localhost:8080/wf/service/rest/file/upload_content_as_attachment?nTaskId=24&sDescription=someText&sFileName=FlyWithMe.html\n"
            + noteCODEJSON
            + "Ответ без ошибок:\n"
            + "{\n"
            + "  \"taskId\": \"38\",\n"
            + "  \"processInstanceId\": null,\n"
            + "  \"userId\": \"kermit\",\n"
            + "  \"name\": \"FlyWithMe.html\",\n"
            + "  \"id\": \"25\",\n"
            + "  \"type\": \"text/html;html\",\n"
            + "  \"description\": \"someText\",\n"
            + "  \"time\": 1433539278957,\n"
            + "  \"url\": null\n"
            + "}\n\n"
            + "ID созданного attachment - \"id\": \"25\"\n"
            + noteCODE
            + "\nОтвет с ошибкой:\n"
            + noteCODEJSON
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Cannot find task with id 384\"}\n"
            + noteCODE;
    private static final String noteGetTimingForBusinessProcessNew =
            noteController + "Получение статистики по задачам в рамках бизнес процесса #####\n\n"
        	    + "HTTP Context: https://server:port/wf/service/rest/download_bp_timing?sID_BP_Name=XXX&sDateAt=XXX8&sDateTo=XXX\n\n"
                    + "- sID_BP_Name - ID бизнес процесса\n"
                    + "- sDateAt - Дата начала периода для выборки в формате yyyy-MM-dd\n"
                    + "- sDateTo - Дата окончания периода для выборки в формате yyyy-MM-dd\n"
                    + "- nRowsMax - необязательный параметр. Максимальное значение завершенных задач для возврата. По умолчанию 1000.\n"
                    + "- nRowStart - Необязательный параметр. Порядковый номер завершенной задачи в списке для возврата. По умолчанию 0.\n"
                    + "- bDetail - Необязательный параметр. Необходим ли расширенный вариант (с полями задач). По умолчанию true.\n"
                    + "- saFields - настраиваемые поля (название поля -- формула, issue 907)\n"
                    + "- saFieldSummary - сведение полей, которое производится над выборкой (issue 916)\n\n"
                    + "Метод возвращает .csv файл со информацией о завершенных задачах в указанном бизнес процессе за период. Если указан параметр saFieldSummary -- "
                    + "то также будет выполнено \"сведение\" полей (описано ниже). Если не указан, то формат выходного файла:\n\n"
                    + "- nID_Process - ид задачи\n"
                    + "- sLoginAssignee - кто выполнял задачу\n"
                    + "- sDateTimeStart - Дата и время начала\n"
                    + "- nDurationMS - Длительность выполнения задачи в миллисекундах\n"
                    + "- nDurationHour - Длительность выполнения задачи в часах\n"
                    + "- sName - Название задачи\n\n"
                    + "Поля из FormProperty (если bDetail=true)\n"
                    + "настраиваемые поля из saFields\n"
                    + "Пример: https://test.region.igov.org.ua/wf/service/rest/file/download_bp_timing?sID_BP_Name=lviv_mvk-1&sDateAt=2015-06-28&sDateTo=2015-07-01\n\n"
                    + "Пример выходного файла\n"
                    + noteCODE
                    + "\"Assignee\",\"Start Time\",\"Duration in millis\",\"Duration in hours\",\"Name of Task\"\n"
                    + "\"kermit\",\"2015-06-21:09-20-40\",\"711231882\",\"197\",\"Підготовка відповіді на запит: пошук документа\"\n"
                    + noteCODE
                    + "Сведение полей\n"
                    + "параметр saFieldSummary может содержать примерно такое значение: \"sRegion;nSum=sum(nMinutes);nVisites=count()\"\n"
                    + "тот элемент, который задан первым в параметре saFieldSummary - является \"ключевым полем\" "
                    + "следующие элементы состоят из названия для колонки, агрегирующей функции и названия агрегируемого поля. Например: \"nSum=sum(nMinutes)\"\n\n"
                    + "где:\n\n"
                    + "- nSum - название поля, куда будет попадать результат\n"
                    + "- sum - оператор сведения\n"
                    + "- nMinutes - расчетное поле переменная, которая хранит в себе значение уже существующего или посчитанного поля формируемой таблицы\n\n"
                    + "Перечень поддерживаемых \"операторов сведения\":\n\n"
                    + "- count() - число строк/элементов (не содержит аргументов)\n"
                    + "- sum(field) - сумма чисел (содержит аргумент - название обрабатываемого поля)\n"
                    + "- avg(field) - среднее число (содержит аргумент - название обрабатываемого поля)\n\n"
                    + "Операторы можно указывать в произвольном регистре, т.е. SUM, sum и SuM \"распознаются\" как оператор суммы sum. \n"
                    + "Для среднего числа также предусмотрено альтернативное название \"average\".\n"
                    + "Если в скобках не указано поле, то берется ключевое.\n\n"
                    + "Значение \"ключевого поля\" переносится в новую таблицу без изменений в виде единой строки,и все остальные сводные поля подсчитываются исключительно в контексте\n"
                    + "значения этого ключевого поля, и проставляютя соседними полями в рамках этой единой строки.\n\n"
                    + "Особенности подсчета:\n\n"
                    + "- если нету исходных данных или нету такого ключевого поля, то ничего не считается (в исходном файле просто будут заголовки)\n"
                    + "- если расчетного поля нету, то поле не считается (т.е. сумма и количество для ключевого не меняется)\n"
                    + "тип поля Сумма и Среднее -- дробное число, Количество -- целое. Исходя из этого при подсчете суммы значение конвертируется в число, если конвертация неудачна, то "
                    + "сумма не меняется. (т.е. если расчетное поле чисто текстовое, то сумма и среднее будет 0.0)\n\n"
                    + "Пример: https://test.region.igov.org.ua/wf/service/rest/file/download_bp_timing?sID_BP_Name=_test_queue_cancel&sDateAt=2015-04-01&sDateTo=2015-10-31&saFieldSummary=email;nSum=sum(nDurationHour);nVisites=count();nAvg=avg(nDurationHour)\n\n"
                    + "Ответ:\n"
                    + noteCODE
                    + "\"email\",\"nSum\",\"nVisites\",\"nAvg\"\n"
                    + "\"email1\",\"362.0\",\"5\",\"72.4\"\n"
                    + "\"email2\",\"0.0\",\"1\",\"0.0\"\n\n"
                    + noteCODE
                    + "Настраиваемые поля\n"
                    + "Параметр saFields может содержать набор полей с выражениями, разделенными символом ; \n"
                    + "Вычисленное выражение, расчитанное на основании значений текущей задачи, подставляется в выходной файл \n\n"
                    + "Пример выражения \n"
                    + "saFields=\"nCount=(sID_UserTask=='usertask1'?1:0);nTest=(sAssignedLogin=='kermit'?1:0)\" \n"
                    + "где:\n\n"
                    + "- nCount, nTest - названия колонок в выходном файле\n"
                    + "- sID_UserTask, sAssignedLogin - ID таски в бизнес процессе и пользователь, на которого заассайнена таска, соответственно\n\n"
                    + "Пример: https://test.region.igov.org.ua/wf/service/rest/file/download_bp_timing?sID_BP_Name=_test_queue_cancel&sDateAt=2015-04-01&sDateTo=2015-10-31&saFields=\"nCount=(sID_UserTask=='usertask1'?1:0);nTest=(sAssignedLogin=='kermit'?1:0)\"\n\n"
                    + "Результат:\n"
                    + noteCODE
                    + "\"nID_Process\",\"sLoginAssignee\",\"sDateTimeStart\",\"nDurationMS\",\"nDurationHour\",\"sName\",\"bankIdPassport\",\"bankIdfirstName\",\"bankIdlastName\",\"bankIdmiddleName\",\"biometrical\",\"date_of_visit\",\"date_of_visit1\",\"email\",\"finish\",\"have_passport\",\"initiator\",\"phone\",\"urgent\",\"visitDate\",\"nCount\",\"nTest\"\n"
                    + "\"5207501\",\"kermit\",\"2015-09-25:12-18-28\",\"1433990\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-10-09 09:00:00.00\",\"dd.MM.yyyy HH:MI\",\"nazarenkod1990@gmail.com\",\"attr1_ok\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27764,\"\"sDate\"\":\"\"2015-10-09 09:00:00.00\"\"}\",\"0.0\",\"1.0\"\n"
                    + "\"5215001\",\"kermit\",\"2015-09-25:13-03-29\",\"75259\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-10-14 11:15:00.00\",\"dd.MM.yyyy HH:MI\",\"nazarenkod1990@gmail.com\",\"attr1_ok\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27767,\"\"sDate\"\":\"\"2015-10-14 11:15:00.00\"\"}\",\"0.0\",\"1.0\"\n"
                    + "\"5215055\",\"dn200986zda\",\"2015-09-25:13-05-22\",\"1565056\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-09-28 08:15:00.00\",\"dd.MM.yyyy HH:MI\",\"dmitrij.zabrudskij@privatbank.ua\",\"attr2_missed\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27768,\"\"sDate\"\":\"\"2015-09-28 08:15:00.00\"\"}\",\"0.0\",\"0.0\"\n"
                    + noteCODE;
    private static final String noteDownloadTasksData = noteController + "Загрузка данных по задачам #####\n\n"
            + "HTTP Context: https://server:port/wf/service/rest/file/downloadTasksData\n\n"
            + "Загрузка полей по задачам в виде файла.\n\n"
            + "Параметры:\n\n"
            + "- sID_BP - название бизнесс процесса\n"
            + "- sID_State_BP - состояние задачи, по умолчанию исключается из фильтра Берется из поля taskDefinitionKey задачи\n"
            + "- saFields - имена полей для выборкы разделенных через ';', чтобы добавить все поля можно использовать - '*' или не передевать параметр в запросе. Поле также может содержать названия колонок. Например, saFields=Passport\\=${passport};{email}\n"
            + "- nASCI_Spliter - ASCII код для разделителя\n"
            + "- sFileName - имя исходящего файла, по умолчанию - data_BP-bpName_.txt\"\n"
            + "- sID_Codepage - кодировка исходящего файла, по умолчанию - win1251\n"
            + "- sDateCreateFormat - форматирование даты создания таски, по умолчанию - yyyy-MM-dd HH:mm:ss\n"
            + "- sDateAt - начальная дата создания таски, по умолчанию - вчера\n"
            + "- sDateTo - конечная дата создания таски, по умолчанию - сегодня\n"
            + "- nRowStart - начало выборки для пейджирования, по умолчанию - 0\n"
            + "- nRowsMax - размер выборки для пейджирования, по умолчанию - 1000\n"
            + "- bIncludeHistory - включить информацию по хисторик задачам, по умолчанию - true\n"
            + "- bHeader - добавить заголовок с названиями полей в выходной файл, по умолчанию - false\n"
            + "- saFieldsCalc - настраиваемые поля (название поля -- формула, issue 907)\n"
            + "- saFieldSummary - сведение полей, которое производится над выборкой (issue 916)\n\n"
            + "Поля по умолчанию, которые всегда включены в выборку:\n"
            + "- nID_Task - \"id таски\"\n"
            + "- sDateCreate - \"дата создания таски\" (в формате sDateCreateFormat)\n\n"
            + "Особенности обработки полей:\n"
            + "- Если тип поля enum, то брать не его ИД пункта в энуме а именно значение Если тип поля enum, и в значении присутствует знак \";\", то брать только то ту часть текста, которая находится справа от этого знака\n\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/rest/file/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-08-01&saFields=${nID_Task};${sDateCreate};${area};;;0;${bankIdlastName} ${bankIdfirstName} ${bankIdmiddleName};4;${aim};${date_start};${date_stop};${place_living};${bankIdPassport};1;${phone};${email}&sID_Codepage=win1251&nASCI_Spliter=18&sDateCreateFormat=dd.mm.yyyy hh:MM:ss&sFileName=dohody.dat\n\n"
            + "Пример ответа:\n"
            + noteCODE
            + "1410042;16.32.2015 10:07:17;АНД (пров. Універсальний, 12);;;0;БІЛЯВЦЕВ ВОЛОДИМИР ВОЛОДИМИРОВИЧ;4;мета;16/07/2015;17/07/2015;мокешрмшгкеу;АЕ432204 БАБУШКИНСКИМ РО ДГУ УМВД 26.09.1996;1;380102030405;mendeleev.ua@gmail.com\n"
            + "995161;07.07.2015 05:07:27;;;;0;ДУБІЛЕТ ДМИТРО ОЛЕКСАНДРОВИЧ;4;для роботи;01/07/2015;07/07/2015;Дніпропетровська, Дніпропетровськ, вул. Донецьке шосе, 15/110;АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЙНИ В ДНИПРОПЕТРОВСЬКИЙ ОБЛАСТИ 18.03.2002;1;;ukr_rybak@rambler.ru\n"
            + noteCODE
            + "Формат поля saFieldsCalc - смотри сервис https://github.com/e-government-ua/i/blob/test/docs/specification.md#16-Получение-статистики-по-задачам-в-рамках-бизнес-процесса и параметр saFields\n"
            + "Пример запроса: https://test.region.igov.org.ua/wf/service/rest/file/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&bHeader=true&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-10-01&saFieldsCalc=%22nCount=(sID_UserTask==%27usertask1%27?1:0);nTest=(sAssignedLogin==%27kermit%27?1:0)%22\n\n"
            + "Пример ответа (фрагмент):\n"
            + noteCODE
            + ";380970044803;ДМИТРО;;ОЛЕКСАНДРОВИЧ;;dd.MM.yyyy;Днепропетровск;;;3119325858;АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002;0463;dd.MM.yyyy;;тест;;ДУБІЛЕТ;vidokgulich@gmail.com;1.0;1.0\n"
            + noteCODE
            + "Формат поля saFieldSummary - смотри сервис https://github.com/e-government-ua/i/blob/test/docs/specification.md#16-Получение-статистики-по-задачам-в-рамках-бизнес-процесса и параметр saFieldSummary\n"
            + "Пример запроса: https://test.region.igov.org.ua/wf/service/rest/file/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&bHeader=true&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-10-01&saFieldSummary=email;nVisites=count()\n\n"
            + "Пример ответа:\n"
            + noteCODE
            + "vidokgulich@gmail.com;2\n"
            + "kermit;1\n"
            + "rostislav.siryk@gmail.com;4\n"
            + "rostislav.siryk+igov.org.ua@gmail.com;3\n"
            + noteCODE;
    private static final String noteGetBusinessProcessesForUser =
            noteController + "Получение списка бизнес процессов к которым у пользователя есть доступ #####\n\n"
                    + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/getLoginBPs?sLogin=userId\n\n"
                    + "- sLogin - ID пользователя\n"
                    + "Метод возвращает json со списком бизнес процессов, к которым у пользователя есть доступ, в формате:\n"
                    + noteCODEJSON
                    + "[\n"
                    + "  {\n"
                    + "    \"sID\": \"[process definition key]\"\"sName\": \"[process definition name]\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"[process definition key]\"\"sName\": \"[process definition name]\"\n"
                    + "  }\n"
                    + "]\n"
                    + noteCODE
                    + "Принадлежность пользователя к процессу проверяется по вхождению в группы, которые могут запускать usertask-и внутри процесса, или по вхождению в группу, которая может стартовать процесс\n\n"
                    + "Пример:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/getLoginBPs?sLogin=kermit\n"
                    + "Пример результата\n"
                    + noteCODEJSON
                    + "[\n"
                    + "{\n"
                    + "    \"sID\": \"dnepr_spravka_o_doxodax\",\n"
                    + "    \"sName\": \"Дніпропетровськ - Отримання довідки про доходи фіз. осіб\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"dnepr_subsidies2\",\n"
                    + "    \"sName\": \"Отримання субсидії на оплату житлово-комунальних послуг2\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"khmelnitskij_mvk_2\",\n"
                    + "    \"sName\": \"Хмельницький - Надання інформації, що підтверджує відсутність (наявність) земельної ділянки\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"khmelnitskij_zemlya\",\n"
                    + "    \"sName\": \"Заява про наявність земельної ділянки\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"kiev_spravka_o_doxodax\",\n"
                    + "    \"sName\": \"Київ - Отримання довідки про доходи фіз. осіб\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"kuznetsovsk_mvk_5\",\n"
                    + "    \"sName\": \"Кузнецовськ МВК - Узгодження графіка роботи підприємства торгівлі\\/обслуговування\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"sID\": \"post_spravka_o_doxodax_pens\",\n"
                    + "    \"sName\": \"Отримання довідки про доходи (пенсійний фонд)\"\n"
                    + "  }\n"
                    + "]\n"
                    + noteCODE;
    private static final String noteSendAttachmentsByMail = noteController + "описания нет #####\n\n";
    private static final String noteGetPatternFile = noteController + "Работа с файлами-шаблонами #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/getPatternFile?sPathFile=[full-path-file]&sContentType=[content-type] --возвращает содержимое указанного файла с указанным типом контента (если он задан).\n\n"
            + "- sPathFile - полный путь к файлу, например: folder/file.html.\n\n"
            + "- sContentType - тип контента (опционально, по умолчанию обычный текст: text/plain)\n\n"
            + "Если указанный путь неверен и файл не найден -- вернется соответствующая ошибка.\n\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/rest/getPatternFile?sPathFile=print//subsidy_zayava.html\n\n"
            + "ответ: вернется текст исходного кода файла-шаблона\n\n"
            + "https://test.region.igov.org.ua/wf/service/rest/getPatternFile?sPathFile=print//subsidy_zayava.html&sContentType=text/html\n\n"
            + "ответ: файл-шаблон будет отображаться в виде html-страницы";
    private static final String noteSetTaskQuestions = noteController + "Вызов сервиса уточнения полей формы #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/setTaskQuestions?nID_Protected=[nID_Protected]&saField=[saField]&sMail=[sMail] сервис запроса полей, требующих уточнения у гражданина, с отсылкой уведомления параметры:\n\n"
            + "- nID_Protected - номер-ИД заявки (защищенный, опционально, если есть sID_Order или nID_Process)\n"
            + "- sID_Order - строка-ид заявки (опционально, подробнее тут )\n"
            + "- nID_Process - ид заявки (опционально)\n"
            + "- nID_Server - ид сервера, где расположена заявка\n"
            + "- saField - строка-массива полей (пример: \"[{'id':'sFamily','type':'string','value':'Иванов'},{'id':'nAge','type':'long'}]\")\n"
            + "- sMail - строка электронного адреса гражданина\n"
            + "- sHead - строка заголовка письма (опциональный, если не задан, то \"Необхідно уточнити дані\")\n"
            + "- sBody - строка тела письма (опциональный, добавляется перед таблицей, сли не задан, то пустота)\n\n"
            + "при вызове сервиса:\n\n"
            + "- обновляется запись HistoryEvent_Service полем значениями из soData (из saField), sToken (сгенерированый случайно 20-ти символьный код), sHead, sBody (т.е. на этоп этапе могут быть ошибки, связанные с нахождением и апдейтом обьекта события по услуге)\n"
            + "- отсылается письмо гражданину на указанный емейл (sMail):\n"
            + "  с заголовком sHead,\n"
            + "  телом sBody\n"
            + "  перечисление полей из saField в формате таблицы: Поле / Тип / Текущее значение\n"
            + "  гиперссылкой в конце типа: https://[hostCentral]/order?nID_Protected=[nID_Protected]&sToken=[sToken]\n"
            + "- находитcя на региональном портале таска, которой устанавливается в глобальную переменные sQuestion содержимое sBody и saFieldQuestion - содержимое saField\n"
            + "- сохраняется информация о действии в Моем Журнале в виде\n"
            + "  По заявці №____ задане прохання уточнення: [sBody]\n"
            + "  плюс перечисление полей из saField в формате таблицы Поле / Тип / Текущее значение\n"
            + "- Пример: https://test.region.igov.org.ua/wf/service/rest/setTaskQuestions?nID_Protected=52302969&saField=[{'id':'bankIdfirstName','type':'string','value':'3119325858'}]&sMail=test@email\n\n"
            + "Ответы: Пустой ответ в случае успешного обновления (и приход на указанный емейл письма описанного выше формата)\n\n"
            + "Возможные ошибки:\n\n"
            + "- не найдена заявка (Record not found) или ид заявки неверное (CRC Error)\n"
            + "- связанные с отсылкой письма, например, невалидный емейл (Error happened when sending email)\n"
            + "- из-за некорректных входящих данных, например неверный формат saField (пример ошибки: Expected a ',' or ']' at 72 [character 73 line 1])";
    private static final String noteSetTaskAnswer_Region =
            noteController + "Вызов сервиса ответа по полям требующим уточнения #####\n\n"
                    + "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/setTaskAnswer?nID_Protected=nID_Protected&saField=saField&sToken=sToken&sBody=sBody\n\n"
                    + "-- обновляет поля формы указанного процесса значениями, переданными в параметре saField Важно:позволяет обновлять только те поля, для которых в форме бизнес процесса не стоит атрибут writable=\"false\"\n\n"
                    + "- nID_Protected - номер-ИД заявки (защищенный, опционально, если есть sID_Order или nID_Process)\n"
                    + "- sID_Order - строка-ид заявки (опционально, подробнее тут )\n"
                    + "- nID_Process - ид заявки (опционально)\n"
                    + "- nID_Server - ид сервера, где расположена заявка\n"
                    + "- saField - строка-массива полей (например: \"[{'id':'sFamily','type':'string','value':'Белявцев'},{'id':'nAge','type':'long','value':35}]\")\n"
                    + "- sToken - строка-токена. Данный параметр формируется и сохраняется в запись HistoryEvent_Service во время вызова метода setTaskQuestions\n\n"
                    + "- sBody - строка тела сообщения (опциональный параметр)\n"
                    + "Во время выполнения метод выполняет такие действия:\n\n"
                    + "- Находит в сущности HistoryEvent_Service нужную запись (по nID_Protected) и сверяет токен. Eсли токен в сущности указан но не совпадает с переданным, возвращается ошибка \"Token wrong\". Если он в сущности не указан (null) - возвращается ошибка \"Token absent\".\n"
                    + "- Находит на региональном портале таску и устанавливает в глобальную переменную sAnswer найденной таски содержимое sBody.\n"
                    + "- Устанавливает в каждое из полей из saField новые значения\n"
                    + "- Обновляет в сущности HistoryEvent_Service поле soData значением из saField и поле sToken значением null.\n"
                    + "- Сохраняет информацию о действии в Мой Журнал (Текст: На заявку №____ дан ответ гражданином: [sBody])\n\n"
                    + "Примеры:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/setTaskAnswer?nID_Protected=54352839&saField=[{%27id%27:%27bankIdinn%27,%27type%27:%27string%27,%27value%27:%271234567890%27}]&sToken=93ODp4uPBb5To4Nn3kY1\n\n"
                    + "Ответы: Пустой ответ в случае успешного обновления\n\n"
                    + "Токен отсутствует\n\n"
                    + noteCODEJSON
                    + "{\"code\":\"BUSINESS_ERR\",\"message\":\"Token is absent\"}\n\n"
                    + noteCODE
                    + "Токен не совпадает со значением в HistoryEvent_Service\n"
                    + noteCODEJSON
                    + "{\"code\":\"BUSINESS_ERR\",\"message\":\"Token is absent\"}\n\n"
                    + noteCODE
                    + "Попытка обновить поле с атрибутом writable=\"false\"\n"
                    + noteCODEJSON
                    + "{\"code\":\"BUSINESS_ERR\",\"message\":\"form property 'bankIdinn' is not writable\"}\n"
                    + noteCODE;
    private static final String noteSendProccessToGRES = noteController + "описания нет #####\n\n";
    private static final String noteGetTaskFormData = noteController + "описания нет #####\n\n";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteVerifyContactEmail =
            noteController + "Сервис верификации контакта - электронного адреса #####\n\n"
                    + "HTTP Context: https://server:port/wf/service/rest/verifyContactEmail?sQuestion=[sQuestion]&sAnswer=[sAnswer]\n\n\n"
                    + "Параметры:\n"
                    + "- sQuestion - строка-запроса (сам электронный адрес)\n"
                    + "- sAnswer - строка-ответа (тот код, что пришел на электронку) //опциональный\n\n"
                    + "Принцип работы:\n"
                    + "1) если sAnswer не задан, то отсылать на адрес, указанный в sQuestion письмо(класс Mail) с:\n"
                    + "темой: Верификация адреса\n"
                    + "телом: Код подтверждения: ________\n"
                    + "2) код подтверждения (для п.1) генерировать из больших и маленьких латинских символов и цифр, длиной 15 символов\n"
                    + "3) также сохоанять этот-же код в Редис-хранилище с ключем, в виде присланного электронного адреса \n"
                    + "4) также проверять по маске сам формат электронного адреса при запросе, и если он не валидный, то возвращать в ответе bVerified: false\n"
                    + "5) если sAnswer задан, то сверять его с сохраненным ранее в хранилище Редис (п.4.3) и при его совпадении выводить в ответе bVerified: true иначе bVerified: false\n"
                    + "Примеры:\n\n"
                    + "https://test.region.igov.org.ua/wf/service/rest/verifyContactEmail?sQuestion=test@igov.org.ua\n\n"
                    + "Response\n"
                    + noteCODEJSON
                    + "{\n"
                    + "    \"bVerified\":true,\n"
                    + "}\n"
                    + noteCODE;
//    private static final Logger LOG = LoggerFactory.getLogger(ActivitiCustomController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
//    private static final String noteCODE = "\n```\n";
//    private static final String noteCODEJSON = "\n```json\n";
//    private static final String noteController = "#####  Электронная эскалация. ";
    private static final String noteGetTasksByAssignee = noteController + "Загрузка задач из Activiti #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/rest/tasks/{assignee}\n\n"
		+ "- assignee - Владелец\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Request:\n"
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/kermit\n\n"
		+ "Response:\n"
		+ noteCODEJSON
		+ "    [\n"
		+ "      {\n"
		+ "            \"delegationState\": \"RESOLVED\",\n"
		+ "            \"id\": \"38\",\n"
		+ "            \"name\": \"Первый процесс пользователя kermit\",\n"
		+ "            \"description\": \"Описание процесса\",\n"
		+ "            \"priority\": 51,\n"
		+ "            \"owner\": \"kermit-owner\",\n"
		+ "            \"assignee\": \"kermit-assignee\",\n"
		+ "            \"processInstanceId\": \"12\",\n"
		+ "            \"executionId\": \"1\",\n"
		+ "            \"createTime\": \"2015-04-13 00:51:34.527\",\n"
		+ "            \"taskDefinitionKey\": \"task-definition\",\n"
		+ "            \"dueDate\": \"2015-04-13 00:51:36.527\",\n"
		+ "            \"category\": \"my-category\",\n"
		+ "            \"parentTaskId\": \"2\",\n"
		+ "            \"tenantId\": \"diver\",\n"
		+ "            \"formKey\": \"form-key-12\",\n"
		+ "            \"suspended\": true,\n"
		+ "            \"processDefinitionId\": \"21\"\n"
		+ "      }\n"
		+ "    ]\n"
		+ noteCODE;

    private static final String noteGetTasksByOrder = noteController + "Получение списка ID пользовательских тасок по номеру заявки #####\n\n"
		+ "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=nID_Protected\n\n"
		+ " -- возвращает спискок ID пользовательских тасок по номеру заявки\n\n"
		+ "- nID_Protected - Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.\n\n"
		+ "Примеры:\n"
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=123452\n\n"
		+ "Responce status 403.\n\n"
		+ noteCODEJSON
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"CRC Error\"}\n\n"
		+ noteCODE
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=123451\n\n"
		+ "1) Если процесса с ID 12345 и тасками нет в базе то:\n\n"
		+ "Responce status 403.\n\n"
		+ noteCODEJSON
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"Record not found\"}\n\n"
		+ noteCODE
		+ "2) Если процесс с ID 12345 есть в базе с таской ID которой 555, то:\n\n"
		+ "Responce status 200.\n"
		+ noteCODEJSON
		+ "[ 555 ]\n"
		+ noteCODE;

    private static final String noteGetTasksByText = noteController + "Поиск заявок по тексту (в значениях полей без учета регистра) #####\n\n"
        	+ "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=sFind&sLogin=sLogin&bAssigned=true\n\n"
        	+ " -- возвращает список ID тасок у которых в полях встречается указанный текст\n\n"
        	+ "- sFind - текст для поиска в полях заявки.\n"
        	+ "- sLogin - необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin\n"
        	+ "- bAssigned - необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin\n\n"
        	+ "Примеры:\n"
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк\n"
        	+ noteCODEJSON
        	+ "[\"4637994\",\"4715238\",\"4585497\",\"4585243\",\"4730773\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit\n"
        	+ noteCODEJSON
        	+ "[\"4637994\",\"4715238\",\"4585243\",\"4730773\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=false\n"
        	+ noteCODEJSON
        	+ "[\"4637994\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=true\n"
        	+ noteCODEJSON
        	+ "[\"4715238\",\"4585243\",\"4730773\"]\n"
        	+ noteCODE;

    private static final String noteCancelTask = noteController + "нет описания #####\n\n";

    private static final String noteGetStartFormData = noteController + "Получение полей стартовой формы по ID таски #####\n\n"
    		+ "HTTP Context: http://test.region.igov.org.ua/wf/service/rest/tasks/getStartFormData?nID_Task=[nID_Task] -- возвращает JSON содержащий поля стартовой формы процесса.\n\n"
    		+ "- nID_Task - номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.\n\n"
    		+ "Примеры:\n"
    		+ "http://test.region.igov.org.ua/wf/service/rest/tasks/getStartFormData?nID_Task=5170256\n"
    		+ "Ответ, если запись существует (HTTP status Code: 200 OK):\n\n"
    		+ noteCODEJSON
    		+ "{\n"
    		+ "  waterback=\"--------------------\",\n"
    		+ "  phone=\"380979362996\",\n"
    		+ "  date_from=\"01/01/2014\",\n"
    		+ "  bankIdbirthDay=\"27.05.1985\",\n"
    		+ "  notice2=\"Я та особи, які зареєстровані (фактично проживають) у житловому приміщенні/будинку, даємо згоду на обробку персональних даних про сім’ю, доходи, майно, що необхідні для призначення житлової субсидії, та оприлюднення відомостей щодо її призначення.\",\n"
      		+ "house=\"--------------------\",\n"
    		+ "  garbage=\"--------------------\",\n"
    		+ "  waterback_notice=\"\",\n"
    		+ "  garbage_number=\"\",\n"
    		+ "  floors=\"10\",\n"
    		+ "  name_services=\"--------------------\",\n"
    		+ "  date_to=\"30/12/2014\",\n"
    		+ "  date3=\"\",\n"
    		+ "  date2=\"\",\n"
    		+ "  electricity=\"--------------------\",\n"
    		+ "  garbage_name=\"\",\n"
    		+ "  date1=\"\",\n"
    		+ "  place_type=\"2\",\n"
    		+ "  bankIdfirstName=\"ДМИТРО\",\n"
    		+ "  declaration=\"--------------------\",\n"
    		+ "  waterback_name=\"\",\n"
    		+ "  electricity_notice=\"\",\n"
    		+ "  bankIdinn=\"3119325858\",\n"
    		+ "  house_name=\"\",\n"
    		+ "  gas=\"--------------------\",\n"
    		+ "  house_number=\"\",\n"
    		+ "  subsidy=\"1\",\n"
    		+ "  email=\"dmitrij.zabrudskij@privatbank.ua\",\n"
    		+ "  warming=\"--------------------\",\n"
    		+ "  hotwater_notice=\"\",\n"
    		+ "  org0=\"Назва організації\",\n"
    		+ "  org1=\"\",\n"
    		+ "  electricity_number=\"123456\",\n"
    		+ "  org2=\"\",\n"
    		+ "  org3=\"\",\n"
    		+ "  warming_name=\"\",\n"
    		+ "  place_of_living=\"Дніпропетровська, Дніпропетровськ, пр. Героїв, 17, кв 120\",\n"
    		+ "  fio2=\"\",\n"
    		+ "  fio3=\"\",\n"
    		+ "  total_place=\"68\",\n"
    		+ "  garbage_notice=\"\",\n"
    		+ "  fio1=\"\",\n"
    		+ "  chapter1=\"--------------------\",\n"
    		+ "  bankIdmiddleName=\"ОЛЕКСАНДРОВИЧ\",\n"
    		+ "  gas_name=\"\",\n"
    		+ "  bankIdPassport=\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\n"
    		+ "  warming_place=\"45\",\n"
    		+ "  passport3=\"\",\n"
    		+ "  gas_number=\"\",\n"
    		+ "  passport2=\"\",\n"
    		+ "  electricity_name=\"коммуна\",\n"
    		+ "  area=\"samar\",\n"
    		+ "  house_notice=\"\",\n"
    		+ "  bankIdlastName=\"ДУБІЛЕТ\",\n"
    		+ "  card1=\"\",\n"
    		+ "  card3=\"\",\n"
    		+ "  coolwater_number=\"\",\n"
    		+ "  card2=\"\",\n"
    		+ "  warming_notice=\"\",\n"
    		+ "  hotwater_name=\"\",\n"
    		+ "  income0=\"attr9\",\n"
    		+ "  coolwater=\"--------------------\",\n"
    		+ "  gas_notice=\"\",\n"
    		+ "  overload=\"hxhxfhfxhfghg\",\n"
    		+ "  warming_number=\"\",\n"
    		+ "  income3=\"attr0\",\n"
    		+ "  income1=\"attr0\",\n"
    		+ "  income2=\"attr0\",\n"
    		+ "  passport1=\"\",\n"
    		+ "  coolwater_notice=\"\",\n"
    		+ "  sBody_1=\"null\",\n"
    		+ "  hotwater=\"--------------------\",\n"
    		+ "  coolwater_name=\"\",\n"
    		+ "  waterback_number=\"\",\n"
    		+ "  man1=\"\",\n"
    		+ "  hotwater_number=\"\",\n"
    		+ "  sBody_2=\"null\",\n"
    		+ "  comment=\"null\",\n"
    		+ "  decision=\"null\",\n"
    		+ "  selection=\"attr1\"\n"
    		+ "}\n"
    		+ noteCODE
    		+ "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n\n"
    		+ noteCODEJSON
    		+ "{\n"
    		+ "  \"code\": \"BUSINESS_ERR\",\n"
    		+ "  \"message\": \"Record not found\"\n"
    		+ "}\n"
    		+ noteCODE;

    private static final String noteResetUserTaskAssign = noteController
            + "Удаление назначенного пользователя с задачи по ИД. #####\n\n"
            + "HTTP Context: https://server:port/wf/service/rest/tasks/resetUserTaskAssign?nID_UserTask=[nID_UserTask]\n\n"
            + "- nID_UserTask - ID таски для удаления пользователя с нее.\n\n"
            + "Request:\n"
            + "https://test.region.igov.org.ua/wf/service/rest/tasks/resetUserTaskAssign\n\n"
            + "- nID_UserTask=24\n"
            + "Responce if task assigned: HTTP STATUS 200\n\n"
            + noteCODEJSON
            + "{}\n"
            + noteCODE
            + "Response if task is not assigned: HTTP STATUS 200\n\n"
            + noteCODEJSON
            + "{\"Not assigned UserTask\"}\n\n"
            + noteCODE
            + "Response if task not found: HTTP STATUS 403 Forbidden\n\n"
            + noteCODEJSON
            + "{\n"
            + "\"code\": \"BUSINESS_ERR\"\n"
            + "\"message\": \"Record not found\"\n"
            + "}"
            + noteCODE;

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private TaskService taskService;
    //@Autowired
    //private ActivitiExceptionController exceptionController;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;
    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;


    
    
    //@Autowired
    //private RuntimeService runtimeService;
    //@Autowired
    //private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    //@Autowired
    //private HistoryService historyService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private IdentityService identityService;
    //@Autowired
    //private FormService formService;
    @Autowired
    private Mail oMail;
    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private BankIDConfig bankIDConfig;
    //@Autowired
    //private ActivitiExceptionController exceptionController;
    
    /*@ExceptionHandler({CRCInvalidException.class, EntityNotFoundException.class, RecordNotFoundException.class, TaskAlreadyUnboundException.class})
    @ResponseBody
    public ResponseEntity<String> handleAccessException(Exception e) throws ActivitiRestException {
        return exceptionController.catchActivitiRestException(new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                e.getMessage(), e,
                HttpStatus.FORBIDDEN));
    }*/

        public static String parseEnumProperty(FormProperty property) {
                Object oValues = property.getType().getInformation("values");
                if (oValues instanceof Map) {
                        Map<String, String> mValue = (Map) oValues;
                        LOG.info("m=" + mValue);
                        String sName = property.getValue();
                        LOG.info("sName=" + sName);
                        String sValue = mValue.get(sName);
                        LOG.info("sValue=" + sValue);
                        return parseEnumValue(sValue);
                } else {
                        LOG.error("Cannot parse values for property - {}", property);
                        return "";
                }
        }

        public static String parseEnumProperty(FormProperty property, String sName) {
                Object oValues = property.getType().getInformation("values");
                if (oValues instanceof Map) {
                        Map<String, String> mValue = (Map) oValues;
                        LOG.info("m=" + mValue);
                        LOG.info("sName=" + sName);
                        String sValue = mValue.get(sName);
                        LOG.info("sValue=" + sValue);
                        return parseEnumValue(sValue);
                } else {
                        LOG.error("Cannot parse values for property - {}", property);
                        return "";
                }
        }

        public static String parseEnumValue(String sEnumName) {
                LOG.info("sEnumName=" + sEnumName);

                String res = StringUtils.defaultString(sEnumName);
                LOG.info("sEnumName(2)=" + sEnumName);
                if (res.contains("|")) {
                        String[] as = sEnumName.split("\\|");
                        LOG.info("as.length - 1=" + (as.length - 1));
                        LOG.info("as=" + as);
                        res = as[as.length - 1];
                }

                return res;
        }

    /**
     * Загрузка задач из Activiti:
     * @param assignee Владелец
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Загрузка задач из Activiti", notes =  noteGetTasksByAssignee )
    @RequestMapping(value = "/tasks/{assignee}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TaskAssigneeI> getTasksByAssignee( @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true)  @PathVariable("assignee") String assignee) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        List<TaskAssigneeI> facadeTasks = new ArrayList<>();
        TaskAssigneeAdapter adapter = new TaskAssigneeAdapter();
        for (Task task : tasks) {
            facadeTasks.add(adapter.apply(task));
        }
        return facadeTasks;
    }

    /**
     * @param nID_Protected Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.
     */
    @ApiOperation(value = "Получение списка ID пользовательских тасок по номеру заявки", notes =  noteGetTasksByOrder )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "CRC Error или Record not found") })
    @RequestMapping(value = "/tasks/getTasksByOrder", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getTasksByOrder( @ApiParam(value = " Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.", required = true)  @RequestParam(value = "nID_Protected") Long nID_Protected)
            throws ActivitiRestException, CRCInvalidException, RecordNotFoundException {

        String processInstanceID = getOriginalProcessInstanceId(nID_Protected);
        return getTaskIdsByProcessInstanceId(processInstanceID);

    }

    /**
     * @param sFind текст для поиска в полях заявки.
     * @param sLogin необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin
     * @param bAssigned необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin
     */
    @ApiOperation(value = "Поиск заявок по тексту (в значениях полей без учета регистра)", notes =  noteGetTasksByText )
    @RequestMapping(value = "/tasks/getTasksByText", method = RequestMethod.GET)
    public
    @ResponseBody
    Set<String> getTasksByText( @ApiParam(value = "текст для поиска в полях заявки", required = true)  @RequestParam(value = "sFind") String sFind,
	    @ApiParam(value = "необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin", required = false )  @RequestParam(value = "sLogin", required = false) String sLogin,
	    @ApiParam(value = "необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin", required = false )  @RequestParam(value = "bAssigned", required = false) String bAssigned) throws ActivitiRestException {
        Set<String> res = new HashSet<String>();

        String searchTeam = sFind.toLowerCase();
        TaskQuery taskQuery = buildTaskQuery(sLogin, bAssigned);
        List<Task> activeTasks = taskQuery.active().list();
        for (Task currTask : activeTasks) {
            TaskFormData data = formService.getTaskFormData(currTask.getId());
            if (data != null) {
                for (FormProperty property : data.getFormProperties()) {

                    String sValue = "";
                    String sType = property.getType().getName();
                    if ("enum".equalsIgnoreCase(sType)) {
                        sValue = parseEnumProperty(property);
                    } else {
                        sValue = property.getValue();
                    }
                    LOG.info("taskId=" + currTask.getId() + "propertyName=" + property.getName() + "sValue=" + sValue);
                    if (sValue != null) {
                        if (sValue.toLowerCase().contains(searchTeam)) {
                            res.add(currTask.getId());
                        }
                    }
                }
            } else {
                LOG.info("TaskFormData for task " + currTask.getId() + "is null. Skipping from processing.");
            }
        }

        return res;
    }

    @ApiOperation(value = "cancelTask", notes =  noteCancelTask )
    @RequestMapping(value = "/tasks/cancelTask", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public
    @ResponseBody
        //void cancelTask(@RequestParam(value = "nID_Protected") Long nID_Protected,
    ResponseEntity<String> cancelTask( @ApiParam(value = "нет описания", required = true )  @RequestParam(value = "nID_Protected") Long nID_Protected,
	    @ApiParam(value = "нет описания", required = false )  @RequestParam(value = "sInfo", required = false) String sInfo)
            throws ActivitiRestException, TaskAlreadyUnboundException {

        String sMessage = "Ваша заявка відмінена. Ви можете подати нову на Порталі державних послуг iGov.org.ua.<\n<br>"
                + "З повагою, команда порталу  iGov.org.ua";

        try {
            cancelTasksInternal(nID_Protected, sInfo);
            return new ResponseEntity<String>(sMessage, HttpStatus.OK);
        } catch (CRCInvalidException | RecordNotFoundException e) {
            ActivitiRestException newErr = new ActivitiRestException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn(e.getMessage(), e);
            sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";

            return new ResponseEntity<String>(sMessage, HttpStatus.FORBIDDEN);
        }

    }

    /**
     * @param nID_Task номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.
     */
    @ApiOperation(value = "Получение полей стартовой формы по ID таски", notes =  noteGetStartFormData )
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/tasks/getStartFormData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getFormDat( @ApiParam(value = " номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.", required = true )  @RequestParam(value = "nID_Task") String nID_Task)
            throws ActivitiRestException, JsonProcessingException, RecordNotFoundException {
        StringBuilder sb;

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(nID_Task).singleResult();
        LOG.info("historicTaskInstance {} ", historicTaskInstance);

        List<HistoricDetail> details = null;
        String processInstanceId;
        if (historicTaskInstance == null) {
            throw new RecordNotFoundException();
        }
        processInstanceId = historicTaskInstance.getProcessInstanceId();
        LOG.info("processInstanceId {} ", processInstanceId);

        if(processInstanceId != null){
            details = historyService.createHistoricDetailQuery().formProperties()
                    .executionId(processInstanceId).list();
        }

        LOG.info("details {} ", details);
        if(details == null){
            throw new RecordNotFoundException();
        }

        sb = new StringBuilder("{");
        for (Iterator<HistoricDetail> iterator = details.iterator(); iterator.hasNext(); ) {
            HistoricDetail detail = iterator.next();
            HistoricFormProperty property = (HistoricFormProperty) detail;
            sb.append(property.getPropertyId());
            sb.append("=");
            sb.append("\"");
            sb.append(property.getPropertyValue());
            sb.append("\"");
            if(iterator.hasNext()){
                sb.append(",");
            }
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * @param nID_UserTask номер-ИД задачи, для которой нужно удалить назначенного пользователя.
     */
    @ApiOperation(value = "Удаление назначенного пользователя с задачи по ИД.", notes = noteResetUserTaskAssign)
    @RequestMapping(value = "/tasks/resetUserTaskAssign", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<String> resetUserTaskAssign(
            @ApiParam(value = "nID_UserTask - номер-ИД юзертаски", required = true) @RequestParam(value = "nID_UserTask", required = true) String nID_UserTask)
            throws ActivitiRestException, RecordNotFoundException {
        return unclaimUserTask(nID_UserTask);
    }

    private ResponseEntity<String> unclaimUserTask(String nID_UserTask)
            throws ActivitiRestException, RecordNotFoundException {
        Task task = taskService.createTaskQuery().taskId(nID_UserTask).singleResult();
        if (task == null)
            throw new RecordNotFoundException();
        if (task.getAssignee() == null || task.getAssignee().isEmpty())
            return new ResponseEntity<>("Not assigned UserTask", HttpStatus.OK);
        taskService.unclaim(task.getId());
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private TaskQuery buildTaskQuery(String sLogin, String bAssigned) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (bAssigned != null) {
            if (!Boolean.valueOf(bAssigned)) {
                taskQuery.taskUnassigned();
                if (sLogin != null && !sLogin.isEmpty()) {
                    taskQuery.taskCandidateUser(sLogin);
                }
            } else if (sLogin != null && !sLogin.isEmpty()) {
                taskQuery.taskAssignee(sLogin);
            }
        } else {
            if (sLogin != null && !sLogin.isEmpty()) {
                taskQuery.taskCandidateOrAssigned(sLogin);
            }
        }
        return taskQuery;
    }

    private void cancelTasksInternal(Long nID_Protected, String sInfo) throws ActivitiRestException,
            CRCInvalidException, RecordNotFoundException, TaskAlreadyUnboundException {

        String processInstanceId = getOriginalProcessInstanceId(nID_Protected);

        getTasksByProcessInstanceId(processInstanceId);
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(
                processInstanceId).singleResult();

        FormData formData = formService.getStartFormData(processInstance.getProcessDefinitionId());

        List<String> propertyIds = AbstractModelTask.getListField_QueueDataFormType(formData);
        List<String> queueDataList = AbstractModelTask.getVariableValues(runtimeService, processInstanceId,
                propertyIds);

        if (queueDataList.isEmpty()) {
            LOG.error(String.format("Queue data list for Process Instance [id = '%s'] not found", processInstanceId));
            throw new RecordNotFoundException("Метаданные электронной очереди не найдены");
        }

            for (String queueData : queueDataList) {
                    Map<String, Object> m = QueueDataFormType.parseQueueData(queueData);
                    long nID_FlowSlotTicket = QueueDataFormType.get_nID_FlowSlotTicket(m);
                    if (!flowSlotTicketDao.unbindFromTask(nID_FlowSlotTicket)) {
                            throw new TaskAlreadyUnboundException("Заявка уже отменена");
                    }
            }

            runtimeService.setVariable(processInstanceId, CANCEL_INFO_FIELD,
                    //String.format("[%s] Причина отмены заявки: %s", DateTime.now(), sInfo == null ? "" : sInfo));
                    String.format("[%s] Заявка скасована: %s", DateTime.now(), sInfo == null ? "" : sInfo));

    }

    /**
     * Cервис получения данных по Таске
     *
     * @param nID_Task  номер-ИД таски (обязательный)
     * @param sID_Order номер-ИД заявки (опциональный, но обязательный если не задан nID_Task)
     * @return сериализованный объект <br> <b>oProcess</b> {<br><kbd>sName</kbd> - название услуги (БП);<br> <kbd>sBP</kbd> - id-бизнес-процесса (БП);<br> <kbd>nID</kbd> - номер-ИД процесса;<br> <kbd>sDateCreate</kbd> - дата создания процесса<br>}
     */
    @RequestMapping(value = "/getTaskData", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getTaskData(
            @RequestParam(value = "nID_Task", required = true) Long nID_Task,
            @RequestParam(value = "sID_Order", required = false) String sID_Order)
            throws CRCInvalidException, ActivitiRestException, RecordNotFoundException {

        if (nID_Task == null) {
            LOG.info("start process getting Task Data by sID_Order = " + sID_Order);
            Long ProtectedID = getIDProtectedFromIDOrder(sID_Order);
            ArrayList<String> taskIDsList = (ArrayList) getTasksByOrder(ProtectedID);
            Task task = getTaskByID(taskIDsList.get(0));
            Task taskOpponent;
            for (int i = 1; i < taskIDsList.size(); i++) {
                taskOpponent = getTaskByID(taskIDsList.get(i));
                if (task.getCreateTime().after(taskOpponent.getCreateTime())) {
                    task = taskOpponent;
                }
            }
            nID_Task = Long.parseLong(task.getId());
        }
        LOG.info("start process getting Task Data by nID_Task = " + nID_Task.toString());

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(nID_Task.toString()).singleResult();

        String sBP = historicTaskInstance.getProcessDefinitionId();
        LOG.info("id-бизнес-процесса (БП) sBP = " + sBP);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(sBP).singleResult();

        String sName = processDefinition.getName();
        LOG.info("название услуги (БП) sName = " + sName);

        String sDateCreate = getCreateTime(findBasicTask(nID_Task.toString()));
        LOG.info("дата создания таски sDateCreate = " + sDateCreate);

        Long nID = Long.valueOf(historicTaskInstance.getProcessInstanceId());
        LOG.info("id процесса nID = " + nID.toString());

        ProcessDTO oProcess = new ProcessDTO(sName, sBP, nID, sDateCreate);
        return JsonRestUtils.toJsonResponse(oProcess);
    }

    private class ProcessDTO {
        private String sName;
        private String sBP;
        private Long nID;
        private String sDateCreate;

        public ProcessDTO(String sName, String sBP, Long nID, String sDateCreate) {
            this.sName = sName;
            this.sBP = sBP;
            this.nID = nID;
            this.sDateCreate = sDateCreate;
        }

        public String getName() {
            return sName;
        }

        public String getBP() {
            return sBP;
        }

        public Long getID() {
            return nID;
        }

        public String getDateCreate() {
            return sDateCreate;
        }
    }

    private Task getTaskByID(String taskID) {
        return taskService.createTaskQuery().taskId(taskID).singleResult();
    }

    private Long getIDProtectedFromIDOrder(String sID_order) {
        String ID_Protected = "";
        int hyphenPosition = sID_order.lastIndexOf("-");
        if (hyphenPosition < 0) {
            ID_Protected = sID_order;
        } else {
            for (int i = hyphenPosition + 1; i < sID_order.length(); i++) {
                ID_Protected = ID_Protected + sID_order.charAt(i);
            }
        }
        return Long.parseLong(ID_Protected);
    }

    private Task findBasicTask(String ID_task) {
        boolean nextCycle = true;
        Task task = getTaskByID(ID_task);
        while (nextCycle) {
            if (task.getParentTaskId() == null || task.getParentTaskId().equals("")) {
                nextCycle = false;
            } else {
                task = getTaskByID(task.getParentTaskId());
            }
        }
        return task;
    }

    private String getCreateTime(Task task) {
        DateTimeFormatter formatter = JsonDateTimeSerializer.DATETIME_FORMATTER;
        Date date = task.getCreateTime();
        return formatter.print(date.getTime());
    }


    /*private static class TaskAlreadyUnboundException extends Exception {
        private TaskAlreadyUnboundException(String message) {
            super(message);
        }
    }*/
    
    
    
    
    
    
//@RequestMapping("/web")
//public class StartWebController {
    /*private final Logger LOG = LoggerFactory
            .getLogger(StartWebController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FormService formService;

    @RequestMapping(value = "/activiti/index", method = RequestMethod.GET)
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView("index");
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().latestVersion()
                .list();
        modelAndView.addObject("processList", processDefinitions);
        return modelAndView;
    }

    @RequestMapping(value = "/activiti/startForm/{id}", method = RequestMethod.GET)
    public ModelAndView startForm(@PathVariable("id") String id) {

        StartFormData sfd = formService.getStartFormData(id);

        List<FormProperty> fpList = sfd.getFormProperties();
        ModelAndView modelAndView = new ModelAndView("startForm");
        modelAndView.addObject("fpList", fpList);
        modelAndView.addObject("id", id);
        return modelAndView;
    }

    @RequestMapping(value = "/activiti/startProcess/{id}", method = RequestMethod.POST)
    public ModelAndView startProcess(@PathVariable("id") String id, @RequestParam Map<String, String> params) {
        ProcessInstance pi = formService.submitStartFormData(id, params);

        ModelAndView modelAndView = new ModelAndView("startedProcess");
        modelAndView.addObject("pi", pi.getProcessInstanceId());
        modelAndView.addObject("bk", pi.getBusinessKey());
        return modelAndView;
    }*/

        private String getOriginalProcessInstanceId(Long nID_Protected) throws CRCInvalidException {
                return Long.toString(AlgorithmLuna.getValidatedOriginalNumber(nID_Protected));
    }

        private List<String> getTaskIdsByProcessInstanceId(String processInstanceID) throws RecordNotFoundException {
                List<Task> aTask = getTasksByProcessInstanceId(processInstanceID);
                List<String> res = new ArrayList<>();

                for (Task task : aTask) {
                        res.add(task.getId());
        }

                return res;
    }

        private List<Task> getTasksByProcessInstanceId(String processInstanceID) throws RecordNotFoundException {
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceID).list();
                if (tasks == null || tasks.isEmpty()) {
                        LOG.error(
                                String.format("Tasks for Process Instance [id = '%s'] not found", processInstanceID));
                        throw new RecordNotFoundException();
        }
                return tasks;
    }

    /**
     * Запуск процесса Activiti:
     *
     * @param key         Ключ процесса
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/start-process/{key}", method = RequestMethod.GET)
    @ApiOperation(value = "Запуск процесса Activiti", notes = noteStartProcessByKey)
    @Transactional
    public
    @ResponseBody
    ProcessI startProcessByKey(
            @ApiParam(value = "Ключ процесса", required = true) @PathVariable("key") String key) {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
        if (pi == null || pi.getId() == null) {
            throw new IllegalArgumentException(String.format(
                    "process did not started by key:{%s}", key));
        }
        return new Process(pi.getProcessInstanceId());
    }

    @RequestMapping(value = "/process/getTasks", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getProcessTasks(@RequestParam String processInstanceId)
            throws CRCInvalidException, ActivitiRestException, RecordNotFoundException {
        return getTasksByOrder(AlgorithmLuna.getProtectedNumber(Long.valueOf(processInstanceId)));
    }

    @RequestMapping(value = "/process/setVariable", method = RequestMethod.GET)
    public void setVariableToProcessInstance(
            @RequestParam String processInstanceId,
            @RequestParam String key,
            @RequestParam Object value) {
        runtimeService.setVariable(processInstanceId, key, value);
    }

    /**
     * Загрузка каталога сервисов из Activiti:
     *
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/process-definitions", method = RequestMethod.GET)
    @ApiOperation(value = "Загрузка каталога сервисов из Activiti", notes = noteGetProcessDefinitions)
    @Transactional
    public
    @ResponseBody
    List<ProcDefinitionI> getProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().latestVersion().list();
        List<ProcDefinitionI> procDefinitions = new ArrayList<>();
        ProcDefinitionAdapter adapter = new ProcDefinitionAdapter();
        for (ProcessDefinition processDefinition : processDefinitions) {
            procDefinitions.add(adapter.apply(processDefinition));
        }
        return procDefinitions;
    }

    @ApiOperation(value = "DeleteProcess", notes = noteDeleteProcess)
    @RequestMapping(value = "/delete-process", method = RequestMethod.DELETE)
    public
    @ResponseBody
    void deleteProcess(@RequestParam(value = "nID_Protected") Long nID_Protected,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason
    )
            throws Exception {

        String processInstanceID = String.valueOf(AlgorithmLuna.getValidatedOriginalNumber(nID_Protected));

        String sID_status = "Заявка была удалена";
        if (sLogin != null) {
            sID_status += " (" + sLogin + ")";
        }
        if (sReason != null) {
            sID_status += ": " + sReason;
        }
        LOG.info("Deleting process {}: {}", processInstanceID, sID_status);
        try {
            runtimeService.deleteProcessInstance(processInstanceID, sReason);
        } catch (ActivitiObjectNotFoundException e) {
            LOG.info("Could not find process {} to delete: {}", processInstanceID, e);
            throw new RecordNotFoundException();
        }
        historyEventService.updateHistoryEvent(processInstanceID, sID_status, false, null);
    }

    @ApiOperation(value = "DeleteProcessTest", notes = noteDeleteProcessTest)
    @RequestMapping(value = "/delete-processTest", method = RequestMethod.GET)
    public
    @ResponseBody
    void deleteProcessTest(@RequestParam(value = "sProcessInstanceID") String processInstanceID,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason
    ) throws Exception {
        runtimeService.deleteProcessInstance(processInstanceID, sReason);
    }

    /**
     * Укладываем в редис multipartFileToByteArray
     *
     * @param file
     * @return attachId
     * @throws org.igov.service.controller.ActivitiIOException
     */
    @ApiOperation(value = "PutAttachmentsToRedis", notes = notePutAttachmentsToRedis)
    @RequestMapping(value = "/file/upload_file_to_redis", method = RequestMethod.POST)
    @Transactional
    public
    @ResponseBody
    String putAttachmentsToRedis(
            @RequestParam(required = true, value = "file") MultipartFile file)
            throws ActivitiIOException {
        try {
            String key = oBytesDataInmemoryStorage.putBytes(AbstractModelTask
                    .multipartFileToByteArray(file, file.getOriginalFilename())
                    .toByteArray());
            return key;
        } catch (RecordInmemoryException | IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "GetAttachmentsFromRedis", notes = noteGetAttachmentsFromRedis)
    @RequestMapping(value = "/file/download_file_from_redis", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentsFromRedis(
            @RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            upload = oBytesDataInmemoryStorage.getBytes(key);
        } catch (RecordInmemoryException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
        return upload;
    }

    @ApiOperation(value = "GetAttachmentsFromRedisBytes", notes = noteGetAttachmentsFromRedisBytes)
    @RequestMapping(value = "/file/download_file_from_redis_bytes", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentsFromRedisBytes(
            @RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            byte[] aByteFile = oBytesDataInmemoryStorage.getBytes(key);
            ByteArrayMultipartFile oByteArrayMultipartFile = null;
            oByteArrayMultipartFile = getByteArrayMultipartFileFromRedis(aByteFile);

            if (oByteArrayMultipartFile != null) {

                upload = oByteArrayMultipartFile.getBytes();

            } else {
                // LOG.error("[getAttachmentsFromRedisBytes]oByteArrayMultipartFile==null! aByteFile="
                // + aByteFile.
                // .toString());
                // Unreachable code?
                LOG.error("[getAttachmentsFromRedisBytes]oByteArrayMultipartFile==null! key="
                        + key);
            }

        } catch (RecordInmemoryException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        } catch (ClassNotFoundException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ActivitiException(e.getMessage(), e);
        }

        return upload;
    }

    @ApiOperation(value = "Проверка ЭЦП на файле хранящемся в Redis", notes = noteCheckAttachmentsFromRedisSign)
    @RequestMapping(value = "/file/check_file_from_redis_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String checkAttachmentsFromRedisSign(
            @ApiParam(value = "key по которому можно получить файл из хранилища Redis", required = true) @RequestParam("sID_File_Redis") String sID_File_Redis)
            throws ActivitiIOException {
        byte[] upload = null;
        String fileName = null;
        try {
            byte[] aByteFile = oBytesDataInmemoryStorage.getBytes(sID_File_Redis);
            ByteArrayMultipartFile oByteArrayMultipartFile = null;

            if (aByteFile == null) {
                throw new ActivitiObjectNotFoundException(
                        "File with sID_File_Redis '" + sID_File_Redis
                                + "' not found.");
            }
            try {
                oByteArrayMultipartFile = getByteArrayMultipartFileFromRedis(aByteFile);
            } catch (ClassNotFoundException | IOException e1) {
                throw new ActivitiException(e1.getMessage(), e1);
            }
            if (oByteArrayMultipartFile != null) {

                upload = oByteArrayMultipartFile.getBytes();
                fileName = oByteArrayMultipartFile.getName();

            } else {
                // /
                // LOG.error("[checkAttachmentsFromRedisSign]oByteArrayMultipartFile==null! aByteFile="
                // + aByteFile
                // / .toString());
                // Unreachable code?
                LOG.error("[checkAttachmentsFromRedisSign]oByteArrayMultipartFile==null! sID_File_Redis="
                        + sID_File_Redis);
            }

        } catch (RecordInmemoryException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }

        String soSignData = BankIDUtils.checkECP(bankIDConfig.sClientId(),
                bankIDConfig.sClientSecret(), generalConfig.sHostCentral(),
                upload, fileName);

        return soSignData;
    }

    /**
     * Получение Attachment средствами активити из таблицы ACT_HI_ATTACHMENT
     *
     * @param taskId
     * @param attachmentId
     * @param nFile
     * @param httpResponse
     * @return
     * @throws java.io.IOException
     */
    @ApiOperation(value = "Загрузки прикрепленного к заявке файла из постоянной базы", notes = noteGetAttachmentFromDb)
    @RequestMapping(value = "/file/download_file_from_db", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentFromDb(
            @ApiParam(value = "ид задачи", required = true) @RequestParam(value = "taskId") String taskId,
            @ApiParam(value = "ID прикрепленного файла", required = false) @RequestParam(required = false, value = "attachmentId") String attachmentId,
            @ApiParam(value = "порядковый номер прикрепленного файла", required = false) @RequestParam(required = false, value = "nFile") Integer nFile,
            HttpServletResponse httpResponse) throws IOException {

        // Получаем по задаче ид процесса
        HistoricTaskInstance historicTaskInstanceQuery = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        String processInstanceId = historicTaskInstanceQuery
                .getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(
                    "ProcessInstanceId for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }

        // Выбираем по процессу прикрепленные файлы
        Attachment attachmentRequested = getAttachment(attachmentId, taskId,
                nFile, processInstanceId);

        InputStream attachmentStream = taskService
                .getAttachmentContent(attachmentRequested.getId());
        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException("Attachment for taskId '"
                    + taskId + "' doesn't have content associated with it.",
                    Attachment.class);
        }

        String sFileName = attachmentRequested.getName();
        int nTo = sFileName.lastIndexOf(".");
        if (nTo >= 0) {
            sFileName = "attach_" + attachmentRequested.getId() + "."
                    + sFileName.substring(nTo + 1);
        }

        // Вычитывем из потока массив байтов контента и помещаем параметры
        // контента в header
        ByteArrayMultipartFileOld multipartFile = new ByteArrayMultipartFileOld(
                attachmentStream, attachmentRequested.getDescription(),
                sFileName, attachmentRequested.getType());
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + sFileName);
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        return multipartFile.getBytes();
    }

    /**
     * @param taskId       id таски Activiti BP
     * @param attachmentId id атачмента приложеного к таске
     */
    @ApiOperation(value = "Проверка ЭЦП на атачменте(файл) таски Activiti", notes = noteCheckAttachSign)
    @RequestMapping(value = "/file/check_attachment_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String checkAttachSign(
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam(value = "nID_Task") String taskId,
            @ApiParam(value = "id атачмента приложеного к таске", required = true) @RequestParam(value = "nID_Attach") String attachmentId)
            throws IOException {

        HistoricTaskInstance historicTaskInstanceQuery = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        String processInstanceId = null;

        if (historicTaskInstanceQuery != null) {
            processInstanceId = historicTaskInstanceQuery
                    .getProcessInstanceId();
        }
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(
                    "ProcessInstanceId for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }

        Attachment attachmentRequested = getAttachment(attachmentId, taskId,
                processInstanceId);

        InputStream attachmentStream = null;
        if (attachmentRequested != null) {
            attachmentStream = taskService
                    .getAttachmentContent(attachmentRequested.getId());
        }

        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException("Attachment for taskId '"
                    + taskId + "' doesn't have content associated with it.",
                    Attachment.class);
        }

        LOG.info("Attachment found. taskId {}, attachmentID {} With name {} ",
                taskId, attachmentId, attachmentRequested.getName());

        byte[] content = IOUtils.toByteArray(attachmentStream);

        String soSignData = BankIDUtils.checkECP(bankIDConfig.sClientId(),
                bankIDConfig.sClientSecret(), generalConfig.sHostCentral(),
                content, attachmentRequested.getName());

        return soSignData;
    }

    private Attachment getAttachment(String attachmentId, String taskId,
            Integer nFile, String processInstanceId) {
        List<Attachment> attachments = taskService
                .getProcessInstanceAttachments(processInstanceId);
        Attachment attachmentRequested = null;
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getId().equalsIgnoreCase(attachmentId)
                    || (null != nFile && nFile.equals(i + 1))) {
                attachmentRequested = attachments.get(i);
                break;
            }
        }

        if (attachmentRequested == null && !attachments.isEmpty()) {
            attachmentRequested = attachments.get(0);
        }

        if (attachmentRequested == null) {
            throw new ActivitiObjectNotFoundException("Attachment for taskId '"
                    + taskId + "' not found.", Attachment.class);
        }
        return attachmentRequested;
    }

    private Attachment getAttachment(String attachmentId, String taskId,
            String processInstanceId) {
        List<Attachment> attachments = taskService
                .getProcessInstanceAttachments(processInstanceId);
        Attachment attachmentRequested = null;
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getId().equalsIgnoreCase(attachmentId)) {
                attachmentRequested = attachments.get(i);
                break;
            }
        }

        if (attachmentRequested == null) {
            throw new ActivitiObjectNotFoundException("Attachment for taskId '"
                    + taskId + "' not found.", Attachment.class);
        }
        return attachmentRequested;
    }

    /**
     * Сервис для получения Attachment из execution
     *
     * @param taskId
     * @param httpResponse
     * @return
     * @throws java.io.IOException
     */
    @ApiOperation(value = "Сервис для получения Attachment из execution", notes = noteGetAttachmentFromDbExecution)
    @RequestMapping(value = "/file/download_file_from_db_execution", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentFromDbExecution(
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam("taskId") String taskId,
            HttpServletResponse httpResponse) throws IOException {

        // получаем по задаче ид процесса
        HistoricTaskInstance historicTaskInstanceQuery = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        String processInstanceId = historicTaskInstanceQuery
                .getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(String.format(
                    "ProcessInstanceId for taskId '{%s}' not found.", taskId),
                    Attachment.class);
        }

        // получаем по ид процесса сам процесс
        HistoricProcessInstance processInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).includeProcessVariables()
                .singleResult();
        if (processInstance == null) {
            throw new ActivitiObjectNotFoundException(String.format(
                    "ProcessInstance for processInstanceId '{%s}' not found.",
                    processInstanceId), Attachment.class);
        }

        // получаем коллекцию переменных процеса и прикрепленный файл
        Map<String, Object> processVariables = processInstance
                .getProcessVariables();
        BuilderAttachModel attachModel = null;

        if (processVariables != null) {
            List<BuilderAttachModel> builderAttachModelList = (List) processVariables
                    .get(FileTaskUpload.BUILDER_ATACH_MODEL_LIST);

            if (builderAttachModelList != null) {
                attachModel = builderAttachModelList.get(0);
            }
        }

        if (attachModel == null) {
            throw new ActivitiObjectNotFoundException(
                    String.format(
                            "ProcessVariable '{%s}' for processInstanceId '{%s}' not found.",
                            FileTaskUpload.BUILDER_ATACH_MODEL_LIST,
                            processInstanceId));
        }

        // Помещаем параметры контента в header
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + attachModel.getOriginalFilename());
        httpResponse.setHeader("Content-Type", attachModel.getContentType()
                + ";charset=UTF-8");
        httpResponse.setContentLength(attachModel.getByteToStringContent()
                .getBytes().length);

        return AbstractModelTask.contentStringToByte(attachModel
                .getByteToStringContent());
    }

    /**
     * Аплоад(upload) и прикрепление файла в виде атачмента к таске Activiti
     *
     * @param taskId      ИД-номер таски
     * @param description описание
     * @param file        в html это имя элемента input типа file - <input name="file" type="file" />. в HTTP заголовках - Content-Disposition: form-data; name="file" ...
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Аплоад(upload) и прикрепление файла в виде атачмента к таске Activiti", notes = notePutAttachmentsToExecution)
    @RequestMapping(value = "/file/upload_file_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public
    @ResponseBody
    AttachmentEntityI putAttachmentsToExecution(
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam(value = "taskId") String taskId,
            @ApiParam(value = "в html это имя элемента input типа file - <input name=\"file\" type=\"file\" />. в HTTP заголовках - Content-Disposition: form-data; name=\"file\" ...", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "описание", required = true) @RequestParam(value = "description") String description)
            throws IOException {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (!tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee()
                    : "kermit";
            LOG.debug("processInstanceId: " + processInstanceId + " taskId: "
                    + taskId + "assignee: " + assignee);
        } else {
            LOG.error("There is no tasks at all!");
        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = file.getOriginalFilename();
        LOG.debug("sFilename=" + file.getOriginalFilename());
        sFilename = Renamer.sRenamed(sFilename);
        LOG.debug("FileExtention: " + getFileExtention(file)
                + " fileContentType: " + file.getContentType() + "fileName: "
                + sFilename);
        LOG.debug("description: " + description);

        Attachment attachment = taskService.createAttachment(
                file.getContentType() + ";" + getFileExtention(file), taskId,
                processInstanceId, sFilename,// file.getOriginalFilename()
                description, file.getInputStream());

        AttachmentEntityAdapter adapter = new AttachmentEntityAdapter();

        return adapter.apply(attachment);
    }

    /**
     * Аплоад(upload) и прикрепление текстового файла в виде атачмента к таске Activiti
     *
     * @param taskId       ИД-номер таски
     * @param sContentType MIME тип отправляемого файла (опциоанльно) (значение по умолчанию = "text/html")
     * @param description  описание
     * @param sFileName    имя отправляемого файла
     */
    @ApiOperation(value = "Аплоад(upload) и прикрепление текстового файла в виде атачмента к таске Activiti", notes = notePutTextAttachmentsToExecution)
    @RequestMapping(value = "/file/upload_content_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public
    @ResponseBody
    AttachmentEntityI putTextAttachmentsToExecution(
            @ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "nTaskId") String taskId,
            @ApiParam(value = "MIME тип отправляемого файла (опциоанльно) (значение по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "описание", required = true) @RequestParam(value = "sDescription") String description,
            @RequestParam(value = "sFileName") String sFileName,
            @RequestBody String sData) {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (!tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee()
                    : "kermit";
            LOG.debug("processInstanceId: " + processInstanceId + " taskId: "
                    + taskId + "assignee: " + assignee);
        } else {
            LOG.error("There is no tasks at all!");

        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = sFileName;
        LOG.debug("sFilename=" + sFileName);
        sFilename = Renamer.sRenamed(sFilename);
        LOG.debug("FileExtention: " + getFileExtention(sFileName)
                + " fileContentType: " + sContentType + "fileName: "
                + sFilename);
        LOG.debug("description: " + description);

        Attachment attachment = taskService.createAttachment(sContentType + ";"
                        + getFileExtention(sFileName), taskId, processInstanceId,
                sFilename, description,
                new ByteArrayInputStream(sData.getBytes(Charsets.UTF_8)));

        AttachmentEntityAdapter adapter = new AttachmentEntityAdapter();

        return adapter.apply(attachment);
    }

    /**
     * Получение статистики по бизнес процессу за указанный период
     *
     * @param sID_BP_Name    - ИД бизнес процесса
     * @param dateAt         - дата начала периода выборки
     * @param dateTo         - дата окончания периода выборки
     * @param nRowStart      - позиция начальной строки для возврата (0 по умолчанию)
     * @param nRowsMax       - количество записей для возврата (1000 по умолчанию)
     * @param bDetail        - если да, то выгружать все поля тасок, иначе -- только
     *                       основные (по умолчанию да)
     * @param saFields       - вычисляемые поля (название поля -- формула, issue 907)
     * @param saFieldSummary - сведение полей, которое производится над выборкой
     *                       (issue 916)
     * @param httpResponse   - респонс, в который пишется ответ -- csv-файл
     * @throws java.io.IOException
     */
    @ApiOperation(value = "Получение статистики по задачам в рамках бизнес процесса", notes = noteGetTimingForBusinessProcessNew)
    @RequestMapping(value = "/file/download_bp_timing", method = RequestMethod.GET)
    @Transactional
    public void getTimingForBusinessProcessNew(
            @ApiParam(value = "ИД бизнес процесса", required = true) @RequestParam(value = "sID_BP_Name") String sID_BP_Name,
            @ApiParam(value = "дата начала периода выборки", required = false) @RequestParam(value = "sDateAt") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @ApiParam(value = "дата окончания периода выборки", required = false) @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @ApiParam(value = "позиция начальной строки для возврата (0 по умолчанию)", required = false) @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @ApiParam(value = "количество записей для возврата (1000 по умолчанию)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @ApiParam(value = "если да, то выгружать все поля тасок, иначе -- только основные (по умолчанию да)", required = false) @RequestParam(value = "bDetail", required = false, defaultValue = "true") Boolean bDetail,
            @ApiParam(value = "сведение полей, которое производится над выборкой", required = false) @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            @ApiParam(value = "вычисляемые поля (название поля -- формула)", required = false) @RequestParam(value = "saFields", required = false) String saFields,
            HttpServletResponse httpResponse) throws IOException {

        if (sID_BP_Name == null || sID_BP_Name.isEmpty()) {
            LOG.error(String.format(
                    "Statistics for the business process '{%s}' not found.",
                    sID_BP_Name));
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business process '" + sID_BP_Name
                            + "' not found.", Process.class);
        }
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                "yyyy-MM-ddHH-mm-ss", Locale.ENGLISH);
        String fileName = sID_BP_Name + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".csv";
        LOG.debug("File name for statistics : {%s}", fileName);
        boolean isByFieldsSummary = saFieldSummary != null
                && !saFieldSummary.isEmpty();
        httpResponse.setContentType("text/csv;charset=UTF-8");
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + fileName);

        List<HistoricTaskInstance> foundResults = historyService
                .createHistoricTaskInstanceQuery().taskCompletedAfter(dateAt)
                .taskCompletedBefore(dateTo).processDefinitionKey(sID_BP_Name)
                .listPage(nRowStart, nRowsMax);

        List<String> headers = new ArrayList<>();
        String[] headersMainField = { "nID_Process", "sLoginAssignee",
                "sDateTimeStart", "nDurationMS", "nDurationHour", "sName" };
        headers.addAll(Arrays.asList(headersMainField));
        LOG.debug("headers: " + headers);
        Set<String> headersExtra = findExtraHeaders(bDetail, foundResults,
                headers);
        if (saFields != null) {
            saFields = StringUtils.substringAfter(saFields, "\"");
            saFields = StringUtils.substringBeforeLast(saFields, "\"");
            String[] params = saFields.split(";");
            for (String header : params) {
                String cutHeader = StringUtils.substringBefore(header, "=");
                LOG.info("Adding header to the csv file from saFields: "
                        + cutHeader);
                headers.add(cutHeader);
            }
        }
        LOG.info("headers: " + headers);

        CSVWriter csvWriter = new CSVWriter(httpResponse.getWriter());
        if (!isByFieldsSummary) {
            csvWriter.writeNext(headers.toArray(new String[headers.size()]));
        }
        List<Map<String, Object>> csvLines = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(foundResults)) {
            LOG.debug(String
                    .format("Found {%s} completed tasks for business process {%s} for date period {%s} - {%s}",
                            foundResults.size(), sID_BP_Name,
                            DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
            for (HistoricTaskInstance currTask : foundResults) {
                Map<String, Object> csvLine = createCsvLine(bDetail
                        || isByFieldsSummary, headersExtra, currTask, saFields);
                String[] line = createStringArray(csvLine, headers);
                LOG.info("line: " + csvLine);
                if (!isByFieldsSummary) {
                    csvWriter.writeNext(line);
                }
                csvLines.add(csvLine);
            }
        } else {
            LOG.debug(String
                    .format("No completed tasks found for business process {%s} for date period {%s} - {%s}",
                            sID_BP_Name, DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
        }
        if (isByFieldsSummary) { // issue 916
            LOG.info(">>>saFieldsSummary=" + saFieldSummary);
            try {
                List<List<String>> stringResults = new FieldsSummaryUtil()
                        .getFieldsSummary(csvLines, saFieldSummary);
                for (List<String> line : stringResults) {
                    csvWriter.writeNext(line.toArray(new String[line.size()]));
                }
            } catch (Exception e) {
                List<String> errorList = new LinkedList<>();
                errorList.add(e.getMessage());
                errorList.add(e.getCause() != null ? e.getCause().getMessage()
                        : "");
                csvWriter.writeNext(errorList.toArray(new String[errorList
                        .size()]));
                LOG.error(e.getMessage(), e);
            }
            LOG.info(">>>>csv for saFieldSummary is complete.");
        }
        csvWriter.close();
    }

    private String[] createStringArray(Map<String, Object> csvLine,
            List<String> headers) {
        List<String> result = new LinkedList<>();
        for (String header : headers) {
            Object value = csvLine.get(header);
            result.add(value == null ? "" : value.toString());
        }
        return result.toArray(new String[result.size()]);
    }

    private Map<String, Object> createCsvLine(boolean bDetail,
            Set<String> headersExtra, HistoricTaskInstance currTask,
            String saFields) {
        Map<String, Object> line = new HashMap<>();
        line.put("nID_Process", currTask.getProcessInstanceId());
        line.put("sLoginAssignee", currTask.getAssignee());
        Date startDate = currTask.getStartTime();
        line.put("sDateTimeStart", DATE_TIME_FORMAT.format(startDate));
        line.put("nDurationMS", String.valueOf(currTask.getDurationInMillis()));
        long durationInHours = currTask.getDurationInMillis() / MILLIS_IN_HOUR;
        line.put("nDurationHour", String.valueOf(durationInHours));
        line.put("sName", currTask.getName());

        if (bDetail) {
            addTasksDetailsToLine(headersExtra, currTask, line);
        }
        if (saFields != null) {
            processExtractFieldsParameter(headersExtra, currTask, saFields,
                    line);
        }
        return line;
    }

    protected void processExtractFieldsParameter(Set<String> headersExtra,
            HistoricTaskInstance currTask, String saFields,
            Map<String, Object> line) {
        HistoricTaskInstance details = historyService
                .createHistoricTaskInstanceQuery().includeProcessVariables()
                .taskId(currTask.getId()).singleResult();
        LOG.info("Process variables of the task " + currTask.getId() + ":"
                + details.getProcessVariables());
        if (details != null && details.getProcessVariables() != null) {
            LOG.info("Cleaned saFields:" + saFields);
            String[] expressions = saFields.split(";");
            if (expressions != null) {
                for (String expression : expressions) {
                    String variableName = StringUtils.substringBefore(
                            expression, "=");
                    String condition = StringUtils.substringAfter(expression,
                            "=");
                    LOG.info("Checking variable with name " + variableName
                            + " and condition " + condition
                            + " from expression:" + expression);
                    try {
                        Object conditionResult = getObjectResultofCondition(
                                headersExtra, currTask, details, condition);
                        line.put(variableName, conditionResult);
                    } catch (Exception e) {
                        LOG.error("Error occured while processing variable "
                                + variableName, e);
                    }
                }
            }
        }
    }

    private Object getObjectResultofCondition(Set<String> headersExtra,
            HistoricTaskInstance currTask, HistoricTaskInstance details,
            String condition) throws ScriptException, NoSuchMethodException {
        Map<String, Object> params = new HashMap<String, Object>();
        for (String headerExtra : headersExtra) {
            Object variableValue = details.getProcessVariables().get(
                    headerExtra);
            String propertyValue = EGovStringUtils
                    .toStringWithBlankIfNull(variableValue);
            params.put(headerExtra, propertyValue);
        }
        params.put("sAssignedLogin", currTask.getAssignee());
        params.put("sID_UserTask", currTask.getTaskDefinitionKey());
        LOG.info("Calculating expression with params: " + params);
        Object conditionResult = new JSExpressionUtil()
                .getObjectResultOfCondition(new HashMap<String, Object>(),
                        params, condition);
        LOG.info("Condition of the expression is " + conditionResult.toString());
        return conditionResult;
    }

    /*
     * private void clearEmptyValues(Map<String, Object> params) {
     * Iterator<String> iterator = params.keySet().iterator(); while
     * (iterator.hasNext()){ String key = iterator.next(); if (params.get(key)
     * == null){ iterator.remove(); } } }
     */
    private void addTasksDetailsToLine(Set<String> headersExtra,
            HistoricTaskInstance currTask, Map<String, Object> resultLine) {
        LOG.debug("currTask: " + currTask.getId());
        HistoricTaskInstance details = historyService
                .createHistoricTaskInstanceQuery().includeProcessVariables()
                .taskId(currTask.getId()).singleResult();
        if (details != null && details.getProcessVariables() != null) {
            for (String headerExtra : headersExtra) {
                Object variableValue = details.getProcessVariables().get(
                        headerExtra);
                // String propertyValue =
                // EGovStringUtils.toStringWithBlankIfNull(variableValue);
                resultLine.put(headerExtra, variableValue);
            }
        }
    }

    private Set<String> findExtraHeaders(Boolean bDetail,
            List<HistoricTaskInstance> foundResults, List<String> headers) {
        if (bDetail) {
            Set<String> headersExtra = findExtraHeadersForDetail(foundResults,
                    headers);
            return headersExtra;
        } else {
            return new TreeSet<String>();
        }
    }

    private Set<String> findExtraHeadersForDetail(
            List<HistoricTaskInstance> foundResults, List<String> headers) {
        Set<String> headersExtra = new TreeSet<String>();
        for (HistoricTaskInstance currTask : foundResults) {

            HistoricTaskInstance details = historyService
                    .createHistoricTaskInstanceQuery()
                    .includeProcessVariables().taskId(currTask.getId())
                    .singleResult();
            if (details != null && details.getProcessVariables() != null) {
                LOG.info(" proccessVariavles: " + details.getProcessVariables());
                for (String key : details.getProcessVariables().keySet()) {
                    if (!key.startsWith("sBody")) {
                        headersExtra.add(key);
                    }
                }
            }
        }
        headers.addAll(headersExtra);
        return headersExtra;
    }

    /**
     * Download information about the tasks in csv format
     *
     * @param sID_BP            business process name
     * @param sID_State_BP      task state id
     * @param saFields          field of the tasks to download. Separated by comma
     * @param nASCI_Spliter     splitter of the fields
     * @param sID_Codepage      encoding for the file
     * @param sDateCreateFormat format for sDateCreate
     * @param dateAt            start date for the filter
     * @param dateTo            end date for the filter
     * @param nRowStart         start row for paging
     * @param nRowsMax          maximal amount of row for paging
     * @param bIncludeHistory   to include historic task instances. default value
     *                          is true
     * @param saFieldsCalc      list of calculated fields
     * @param saFieldSummary    parap to specify aggregated fields
     * @param httpResponse      http responce wrapper
     * @throws IOException in case of connection aborted with client
     *                     <p/>
     *                     example: https://test.region.igov.org.ua/wf/service/rest/file/
     *                     downloadTasksData ?sID_BP=kiev_mreo_1&sDateAt=2015-06-28&sDateTo
     *                     =2015-08-01&nASCI_Spliter =59&sID_Codepage=UTF8&saFields=nID_Task
     *                     ;bankIdPassport;bankIdlastName
     *                     ;bankIdfirstName;bankIdmiddleName;1;sDateCreate
     */
    @ApiOperation(value = "Загрузка данных по задачам", notes = noteDownloadTasksData)
    @RequestMapping(value = "/file/downloadTasksData", method = RequestMethod.GET)
    @Transactional
    public void downloadTasksData(
            @ApiParam(value = "название бизнесс процесса", required = true) @RequestParam(value = "sID_BP") String sID_BP,
            @ApiParam(value = "состояние задачи, по умолчанию исключается из фильтра Берется из поля taskDefinitionKey задачи", required = false) @RequestParam(value = "sID_State_BP", required = false) String sID_State_BP,
            @ApiParam(value = "имена полей для выборкы разделенных через ';', чтобы добавить все поля можно использовать - '*' или не передевать параметр в запросе. Поле также может содержать названия колонок. Например, saFields=Passport\\=${passport};{email}", required = false) @RequestParam(value = "saFields", required = false) String saFields,
            @ApiParam(value = "ASCII код для разделителя", required = false) @RequestParam(value = "nASCI_Spliter", required = false) String nASCI_Spliter,
            @ApiParam(value = "имя исходящего файла, по умолчанию - data_BP-bpName_.txt\"", required = false) @RequestParam(value = "sFileName", required = false) String fileName,
            @ApiParam(value = "кодировка исходящего файла, по умолчанию - win1251", required = false) @RequestParam(value = "sID_Codepage", required = false, defaultValue = "win1251") String sID_Codepage,
            @ApiParam(value = "форматирование даты создания таски, по умолчанию - yyyy-MM-dd HH:mm:ss", required = false) @RequestParam(value = "sDateCreateFormat", required = false, defaultValue = "yyyy-MM-dd HH:mm:ss") String sDateCreateFormat,
            @ApiParam(value = "начальная дата создания таски, по умолчанию - вчера", required = false) @RequestParam(value = "sDateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @ApiParam(value = "конечная дата создания таски, по умолчанию - сегодня", required = false) @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @ApiParam(value = "начало выборки для пейджирования, по умолчанию - 0", required = false) @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @ApiParam(value = "размер выборки для пейджирования, по умолчанию - 1000", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @ApiParam(value = "включить информацию по хисторик задачам, по умолчанию - true", required = false) @RequestParam(value = "bIncludeHistory", required = false, defaultValue = "true") Boolean bIncludeHistory,
            @ApiParam(value = "добавить заголовок с названиями полей в выходной файл, по умолчанию - false", required = false) @RequestParam(value = "bHeader", required = false, defaultValue = "false") Boolean bHeader,
            @ApiParam(value = "настраиваемые поля (название поля -- формула, issue 907", required = false) @RequestParam(value = "saFieldsCalc", required = false) String saFieldsCalc,
            @ApiParam(value = "сведение полей, которое производится над выборкой (issue 916)", required = false) @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            HttpServletResponse httpResponse) throws IOException {
        // 1. validation
        if (StringUtils.isBlank(sID_BP)) {
            LOG.error("Wrong name of business task - {}", sID_BP);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task '" + sID_BP
                            + "' not found. Wrong BP name.", Task.class);
        }

        Date dBeginDate = getBeginDate(dateAt);
        Date dEndDate = getEndDate(dateTo);
        String separator = getSeparator(sID_BP, nASCI_Spliter);
        Charset charset = getCharset(sID_Codepage);

        // 2. query
        TaskQuery query = taskService.createTaskQuery()
                .processDefinitionKey(sID_BP).taskCreatedAfter(dBeginDate)
                .taskCreatedBefore(dEndDate);
        HistoricTaskInstanceQuery historicQuery = historyService
                .createHistoricTaskInstanceQuery()
                .processDefinitionKey(sID_BP).taskCreatedAfter(dBeginDate)
                .taskCreatedBefore(dEndDate).includeProcessVariables();
        if (sID_State_BP != null) {
            historicQuery.taskDefinitionKey(sID_State_BP);
        }
        List<HistoricTaskInstance> foundHistoricResults = historicQuery
                .listPage(nRowStart, nRowsMax);

        String header = formHeader(saFields, foundHistoricResults, saFieldsCalc);
        String[] headers = header.split(";");

        saFields = processSaFields(saFields, foundHistoricResults);

        if (sID_State_BP != null) {
            query = query.taskDefinitionKey(sID_State_BP);
        }
        List<Task> foundResults = query.listPage(nRowStart, nRowsMax);

        // 3. response
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                "yyyy-MM-ddHH-mm-ss", Locale.ENGLISH);
        String sTaskDataFileName = fileName != null ? fileName : "data_BP-"
                + sID_BP + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".txt";

        SimpleDateFormat sDateCreateDF = new SimpleDateFormat(
                sDateCreateFormat, Locale.ENGLISH);

        LOG.debug("File name to return statistics : {}", sTaskDataFileName);

        httpResponse.setContentType("text/csv;charset=" + charset.name());
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + sTaskDataFileName);

        CSVWriter printWriter = new CSVWriter(httpResponse.getWriter(), separator.charAt(0),
                CSVWriter.NO_QUOTE_CHARACTER);

        List<Map<String, Object>> csvLines = new LinkedList<>();

        if (bHeader && header != null && saFieldSummary == null) {
            printWriter.writeNext(headers);
        }

        fillTheCSVMap(sID_BP, dBeginDate, dEndDate, foundResults, sDateCreateDF,
                csvLines, saFields, saFieldsCalc, headers);
        if (Boolean.TRUE.equals(bIncludeHistory)) {
            Set<String> tasksIdToExclude = new HashSet<String>();
            for (Task task : foundResults) {
                tasksIdToExclude.add(task.getId());
            }
            fillTheCSVMapHistoricTasks(sID_BP, dBeginDate, dEndDate,
                    foundHistoricResults, sDateCreateDF, csvLines, saFields,
                    tasksIdToExclude, saFieldsCalc, headers);
        }

        if (saFieldSummary != null) {
            LOG.info(">>>saFieldsSummary=" + saFieldSummary);
            try {
                List<List<String>> stringResults = new FieldsSummaryUtil()
                        .getFieldsSummary(csvLines, saFieldSummary);
                for (int i = 0; i < stringResults.size(); i++) {
                    if (i == 0 && !bHeader)
                        continue;
                    List<String> line = stringResults.get(i);
                    printWriter.writeNext(line.toArray(new String[line.size()]));
                }
            } catch (Exception e) {
                List<String> errorList = new LinkedList<>();
                errorList.add(e.getMessage());
                errorList.add(e.getCause() != null ? e.getCause().getMessage()
                        : "");
                printWriter.writeNext(errorList.toArray(new String[errorList
                        .size()]));
                LOG.error(e.getMessage(), e);
            }
            LOG.info(">>>>csv for saFieldSummary is complete.");
        } else {
            for (Map<String, Object> currLine : csvLines) {
                String[] line = createStringArray(currLine, Arrays.asList(headers));
                printWriter.writeNext(line);
            }
        }

        printWriter.close();
    }

    /**
     * saFeilds paramter may contain name of headers or can be empty. Before
     * forming the result - we need to cut header names
     *
     * @param saFields
     * @param foundHistoricResults
     * @return
     */
    private String processSaFields(String saFields,
            List<HistoricTaskInstance> foundHistoricResults) {
        String res = null;
        if (saFields != null) {
            // we need to check the case when this parameter is not empty.
            // when ti is empty - we will not contain custom names
            //if (saFields.contains("=")) {
            LOG.info("saFields has custom header names");
            StringBuilder sb = new StringBuilder();
            String[] fields = saFields.split(";");
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].contains("=")) {
                    sb.append(StringUtils.substringAfter(fields[i], "="));
                } else {
                    sb.append(fields[i]);
                }
                if (i < fields.length - 1) {
                    sb.append(";");
                }
            }
            res = sb.toString();
            //}
        } else {
            // need to take all fields from the tasks
            if (foundHistoricResults != null && foundHistoricResults.size() > 0) {
                HistoricTaskInstance historicTask = foundHistoricResults.get(0);
                Set<String> keys = historicTask.getProcessVariables().keySet();
                StringBuilder sb = new StringBuilder();
                Iterator<String> iter = keys.iterator();
                while (iter.hasNext()) {
                    sb.append("${" + iter.next() + "}");
                    if (iter.hasNext()) {
                        sb.append(";");
                    }
                }
                res = sb.toString();
            }
            LOG.info("Formed header from all the fields of a task: " + res);
        }
        return res;
    }

    private String formHeader(String saFields, List<HistoricTaskInstance> foundHistoricResults, String saFieldsCalc) {
        String res = null;
        if (saFields != null && !"".equals(saFields.trim())) {
            //if (saFields.contains("=")) {
            LOG.info("Fields have custom header names");
            StringBuilder sb = new StringBuilder();
            String[] fields = saFields.split(";");
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].contains("\\=")) {
                    sb.append(StringUtils.substringBefore(fields[i], "\\="));
                } else {
                    sb.append(fields[i]);
                }
                if (i < fields.length - 1) {
                    sb.append(";");
                }
            }
            res = sb.toString();
            //}
            res = res.replaceAll("\\$\\{", "");
            res = res.replaceAll("\\}", "");
            LOG.info("Formed header from list of fields: " + res);
        } else {
            // need to take all fields from the tasks
            if (foundHistoricResults != null && foundHistoricResults.size() > 0) {
                HistoricTaskInstance historicTask = foundHistoricResults.get(0);
                Set<String> keys = historicTask.getProcessVariables().keySet();
                StringBuilder sb = new StringBuilder();
                Iterator<String> iter = keys.iterator();
                while (iter.hasNext()) {
                    sb.append(iter.next());
                    if (iter.hasNext()) {
                        sb.append(";");
                    }
                }
                res = sb.toString();
            }
            LOG.info("Formed header from all the fields of a task: " + res);
        }

        if (saFieldsCalc != null) {
            saFieldsCalc = StringUtils.substringAfter(saFieldsCalc, "\"");
            saFieldsCalc = StringUtils.substringBeforeLast(saFieldsCalc, "\"");
            String[] params = saFieldsCalc.split(";");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                String currParam = params[i];
                String cutHeader = StringUtils.substringBefore(currParam, "=");
                LOG.info("Adding header to the csv file from saFieldsCalc: " + cutHeader);
                sb.append(cutHeader);
                if (i < params.length - 1) {
                    sb.append(";");
                }
            }
            res = res + ";" + sb.toString();
            LOG.info("Header with calculated fields: " + res);
        }

        return res;
    }

    private void fillTheCSVMapHistoricTasks(String sID_BP, Date dateAt,
            Date dateTo, List<HistoricTaskInstance> foundResults,
            SimpleDateFormat sDateCreateDF, List<Map<String, Object>> csvLines,
            String pattern, Set<String> tasksIdToExclude, String saFieldsCalc, String[] headers) {
        if (CollectionUtils.isEmpty(foundResults)) {
            LOG.info(String
                    .format("No historic tasks found for business process %s for date period %s - %s",
                            sID_BP, DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
            return;
        }

        LOG.info(String
                .format("Found %s historic tasks for business process %s for date period %s - %s",
                        foundResults.size(), sID_BP,
                        DATE_TIME_FORMAT.format(dateAt),
                        DATE_TIME_FORMAT.format(dateTo)));

        if (pattern != null) {
            LOG.info("List of fields to retrieve: " + pattern);
        } else {
            LOG.info("Will retreive all fields from tasks");
        }
        LOG.info("Tasks to skip" + tasksIdToExclude);

        for (HistoricTaskInstance curTask : foundResults) {

            if (tasksIdToExclude.contains(curTask.getId())) {
                LOG.info("Skipping historic task " + curTask.getId()
                        + " from processing as it is already in the response");
                continue;
            }
            String currentRow = pattern;
            Map<String, Object> variables = curTask.getProcessVariables();
            LOG.info("Loaded historic variables for the task "
                    + curTask.getId() + "|" + variables);
            currentRow = replaceFormProperties(currentRow, variables);

            if (saFieldsCalc != null) {
                currentRow = addCalculatedFields(saFieldsCalc, curTask,
                        currentRow);
            }

            if (pattern != null) {
                currentRow = replaceReportFields(sDateCreateDF, curTask, currentRow);
                // replacing all the fields which were empty in the form with empty
                // string
                currentRow = currentRow.replaceAll("\\$\\{.*?\\}", "");
            }
            String[] values = currentRow.split(";");

            if (headers.length != values.length) {
                LOG.info("Size of header : " + headers.length + " Size of values array:" + values.length);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < headers.length; i++) {
                    sb.append(headers[i]);
                    sb.append(";");
                }
                LOG.info("headers:" + sb.toString());
                sb = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    sb.append(values[i]);
                    sb.append(";");
                }
                LOG.info("values:" + sb.toString());
            }
            Map<String, Object> currRow = new HashMap<String, Object>();
            for (int i = 0; i < headers.length; i++) {
                currRow.put(headers[i], values[i]);
            }
            csvLines.add(currRow);
        }
    }

    private String replaceFormProperties(String currentRow,
            Map<String, Object> data) {
        String res = currentRow;

        for (Map.Entry<String, Object> property : data.entrySet()) {
            LOG.info(String.format("Matching property %s:%s with fieldNames",
                    property.getKey(), property.getValue()));
            if (currentRow != null && res.contains("${" + property.getKey() + "}")) {
                LOG.info(String
                        .format("Found field with id %s in the pattern. Adding value to the result",
                                "${" + property.getKey() + "}"));
                if (property.getValue() != null) {
                    String sValue = property.getValue().toString();
                    LOG.info("sValue=" + sValue);
                    if (sValue != null) {
                        LOG.info(String.format("Replacing field with the value %s",
                                sValue));
                        res = res.replace("${" + property.getKey() + "}", sValue);
                    }
                }
            }
        }
        return res;
    }

    private void fillTheCSVMap(String sID_BP, Date dateAt, Date dateTo,
            List<Task> foundResults, SimpleDateFormat sDateCreateDF,
            List<Map<String, Object>> csvLines, String pattern, String saFieldsCalc, String[] headers) {
        if (CollectionUtils.isEmpty(foundResults)) {
            LOG.info(String
                    .format("No tasks found for business process %s for date period %s - %s",
                            sID_BP, DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
            return;
        }

        LOG.info(String
                .format("Found %s tasks for business process %s for date period %s - %s",
                        foundResults.size(), sID_BP,
                        DATE_TIME_FORMAT.format(dateAt),
                        DATE_TIME_FORMAT.format(dateTo)));

        if (pattern != null) {
            LOG.info("List of fields to retrieve: " + pattern);
        } else {
            LOG.info("Will retreive all fields from tasks");
        }

        for (Task curTask : foundResults) {

            String currentRow = pattern;
            LOG.trace("Process task - {}", curTask);
            TaskFormData data = formService.getTaskFormData(curTask.getId());
            currentRow = replaceFormProperties(currentRow, data);

            if (saFieldsCalc != null) {
                currentRow = addCalculatedFields(saFieldsCalc, curTask,
                        currentRow);
            }

            if (pattern != null) {
                // in case we need to pass all fields to the response - there are no report fields in the row
                currentRow = replaceReportFields(sDateCreateDF, curTask, currentRow);
                // replacing all the fields which were empty in the form with empty
                // string
                currentRow = currentRow.replaceAll("\\$\\{.*?\\}", "");
            }
            String[] values = currentRow.split(";");
            Map<String, Object> currRow = new HashMap<String, Object>();
            for (int i = 0; i < values.length; i++) {
                currRow.put(headers[i], values[i]);
            }
            csvLines.add(currRow);
        }
    }

    private String addCalculatedFields(String saFieldsCalc, TaskInfo curTask,
            String currentRow) {
        HistoricTaskInstance details = historyService
                .createHistoricTaskInstanceQuery().includeProcessVariables()
                .taskId(curTask.getId()).singleResult();
        LOG.info("Process variables of the task " + curTask.getId() + ":"
                + details.getProcessVariables());
        if (details != null && details.getProcessVariables() != null) {
            Set<String> headersExtra = new HashSet<String>();
            for (String key : details.getProcessVariables().keySet()) {
                if (!key.startsWith("sBody")) {
                    headersExtra.add(key);
                }
            }

            saFieldsCalc = StringUtils.substringAfter(saFieldsCalc, "\"");
            saFieldsCalc = StringUtils.substringBeforeLast(saFieldsCalc, "\"");
            for (String expression : saFieldsCalc.split(";")) {
                String variableName = StringUtils.substringBefore(
                        expression, "=");
                String condition = StringUtils.substringAfter(expression,
                        "=");
                LOG.info("Checking variable with name " + variableName
                        + " and condition " + condition
                        + " from expression:" + expression);
                try {
                    Object conditionResult = getObjectResultofCondition(
                            headersExtra, details, details, condition);
                    currentRow = currentRow + ";" + conditionResult;
                    LOG.info("Adding calculated field " + variableName + " with the value " + conditionResult);
                } catch (Exception e) {
                    LOG.error("Error occured while processing variable "
                            + variableName, e);
                }
            }
        }
        return currentRow;
    }

    private String replaceFormProperties(String currentRow, TaskFormData data) {
        String res = currentRow;

        for (FormProperty property : data.getFormProperties()) {
            LOG.info(String.format(
                    "Matching property %s:%s:%s with fieldNames", property
                            .getId(), property.getName(), property.getType()
                            .getName()));
            if (currentRow != null && res.contains("${" + property.getId() + "}")) {
                LOG.info(String
                        .format("Found field with id %s in the pattern. Adding value to the result",
                                "${" + property.getId() + "}"));
                String sValue = getPropertyValue(property);
                if (sValue != null) {
                    LOG.info(String.format("Replacing field with the value %s",
                            sValue));
                    res = res.replace("${" + property.getId() + "}", sValue);
                }
            }
        }
        return res;
    }

    private String getPropertyValue(FormProperty property) {
        String sValue = "";
        String sType = property.getType().getName();
        LOG.info("sType=" + sType);
        if ("enum".equalsIgnoreCase(sType)) {
            sValue = parseEnumProperty(property);
        } else {
            sValue = property.getValue();
        }
        LOG.info("sValue=" + sValue);
        return sValue;
    }

    private String replaceReportFields(SimpleDateFormat sDateCreateDF,
            Task curTask, String currentRow) {
        String res = currentRow;

        for (ReportField field : ReportField.values()) {
            if (res.contains(field.getPattern())) {
                res = field.replaceValue(res, curTask, sDateCreateDF);
            }
        }
        return res;
    }

    private String replaceReportFields(SimpleDateFormat sDateCreateDF,
            HistoricTaskInstance curTask, String currentRow) {
        String res = currentRow;

        for (ReportField field : ReportField.values()) {
            if (res.contains(field.getPattern())) {
                res = field.replaceValue(res, curTask, sDateCreateDF);
            }
        }
        return res;
    }

    private Date getBeginDate(Date date) {
        if (date == null) {
            return DateTime.now().minusDays(1).toDate();
        }
        return date;
    }

    private Date getEndDate(Date date) {
        if (date == null) {
            return DateTime.now().toDate();
        }
        return date;
    }

    private Charset getCharset(String sID_Codepage) {
        Charset charset;

        String codePage = sID_Codepage.replaceAll("-", "");
        try {
            if ("win1251".equalsIgnoreCase(codePage)
                    || "CL8MSWIN1251".equalsIgnoreCase(codePage)) {
                codePage = "CP1251"; // hack for alias
            }
            charset = Charset.forName(codePage);
            LOG.debug("use charset - {}", charset);
        } catch (IllegalArgumentException e) {
            LOG.error("Do not support charset - {}", codePage, e);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task for charset '" + codePage
                            + "' cannot be construct.", Task.class, e);
        }
        return charset;
    }

    private String getSeparator(String sID_BP, String nASCI_Spliter) {
        if (nASCI_Spliter == null) {
            return String.valueOf(Character
                    .toChars(DEFAULT_REPORT_FIELD_SPLITTER));
        }
        if (!StringUtils.isNumeric(nASCI_Spliter)) {
            LOG.error("ASCI code is not a number {}", nASCI_Spliter);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task with name '" + sID_BP
                            + "' not found. Wrong splitter.", Task.class);
        }
        return String
                .valueOf(Character.toChars(Integer.valueOf(nASCI_Spliter)));
    }

    /**
     * Returns business processes which belong to a specified user
     *
     * @param sLogin - login of user in user activity
     */
    @ApiOperation(value = "Получение списка бизнес процессов к которым у пользователя есть доступ", notes = noteGetBusinessProcessesForUser)
    @RequestMapping(value = "/getLoginBPs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String getBusinessProcessesForUser(
            @ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin)
            throws IOException {
        if (sLogin.isEmpty()) {
            LOG.error("Unable to found business processes for user with empty login");
            throw new ActivitiObjectNotFoundException(
                    "Unable to found business processes for user with empty login",
                    ProcessDefinition.class);
        }

        List<Map<String, String>> res = new LinkedList<Map<String, String>>();

        LOG.info(String.format(
                "Selecting business processes for the user with login: %s",
                sLogin));

        List<ProcessDefinition> processDefinitionsList = repositoryService
                .createProcessDefinitionQuery().active().latestVersion().list();
        if (CollectionUtils.isNotEmpty(processDefinitionsList)) {
            LOG.info(String.format("Found %d active process definitions",
                    processDefinitionsList.size()));

            List<Group> groups = identityService.createGroupQuery().groupMember(sLogin).list();
            if (groups != null && !groups.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Group group : groups) {
                    sb.append(group.getId());
                    sb.append(",");
                }
                LOG.info("Found " + groups.size() + "  groups for the user " + sLogin + ":" + sb.toString());
            }

            for (ProcessDefinition processDef : processDefinitionsList) {
                LOG.info("process definition id: " + processDef.getId());

                Set<String> candidateCroupsToCheck = new HashSet<String>();
                loadCandidateGroupsFromTasks(processDef, candidateCroupsToCheck);

                loadCandidateStarterGroup(processDef, candidateCroupsToCheck);

                findUsersGroups(groups, res, processDef, candidateCroupsToCheck);
            }
        } else {
            LOG.info("Have not found active process definitions.");
        }

        String jsonRes = JSONValue.toJSONString(res);
        LOG.info("Result" + jsonRes);
        return jsonRes;
    }

    protected void findUsersGroups(List<Group> groups,
            List<Map<String, String>> res, ProcessDefinition processDef,
            Set<String> candidateCroupsToCheck) {
        for (Group group : groups) {
            //LOG.info("Checking user group:" + group.getId());
            for (String groupFromProcess : candidateCroupsToCheck) {
                if (groupFromProcess.contains("${")) {
                    //LOG.info("Group from process contains pattern. Replacing it." + groupFromProcess);
                    groupFromProcess = groupFromProcess.replaceAll("\\$\\{?.*}", "(.*)");
                    //LOG.info("Result group to check: " + groupFromProcess);
                }
                if (group.getId().matches(groupFromProcess)) {
                    Map<String, String> process = new HashMap<String, String>();
                    process.put("sID", processDef.getKey());
                    process.put("sName", processDef.getName());
                    LOG.info(String.format("Added record to response %s",
                            process.toString()));
                    res.add(process);
                    return;
                }
            }
        }
    }

    protected void loadCandidateStarterGroup(ProcessDefinition processDef,
            Set<String> candidateCroupsToCheck) {
        List<IdentityLink> identityLinks = repositoryService
                .getIdentityLinksForProcessDefinition(processDef.getId());
        LOG.info(String.format("Found %d identity links for the process %s",
                identityLinks.size(), processDef.getKey()));
        for (IdentityLink identity : identityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identity.getType())) {
                String groupId = identity.getGroupId();
                candidateCroupsToCheck.add(groupId);
                LOG.info(String.format("Added candidate starter group %s ",
                        groupId));
            }
        }
    }

    protected void loadCandidateGroupsFromTasks(ProcessDefinition processDef,
            Set<String> candidateCroupsToCheck) {
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(processDef.getId());

        for (FlowElement flowElement : bpmnModel.getMainProcess()
                .getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<String> candidateGroups = userTask.getCandidateGroups();
                if (candidateGroups != null && !candidateGroups.isEmpty()) {
                    candidateCroupsToCheck.addAll(candidateGroups);
                    LOG.info(String.format(
                            "Added candidate groups %s from user task %s",
                            candidateGroups, userTask.getId()));
                }
            }
        }
    }

    private String getFileExtention(MultipartFile file) {

        String[] parts = file.getOriginalFilename().split("\\.");
        if (parts.length != 0) {
            return parts[parts.length - 1];
        }
        return "";
    }

    private String getFileExtention(String fileName) {

        String[] parts = fileName.split("\\.");
        if (parts.length != 0) {
            return parts[parts.length - 1];
        }
        return "";
    }

    @ApiOperation(value = "SendAttachmentsByMail", notes = noteSendAttachmentsByMail)
    @RequestMapping(value = "/test/sendAttachmentsByMail", method = RequestMethod.GET)
    @Transactional
    public void sendAttachmentsByMail(
            @RequestParam(value = "sMailTo", required = false) String sMailTo,
            @RequestParam(value = "nID_Task", required = false) String snID_Task,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "bHTML", required = false) boolean bHTML,
            @RequestParam(value = "naID_Attachment", required = false) String snaID_Attachment,
            @RequestParam(value = "bUnisender", required = false) Boolean bUnisender)
            throws IOException, MessagingException, EmailException {

        oMail._To("bvv4ik@gmail.com");
        oMail._Body(sBody == null ? "<a href=\"http:\\\\google.com\">Google</a> It's test Проверка ! ��� ��������!"
                : sBody);

        LOG.info("oMail.getHead()=" + oMail.getHead());
        LOG.info("oMail.getBody()=" + oMail.getBody());
        LOG.info("oMail.getAuthUser()=" + oMail.getAuthUser());
        LOG.info("oMail.getAuthPassword()=" + oMail.getAuthPassword());
        LOG.info("oMail.getFrom()=" + oMail.getFrom());
        LOG.info("oMail.getTo()=" + oMail.getTo());
        LOG.info("oMail.getHost()=" + oMail.getHost());
        LOG.info("oMail.getPort()=" + oMail.getPort());

        if (snaID_Attachment != null) {
            String[] ansID_Attachment = snaID_Attachment.split(",");
            for (String snID_Attachment : ansID_Attachment) {
                Attachment oAttachment = taskService
                        .getAttachment(snID_Attachment);
                String sFileName = oAttachment.getName();
                String sFileExt = oAttachment.getType().split(";")[0];
                String sDescription = oAttachment.getDescription();
                LOG.info("oAttachment.getId()=" + oAttachment.getId()
                        + ", sFileName=" + sFileName + ", sFileExt=" + sFileExt
                        + ", sDescription=" + sDescription);
                InputStream oInputStream = taskService
                        .getAttachmentContent(oAttachment.getId());
                DataSource oDataSource = new ByteArrayDataSource(oInputStream,
                        sFileExt);

                oMail._Attach(oDataSource, sFileName + "." + sFileExt,
                        sDescription);
            }
        }

        if (bUnisender != null && bUnisender) {
            oMail.sendWithUniSender();
        } else {
            oMail.send();
        }
    }

    /**
     * @param sPathFile    полный путь к файлу, например: folder/file.html.
     * @param sContentType тип контента (опционально, по умолчанию обычный текст: text/plain)
     */
    @ApiOperation(value = "Работа с файлами-шаблонами", notes = noteGetPatternFile)
    @RequestMapping(value = "/getPatternFile", method = RequestMethod.GET)
    public void getPatternFile(
            @ApiParam(value = "полный путь к файлу", required = true) @RequestParam(value = "sPathFile") String sPathFile,
            @ApiParam(value = "тип контента", required = false) @RequestParam(value = "sContentType", required = false) String sContentType,
            HttpServletResponse response) throws ActivitiRestException {

        try {
            String contentType = sContentType == null ? Util.PATTERN_DEFAULT_CONTENT_TYPE
                    : sContentType;
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.toString());
            byte[] resultObj = Util.getPatternFile(sPathFile);
            response.getOutputStream().write(resultObj);
        } catch (IllegalArgumentException | IOException e) {
            ActivitiRestException newErr = new ActivitiRestException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        } catch (Exception e) {
            ActivitiRestException newErr = new ActivitiRestException(
                    "SYSTEM_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        }
    }

    /**
     * issue 808. сервис ЗАПРОСА полей, требующих уточнения, c отсылкой
     * уведомления гражданину
     *
     * @param nID_Protected - номер-�?Д заявки (защищенный)
     * @param saField       -- строка-массива полей (например:
     *                      "[{'id':'sFamily','type':'string','value':'Белявский'},{'id':'nAge','type':'long'}]"
     *                      )
     * @param sMail         -- строка электронного адреса гражданина
     * @param sHead         -- строка заголовка письма //опциональный (если не задан, то
     *                      "Необходимо уточнить данные")
     * @param sBody         -- строка тела письма //опциональный (если не задан, то
     *                      пустота)
     * @throws ActivitiRestException
     * @throws CRCInvalidException
     */
    @ApiOperation(value = "Вызов сервиса уточнения полей формы", notes = noteSetTaskQuestions)
    @RequestMapping(value = "/setTaskQuestions", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskQuestions(
            @ApiParam(value = "строка-ид заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "номер-ИД заявки", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @ApiParam(value = "ид заявки", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "ид сервера", required = false) @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @ApiParam(value = "строка-массива полей", required = true) @RequestParam(value = "saField") String saField,
            @ApiParam(value = "строка электронного адреса гражданина", required = true) @RequestParam(value = "sMail") String sMail,
            @ApiParam(value = "строка заголовка письма", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "sBody", required = false) @RequestParam(value = "sBody", required = false) String sBody)
            throws ActivitiRestException, CRCInvalidException {

        sHead = sHead == null ? "Необхідно уточнити дані" : sHead;
        sBody = EGovStringUtils.toStringWithBlankIfNull(sBody);
        String sToken = SecurityUtils.generateSecret();
        try {
            LOG.info(
                    "try to update historyEvent_service by sID_Order=%s, nID_Protected=%s, nID_Process=%s and nID_Server=%s",
                    sID_Order, nID_Protected, nID_Process, nID_Server);
            String historyEventServiceJson = updateHistoryEvent_Service(
                    sID_Order, nID_Protected, nID_Process, nID_Server, saField,
                    sHead, sBody, sToken, "Запит на уточнення даних");
            LOG.info("....ok! successfully update historyEvent_service! event = "
                    + historyEventServiceJson);
            ActivitiProcessId activitiProcessId = new ActivitiProcessId(
                    sID_Order, nID_Protected, nID_Process, nID_Server);
            sendEmail(
                    sHead,
                    createEmailBody(activitiProcessId.nID_Protected, saField,
                            sBody, sToken), sMail);// todo ask about sID_order
            // (889)
            // Long processId = getProcessId(sID_Order, nID_Protected,
            // nID_Process);
            setInfo_ToActiviti("" + activitiProcessId.nID_Process, saField,
                    sBody);
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    "error during setTaskQuestions: " + e.getMessage(), e,
                    HttpStatus.FORBIDDEN);
        }
    }

    private String createEmailBody(Long nID_Protected, String soData,
            String sBody, String sToken) throws UnsupportedEncodingException {
        StringBuilder emailBody = new StringBuilder(sBody);
        emailBody.append("<br/>").append(createTable(soData)).append("<br/>");
        String link = (new StringBuilder(generalConfig.sHostCentral())
                .append("/order/search?nID=").append(nID_Protected)
                .append("&sToken=").append(sToken)).toString();
        emailBody.append(link).append("<br/>");
        return emailBody.toString();
    }

    // private Long getProcessId(String sID_Order, Long nID_Protected, Long
    // nID_Process) {
    // Long result = null;
    // if (nID_Process != null) {
    // result = nID_Process;
    // } else if (nID_Protected != null) {
    // result = AlgorithmLuna.getOriginalNumber(nID_Protected);
    // } else if (sID_Order != null && !sID_Order.isEmpty()) {
    // Long protectedId;
    // if (sID_Order.contains("-")) {
    // int dash_position = sID_Order.indexOf("-");
    // protectedId = Long.valueOf(sID_Order.substring(dash_position + 1));
    // } else {
    // protectedId = Long.valueOf(sID_Order);
    // }
    // result = AlgorithmLuna.getOriginalNumber(protectedId);
    // }
    // return result;
    // }
    private void sendEmail(String sHead, String sBody, String recipient)
            throws EmailException {
        oMail.reset();
        oMail._To(recipient)._Head(sHead)._Body(sBody);
        oMail.send();
    }

    private String createTable(String soData)
            throws UnsupportedEncodingException {
        if (soData == null || "[]".equals(soData) || "".equals(soData)) {
            return "";
        }
        StringBuilder tableStr = new StringBuilder(
                "<table><tr><th>Поле</th><th>Тип </th><th> Поточне значення</th></tr>");
        JSONObject jsnobject = new JSONObject("{ soData:" + soData + "}");
        JSONArray jsonArray = jsnobject.getJSONArray("soData");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);
            tableStr.append("<tr><td>")
                    .append(record.opt("id") != null ? record.get("id") : "?")
                    .append("</td><td>")
                    .append(record.opt("type") != null ? record.get("type")
                            .toString() : "??")
                    .append("</td><td>")
                    .append(record.opt("value") != null ? record.get("value")
                            .toString() : "").append("</td></tr>");
        }
        tableStr.append("</table>");
        return tableStr.toString();
    }

    @ApiOperation(value = "Вызов сервиса ответа по полям требующим уточнения", notes = noteSetTaskAnswer_Region)
    @RequestMapping(value = "/setTaskAnswer", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskAnswer_Region(
            @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @RequestParam(value = "saField") String saField,
            @RequestParam(value = "sBody", required = false) String sBody)
            throws ActivitiRestException {

        try {
            /*LOG.info(
                    "try to find history event_service by sID_Order=%s, nID_Protected-%s, nID_Process=%s and nID_Server=%s",
					sID_Order, nID_Protected, nID_Process, nID_Server);*/

                    
                        /*String historyEvent = historyEventService.getHistoryEvent(
					sID_Order, nID_Protected, nID_Process, nID_Server);
			LOG.info("....ok! successfully get historyEvent_service! event="
					+ historyEvent);
			JSONObject fieldsJson = new JSONObject(historyEvent);
			String processInstanceID = fieldsJson.get("nID_Task").toString();
			sHead = sHead != null ? sHead : "На заявку "
					+ fieldsJson.getString("sID_Order")
					+ " дана відповідь громаданином";
			if (fieldsJson.has("sToken")) {
				String tasksToken = fieldsJson.getString("sToken");
				if (tasksToken.isEmpty() || !tasksToken.equals(sToken)) {
					throw new ActivitiRestException(
							ActivitiExceptionController.BUSINESS_ERROR_CODE,
							"Token is wrong");
				}
			} else {
				throw new ActivitiRestException(
						ActivitiExceptionController.BUSINESS_ERROR_CODE,
						"Token is absent");
			}*/

            String processInstanceID = "" + nID_Process; //  "11111";//fieldsJson.get("nID_Task").toString();

            JSONObject jsnobject = new JSONObject("{ soData:" + saField + "}");
            JSONArray jsonArray = jsnobject.getJSONArray("soData");
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceID).list();

            runtimeService.setVariable(processInstanceID, "sAnswer", sBody);
            LOG.info("Added variable sAnswer to the process "
                    + processInstanceID);

            LOG.info("Found " + tasks.size() + " tasks by nID_Protected...");
            for (Task task : tasks) {
                LOG.info("task;" + task.getName() + "|" + task.getDescription()
                        + "|" + task.getId());
                TaskFormData data = formService.getTaskFormData(task.getId());
                Map<String, String> newProperties = new HashMap<String, String>();
                for (FormProperty property : data.getFormProperties()) {
                    if (property.isWritable()) {
                        newProperties
                                .put(property.getId(), property.getValue());
                    }
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject record = jsonArray.getJSONObject(i);
                    newProperties.put((String) record.get("id"),
                            (String) record.get("value"));
                    LOG.info("Set variable " + record.get("id")
                            + " with value " + record.get("value"));
                }
                LOG.info("Updating form data for the task " + task.getId()
                        + "|" + newProperties);
                formService.saveFormData(task.getId(), newProperties);
            }

			/*LOG.info(
					"try to find history event_service by sID_Order=%s, nID_Protected-%s and nID_Server=%s",
					sID_Order, nID_Protected, nID_Server);
                        
			historyEvent = updateHistoryEvent_Service(sID_Order, nID_Protected,
					nID_Process, nID_Server, saField, sHead, null, null,
					"Відповідь на запит по уточненню даних");
			LOG.info("....ok! successfully get historyEvent_service! event="
					+ historyEvent);*/
            LOG.info("....ok!");
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e, HttpStatus.FORBIDDEN);
        }
    }

    public String updateHistoryEvent_Service(String sID_Order,
            Long nID_Protected, Long nID_Process, Integer nID_Server,
            String saField, String sHead, String sBody, String sToken,
            String sID_Status) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order);
        params.put("nID_Protected", nID_Protected != null ? "" + nID_Protected
                : null);
        String sID_Process = nID_Process != null ? "" + nID_Process : null;
        params.put("nID_Process", sID_Process);
        params.put("nID_Server", nID_Server != null ? "" + nID_Server : null);
        params.put("soData", saField);
        params.put("sHead", sHead);
        params.put("sBody", sBody);
        params.put("sToken", sToken);
        params.put("sID_Status", sID_Status);
        return historyEventService.updateHistoryEvent(sID_Process, sID_Status,
                true, params);
    }

    private void setInfo_ToActiviti(String snID_Process, String saField,
            String sBody) {
        try {
            LOG.info(String.format(
                    "try to set saField=%s and sBody=%s to snID_Process=%s",
                    saField, sBody, snID_Process));
            runtimeService
                    .setVariable(snID_Process, "saFieldQuestion", saField);
            runtimeService.setVariable(snID_Process, "sQuestion", sBody);
            LOG.info(String.format(
                    "completed set saField=%s and sBody=%s to snID_Process=%s",
                    saField, sBody, snID_Process));
        } catch (Exception ex) {
            LOG.error("error during set variables to Activiti!", ex);
        }
    }

    @ApiOperation(value = "SendProccessToGRES", notes = noteSendProccessToGRES)
    @RequestMapping(value = "/sendProccessToGRES", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> sendProccessToGRES(@RequestParam(value = "nID_Task") Long nID_Task)
            throws ActivitiRestException {
        Map<String, Object> res = new HashMap<String, Object>();

        Task task = taskService.createTaskQuery().taskId(nID_Task.toString()).singleResult();

        LOG.info("Found task with ID:" + nID_Task + " process inctanse ID:" + task.getProcessInstanceId());

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(
                task.getProcessInstanceId()).singleResult();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult();

        FormData startFormData = formService.getStartFormData(processInstance.getProcessDefinitionId());
        FormData taskFormData = formService.getTaskFormData(task.getId());

        res.put("nID_Task", nID_Task.toString());
        res.put("nID_Proccess", task.getProcessInstanceId());
        res.put("sProcessName", processDefinition.getName());
        res.put("sProcessDefinitionKey", processDefinition.getKey());

        Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());

        Map<String, String> startFormValues = new HashMap<String, String>();
        Map<String, String> taskFormValues = new HashMap<String, String>();
        if (startFormData != null) {
            loadFormPropertiesToMap(startFormData, variables, startFormValues);
        }
        if (taskFormData != null) {
            loadFormPropertiesToMap(taskFormData, variables, taskFormValues);
        }

        res.put("startFormData", startFormValues);
        res.put("taskFormData", taskFormValues);

        return res;
    }

    @ApiOperation(value = "GetTaskFormData", notes = noteGetTaskFormData)
    @RequestMapping(value = "/getTaskFormData", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, String> getTaskFormData(@RequestParam(value = "nID_Task") Long nID_Task) throws ActivitiRestException {
        Map<String, String> result = new HashMap<String, String>();
        Task task = taskService.createTaskQuery().taskId(nID_Task.toString()).singleResult();
        LOG.info("Found task with ID:" + nID_Task + " process inctanse ID:" + task.getProcessInstanceId());
        FormData taskFormData = formService.getTaskFormData(task.getId());
        Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
        if (taskFormData != null) {
            loadFormPropertiesToMap(taskFormData, variables, result);
        }
        return result;
    }
    
    @ApiOperation(value = "verifyContactEmail", notes = noteVerifyContactEmail)
    @RequestMapping(value = "/verifyContactEmail", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, String> verifyContactEmail(
    		@ApiParam(value = "строка-запроса (электронный адрес)", required = true) @RequestParam(value = "sQuestion") String sQuestion,
    		@ApiParam(value = "строка-ответа (код )", required = false) 
    		@RequestParam(value = "sAnswer", required=false) String sAnswer) throws ActivitiRestException, EmailException, RecordInmemoryException {
        Map<String, String> res = new HashMap<String, String>();
    	try {
	    	InternetAddress emailAddr = new InternetAddress(sQuestion);
	        emailAddr.validate();
	        if (sAnswer == null || sAnswer.isEmpty()){
	        	String saToMail = sQuestion;
	            String sHead = "Верификация адреса";
	            String sToken = RandomStringUtils.randomAlphanumeric(15);
	            String sBody = "Код подтверждения: " + sToken;
	            oMail.reset();
	            oMail._To(saToMail)
	                 ._Head(sHead)
	                 ._Body(sBody);
	            oMail.send();
	            
	            oBytesDataInmemoryStorage.putString(saToMail, sToken);
	            LOG.info("Send email with token " + sToken + " to the address:" + saToMail + " and saved token");
	            res.put("bVerified", "true");
	        } else {
	            String sToken = oBytesDataInmemoryStorage.getString(sQuestion);
	            LOG.info("Got token from Redis:" + sToken);
	            if (sAnswer.equals(sToken)){
		            res.put("bVerified", "true");	            	
	            } else {
		            res.put("bVerified", "false");
	            }
	        }
    	} catch (AddressException ex) {
    		LOG.warn("Email address " + sQuestion + " is not correct");
            res.put("bVerified", "false");
    	}
        return res;
    }

    protected void loadFormPropertiesToMap(FormData formData,
            Map<String, Object> variables, Map<String, String> formValues) {
        List<FormProperty> aFormProperty = formData.getFormProperties();
        if (!aFormProperty.isEmpty()) {
            for (FormProperty oFormProperty : aFormProperty) {
                String sType = oFormProperty.getType().getName();
                if (variables.containsKey(oFormProperty.getId())) {
                    if ("enum".equals(sType)) {
                        Object variable = variables.get(oFormProperty.getId());
                        if (variable != null) {
                            String sID_Enum = variable.toString();
                            LOG.info("execution.getVariable()(sID_Enum)="
                                    + sID_Enum);
                            String sValue = parseEnumProperty(oFormProperty,
                                    sID_Enum);
                            formValues.put(oFormProperty.getId(), sValue);
                        }
                    } else {
                        formValues.put(oFormProperty.getId(), variables.get(oFormProperty.getId()) != null ?
                                String.valueOf(variables.get(oFormProperty.getId())) : null);
                    }
                }
            }
        }
    }

    class ActivitiProcessId {

        private String sID_Order;
        private Long nID_Protected;
        private Long nID_Process;
        private Integer nID_Server;

        ActivitiProcessId(String sID_Order, Long nID_Protected,
                Long nID_Process, Integer nID_Server)
                throws ActivitiRestException {
            if (sID_Order != null) {
                this.sID_Order = sID_Order;
                int dash_position = sID_Order.indexOf("-");
                this.nID_Server = dash_position != -1 ? Integer
                        .parseInt(sID_Order.substring(0, dash_position)) : 0;
                this.nID_Protected = Long.valueOf(sID_Order
                        .substring(dash_position + 1));
                this.nID_Process = AlgorithmLuna
                        .getOriginalNumber(this.nID_Protected);
            } else if (nID_Process != null) {
                this.nID_Process = nID_Process;
                this.nID_Protected = AlgorithmLuna
                        .getProtectedNumber(nID_Process);
                this.nID_Server = nID_Server != null ? nID_Server : 0;
                this.sID_Order = "" + this.nID_Server + "-"
                        + this.nID_Protected;

            } else if (nID_Protected != null) {
                this.nID_Protected = nID_Protected;
                this.nID_Process = AlgorithmLuna
                        .getOriginalNumber(this.nID_Protected);
                this.nID_Server = nID_Server != null ? nID_Server : 0;
                this.sID_Order = "" + this.nID_Server + "-"
                        + this.nID_Protected;
            } else {
                throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "incorrect input data!! must be: [sID_Order] OR [nID_Protected + nID_Server (optional)] OR [nID_Process + nID_Server(optional)]",
                        HttpStatus.FORBIDDEN);
            }
        }
    }

}
