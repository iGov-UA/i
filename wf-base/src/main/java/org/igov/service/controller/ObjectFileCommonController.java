package org.igov.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.fs.FileSystemData;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.task.core.AttachmentCover;
import org.igov.model.action.task.core.BuilderAttachModelCover;
import org.igov.model.action.task.core.entity.AttachmentEntityI;
import org.igov.service.business.access.BankIDUtils;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.action.task.systemtask.ProcedQueueTickets;
import org.igov.service.business.object.ObjectFileService;
import org.igov.service.conf.AttachmetService;
import org.igov.service.controller.interceptor.ActionProcessCountUtils;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.FileServiceIOException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.VariableMultipartFile;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.igov.io.fs.FileSystemData.getFileData_Pattern;
import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import static org.igov.util.Tool.sTextTranslit;
 
//import org.igov.service.business.access.BankIDConfig;

/**
 * @author BW
 */
@Controller
@Api(tags = {"ObjectFileCommonController -- Обьекты файлов общие"})
@RequestMapping(value = "/object/file")
public class ObjectFileCommonController {

    public static final String PATTERN_DEFAULT_CONTENT_TYPE = "text/plain";
    private static final Logger LOG = LoggerFactory.getLogger(ObjectFileCommonController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService oRuntimeService;

    @Autowired
    private GeneralConfig generalConfig;
    //@Autowired
    //private BankIDConfig bankIDConfig;

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    private ActionTaskService oActionTaskService;

    @Autowired
    private ObjectFileService oObjectFileService;

    @Autowired
    protected AttachmetService attachmetService;

    @ApiOperation(value = "PutAttachmentsToRedis", notes = "#####  Укладываем в редис multipartFileToByteArray\n")
    @RequestMapping(value = "/upload_file_to_redis", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody
    String putAttachmentsToRedis(
            @RequestParam(required = true, value = "file") MultipartFile file) //Название не менять! Не будет работать прикрепление файла через проксю!!!
            throws FileServiceIOException {
        try {
            LOG.info("file.getContentType: " + file.getContentType());
            String key = oBytesDataInmemoryStorage.putBytes(AbstractModelTask
                    .multipartFileToByteArray(file, file.getOriginalFilename())
                    .toByteArray());
            return key;
        } catch (RecordInmemoryException | IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "GetAttachmentsFromRedis", notes = "#####  ObjectFileCommonController: описания нет\n")
    @RequestMapping(value = "/download_file_from_redis", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentsFromRedis(
            @RequestParam("key") String key) throws FileServiceIOException {
        byte[] upload = null;
        try {
            upload = oBytesDataInmemoryStorage.getBytes(key);
        } catch (RecordInmemoryException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }
        return upload;
    }

    @ApiOperation(value = "GetAttachmentsFromRedisBytes", notes = "#####  ObjectFileCommonController: описания нет\n")
    @RequestMapping(value = "/download_file_from_redis_bytes", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentsFromRedisBytes(
            @RequestParam("key") String key) throws FileServiceIOException {
        LOG.info("download_file_from_redis_bytes is starting now...");
        byte[] upload = null;
        try {
            byte[] aByteFile = oBytesDataInmemoryStorage.getBytes(key);
            ByteArrayMultipartFile oByteArrayMultipartFile = null;
            oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aByteFile);

            if (oByteArrayMultipartFile != null) {

                upload = oByteArrayMultipartFile.getBytes();

            } else {
                // LOG.error("[getAttachmentsFromRedisBytes]oByteArrayMultipartFile==null! aByteFile="
                // + aByteFile.
                // .toString());
                // Unreachable code?
                LOG.error("[getAttachmentsFromRedisBytes]oByteArrayMultipartFile==null! key={}", key);
            }

        } catch (RecordInmemoryException e) {
            LOG.warn("Error: {}", e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        } catch (ClassNotFoundException | IOException e) {
            LOG.error("Error: {}", e.getMessage(), e);
            throw new ActivitiException(e.getMessage(), e);
        }

        return upload;
    }

    @ApiOperation(value = "checkProcessAttach", notes
            = "##### проверка ЭЦП по новому концепту")
    @RequestMapping(value = "/checkProcessAttach", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String checkProcessAttach(
            @ApiParam(value = "cтрока-ИД типа хранилища Redis или Mongo", required = false) @RequestParam(value = "sID_StorageType", required = false, defaultValue = "Mongo") String sID_StorageType,
            @ApiParam(value = "название и расширение файла", required = false) @RequestParam(value = "sFileNameAndExt", required = false) String sFileNameAndExt,
            @ApiParam(value = "ид процесса", required = false) @RequestParam(value = "sID_Process", required = false) String sID_Process,
            @ApiParam(value = "ид поля", required = false) @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @ApiParam(value = "ключ в базе данных", required = false) @RequestParam(value = "sKey", required = false) String sKey) throws IOException, ParseException, RecordInmemoryException, ClassNotFoundException, CRCInvalidException, RecordNotFoundException {

        MultipartFile multipartFile = attachmetService.getAttachment(sID_Process, sID_Field, sKey, sID_StorageType);

        if (sFileNameAndExt == null) {
            sFileNameAndExt = multipartFile.getOriginalFilename();
        }

        String soSignData = BankIDUtils.checkECP(generalConfig, multipartFile.getBytes(), sFileNameAndExt);

        return soSignData;
    }

    @ApiOperation(value = "Проверка ЭЦП на файле хранящемся в Redis", notes = "#####  Примеры:\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/check_file_from_redis_sign?sID_File_Redis=d2993755-70e5-409e-85e5-46ba8ce98e1d\n\n"
            + "Ответ json описывающий ЭЦП:\n\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "Ответ для несуществующего ключа (sID_File_Redis):\n"
            + "\n```json\n"
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"File with sID_File_Redis 'd2993755-70e5-409e-85e5-46ba8ce98e1e' not found.\"}\n\n"
            + "\n```\n"
            + "Ответ для файла который не имеет наложеной ЭЦП:\n\n"
            + "\n```json\n"
            + "{}\n"
            + "\n```\n")
    @RequestMapping(value = "/check_file_from_redis_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String checkAttachmentsFromRedisSign(
            @ApiParam(value = "строка-ключ по которому можно получить файл из хранилища Redis", required = true) @RequestParam("sID_File_Redis") String sID_File_Redis)
            throws FileServiceIOException {
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
                oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aByteFile);
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
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }

        String soSignData = BankIDUtils.checkECP(generalConfig, upload, fileName);

        return soSignData;
    }

    @ApiOperation(value = "Загрузки прикрепленного к заявке файла из постоянной базы", notes = "##### Пример:\n "
            + "https://alpha.test.igov.org.ua/wf/service/object/file/download_file_from_db?taskId=82596&attachmentId=6726532&nFile=7\n")
    @RequestMapping(value = "/download_file_from_db", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentFromDb(
            @ApiParam(value = "строка-ИД задачи", required = true) @RequestParam(value = "taskId") String taskId,
            @ApiParam(value = "строка-ID прикрепленного файла", required = false) @RequestParam(required = false, value = "attachmentId") String attachmentId,
            @ApiParam(value = "порядковый номер прикрепленного файла", required = false) @RequestParam(required = false, value = "nFile") Integer nFile,
            HttpServletResponse httpResponse) throws IOException {

        VariableMultipartFile multipartFile = oObjectFileService.download_file_from_db(taskId, attachmentId, nFile);

        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + multipartFile.getOriginalFilename());
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        return multipartFile.getBytes();
    }

    @ApiOperation(value = "Загрузки прикрепленного к заявке файла из постоянной базы", notes = "##### Пример:\n "
            + "https://alpha.test.igov.org.ua/wf/service/object/file/download_file_from_storage_static?sId=111111&sFileName=111.txt&sType=text\n")
    @RequestMapping(value = "/download_file_from_storage_static", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentByID(
            @ApiParam(value = "sId", required = true) @RequestParam(required = true, value = "sId") String nId,
            @ApiParam(value = "sFileName", required = true) @RequestParam(value = "sFileName") String sFileName,
            @ApiParam(value = "sType", required = true) @RequestParam(value = "sType") String sType,
            HttpServletResponse httpResponse) throws IOException {
        InputStream attachmentStream = ((org.igov.service.conf.TaskServiceImpl) taskService)
                .getAttachmentContentByMongoID(nId);
        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException("Attachment with ID '"
                    + nId + "' doesn't have content associated with it.",
                    Attachment.class);
        }

        int nTo = sFileName.lastIndexOf(".");
        if (nTo >= 0) {
            sFileName = "attach_" + nId + "."
                    + sFileName.substring(nTo + 1);
        }

        // Вычитывем из потока массив байтов контента и помещаем параметры
        // контента в header
        VariableMultipartFile multipartFile = new VariableMultipartFile(
                attachmentStream, sFileName,
                sFileName, sType);
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + sFileName);
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        return multipartFile.getBytes();
    }

    @ApiOperation(value = "Проверка ЭЦП на атачменте(файл) таски Activiti", notes = "##### Примеры:\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/check_attachment_sign?nID_Task=7315073&nID_Attach=7315075\n"
            + "Ответ:\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "\nОтвет для несуществующей таски (nID_Task):\n"
            + "\n```json\n"
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"ProcessInstanceId for taskId '7315070' not found.\"}\n"
            + "\n```\n"
            + "\nОтвет для несуществующего атачмента (nID_Attach):\n"
            + "\n```json\n"
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Attachment for taskId '7315073' not found.\"}\n"
            + "\n```\n"
            + "\nОтвет для атачмента который не имеет наложеной ЭЦП:\n"
            + "\n```json\n"
            + "{}\n"
            + "\n```\n")
    @RequestMapping(value = "/check_attachment_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String checkAttachSign(
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam(value = "nID_Task") String taskId,
            @ApiParam(value = "строка-ИД атачмента приложеного к таске", required = true) @RequestParam(value = "nID_Attach") String attachmentId)
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

        Attachment attachmentRequested = oActionTaskService.getAttachment(attachmentId, null, processInstanceId);

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

        String soSignData = BankIDUtils.checkECP(generalConfig, content, attachmentRequested.getName());

        return soSignData;
    }

    @ApiOperation(value = "Сервис для получения Attachment из execution", notes = "")
    @RequestMapping(value = "/download_file_from_db_execution", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentFromDbExecution(
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam("taskId") String taskId,
            HttpServletResponse httpResponse) throws IOException {

        String processInstanceId = oActionTaskService.getProcessInstanceIDByTaskID(taskId);
        HistoricProcessInstance processInstance = oActionTaskService.getProcessInstancyByID(processInstanceId);

        // получаем коллекцию переменных процеса и прикрепленный файл
        Map<String, Object> processVariables = processInstance
                .getProcessVariables();
        BuilderAttachModelCover attachModel = null;

        if (processVariables != null) {
            List<BuilderAttachModelCover> builderAttachModelList = (List) processVariables
                    .get(ProcedQueueTickets.BUILDER_ATACH_MODEL_LIST);

            if (builderAttachModelList != null) {
                attachModel = builderAttachModelList.get(0);
            }
        }

        if (attachModel == null) {
            throw new ActivitiObjectNotFoundException(
                    String.format("ProcessVariable '{%s}' for processInstanceId '{%s}' not found.",
                            ProcedQueueTickets.BUILDER_ATACH_MODEL_LIST,
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

    @ApiOperation(value = "Аплоад(upload) и прикрепление файла в виде атачмента к таске Activiti", notes
            = "#####  Примеры: \n"
            + "http://test.igov.org.ua/wf/service/object/file/upload_file_as_attachment?taskId=68&description=ololo\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "Ответ с ошибкой:\n\n"
            + "\n```json\n"
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Cannot find task with id 384\"}\n"
            + "\n```\n")
    @RequestMapping(value = "/upload_file_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    AttachmentEntityI putAttachmentsToExecution(//ResponseEntity
            @ApiParam(value = "ИД-номер таски", required = true) @RequestParam(value = "taskId") String taskId,
            @ApiParam(value = "файл html. в html это имя элемента input типа file - <input name=\"file\" type=\"file\" />. в HTTP заголовках - Content-Disposition: form-data; name=\"file\" ...", required = true) @RequestParam("file") MultipartFile file,//Название не менять! Не будет работать прикрепление файла через проксю!!!
            @ApiParam(value = "строка-описание", required = true) @RequestParam(value = "description") String description,
            @ApiParam(value = "ИД поля формы, к которому загружается файл", required = false) @RequestParam(value = "sID_Field") String sID_Field)
            throws IOException {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (!tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee()
                    : "kermit";
            LOG.debug("(processInstanceId={}, taskId={}, assignee={})", processInstanceId, taskId, assignee);
        } else {
            LOG.error("There is no tasks at all!");
        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = file.getOriginalFilename();
        LOG.debug("(sFilename={})", file.getOriginalFilename());
        sFilename = sTextTranslit(sFilename);
        LOG.debug("(FileExtention:{}, fileContentType:{}, fileName:{}) ",
                oActionTaskService.getFileExtention(file), file.getContentType(), sFilename);
        LOG.debug("description: {}", description);

        Attachment oAttachment = taskService.createAttachment(file.getContentType() + ";" + oActionTaskService.getFileExtention(file), taskId,
                processInstanceId, sFilename,// file.getOriginalFilename()
                description, file.getInputStream());
        if (oAttachment != null && sID_Field != null && !"".equals(sID_Field.trim())) {
            //oRuntimeService.setVariable(processInstanceId, sID_Field, oAttachment.getId());
            LOG.debug("setVariable: processInstanceId = {}, sID_Field = {}, attachmentId = {}", processInstanceId, sID_Field, oAttachment.getId());
        }

        AttachmentCover oAttachmentCover = new AttachmentCover();
        return oAttachmentCover.apply(oAttachment);
    }

    @ApiOperation(value = "Аплоад(upload) и прикрепление текстового файла в виде атачмента к таске Activiti", notes
            = "#####Пример: "
            + "http://localhost:8080/wf/service/object/file/upload_content_as_attachment?nTaskId=24&sDescription=someText&sFileName=FlyWithMe.html\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "\nОтвет с ошибкой:\n"
            + "\n```json\n"
            + "{\"code\":\"SYSTEM_ERR\",\"message\":\"Cannot find task with id 384\"}\n"
            + "\n```\n")
    @RequestMapping(value = "/upload_content_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    AttachmentEntityI putTextAttachmentsToExecution(
            @ApiParam(value = "строка-Логин пользователя", required = true) @RequestParam(value = "nTaskId") String taskId,
            @ApiParam(value = "строка-MIME тип отправляемого файла (по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "строка-описание", required = true) @RequestParam(value = "sDescription") String description,
            @RequestParam(value = "sFileName") String sFileName,
            @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @RequestBody String sData) {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (!tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee()
                    : "kermit";
            LOG.debug("processInstanceId:{}, taskId:{}, assignee:{} ", processInstanceId, taskId, assignee);
        } else {
            LOG.error("There is no tasks at all!");

        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = sFileName;
        LOG.debug("sFilename={}", sFileName);
        sFilename = sTextTranslit(sFilename);
        LOG.debug("FileExtention: {}, fileContentType:{}, fileName:{}",
                oActionTaskService.getFileExtention(sFileName), sContentType, sFilename);
        LOG.debug("description: {}", description);

        Attachment attachment = taskService.createAttachment(sContentType + ";"
                + oActionTaskService.getFileExtention(sFileName), taskId, processInstanceId,
                sFilename, description,
                new ByteArrayInputStream(sData.getBytes(Charsets.UTF_8)));
        if (attachment != null && sID_Field != null && !"".equals(sID_Field.trim())) {
            //oRuntimeService.setVariable(processInstanceId, sID_Field, attachment.getId());
            LOG.debug("setVariable: processInstanceId = {}, sID_Field = {}, attachmentId = {}", processInstanceId, sID_Field, attachment.getId());
        }
        AttachmentCover oAttachmentCover = new AttachmentCover();

        return oAttachmentCover.apply(attachment);
    }

    @ApiOperation(value = "Работа с файлами-шаблонами", notes
            = "#####  возвращает содержимое указанного файла с указанным типом контента (если он задан).\n"
            + "Если указанный путь неверен и файл не найден -- вернется соответствующая ошибка.\n\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/getPatternFile?sPathFile=print//subsidy_zayava.html\n\n"
            + "ответ: вернется текст исходного кода файла-шаблона\n\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/getPatternFile?sPathFile=print//subsidy_zayava.html&sContentType=text/html\n\n"
            + "ответ: файл-шаблон будет отображаться в виде html-страницы")
    @RequestMapping(value = "/getPatternFile", method = RequestMethod.GET)
    public void getPatternFile(
            @ApiParam(value = "строка-полный путь к файлу", required = true) @RequestParam(value = "sPathFile") String sPathFile,
            @ApiParam(value = "строка-тип контента", required = false) @RequestParam(value = "sContentType", required = false) String sContentType,
            HttpServletResponse response) throws CommonServiceException {

        try {
            String contentType = sContentType == null ? PATTERN_DEFAULT_CONTENT_TYPE
                    : sContentType;
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.toString());
            LOG.info(String.format("Getting FileData pattern [sPathFile = %s]", sPathFile));
            byte[] resultObj = getFileData_Pattern(sPathFile);
            response.getOutputStream().write(resultObj);
        } catch (IllegalArgumentException | IOException e) {
            CommonServiceException newErr = new CommonServiceException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        } catch (Exception e) {
            CommonServiceException newErr = new CommonServiceException(
                    "SYSTEM_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        }
    }

    @ApiOperation(value = "moveAttachsToMongo", notes
            = "#####  ObjectFileCommonController: Перенос атачментов задач активити в mongo DB  \n"
            + "пробегается по всем активным задачам и переносит их атачменты в mongo DB (если они еще не там) \n"
            + "и в самом объекте атачмента меняет айдишники атачментов на новые\n"
            + "Метод содержит необязательные параметры, которые определяют какие задачи обрабатывать\n"
            + "nStartFrom - порядковый номер задачи в списке всех задач, с которого начинать обработку\n"
            + "nChunkSize - количество задач, которые обрабатывать начиная или с первой или со значения nStartFrom. \n"
            + "Задачи выюираются по 10 из базы, поэтому лучше делать значени nChunkSize кратным 10\n"
            + "nProcessId - обрабатывать задачу с заданным айдишником\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/moveAttachsToMongo\n"
            + "Перенести все атачменты задач в Монго ДБ\n\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/moveAttachsToMongo?nProcessId=9397569\n"
            + "Перенести атачменты процесса с ID 9397569 в Монго ДБ\n\n"
            + "https://test.region.igov.org.ua/wf/service/object/file/moveAttachsToMongo?nStartFrom=0&nChunkSize=10\n\n"
            + "Перенести аттачменты процесса с 0 по 10 в монго")
    @RequestMapping(value = "/moveAttachsToMongo", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    String moveAttachsToMongo(@ApiParam(value = "Порядковый номер процесса с которого начинать обработку аттачментов", required = false)
            @RequestParam(value = "nStartFrom", required = false) String nStartFrom,
            @ApiParam(value = "строка-размер блока для выборки процесса на обработку", required = false) @RequestParam(value = "nChunkSize", required = false) String nChunkSize,
            @ApiParam(value = "строка-ИД конкретного процесса", required = false) @RequestParam(value = "nProcessId", required = false) String nProcessId) {
        return oObjectFileService.moveAttachsToMongo(nStartFrom, nChunkSize, nProcessId);
    }

    @ApiOperation(value = "/dfs/getPatternFilled", notes = "##### Заполнение указанного шаблона(по его ИД ищется файл в подпапке проекта) указанными параметрами, и его отдача.\n<br>"
            + "Пример запроса: https://alpha.test.region.igov.org.ua/wf/service/object/file/dfs/getPatternFilled?sID_Pattern=F1301801"
            + "в теле запроса передавать JSON-обьект в виде строки, например:<br>"
            + "{<br>"
            + "{\n"
            + "	\"HLNAME\":\"БІЛЯВЦЕВ\"\n"
            + "	,\"HPNAME\":\"ВОЛОДИМИР\"\n"
            + "	,\"HFNAME\":\"ВОЛОДИМИРОВИЧ\"\n"
            + "	,\"HTIN\":\"2943209693\"\n"
            + "	,\"HKSTI\":\"2301\"\n"
            + "	,\"HSTI\":\"2650 ДПІ У ГОЛОСІЇВСЬКОМУ Р-НІ ГУ МІНДОХОДІВ У М.КИЄВІ\"\n"
            + "	,\"HEMAIL\":\"olga.kuzminova87@gmail.com\"\n"
            + "	,\"HCOUNTRY\":\"Украіна\"\n"
            + "	,\"HCITY\":\"Дніпро\"\n"
            + "	,\"HSTREET\":\"Красный камень\"\n"
            + "	,\"HBUILD\":\"5\"\n"
            + "	,\"R01G01\":\"22\"\n"
            + "	,\"R01G01\":\"22\"\n"
            + "	,\"R01G02\":\"22\"\n"
            + "	,\"R02G01\":\"22\"\n"
            + "	,\"R02G02\":\"22\"\n"
            + "	,\"C_REG\":\"23\"\n"
            + "	,\"C_RAJ\":\"01\"\n"
            + "	,\"TIN\":\"3578965412\"\n"
            + "	,\"C_DOC\":\"F13\"\n"
            + "	,\"C_DOC_SUB\":\"018\"\n"
            + "	,\"C_DOC_VER\":\"01\"\n"
            + "	,\"C_DOC_STAN\":\"1\"\n"
            + "	,\"C_DOC_TYPE\":\"00\"\n"
            + "	,\"C_DOC_CNT\":\"0000001\"\n"
            + "	,\"PERIOD_TYPE\":\"1\"\n"
            + "	,\"PERIOD_MONTH\":\"12\"\n"
            + "	,\"PERIOD_YEAR\":\"2015\"\n"
            + "	,\"C_STI_ORIG\":\"2301\"\n"
            + "\n"
            + ")<br>"
            + "<br>"
            + "В ответ получим заполненный шаблон. например:"
            + "<br>"
            + "{\n"
            + "  \"soPatternFilled\": \"<?xml version=\\\"1.0\\\" encoding=\\\"windows-1251\\\"?><DECLAR xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:noNamespaceSchemaLocation=\\\"F1301801.xsd\\\"><DECLARHEAD><TIN>3578965412</TIN><C_DOC>F13</C_DOC><C_DOC_SUB>018</C_DOC_SUB><C_DOC_VER>01</C_DOC_VER><C_DOC_TYPE>00</C_DOC_TYPE><C_DOC_CNT>0000001</C_DOC_CNT><C_REG>23</C_REG><C_RAJ>01</C_RAJ><PERIOD_MONTH>12</PERIOD_MONTH><PERIOD_TYPE>1</PERIOD_TYPE><PERIOD_YEAR>2015</PERIOD_YEAR><C_STI_ORIG>2301</C_STI_ORIG><C_DOC_STAN>1</C_DOC_STAN><D_FILL>24102016</D_FILL><SOFTWARE xsi:nil=\\\"true\\\"></SOFTWARE></DECLARHEAD><DECLARBODY><HLNAME>БІЛЯВЦЕВ</HLNAME><HPNAME>ВОЛОДИМИР</HPNAME><HFNAME>ВОЛОДИМИРОВИЧ</HFNAME><HTIN>2943209693</HTIN><HKSTI>2301</HKSTI><HSTI>2650 ДПІ У ГОЛОСІЇВСЬКОМУ Р-НІ ГУ МІНДОХОДІВ У М.КИЄВІ</HSTI><HEMAIL>olga.kuzminova87@gmail.com</HEMAIL><HCOUNTRY>Украіна</HCOUNTRY><HREG xsi:nil=\\\"true\\\"></HREG><HRAJ xsi:nil=\\\"true\\\"></HRAJ><HCITY>Дніпро</HCITY><HSTREET>Красный камень</HSTREET><HBUILD>5</HBUILD><HSHOT xsi:nil=\\\"true\\\"></HSHOT><HCORP xsi:nil=\\\"true\\\"></HCORP><HAPT xsi:nil=\\\"true\\\"></HAPT><HPASS xsi:nil=\\\"true\\\"></HPASS><HPASSDATE xsi:nil=\\\"true\\\"></HPASSDATE><HPASSISS xsi:nil=\\\"true\\\"></HPASSISS><R01G01>22</R01G01><R01G02>22</R01G02><R02G01>22</R02G01><R02G02>22</R02G02><HFILL>24102016</HFILL></DECLARBODY></DECLAR>\",\n"
            + "  \"sFileName\": \"23013578965412F1301801100000000111220152301.xml\"\n"
            + "}")
    @RequestMapping(value = {"/dfs/getPatternFilled"}, method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    Map<String, String> getPatternFilled(
            @ApiParam(value = "Список алиасов и значений из формы в json формате", required = false) @RequestBody(required = false) Map<String, String> mField,
            @ApiParam(value = "Ид файла-шаблона", required = true) @RequestParam(value = "sID_Pattern", required = true) String sID_Pattern,
            HttpServletResponse httpResponse) throws Exception {

        Map<String, String> mReturn = new HashMap<>();

        //mField.get("C_DOC_CNT").trim()
        Integer nCountYear = ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, "AlienBP_" + sID_Pattern, null);//Long.valueOf(snID_Service)
        LOG.info("snCountYear(before)=" + nCountYear);
        String snCountYear = String.valueOf(nCountYear);
        if (nCountYear != null && nCountYear > 0) {
            Integer nDigits = 7;
            if (snCountYear.length() < nDigits) {
                snCountYear = "0000000".substring(0, nDigits - snCountYear.length()) + snCountYear;
                LOG.info("snCountYear(after)=" + snCountYear);
            }
            mField.put("C_DOC_CNT", snCountYear);
        }
        LOG.info("mField: " + mField);
        File oFile = FileSystemData.getFile(FileSystemData.SUB_PATH_XML, sID_Pattern + ".xml");
        String sContentReturn = Files.toString(oFile, Charset.defaultCharset());
        LOG.info("Created document with customer info: {}", sContentReturn);
        String sRegex, sReplacement;

        for (Map.Entry<String, String> oField : mField.entrySet()) {
            sRegex = "<" + oField.getKey().trim().toUpperCase() + ">";
            if (oField.getValue() != null) {
                sReplacement = sRegex + oField.getValue().replaceAll(">", "&gt;").replaceAll("<", "&lt;")
                        .replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("&", "&amp;");
                sContentReturn = sContentReturn.replaceAll(sRegex, sReplacement);
            }
        }
        sContentReturn = fillDateToday(sContentReturn);
        LOG.info("sContentReturn: " + sContentReturn);
        mReturn.put("soPatternFilled", sContentReturn.replaceAll(System.getProperty("line.separator"), ""));
        mReturn.put("sFileName", buildFileName(mField));
        mReturn.put("C_DOC_CNT", snCountYear);
        //httpResponse.setContentType("application/json;charset=windows-1251");
        return mReturn;
    }

    //C_REG(2) + C_RAJ(2)+ TIN(10) + C_DOC(3) + C_DOC_SUB(3) + C_DOC_VER(2) + C_DOC_STAN(1) + C_DOC_TYPE(2, для нового 00) + C_DOC_CNT(7, 0000001) + 
    //+ PERIOD_TYPE(1, 1 - місяць, 2 - квартал, 3 - півріччя, 4 - дев’ять місяців, 5 - рік) 
    //+ PERIOD_MONTH(2) + PERIOD_YEAR(4)
    //+ C_STI_ORIG(4, містять код територіального органу отримувача, до якого подається оригінал документа. если оригинал, то = C_REG(2) + C_RAJ(2))
    private String buildFileName(Map<String, String> mField) {
        //2301 3194700944 F1301801 1 00 0000001 1 12 2015 2301.xml
        String C_DOC_VER = mField.get("C_DOC_VER").trim();
        if (C_DOC_VER.length() == 1) {
            C_DOC_VER = "0" + C_DOC_VER;
        }
        String result = new StringBuilder(mField.get("C_REG").trim())
                .append(mField.get("C_RAJ").trim())
                .append(mField.get("TIN").trim())
                .append(mField.get("C_DOC").trim())
                .append(mField.get("C_DOC_SUB").trim())
                .append(C_DOC_VER)
                .append(mField.get("C_DOC_STAN").trim())
                .append(mField.get("C_DOC_TYPE").trim())
                .append(mField.get("C_DOC_CNT").trim())
                .append(mField.get("PERIOD_TYPE").trim())
                .append(mField.get("PERIOD_MONTH").trim())
                .append(mField.get("PERIOD_YEAR").trim())
                .append(mField.get("C_STI_ORIG").trim()).append(".xml").toString();
        LOG.info("buildFileName result: " + result);
        return result;
    }

    private String fillDateToday(String declarContent) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date today = new Date();
        String sDateToday = sdf.format(today).trim();
        String regex = "<D_FILL>";
        String replacement = regex + sDateToday;
        declarContent = declarContent.replaceAll(regex, replacement);
        regex = "<HFILL>";
        replacement = regex + sDateToday;
        declarContent = declarContent.replaceAll(regex, replacement);
        return declarContent;
        //D_FILL=HFILL=01012016
    }

    @ApiOperation(value = "Загрузка прикрепленного к заявке файла из базы по новой схеме")
    @RequestMapping(value = "/getProcessAttach", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachment(
            @ApiParam(value = "ИД процесса", required = false) @RequestParam(required = false, value = "nID_Process") String nID_Process,
            @ApiParam(value = "ИД поля", required = false) @RequestParam(required = false, value = "sID_Field") String sID_Field,
            @ApiParam(value = "Ключ в БД", required = false) @RequestParam(required = false, value = "sKey") String sKey,
            @ApiParam(value = "Тип БД", required = false) @RequestParam(required = false, value = "sID_StorageType") String sID_StorageType,
            @ApiParam(value = "Имя файла по умолчанию", required = false) @RequestParam(required = false, value = "sFileName") String sFileName,
            HttpServletResponse httpResponse) throws Exception {

        LOG.info("nID_Process: " + nID_Process);
        LOG.info("sID_Field: " + sID_Field);

        MultipartFile multipartFile = attachmetService.getAttachment(nID_Process, sID_Field, sKey, sID_StorageType);

        if(sFileName == null || sFileName.equals("")){
            sFileName = multipartFile.getOriginalFilename();
        }

        //byte[] aRes = attachmetService.getAttachment(nID_Process, sID_Field, sKey, sID_StorageType);
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + sFileName);
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        /*httpResponse.setHeader("Content-disposition", "attachment; filename="
                + "test.txt");
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(aRes.length);*/
        return multipartFile.getBytes();
    }

    @ApiOperation(value = "Загрузка прикрепленного к заявке файла из базы по новой схеме")
    @RequestMapping(value = "/getDocumentImage", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getDocumentImage(
            @ApiParam(value = "ИД процесс-активити", required = false) @RequestParam(required = false, value = "nID_Process") String nID_Process,
            @ApiParam(value = "Логин подписанта", required = false) @RequestParam(required = false, value = "sLogin") String sLogin,
            @ApiParam(value = "Ключ шага документа", required = false) @RequestParam(required = false, value = "sKey_Step") String sKey_Step,
                   HttpServletResponse httpResponse) throws Exception {

        LOG.info("snID_Process_Activiti: " + nID_Process);
        LOG.info("sLogin: " + sLogin);
        LOG.info("sKey_Step: " + sKey_Step);

        MultipartFile multipartFile = attachmetService.getDocumentImage(nID_Process, sLogin, sKey_Step);

        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + "ecp_Attach.pdf" );
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        return multipartFile.getBytes();
    }

    
    @ApiOperation(value = "setProcessAttach", notes
            = "##### загрузка файла-атачмента по новому концепту")
    @RequestMapping(value = "/setProcessAttach", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    String setProcessAttach(
            @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) String nID_Process,
            @ApiParam(value = "наложено или не наложено ЭЦП", required = false) @RequestParam(value = "bSigned", required = false, defaultValue = "false") Boolean bSigned,
            @ApiParam(value = "cтрока-ИД типа хранилища Redis или Mongo", required = false) @RequestParam(value = "sID_StorageType", required = false, defaultValue = "Mongo") String sID_StorageType,
            @ApiParam(value = "массив атрибутов в виде сериализованного обьекта JSON", required = false) @RequestParam(value = "aAttribute", required = false) List<Map<String, Object>> aAttribute,
            @ApiParam(value = "строка-MIME тип отправляемого файла (по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "название и расширение файла", required = true) @RequestParam(value = "sFileNameAndExt", required = true) String sFileNameAndExt,
            @ApiParam(value = "ид поля", required = false) @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @ApiParam(value = "файл для сохранения в БД", required = true) @RequestParam(value = "file", required = true) MultipartFile file //Название не менять! Не будет работать прикрепление файла через проксю!!!
    ) throws JsonProcessingException, IOException, CRCInvalidException, RecordNotFoundException {

        LOG.info("setAttachment nID_Process: " + nID_Process);
        LOG.info("setAttachment bSigned: " + bSigned);
        LOG.info("setAttachment sID_StorageType: " + sID_StorageType);
        LOG.info("setAttachment saAttribute_JSON: " + aAttribute);
        //LOG.info("setAttachment file: " + file);
        LOG.info("setAttachment sFileNameAndExt: " + sFileNameAndExt);
        LOG.info("setAttachment sID_Field: " + sID_Field);
        LOG.info("setAttachment sContentType: " + sContentType);

        if (aAttribute == null) {
            aAttribute = new ArrayList<>();
        }

        if (file != null && "Mongo".equals(sID_StorageType)) {
            return attachmetService.createAttachment(nID_Process, sID_Field, sFileNameAndExt, bSigned, sID_StorageType,
                    sContentType, aAttribute, file.getBytes(), true);
        } else if (file != null && "Redis".equals(sID_StorageType)) {
            byte[] aContent = getBytes(file);
            return attachmetService.createAttachment(nID_Process, sID_Field, sFileNameAndExt, bSigned, sID_StorageType,
                    sContentType, aAttribute, aContent, true);
        } else {
            return "data is null";
        }
        //AttachmentCover oAttachmentCover = new AttachmentCover();
        //return oAttachmentCover.apply(attachment);
    }

   
    
    @ApiOperation(value = "setProcessAttachText", notes
            = "##### загрузка body-атачмента по новому концепту")
    @RequestMapping(value = "/setProcessAttachText", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    String setProcessAttachText(
            @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) String nID_Process,
            @ApiParam(value = "наложено или не наложено ЭЦП", required = false) @RequestParam(value = "bSigned", required = false, defaultValue = "false") Boolean bSigned,
            @ApiParam(value = "cтрока-ИД типа хранилища Redis или Mongo", required = false) @RequestParam(value = "sID_StorageType", required = false, defaultValue = "Mongo") String sID_StorageType,
            @ApiParam(value = "массив атрибутов в виде сериализованного обьекта JSON", required = false) @RequestParam(value = "aAttribute", required = false) List<Map<String, Object>> aAttribute,
            @ApiParam(value = "название и расширение файла", required = true) @RequestParam(value = "sFileNameAndExt", required = true) String sFileNameAndExt,
            @ApiParam(value = "ид поля", required = false) @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @ApiParam(value = "строка-MIME тип отправляемого файла (по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "контент файла в виде строки", required = true) @RequestBody String sData) throws IOException, JsonProcessingException, CRCInvalidException, RecordNotFoundException {
        
        LOG.info("setProcessAttachText is started...");
        
        LOG.info("setAttachment nID_Process: " + nID_Process);
        LOG.info("setAttachment bSigned: " + bSigned);
        LOG.info("setAttachment sID_StorageType: " + sID_StorageType);
        LOG.info("setAttachment saAttribute_JSON: " + aAttribute);
        LOG.info("setAttachment sFileNameAndExt: " + sFileNameAndExt);
        LOG.info("setAttachment sID_Field: " + sID_Field);
        LOG.info("setAttachment sContentType: " + sContentType);
        //LOG.info("setAttachment sData: " + sData);

        if (aAttribute == null) {
            aAttribute = new ArrayList<>();
        }

        if (sData != null && "Mongo".equals(sID_StorageType)) {
            String sResultAttach = attachmetService.createAttachment(nID_Process, sID_Field, sFileNameAndExt, bSigned, sID_StorageType,
                    sContentType, aAttribute, sData.getBytes(Charsets.UTF_8), true);
            LOG.info("setProcessAttachText is ended...");
            return sResultAttach;
            
        } else if (sData != null && "Redis".equals(sID_StorageType)) {
            throw new RuntimeException("There is no suitable metod for string data for redis");
        } else {
            return "data is null";
        }
        
        
        //AttachmentCover oAttachmentCover = new AttachmentCover();
        //return oAttachmentCover.apply(attachment);
    }
    
    @ApiOperation(value = "setDocumentImage", notes
            = "##### загрузка body-атачмента по новому концепту(PDF)")
    @RequestMapping(value = "/setDocumentImage", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    String setDocumentImage(
            @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) String nID_Process,
            @ApiParam(value = "наложено или не наложено ЭЦП", required = false) @RequestParam(value = "bSigned", required = false, defaultValue = "false") Boolean bSigned,
            @ApiParam(value = "cтрока-ИД типа хранилища Redis или Mongo", required = false) @RequestParam(value = "sID_StorageType", required = false, defaultValue = "Mongo") String sID_StorageType,
            @ApiParam(value = "массив атрибутов в виде сериализованного обьекта JSON", required = false) @RequestParam(value = "aAttribute", required = false) List<Map<String, Object>> aAttribute,
            @ApiParam(value = "название и расширение файла", required = true) @RequestParam(value = "sFileNameAndExt", required = true) String sFileNameAndExt,
            @ApiParam(value = "ид поля", required = false) @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @ApiParam(value = "строка-MIME тип отправляемого файла (по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "Логин подписанта", required = true) @RequestParam(required = true, value = "sLogin") String sLogin,
            @ApiParam(value = "Ключ шага документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step,
            @ApiParam(value = "файл для сохранения в БД", required = true) @RequestParam(value = "file", required = false) MultipartFile file, //Название не менять! Не будет работать прикрепление файла через проксю!!!
            @ApiParam(value = "контент файла в виде строки", required = false) @RequestBody String sData)
            throws IOException, JsonProcessingException, CRCInvalidException, RecordNotFoundException, ParseException
        {

        /*    if(file != null){
                sData = new String(file.getBytes());
                LOG.info("added file is not null");
            } else if (sData == null || sData.equals("")){
                throw new IllegalArgumentException("Bad request! Context not found");
            }*/
        
        LOG.info("setAttachment nID_Process: " + nID_Process);
        LOG.info("setAttachment bSigned: " + bSigned);
        LOG.info("setAttachment sID_StorageType: " + sID_StorageType);
        LOG.info("setAttachment saAttribute_JSON: " + aAttribute);
        LOG.info("setAttachment sFileNameAndExt: " + sFileNameAndExt);
        LOG.info("setAttachment sID_Field: " + sID_Field);
        LOG.info("setAttachment sContentType: " + sContentType);
        //LOG.info("setAttachment sData: " + sData);
        LOG.info("setAttachment sLogin: " + sLogin);
        LOG.info("setAttachment sKey_Step: " + sKey_Step);

        if (aAttribute == null) {
            aAttribute = new ArrayList<>();
        }
        
       if(file != null){
            if ("Mongo".equals(sID_StorageType)) {
                return attachmetService.setDocumentImage(nID_Process, sID_Field, sFileNameAndExt, bSigned, sID_StorageType,
                        sContentType, aAttribute, file.getBytes(), true, sKey_Step, sLogin);
            } else {
                return "data is null";
            }
       }else{
        
            if (sData != null && "Mongo".equals(sID_StorageType)) {
                return attachmetService.setDocumentImage(nID_Process, sID_Field, sFileNameAndExt, bSigned, sID_StorageType, 
                        sContentType, aAttribute, sData.getBytes(Charsets.UTF_8), true, sKey_Step, sLogin);
            } else if (sData != null && "Redis".equals(sID_StorageType)) {
                throw new RuntimeException("There is no suitable metod for string data for redis");
            } else {
                return "data is null";
            }
       }

    }

    
    @ApiOperation(value = "setTaskAttachment", notes
            = "#####  Set/update файла-атачмента к таске Activiti")
    @RequestMapping(value = "/setTaskAttachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    AttachmentEntityI setTaskAttachment(
            @ApiParam(value = "номер-ИД задачи", required = true) @RequestParam(value = "nTaskId") String taskId,
            @ApiParam(value = "строка-MIME тип отправляемого файла (по умолчанию = \"text/html\")", required = false) @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @ApiParam(value = "строка-описание", required = true) @RequestParam(value = "sDescription") String description,
            @RequestParam(value = "sFileName") String sFileName,
            @ApiParam(value = "id аттача Activiti", required = false) @RequestParam(value = "nID_Attach", required = false) String nID_Attach,
            @RequestBody String sData) {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (!tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee()
                    : "kermit";
            LOG.debug("processInstanceId:{}, taskId:{}, assignee:{} ", processInstanceId, taskId, assignee);
        } else {
            LOG.error("There is no tasks at all!");

        }

        identityService.setAuthenticatedUserId(assignee);

        if (nID_Attach != null) {
            List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);
            for (Attachment oAttachment : attachments) {
                if (nID_Attach.equals(oAttachment.getId())) {
                    taskService.deleteAttachment(nID_Attach);
                    LOG.info("Attachment was deleted. nID_Attach {} ", nID_Attach);
                }
            }
        }

        String sFilename = sFileName;
        LOG.debug("sFilename={}", sFileName);
        sFilename = sTextTranslit(sFilename);
        LOG.debug("FileExtention: {}, fileContentType:{}, fileName:{}",
                oActionTaskService.getFileExtention(sFileName), sContentType, sFilename);
        LOG.debug("description: {}", description);

        Attachment attachment = taskService.createAttachment(sContentType + ";"
                + oActionTaskService.getFileExtention(sFileName), taskId, processInstanceId,
                sFilename, description,
                new ByteArrayInputStream(sData.getBytes(Charsets.UTF_8)));

        AttachmentCover oAttachmentCover = new AttachmentCover();

        return oAttachmentCover.apply(attachment);
    }

    @ApiOperation(value = "/getJsonBase64EncodedFiles", notes
            = "##### загрузка файла-PDF-документа для дальнейшей обработки")
    @RequestMapping(value = "/getJsonBase64EncodedFiles", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    String getJsonBase64EncodedFiles(
            @ApiParam(value = "файл для сохранения в БД", required = true) @RequestParam(value = "file", required = true) MultipartFile file //Название не менять! Не будет работать прикрепление файла через проксю!!!
    ) throws IOException, CRCInvalidException, RecordNotFoundException,
            FileServiceIOException {

        try {
            /*
            String key = oBytesDataInmemoryStorage.putBytes(AbstractModelTask
                    .multipartFileToByteArray(file, file.getOriginalFilename())
                    .toByteArray());

            byte[] upload = oBytesDataInmemoryStorage.getBytes(key);
            */
            byte[] upload = getBytes(file);

            Map<java.lang.String, Object> response = new HashMap<>();
            response.put("Base64", Base64.getEncoder().encode(upload));
            response.put("Base64Mime", Base64.getMimeEncoder().encode(upload));
            response.put("Decoded", upload);

            return JsonRestUtils.toJson(response);
        } catch (/*RecordInmemoryException |*/ IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }

    }

    @ApiOperation(value = "/getBase64EncodedFile", notes
            = "##### кодирования файла в Base64")
    @RequestMapping(value = "/getBase64EncodedFile", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    byte[] getBase64EncodedFile(
            @ApiParam(value = "MultipartFile для кодирования в Base64", required = true) @RequestParam(value = "file", required = true) MultipartFile file //Название не менять! Не будет работать прикрепление файла через проксю!!!
    ) throws IOException, CRCInvalidException, RecordNotFoundException,
            FileServiceIOException {

        try {
            byte[] upload = getBytes(file);

            return Base64.getEncoder().encode(upload);
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }

    }

    @ApiOperation(value = "/getBase64MimeEncodedFile", notes
            = "##### кодирования файла в Base64 MIME")
    @RequestMapping(value = "/getBase64MimeEncodedFile", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    byte[] getBase64MimeEncodedFile(
            @ApiParam(value = "MultipartFile для кодирования в Base64 MIME", required = true) @RequestParam(value = "file", required = true) MultipartFile file //Название не менять! Не будет работать прикрепление файла через проксю!!!
    ) throws IOException, CRCInvalidException, RecordNotFoundException,
            FileServiceIOException {

        try {
            byte[] upload = getBytes(file);

            return Base64.getMimeEncoder().encode(upload);
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }

    }

    @ApiOperation(value = "/getBase64DecodedFile", notes
            = "##### декодирование файла из Base64")
    @RequestMapping(value = "/getBase64DecodedFile", method = RequestMethod.POST, produces = "application/pdf")
    @Transactional
    public @ResponseBody
    byte[] getBase64DecodedFile(
            @ApiParam(value = "использовать MIME декодер", required = false) @RequestParam(value = "isMime", required = false, defaultValue = "false") boolean isMime,
            @ApiParam(value = "MultipartFile для декодирования из Base64", required = true) @RequestParam(value = "file", required = false) MultipartFile file, //Название не менять! Не будет работать прикрепление файла через проксю!!!
            @RequestBody byte[] byteArray
    ) throws IOException, CRCInvalidException, RecordNotFoundException,
            FileServiceIOException {

        try {
            if(isMime){
                if(byteArray.length > 0){
                    return Base64.getMimeDecoder().decode(byteArray);
                }
                return Base64.getMimeDecoder().decode(getBytes(file));
            } else {
                if(byteArray.length > 0){
                    return Base64.getDecoder().decode(byteArray);
                }
                return Base64.getDecoder().decode(getBytes(file));
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new FileServiceIOException(
                    FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
        }

    }

    private byte[] getBytes(MultipartFile file) throws IOException {
        return AbstractModelTask
                        .multipartFileToByteArray(file, file.getOriginalFilename())
                        .toByteArray();
    }

}
