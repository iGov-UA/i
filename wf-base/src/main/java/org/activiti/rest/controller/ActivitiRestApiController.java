package org.activiti.rest.controller;

import static org.wf.dp.dniprorada.base.model.AbstractModelTask.getByteArrayMultipartFileFromRedis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;

import liquibase.util.csv.CSVWriter;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.activiti.redis.exception.RedisException;
import org.activiti.redis.model.ByteArrayMultipartFile;
import org.activiti.redis.service.RedisService;
import org.activiti.rest.controller.adapter.AttachmentEntityAdapter;
import org.activiti.rest.controller.adapter.ProcDefinitionAdapter;
import org.activiti.rest.controller.entity.AttachmentEntityI;
import org.activiti.rest.controller.entity.ProcDefinitionI;
import org.activiti.rest.controller.entity.Process;
import org.activiti.rest.controller.entity.ProcessI;
import org.activiti.rest.service.api.runtime.process.ExecutionBaseResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.egov.service.HistoryEventService;
import org.joda.time.DateTime;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.model.AbstractModelTask;
import org.wf.dp.dniprorada.base.util.FieldsSummaryUtil;
import org.wf.dp.dniprorada.base.util.JSExpressionUtil;
import org.wf.dp.dniprorada.engine.task.FileTaskUpload;
import org.wf.dp.dniprorada.model.BuilderAttachModel;
import org.wf.dp.dniprorada.model.ByteArrayMultipartFileOld;
import org.wf.dp.dniprorada.util.BankIDConfig;
import org.wf.dp.dniprorada.util.BankIDUtils;
import org.wf.dp.dniprorada.util.EGovStringUtils;
import org.wf.dp.dniprorada.util.GeneralConfig;
import org.wf.dp.dniprorada.util.Mail;
import org.wf.dp.dniprorada.util.SecurityUtils;
import org.wf.dp.dniprorada.util.Util;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import com.google.common.base.Charsets;

/**
 * ...wf/service/... Example:
 * .../wf/service/rest/startProcessByKey/citizensRequest
 *
 * @author BW
 */
@Controller
@RequestMapping(value = "/rest")
public class ActivitiRestApiController extends ExecutionBaseResource {

    private static final int DEFAULT_REPORT_FIELD_SPLITTER = 59;
    private static final Logger LOG = LoggerFactory
            .getLogger(ActivitiRestApiController.class);

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd:HH-mm-ss", Locale.ENGLISH);

    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private FormService formService;
    @Autowired
    private Mail oMail;
    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private BankIDConfig bankIDConfig;
    @Autowired
    private ActivitiExceptionController exceptionController;

    @ExceptionHandler({CRCInvalidException.class, EntityNotFoundException.class, RecordNotFoundException.class})
    @ResponseBody
    public ResponseEntity<String> handleAccessException(Exception e) throws ActivitiRestException {
        return exceptionController.catchActivitiRestException(new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                e.getMessage(), e,
                HttpStatus.FORBIDDEN));
    }

    public static String parseEnumProperty(FormProperty property) {
        Object oValues = property.getType().getInformation("values");
        if (oValues instanceof Map) {
            Map<String, String> mValue = (Map) oValues;
            LOG.info("[parseEnumProperty]:m=" + mValue);
            String sName = property.getValue();
            LOG.info("[parseEnumProperty]:sName=" + sName);
            String sValue = mValue.get(sName);
            LOG.info("[parseEnumProperty]:sValue=" + sValue);
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
            LOG.info("[parseEnumProperty]:m=" + mValue);
            LOG.info("[parseEnumProperty]:sName=" + sName);
            String sValue = mValue.get(sName);
            LOG.info("[parseEnumProperty]:sValue=" + sValue);
            return parseEnumValue(sValue);
        } else {
            LOG.error("Cannot parse values for property - {}", property);
            return "";
        }
    }

    public static String parseEnumValue(String sEnumName) {
        LOG.info("[parseEnumValue]:sEnumName=" + sEnumName);

        String res = StringUtils.defaultString(sEnumName);
        LOG.info("[parseEnumValue]:sEnumName(2)=" + sEnumName);
        if (res.contains("|")) {
            String[] as = sEnumName.split("\\|");
            LOG.info("[parseEnumValue]:as.length - 1=" + (as.length - 1));
            LOG.info("[parseEnumValue]:as=" + as);
            res = as[as.length - 1];
        }

        return res;
    }

    /**
     * Запуск процесса Activiti:
     * @param key Ключ процесса
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/start-process/{key}", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    ProcessI startProcessByKey(
            @PathVariable("key") String key) {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
        if (pi == null || pi.getId() == null) {
            throw new IllegalArgumentException(String.format(
                    "process did not started by key:{%s}", key));
        }
        return new Process(pi.getProcessInstanceId());
    }

    /**
     * Загрузка каталога сервисов из Activiti:
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/process-definitions", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
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

    @RequestMapping(value = "/delete-process", method = RequestMethod.DELETE)
    public @ResponseBody
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
    
    @RequestMapping(value = "/delete-processTest", method = RequestMethod.GET)
    public @ResponseBody
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
     * @throws org.activiti.rest.controller.ActivitiIOException
     */
    @RequestMapping(value = "/file/upload_file_to_redis", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody
    String putAttachmentsToRedis(
            @RequestParam(required = true, value = "file") MultipartFile file)
            throws ActivitiIOException {
        try {
            String key = redisService.putAttachments(AbstractModelTask
                    .multipartFileToByteArray(file, file.getOriginalFilename())
                    .toByteArray());
            return key;
        } catch (RedisException | IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/file/download_file_from_redis", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentsFromRedis(
            @RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            upload = redisService.getAttachments(key);
        } catch (RedisException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
        return upload;
    }

    @RequestMapping(value = "/file/download_file_from_redis_bytes", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentsFromRedisBytes(
            @RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            byte[] aByteFile = redisService.getAttachments(key);
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

        } catch (RedisException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(
                    ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        } catch (ClassNotFoundException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ActivitiException(e.getMessage(), e);
        }

        return upload;
    }

    @RequestMapping(value = "/file/check_file_from_redis_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String checkAttachmentsFromRedisSign(
            @RequestParam("sID_File_Redis") String sID_File_Redis)
            throws ActivitiIOException {
        byte[] upload = null;
        String fileName = null;
        try {
            byte[] aByteFile = redisService.getAttachments(sID_File_Redis);
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

        } catch (RedisException e) {
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
    @RequestMapping(value = "/file/download_file_from_db", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentFromDb(
            @RequestParam(value = "taskId") String taskId,
            @RequestParam(required = false, value = "attachmentId") String attachmentId,
            @RequestParam(required = false, value = "nFile") Integer nFile,
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
     * @param taskId id таски Activiti BP
     * @param attachmentId id атачмента приложеного к таске
     */
    @RequestMapping(value = "/file/check_attachment_sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String checkAttachSign(
            @RequestParam(value = "nID_Task") String taskId,
            @RequestParam(value = "nID_Attach") String attachmentId)
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
    @RequestMapping(value = "/file/download_file_from_db_execution", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    byte[] getAttachmentFromDbExecution(
            @RequestParam("taskId") String taskId,
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
     * @param taskId ИД-номер таски
     * @param description описание
     * @param file в html это имя элемента input типа file - <input name="file" type="file" />. в HTTP заголовках - Content-Disposition: form-data; name="file" ...
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/file/upload_file_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    AttachmentEntityI putAttachmentsToExecution(
            @RequestParam(value = "taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description") String description)
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
     * @param taskId ИД-номер таски
     * @param sContentType MIME тип отправляемого файла (опциоанльно) (значение по умолчанию = "text/html")
     * @param description описание
     * @param sFileName имя отправляемого файла
     */
    @RequestMapping(value = "/file/upload_content_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public @ResponseBody
    AttachmentEntityI putTextAttachmentsToExecution(
            @RequestParam(value = "nTaskId") String taskId,
            @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @RequestParam(value = "sDescription") String description,
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
     * @param sID_BP_Name - ИД бизнес процесса
     * @param dateAt - дата начала периода выборки
     * @param dateTo - дата окончания периода выборки
     * @param nRowStart - позиция начальной строки для возврата (0 по умолчанию)
     * @param nRowsMax - количество записей для возврата (1000 по умолчанию)
     * @param bDetail - если да, то выгружать все поля тасок, иначе -- только
     * основные (по умолчанию да)
     * @param saFields - вычисляемые поля (название поля -- формула, issue 907)
     * @param saFieldSummary - сведение полей, которое производится над выборкой
     * (issue 916)
     * @param httpResponse - респонс, в который пишется ответ -- csv-файл
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/file/download_bp_timing", method = RequestMethod.GET)
    @Transactional
    public void getTimingForBusinessProcessNew(
            @RequestParam(value = "sID_BP_Name") String sID_BP_Name,
            @RequestParam(value = "sDateAt") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @RequestParam(value = "bDetail", required = false, defaultValue = "true") Boolean bDetail,
            @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            @RequestParam(value = "saFields", required = false) String saFields,
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
        String[] headersMainField = {"nID_Process", "sLoginAssignee",
            "sDateTimeStart", "nDurationMS", "nDurationHour", "sName"};
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
     * @param sID_BP business process name
     * @param sID_State_BP task state id
     * @param saFields field of the tasks to download. Separated by comma
     * @param nASCI_Spliter splitter of the fields
     * @param sID_Codepage encoding for the file
     * @param sDateCreateFormat format for sDateCreate
     * @param dateAt start date for the filter
     * @param dateTo end date for the filter
     * @param nRowStart start row for paging
     * @param nRowsMax maximal amount of row for paging
     * @param bIncludeHistory to include historic task instances. default value
     * is true
     * @param saFieldsCalc list of calculated fields
     * @param httpResponse http responce wrapper
     * @throws IOException in case of connection aborted with client
     * <p/>
     * example: https://test.region.igov.org.ua/wf/service/rest/file/
     * downloadTasksData ?sID_BP=kiev_mreo_1&sDateAt=2015-06-28&sDateTo
     * =2015-08-01&nASCI_Spliter =59&sID_Codepage=UTF8&saFields=nID_Task
     * ;bankIdPassport;bankIdlastName
     * ;bankIdfirstName;bankIdmiddleName;1;sDateCreate
     */
    @RequestMapping(value = "/file/downloadTasksData", method = RequestMethod.GET)
    @Transactional
    public void downloadTasksData(
            @RequestParam(value = "sID_BP") String sID_BP,
            @RequestParam(value = "sID_State_BP", required = false) String sID_State_BP,
            @RequestParam(value = "saFields", required = false) String saFields,
            @RequestParam(value = "nASCI_Spliter", required = false) String nASCI_Spliter,
            @RequestParam(value = "sFileName", required = false) String fileName,
            @RequestParam(value = "sID_Codepage", required = false, defaultValue = "win1251") String sID_Codepage,
            @RequestParam(value = "sDateCreateFormat", required = false, defaultValue = "yyyy-MM-dd HH:mm:ss") String sDateCreateFormat,
            @RequestParam(value = "sDateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @RequestParam(value = "bIncludeHistory", required = false, defaultValue = "true") Boolean bIncludeHistory,
            @RequestParam(value = "bHeader", required = false, defaultValue = "false") Boolean bHeader,
            @RequestParam(value = "saFieldsCalc", required = false) String saFieldsCalc,
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

        String header = null;
        if (bHeader != null) {
            header = formHeader(saFields, foundHistoricResults, saFieldsCalc);
        }
        
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

        PrintWriter printWriter = new PrintWriter(httpResponse.getWriter());

        if (bHeader && header != null) {
            printWriter.println(header);
        }

        fillTheFile(sID_BP, dBeginDate, dEndDate, foundResults, sDateCreateDF,
                printWriter, saFields, separator, saFieldsCalc);
        if (Boolean.TRUE.equals(bIncludeHistory)) {
            Set<String> tasksIdToExclude = new HashSet<String>();
            for (Task task : foundResults) {
                tasksIdToExclude.add(task.getId());
            }
            fillTheFileHistoricTasks(sID_BP, dBeginDate, dEndDate,
                    foundHistoricResults, sDateCreateDF, printWriter, saFields,
                    separator, tasksIdToExclude, saFieldsCalc);
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
            if (saFields.contains("=")) {
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
            }
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
        if (saFields != null) {
            if (saFields.contains("=")) {
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
            }
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
                if (i < params.length - 1){
                	sb.append(";");
                }
            }
            res = res + ";" + sb.toString();
            LOG.info("Header with calculated fields: " + res);
        }
        
        return res;
    }

    private void fillTheFileHistoricTasks(String sID_BP, Date dateAt,
            Date dateTo, List<HistoricTaskInstance> foundResults,
            SimpleDateFormat sDateCreateDF, PrintWriter printWriter,
            String pattern, String separator, Set<String> tasksIdToExclude, String saFieldsCalc) {
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

            if (saFieldsCalc != null){
        		currentRow = addCalculatedFields(saFieldsCalc, curTask,
						currentRow);	
            }
            
            if (pattern != null) {
                currentRow = replaceReportFields(sDateCreateDF, curTask, currentRow);
				// replacing all the fields which were empty in the form with empty
                // string
                currentRow = currentRow.replaceAll("\\$\\{.*?\\}", "");
            }
            printWriter.println(currentRow.replaceAll(";", separator));
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

    private void fillTheFile(String sID_BP, Date dateAt, Date dateTo,
            List<Task> foundResults, SimpleDateFormat sDateCreateDF,
            PrintWriter printWriter, String pattern, String separator, String saFieldsCalc) {
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
            
            if (saFieldsCalc != null){
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
            printWriter.println(currentRow.replaceAll(";", separator));
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
			for (String expression : saFieldsCalc.split(";")){
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
    @RequestMapping(value = "/getLoginBPs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    String getBusinessProcessesForUser(
            @RequestParam(value = "sLogin") String sLogin) throws IOException {
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
            LOG.info("Checking user group:" + group.getId());
            for (String groupFromProcess : candidateCroupsToCheck) {
                if (groupFromProcess.contains("${")) {
                    LOG.info("Group from process contains pattern. Replacing it." + groupFromProcess);
                    groupFromProcess = groupFromProcess.replaceAll("\\$\\{?.*}", "(.*)");
                    LOG.info("Result group to check: " + groupFromProcess);
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

    @RequestMapping(value = "/test/sendAttachmentsByMail", method = RequestMethod.GET)
    @Transactional
    public void sendAttachmentsByMail(
            @RequestParam(value = "sMailTo", required = false) String sMailTo,
            @RequestParam(value = "nID_Task", required = false) String snID_Task,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "bHTML", required = false) boolean bHTML,
            @RequestParam(value = "naID_Attachment", required = false) String snaID_Attachment)
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

        oMail.send();
    }

    /**
     * @param sPathFile полный путь к файлу, например: folder/file.html.
     * @param sContentType тип контента (опционально, по умолчанию обычный текст: text/plain)
     */
    @RequestMapping(value = "/getPatternFile", method = RequestMethod.GET)
    public void getPatternFile(
            @RequestParam(value = "sPathFile") String sPathFile,
            @RequestParam(value = "sContentType", required = false) String sContentType,
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
     * @param saField -- строка-массива полей (например:
     * "[{'id':'sFamily','type':'string','value':'Белявский'},{'id':'nAge','type':'long'}]"
     * )
     * @param sMail -- строка электронного адреса гражданина
     * @param sHead -- строка заголовка письма //опциональный (если не задан, то
     * "Необходимо уточнить данные")
     * @param sBody -- строка тела письма //опциональный (если не задан, то
     * пустота)
     * @throws ActivitiRestException
     * @throws CRCInvalidException
     */
    @RequestMapping(value = "/setTaskQuestions", method = RequestMethod.GET)
    public @ResponseBody
    void setTaskQuestions(
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @RequestParam(value = "saField") String saField,
            @RequestParam(value = "sMail") String sMail,
            @RequestParam(value = "sHead", required = false) String sHead,
            @RequestParam(value = "sBody", required = false) String sBody)
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

    /**
     * @param nID_Protected номер-ИД заявки (защищенный, опционально, если есть sID_Order или nID_Process)
     * @param sID_Order строка-ид заявки (опционально, подробнее [тут](https://github.com/e-government-ua/i/blob/test/docs/specification.md#17_workWithHistoryEvent_Services) )
     * @param nID_Process ид заявки (опционально)
     * @param nID_Server ид сервера, где расположена заявка
     * @param saField строка-массива полей (например: "[{'id':'sFamily','type':'string','value':'Белявцев'},{'id':'nAge','type':'long','value':35}]")
     * @param sToken строка-токена. Данный параметр формируется и сохраняется в запись HistoryEvent_Service во время вызова метода setTaskQuestions
     * @param sBody строка тела сообщения (опциональный параметр)
     */
    @RequestMapping(value = "/setTaskAnswer", method = RequestMethod.GET)
    public @ResponseBody
    void setTaskAnswer(
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @RequestParam(value = "saField") String saField,
            @RequestParam(value = "sToken") String sToken,
            @RequestParam(value = "sHead", required = false) String sHead,
            @RequestParam(value = "sBody", required = false) String sBody)
            throws ActivitiRestException {

        try {
            LOG.info(
                    "try to find history event_service by sID_Order=%s, nID_Protected-%s, nID_Process=%s and nID_Server=%s",
                    sID_Order, nID_Protected, nID_Process, nID_Server);
            String historyEvent = historyEventService.getHistoryEvent(
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
            }

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

            LOG.info(
                    "try to find history event_service by sID_Order=%s, nID_Protected-%s and nID_Server=%s",
                    sID_Order, nID_Protected, nID_Server);
            historyEvent = updateHistoryEvent_Service(sID_Order, nID_Protected,
                    nID_Process, nID_Server, saField, sHead, null, null,
                    "Відповідь на запит по уточненню даних");
            LOG.info("....ok! successfully get historyEvent_service! event="
                    + historyEvent);
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e, HttpStatus.FORBIDDEN);
        }
    }

    private String updateHistoryEvent_Service(String sID_Order,
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

    @RequestMapping(value = "/sendProccessToGRES", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> sendProccessToGRES(@RequestParam(value = "nID_Task") Long nID_Task) throws ActivitiRestException {
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

        res.put("startFormData", startFormValues.toString());
        res.put("taskFormData", taskFormValues.toString());

        return res;
    }

    @RequestMapping(value = "/getTaskFormData", method = RequestMethod.GET)
    public @ResponseBody
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
