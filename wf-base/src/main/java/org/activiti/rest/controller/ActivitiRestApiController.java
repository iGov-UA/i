package org.activiti.rest.controller;

import com.google.common.base.Charsets;
import liquibase.util.csv.CSVWriter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wf.dp.dniprorada.base.dao.FlowSlotTicketDao;
import org.wf.dp.dniprorada.base.model.AbstractModelTask;
import org.wf.dp.dniprorada.engine.task.FileTaskUpload;
import org.wf.dp.dniprorada.model.BuilderAttachModel;
import org.wf.dp.dniprorada.model.ByteArrayMultipartFileOld;
import org.wf.dp.dniprorada.util.*;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.wf.dp.dniprorada.base.model.AbstractModelTask.getByteArrayMultipartFileFromRedis;

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
    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestApiController.class);

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");

    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;

    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;
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
    private FieldsSummaryUtil fieldsSummaryUtil;

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

    @RequestMapping(value = "/start-process/{key}", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    ProcessI startProcessByKey(@PathVariable("key") String key) {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
        if (pi == null || pi.getId() == null) {
            throw new IllegalArgumentException(String.format(
                    "process did not started by key:{%s}", key));
        }
        return new Process(pi.getProcessInstanceId());
    }

    @RequestMapping(value = "/process-definitions", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    List<ProcDefinitionI> getProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().latestVersion()
                .list();
        List<ProcDefinitionI> procDefinitions = new ArrayList<>();
        ProcDefinitionAdapter adapter = new ProcDefinitionAdapter();
        for (ProcessDefinition processDefinition : processDefinitions) {
            procDefinitions.add(adapter.apply(processDefinition));
        }
        return procDefinitions;
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
    public
    @ResponseBody
    String putAttachmentsToRedis(@RequestParam(required = true, value = "file") MultipartFile file)
            throws Exception {
        return redisService.putAttachments(AbstractModelTask.multipartFileToByteArray(file,
                file.getOriginalFilename()).toByteArray());
    }

    @RequestMapping(value = "/file/download_file_from_redis", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentsFromRedis(@RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            upload = redisService.getAttachments(key);
        } catch (RedisException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiIOException(ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
        return upload;
    }

    @RequestMapping(value = "/file/download_file_from_redis_bytes", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    byte[] getAttachmentsFromRedisBytes(@RequestParam("key") String key) throws ActivitiIOException {
        byte[] upload = null;
        try {
            //            upload = redisService.getAttachments(key);

            //byte[] aByteFile = getRedisService().getAttachments(sKeyRedis);
            byte[] aByteFile = redisService.getAttachments(key);
            ByteArrayMultipartFile oByteArrayMultipartFile = null;
            try {
                oByteArrayMultipartFile = getByteArrayMultipartFileFromRedis(aByteFile);
            } catch (ClassNotFoundException | IOException e1) {
                throw new ActivitiException(e1.getMessage(), e1);
            }
            if (oByteArrayMultipartFile != null) {

                upload = oByteArrayMultipartFile.getBytes();
                            /*
                            String sFileName = null;
                            try {
                                sFileName = new String(oByteArrayMultipartFile.getOriginalFilename().getBytes(), "UTF-8");
                            } catch (java.io.UnsupportedEncodingException e) {
                                log.error("on getting sFileName", e);
                                throw new ActivitiException(e.getMessage(), e);
                            }
                            log.info("sFileName=" + sFileName);

                            //===
                            InputStream oInputStream = null;
                            try {
                                oInputStream = oByteArrayMultipartFile.getInputStream();
                            } catch (Exception e) {
                                throw new ActivitiException(e.getMessage(), e);
                            }
                            Attachment oAttachment = oExecution.getEngineServices().getTaskService().createAttachment(
                                    oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp(), oTask.getId(), oExecution.getProcessInstanceId(), sFileName, sDescription, oInputStream);

                            if (oAttachment != null) {
                                String nID_Attachment = oAttachment.getId();
                                //LOG.info("nID_Attachment=" + nID_Attachment);
                                log.info("Try set variable(sID_Field) '" + sID_Field + "' with the value(nID_Attachment) '" + nID_Attachment + "', for new attachment...");
                                oExecution.getEngineServices().getRuntimeService().setVariable(oExecution.getProcessInstanceId(), sID_Field, nID_Attachment);
                                log.info("Finished setting new value for variable with attachment(sID_Field) '" + sID_Field + "'");
                            } else {
                                log.error("Can't add attachment to oTask.getId()=" + oTask.getId());
                            }
                            //===
                            */
            } else {
                LOG.error("[getAttachmentsFromRedisBytes]oByteArrayMultipartFile==null! aByteFile=" + aByteFile
                        .toString());
            }

        } catch (RedisException e) {
            throw new ActivitiIOException(ActivitiIOException.Error.REDIS_ERROR, e.getMessage());
        }
        return upload;
    }

    /**
     * Получение Attachment средствами активити из
     * таблицы ACT_HI_ATTACHMENT
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
    public
    @ResponseBody
    byte[] getAttachmentFromDb(@RequestParam(value = "taskId") String taskId,
            @RequestParam(required = false, value = "attachmentId") String attachmentId,
            @RequestParam(required = false, value = "nFile") Integer nFile,
            HttpServletResponse httpResponse) throws IOException {

        //Получаем по задаче ид процесса
        HistoricTaskInstance historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        String processInstanceId = historicTaskInstanceQuery.getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(
                    "ProcessInstanceId for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }

        //Выбираем по процессу прикрепленные файлы
        Attachment attachmentRequested = getAttachment(attachmentId, taskId, nFile, processInstanceId);

        InputStream attachmentStream = taskService.getAttachmentContent(attachmentRequested.getId());
        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException(
                    "Attachment for taskId '" + taskId + "' doesn't have content associated with it.",
                    Attachment.class);
        }

        String sFileName = attachmentRequested.getName();
        int nTo = sFileName.lastIndexOf(".");
        if (nTo >= 0) {
            sFileName = "attach_" + attachmentRequested.getId() + "." + sFileName.substring(nTo + 1);
        }

        //Вычитывем из потока массив байтов контента и помещаем параметры контента в header
        ByteArrayMultipartFileOld multipartFile = new ByteArrayMultipartFileOld(
                attachmentStream, attachmentRequested.getDescription(),
                sFileName, attachmentRequested.getType());
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + sFileName);
        httpResponse.setHeader("Content-Type", "application/octet-stream");

        httpResponse.setContentLength(multipartFile.getBytes().length);

        return multipartFile.getBytes();
    }

    @RequestMapping(value = "/file/check_attachment_sign", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String checkAttachSign(@RequestParam(value = "nID_Task") String taskId,
            @RequestParam(value = "nID_Attach") String attachmentId,
            HttpServletResponse httpResponse) throws IOException {

        HistoricTaskInstance historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        String processInstanceId = historicTaskInstanceQuery.getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(
                    "ProcessInstanceId for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }

        Attachment attachmentRequested = getAttachment(attachmentId, taskId, processInstanceId);

        InputStream attachmentStream = taskService.getAttachmentContent(attachmentRequested.getId());
        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException(
                    "Attachment for taskId '" + taskId + "' doesn't have content associated with it.",
                    Attachment.class);
        }

        LOG.info("Attachment found. taskId {}, attachmentID {} With name {} ", taskId, attachmentId,
                attachmentRequested.getName());
        //LOG.info("checkECP params: sClientId {}, sClientSecret {}, sHostCentral {}. and let's check getSID_login {}", bankIDConfig.sClientId(),
        //        bankIDConfig.sClientSecret(), generalConfig.sHostCentral(), generalConfig.getSID_login());
        LOG.info("checkECP params: sClientId {}, sClientSecret {}, sHostCentral {}. and let's check sLogin_BankID()=", generalConfig.sLogin_BankID(),
                generalConfig.sPassword_BankID(), generalConfig.sHostCentral(), generalConfig.sLogin_BankID());
        
        
        byte[] content = IOUtils.toByteArray(attachmentStream);

        String soSignData = BankIDUtils
                .checkECP(/*bankIDConfig.sClientId()*/ "testIgov", /*bankIDConfig.sClientSecret()*/ "testIgovSecret", generalConfig.sHostCentral(),
                        content, attachmentRequested.getName());

        return soSignData;
    }

    private Attachment getAttachment(String attachmentId, String taskId, Integer nFile, String processInstanceId) {
        List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);
        Attachment attachmentRequested = null;
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getId().equalsIgnoreCase(attachmentId)) {
                attachmentRequested = attachments.get(i);
                break;
            }
            if (null != nFile && nFile.equals(i + 1)) {
                attachmentRequested = attachments.get(i);
                break;
            }
        }

        if (attachmentRequested == null && !attachments.isEmpty()) {
            attachmentRequested = attachments.get(0);
        }

        if (attachmentRequested == null) {
            throw new ActivitiObjectNotFoundException(
                    "Attachment for taskId '" + taskId + "' not found.",
                    Attachment.class);
        }
        return attachmentRequested;
    }

    private Attachment getAttachment(String attachmentId, String taskId, String processInstanceId) {
        List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);
        Attachment attachmentRequested = null;
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getId().equalsIgnoreCase(attachmentId)) {
                attachmentRequested = attachments.get(i);
                break;
            }
        }

        if (attachmentRequested == null) {
            throw new ActivitiObjectNotFoundException(
                    "Attachment for taskId '" + taskId + "' not found.",
                    Attachment.class);
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
    public
    @ResponseBody
    byte[] getAttachmentFromDbExecution(@RequestParam("taskId") String taskId, HttpServletResponse httpResponse)
            throws IOException {

        //получаем по задаче ид процесса
        HistoricTaskInstance historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        String processInstanceId = historicTaskInstanceQuery.getProcessInstanceId();
        if (processInstanceId == null) {
            throw new ActivitiObjectNotFoundException(String.format(
                    "ProcessInstanceId for taskId '{%s}' not found.", taskId),
                    Attachment.class);
        }

        //получаем по ид процесса сам процесс
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .includeProcessVariables()
                .singleResult();
        if (processInstance == null) {
            throw new ActivitiObjectNotFoundException(String.format(
                    "ProcessInstance for processInstanceId '{%s}' not found.", processInstanceId),
                    Attachment.class);
        }

        //получаем коллекцию переменных процеса и прикрепленный файл
        Map<String, Object> processVariables = processInstance.getProcessVariables();
        BuilderAttachModel attachModel = null;

        if (processVariables != null) {
            List<BuilderAttachModel> builderAttachModelList = (List) processVariables.get(
                    FileTaskUpload.BUILDER_ATACH_MODEL_LIST);

            if (builderAttachModelList != null) {
                attachModel = builderAttachModelList.get(0);
            }
        }

        if (attachModel == null) {
            throw new ActivitiObjectNotFoundException(String.format(
                    "ProcessVariable '{%s}' for processInstanceId '{%s}' not found.",
                    FileTaskUpload.BUILDER_ATACH_MODEL_LIST, processInstanceId));
        }

        //Помещаем параметры контента в header
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + attachModel.getOriginalFilename());
        httpResponse.setHeader("Content-Type", attachModel.getContentType() + ";charset=UTF-8");
        httpResponse.setContentLength(attachModel.getByteToStringContent().getBytes().length);

        return AbstractModelTask.contentStringToByte(attachModel.getByteToStringContent());
    }

    /**
     * прикрепляем к таске Attachment.
     *
     * @param file
     * @return
     * @throws org.activiti.rest.controller.ActivitiIOException
     */
    @RequestMapping(value = "/file/upload_file_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public
    @ResponseBody
    AttachmentEntityI putAttachmentsToExecution(@RequestParam(value = "taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description") String description) throws IOException {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (tasks != null && !tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee() : "kermit";
            LOG.debug("processInstanceId: " + processInstanceId + " taskId: " + taskId + "assignee: " + assignee);
        } else {
            LOG.error("There is no tasks at all!");

        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = file.getOriginalFilename();
        LOG.debug("sFilename=" + file.getOriginalFilename());
        sFilename = Renamer.sRenamed(sFilename);
        LOG.debug(
                "FileExtention: " + getFileExtention(file) + " fileContentType: " + file.getContentType() + "fileName: "
                        + sFilename);
        LOG.debug("description: " + description);

        Attachment attachment = taskService.createAttachment(file.getContentType()
                        + ";"
                        + getFileExtention(file),
                taskId,
                processInstanceId,
                sFilename,//file.getOriginalFilename()
                description, file.getInputStream());

        AttachmentEntityAdapter adapter = new AttachmentEntityAdapter();

        return adapter.apply(attachment);
    }

    @RequestMapping(value = "/file/upload_content_as_attachment", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public
    @ResponseBody
    AttachmentEntityI putTextAttachmentsToExecution(
            @RequestParam(value = "nTaskId") String taskId,
            @RequestParam(value = "sContentType", required = false, defaultValue = "text/html") String sContentType,
            @RequestParam(value = "sDescription") String description,
            @RequestParam(value = "sFileName") String sFileName,
            @RequestBody String sData) {

        String processInstanceId = null;
        String assignee = null;

        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        if (tasks != null && !tasks.isEmpty()) {
            Task task = tasks.iterator().next();
            processInstanceId = task.getProcessInstanceId();
            assignee = task.getAssignee() != null ? task.getAssignee() : "kermit";
            LOG.debug("processInstanceId: " + processInstanceId + " taskId: " + taskId + "assignee: " + assignee);
        } else {
            LOG.error("There is no tasks at all!");

        }

        identityService.setAuthenticatedUserId(assignee);

        String sFilename = sFileName;
        LOG.debug("sFilename=" + sFileName);
        sFilename = Renamer.sRenamed(sFilename);
        LOG.debug("FileExtention: " + getFileExtention(sFileName) + " fileContentType: " + sContentType + "fileName: "
                + sFilename);
        LOG.debug("description: " + description);

        Attachment attachment = taskService.createAttachment(sContentType + ";" + getFileExtention(sFileName),
                taskId,
                processInstanceId,
                sFilename,
                description, new ByteArrayInputStream(sData.getBytes(Charsets.UTF_8)));

        AttachmentEntityAdapter adapter = new AttachmentEntityAdapter();

        return adapter.apply(attachment);
    }

    /**
     * Получение статистики по бизнес
     * процессу за указанные период
     *
     * @param sID_BP_Name  - �?Д бизнес процесса
     * @param dateAt       - дата начала периода выборки
     * @param dateTo       - дата окончания периода выборки
     * @param nRowStart    - позиция начальной строки для
     *                     возврата (0 по умолчанию)
     * @param nRowsMax     - количество записей для
     *                     возврата (1000 по умолчанию)
     * @param httpResponse
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/file/download_bp_timing", method = RequestMethod.GET)
    @Transactional
    public void getTimingForBusinessProcessNew(@RequestParam(value = "sID_BP_Name") String sID_BP_Name,
            @RequestParam(value = "sDateAt") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @RequestParam(value = "bDetail", required = false, defaultValue = "true") Boolean bDetail,
            @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            HttpServletResponse httpResponse) throws IOException {

        if (sID_BP_Name == null || sID_BP_Name.isEmpty()) {
            LOG.error(String.format("Statistics for the business process '{%s}' not found.", sID_BP_Name));
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business process '" + sID_BP_Name + "' not found.",
                    Process.class);
        }
        SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyy-MM-ddHH-mm-ss");
        String fileName = sID_BP_Name + "_" + sdfFileName.format(Calendar.getInstance().getTime()) + ".csv";

        LOG.debug("File name to return statistics : {%s}", fileName);



        boolean isByFieldsSummary = saFieldSummary != null && !saFieldSummary.isEmpty();

        List<HistoricTaskInstance> foundResults;
        if (isByFieldsSummary) { //issue 916
            LOG.info(">>>saFieldsSummary=" + saFieldSummary);
            foundResults = historyService.createHistoricTaskInstanceQuery()
                    .taskCompletedAfter(dateAt)
                    .taskCompletedBefore(dateTo)
                    .processDefinitionKey(sID_BP_Name)
                    .list();
            List<List<String>> stringResults = fieldsSummaryUtil.getFieldsSummary(foundResults, saFieldSummary);
            //            httpResponse.setContentType("text/csv;charset=UTF-8");
            //            httpResponse.setHeader("Content-disposition", "attachment; filename=" + "[Summary]" + fileName);
            CSVWriter csvWriter = new CSVWriter(httpResponse.getWriter());
            for (List<String> line : stringResults) {
                csvWriter.writeNext(line.toArray(new String[line.size()]));
            }
            csvWriter.close();
            LOG.info(">>>>csv for saFieldSummary is complete.");
            return;
        } else {
            foundResults = historyService.createHistoricTaskInstanceQuery()
                    .taskCompletedAfter(dateAt)
                    .taskCompletedBefore(dateTo)
                    .processDefinitionKey(sID_BP_Name)
                    .listPage(nRowStart, nRowsMax);
        }
        httpResponse.setContentType("text/csv;charset=UTF-8");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);

        List<String> headers = new ArrayList<String>();
        String[] headersMainField = { "nID_Process", "sLoginAssignee", "sDateTimeStart", "nDurationMS", "nDurationHour",
                "sName" };
        headers.addAll(Arrays.asList(headersMainField));
        LOG.debug("headers: " + headers);

        Set<String> headersExtra = findExtraHeaders(bDetail, foundResults, headers);

        LOG.info("headers: " + headers);
        CSVWriter csvWriter = new CSVWriter(httpResponse.getWriter());
        csvWriter.writeNext(headers.toArray(new String[headers.size()]));

        if (foundResults != null && foundResults.size() > 0) {
            LOG.debug(String.format("Found {%s} completed tasks for business process {%s} for date period {%s} - {%s}",
                    foundResults.size(), sID_BP_Name, DATE_TIME_FORMAT.format(dateAt),
                    DATE_TIME_FORMAT.format(dateTo)));

            for (HistoricTaskInstance currTask : foundResults) {
                List<String> line = createCsvLine(bDetail, headersExtra, currTask);
                LOG.info("line: " + line);
                csvWriter.writeNext(line.toArray(new String[line.size()]));
            }
        } else {
            LOG.debug(String.format("No completed tasks found for business process {%s} for date period {%s} - {%s}",
                    sID_BP_Name, DATE_TIME_FORMAT.format(dateAt),
                    DATE_TIME_FORMAT.format(dateTo)));
        }
        csvWriter.close();
    }

    private List<String> createCsvLine(Boolean bDetail, Set<String> headersExtra, HistoricTaskInstance currTask) {
        List<String> line = new ArrayList<String>();
        line.add(currTask.getProcessInstanceId());
        line.add(currTask.getAssignee());
        Date startDate = currTask.getStartTime();
        line.add(DATE_TIME_FORMAT.format(startDate));
        line.add(String.valueOf(currTask.getDurationInMillis()));
        long durationInHours = currTask.getDurationInMillis() / MILLIS_IN_HOUR;
        line.add(String.valueOf(durationInHours));
        line.add(currTask.getName());

        if (bDetail) {
            LOG.debug("currTask: " + currTask.getId());
            HistoricTaskInstance details = historyService.createHistoricTaskInstanceQuery()
                    .includeProcessVariables()
                    .taskId(currTask.getId()).singleResult();
            for (String headerExtra : headersExtra) {
                String propertyValue = "";
                if (details != null && details.getProcessVariables() != null) {
                    Object variableValue = details.getProcessVariables().get(headerExtra);
                    if (variableValue != null) {
                        if (variableValue instanceof String) {
                            propertyValue = (String) variableValue;
                        } else {
                            propertyValue = String.valueOf(variableValue);
                        }
                    }
                }
                line.add(propertyValue);
            }
        }
        return line;
    }

    private Set<String> findExtraHeaders(Boolean bDetail, List<HistoricTaskInstance> foundResults, List<String> headers) {
        Set<String> headersExtra = new TreeSet<String>();
        if (bDetail) {
            for (HistoricTaskInstance currTask : foundResults) {

                HistoricTaskInstance details = historyService.createHistoricTaskInstanceQuery()
                        .includeProcessVariables().taskId(currTask.getId()).singleResult();
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
        }
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
     * @param bIncludeHistory   to include historic task instances. default value is true
     * @param httpResponse      http responce wrapper
     * @throws IOException in case of connection aborted with client
     *                     <p/>
     *                     example:
     *                     https://test.region.igov.org.ua/wf/service/rest/file/downloadTasksData?sID_BP=kiev_mreo_1&sDateAt=2015-06-28&sDateTo=2015-08-01&nASCI_Spliter=59&sID_Codepage=UTF8&saFields=nID_Task;bankIdPassport;bankIdlastName;bankIdfirstName;bankIdmiddleName;1;sDateCreate
     */
    @RequestMapping(value = "/file/downloadTasksData", method = RequestMethod.GET)
    @Transactional
    public void downloadTasksData(@RequestParam(value = "sID_BP") String sID_BP,
            @RequestParam(value = "sID_State_BP", required = false) String sID_State_BP,
            @RequestParam(value = "saFields") String saFields,
            @RequestParam(value = "nASCI_Spliter", required = false) String nASCI_Spliter,
            @RequestParam(value = "sFileName", required = false) String fileName,
            @RequestParam(value = "sID_Codepage", required = false, defaultValue = "win1251") String sID_Codepage,
            @RequestParam(value = "sDateCreateFormat", required = false, defaultValue = "yyyy-MM-dd HH:mm:ss") String sDateCreateFormat,
            @RequestParam(value = "sDateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @RequestParam(value = "bIncludeHistory", required = false, defaultValue = "true") Boolean bIncludeHistory,
            HttpServletResponse httpResponse) throws IOException {
        //1. validation
        if (StringUtils.isBlank(sID_BP)) {
            LOG.error("Wrong name of business task - {}", sID_BP);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task '" + sID_BP + "' not found. Wrong BP name.",
                    Task.class);
        }

        dateAt = validateDateAt(dateAt);
        dateTo = validateDateTo(dateTo);
        String separator = validateSeparator(sID_BP, nASCI_Spliter);
        Charset charset = validateCharset(sID_Codepage);

        //2. query
        TaskQuery query = taskService.createTaskQuery().processDefinitionKey(sID_BP).taskCreatedAfter(dateAt)
                .taskCreatedBefore(dateTo);
        
        if (sID_State_BP != null) {
            query = query.taskDefinitionKey(sID_State_BP);
        }
        List<Task> foundResults = query.listPage(nRowStart, nRowsMax);

        //3. response
        SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyy-MM-ddHH-mm-ss");
        fileName = fileName != null ? fileName
                : "data_BP-" + sID_BP + "_" + sdfFileName.format(Calendar.getInstance().getTime()) + ".txt";
        SimpleDateFormat sDateCreateDF = new SimpleDateFormat(sDateCreateFormat);

        LOG.debug("File name to return statistics : {}", fileName);

        httpResponse.setContentType("text/csv;charset=" + charset.name());
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);

        PrintWriter printWriter = new PrintWriter(httpResponse.getWriter());

        fillTheFile(sID_BP, dateAt, dateTo, foundResults, sDateCreateDF, printWriter, saFields, separator);
        if (Boolean.TRUE.equals(bIncludeHistory)){
        	Set<String> tasksIdToExclude = new HashSet<String>();
        	for (Task task : foundResults){
        		tasksIdToExclude.add(task.getId());
        	}
        	HistoricTaskInstanceQuery historicQuery = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(sID_BP).taskCreatedAfter(dateAt)
                	.taskCreatedBefore(dateTo).includeProcessVariables();
            if (sID_State_BP != null) {
                historicQuery.taskDefinitionKey(sID_State_BP);
            }
            List<HistoricTaskInstance> foundHistoricResults = historicQuery.listPage(nRowStart, nRowsMax);
            fillTheFileHistoricTasks(sID_BP, dateAt, dateTo, foundHistoricResults, sDateCreateDF, printWriter, saFields, separator, tasksIdToExclude);
        }

        printWriter.close();
    }

    private void fillTheFileHistoricTasks(String sID_BP, Date dateAt, Date dateTo,
            List<HistoricTaskInstance> foundResults, SimpleDateFormat sDateCreateDF, PrintWriter printWriter, String pattern,
            String separator, Set<String> tasksIdToExclude) {
        if (CollectionUtils.isEmpty(foundResults)) {
            LOG.info(String.format("No historic tasks found for business process %s for date period %s - %s",
                    sID_BP, DATE_TIME_FORMAT.format(dateAt), DATE_TIME_FORMAT.format(dateTo)));
            return;
        }

        LOG.info(String.format("Found %s historic tasks for business process %s for date period %s - %s",
                foundResults.size(), sID_BP, DATE_TIME_FORMAT.format(dateAt), DATE_TIME_FORMAT.format(dateTo)));

        List<String> fieldNames = Arrays.asList(pattern.split(";"));
        LOG.info("List of fields to retrieve: " + fieldNames.toString());
        LOG.info("Tasks to skip" + tasksIdToExclude);

        for (HistoricTaskInstance curTask : foundResults) {

        	if (tasksIdToExclude.contains(curTask.getId())){
        		LOG.info("Skipping historic task " + curTask.getId() + " from processing as it is already in the response");
        		continue;
        	}
            String currentRow = pattern;
            Map<String, Object> variables = curTask.getProcessVariables();
            LOG.info("Loaded historic variables for the task " + curTask.getId() + "|" + variables);
            currentRow = replaceFormProperties(currentRow, variables);

            currentRow = replaceReportFields(sDateCreateDF, curTask, currentRow);
            // replacing all the fields which were empty in the form with empty string
            currentRow = currentRow.replaceAll("\\$\\{.*?\\}", "");
            printWriter.println(currentRow.replaceAll(";", separator));
        }
    }
    
    private String replaceFormProperties(String currentRow, Map<String, Object> data) {
        for (Map.Entry<String, Object> property : data.entrySet()) {
            LOG.info(String.format(
                    "Matching property %s:%s with fieldNames", property.getKey(), property.getValue()));
            if (currentRow.contains("${" + property.getKey() + "}")) {
                LOG.info(String.format("Found field with id %s in the pattern. Adding value to the result",
                        "${" + property.getKey() + "}"));
                String sValue = property.getValue().toString();
                LOG.info("sValue=" + sValue);
                if (sValue != null) {
                    LOG.info(String.format("Replacing field with the value %s", sValue));
                    currentRow = currentRow.replace("${" + property.getKey() + "}", sValue);
                }

            }
        }
        return currentRow;
    }
    
    private void fillTheFile(String sID_BP, Date dateAt, Date dateTo,
            List<Task> foundResults, SimpleDateFormat sDateCreateDF, PrintWriter printWriter, String pattern,
            String separator) {
        if (CollectionUtils.isEmpty(foundResults)) {
            LOG.info(String.format("No tasks found for business process %s for date period %s - %s",
                    sID_BP, DATE_TIME_FORMAT.format(dateAt), DATE_TIME_FORMAT.format(dateTo)));
            return;
        }

        LOG.info(String.format("Found %s tasks for business process %s for date period %s - %s",
                foundResults.size(), sID_BP, DATE_TIME_FORMAT.format(dateAt), DATE_TIME_FORMAT.format(dateTo)));

        List<String> fieldNames = Arrays.asList(pattern.split(";"));
        LOG.info("List of fields to retrieve: " + fieldNames.toString());

        for (Task curTask : foundResults) {

            String currentRow = pattern;
            LOG.trace("Process task - {}", curTask);
            TaskFormData data = formService.getTaskFormData(curTask.getId());
            currentRow = replaceFormProperties(currentRow, data);

            currentRow = replaceReportFields(sDateCreateDF, curTask, currentRow);
            // replacing all the fields which were empty in the form with empty string
            currentRow = currentRow.replaceAll("\\$\\{.*?\\}", "");
            printWriter.println(currentRow.replaceAll(";", separator));
        }
    }

    private String replaceFormProperties(String currentRow, TaskFormData data) {
        for (FormProperty property : data.getFormProperties()) {
            LOG.info(String.format(
                    "Matching property %s:%s:%s with fieldNames", property.getId(), property.getName(),
                    property.getType().getName()));
            if (currentRow.contains("${" + property.getId() + "}")) {
                LOG.info(String.format("Found field with id %s in the pattern. Adding value to the result",
                        "${" + property.getId() + "}"));
                String sValue = "";
                String sType = property.getType().getName();
                LOG.info("sType=" + sType);
                if ("enum".equalsIgnoreCase(sType)) {
                    sValue = parseEnumProperty(property);
                } else {
                    sValue = property.getValue();
                }
                LOG.info("sValue=" + sValue);
                if (sValue != null) {
                    LOG.info(String.format("Replacing field with the value %s", sValue));
                    currentRow = currentRow.replace("${" + property.getId() + "}", sValue);
                }

            }
        }
        return currentRow;
    }

    private String replaceReportFields(SimpleDateFormat sDateCreateDF, Task curTask, String currentRow) {
        for (ReportField field : ReportField.values()) {
            if (currentRow.contains(field.getPattern())) {
                currentRow = field.replaceValue(currentRow, curTask, sDateCreateDF);
            }
        }
        return currentRow;
    }
    
    private String replaceReportFields(SimpleDateFormat sDateCreateDF, HistoricTaskInstance curTask, String currentRow) {
        for (ReportField field : ReportField.values()) {
            if (currentRow.contains(field.getPattern())) {
                currentRow = field.replaceValue(currentRow, curTask, sDateCreateDF);
            }
        }
        return currentRow;
    }

    private Date validateDateTo(Date dateTo) {
        if (dateTo == null) {
            dateTo = DateTime.now().toDate();
            LOG.debug("No dateTo was set, use - {}", dateTo);
        }
        return dateTo;
    }

    private Date validateDateAt(Date dateAt) {
        if (dateAt == null) {
            dateAt = DateTime.now().minusDays(1).toDate();
            LOG.debug("No dateAt was set, use - {}", dateAt);
        }
        return dateAt;
    }

    private Charset validateCharset(String sID_Codepage) {
        Charset charset;
        try {
            if (sID_Codepage.replaceAll("-", "").equalsIgnoreCase("win1251") || sID_Codepage.replaceAll("-", "")
                    .equalsIgnoreCase("CL8MSWIN1251")) {
                sID_Codepage = "CP1251";    //hack for alias
            }
            charset = Charset.forName(sID_Codepage);
            LOG.debug("use charset - {}", charset);
        } catch (IllegalArgumentException e) {
            LOG.error("Do not support charset - {}", sID_Codepage, e);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task for chatset '" + sID_Codepage + "' cannot be construct.",
                    Task.class, e);
        }
        return charset;
    }

    private String validateSeparator(String sID_BP, String nASCI_Spliter) {
        if (nASCI_Spliter == null) {
            return String.valueOf(Character.toChars(DEFAULT_REPORT_FIELD_SPLITTER));
        }
        if (!StringUtils.isNumeric(nASCI_Spliter)) {
            LOG.error("ASCI code is not a number {}", nASCI_Spliter);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task with name '" + sID_BP + "' not found. Wrong splitter.",
                    Task.class);
        }
        return String.valueOf(Character.toChars(Integer.valueOf(nASCI_Spliter)));
    }

    /**
     * Returns business processes which are belong to a specified user
     *
     * @param sLogin - login of user in user activity
     */
    @RequestMapping(value = "/getLoginBPs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String getBusinessProcessesForUser(@RequestParam(value = "sLogin") String sLogin) throws IOException {
        if (sLogin.isEmpty()) {
            LOG.error("Unable to found business processes for user with empty login");
            throw new ActivitiObjectNotFoundException(
                    "Unable to found business processes for user with empty login",
                    ProcessDefinition.class);
        }

        List<Map<String, String>> res = new LinkedList<Map<String, String>>();

        LOG.info(String.format("Selecting business processes for the user with login: %s", sLogin));

        List<ProcessDefinition> processDefinitionsList = repositoryService.createProcessDefinitionQuery().active()
                .latestVersion().list();
        if (CollectionUtils.isNotEmpty(processDefinitionsList)) {
            LOG.info(String.format("Found %d active process definitions", processDefinitionsList.size()));

            for (ProcessDefinition processDef : processDefinitionsList) {
                LOG.info("process definition id: " + processDef.getId());

                Set<String> candidateCroupsToCheck = new HashSet<String>();
                loadCandidateGroupsFromTasks(processDef, candidateCroupsToCheck);

                loadCandidateStarterGroup(processDef, candidateCroupsToCheck);

                findUsersGroups(sLogin, res, processDef, candidateCroupsToCheck);
            }
        } else {
            LOG.info("Have not found ative process definitions.");
        }

        String jsonRes = JSONValue.toJSONString(res);
        LOG.info("Result" + jsonRes);
        return jsonRes;
    }

    protected void findUsersGroups(String sLogin,
            List<Map<String, String>> res, ProcessDefinition processDef, Set<String> candidateCroupsToCheck) {
        for (String currGroup : candidateCroupsToCheck) {
            LOG.info(String.format("Checking whether user %s belongs to the group %s", sLogin, currGroup));
            User user = identityService.createUserQuery().userId(sLogin).memberOfGroup(currGroup).singleResult();
            if (user != null) {
                Map<String, String> process = new HashMap<String, String>();
                process.put("sID", processDef.getKey());
                process.put("sName", processDef.getName());
                LOG.info(String.format("Added record to response %s", process.toString()));
                res.add(process);
                break;
            } else {
                LOG.info(String.format("user %s is not in group %s", sLogin, currGroup));
            }
        }
    }

    protected void loadCandidateStarterGroup(ProcessDefinition processDef, Set<String> candidateCroupsToCheck) {
        List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDef.getId());
        LOG.info(
                String.format("Found %d identity links for the process %s", identityLinks.size(), processDef.getKey()));
        for (IdentityLink identity : identityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identity.getType())) {
                String groupId = identity.getGroupId();
                candidateCroupsToCheck.add(groupId);
                LOG.info(String.format("Added candidate starter group %s ", groupId));
            }
        }
    }

    protected void loadCandidateGroupsFromTasks(ProcessDefinition processDef, Set<String> candidateCroupsToCheck) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDef.getId());

        for (FlowElement flowElement : bpmnModel.getMainProcess().getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<String> candidateGroups = userTask.getCandidateGroups();
                if (candidateGroups != null && !candidateGroups.isEmpty()) {
                    candidateCroupsToCheck.addAll(candidateGroups);
                    LOG.info(String.format("Added candidate groups %s from user task %s", candidateGroups,
                            userTask.getId()));
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
            //@RequestParam(value = "nID_Attachment", required = false) String snID_Attachment,
            @RequestParam(value = "naID_Attachment", required = false) String snaID_Attachment,//naID_Attachment=1530717
            HttpServletRequest request, HttpServletResponse httpResponse)
            throws IOException, MessagingException, EmailException {

        //            Mail oMail = new Mail();
        oMail._To("bvv4ik@gmail.com");
        //oMail._To(sMailTo==null?"bvv4ik@gmail.com":sMailTo);
        oMail._Body(sBody == null ?
                "<a href=\"http:\\\\google.com\">Google</a> It's test Проверка ! ��� ��������!" :
                sBody);

        LOG.info("oMail.getHead()=" + oMail.getHead());
        LOG.info("oMail.getBody()=" + oMail.getBody());
        LOG.info("oMail.getAuthUser()=" + oMail.getAuthUser());
        LOG.info("oMail.getAuthPassword()=" + oMail.getAuthPassword());
        LOG.info("oMail.getFrom()=" + oMail.getFrom());
        LOG.info("oMail.getTo()=" + oMail.getTo());
        LOG.info("oMail.getHost()=" + oMail.getHost());
        LOG.info("oMail.getPort()=" + oMail.getPort());

        //            oMail.init();
/*            if(bHTML==true){
         log.info("bHTML");
         oMail._BodyAsHTML();
         }else{
         log.info("!bHTML");
         oMail._BodyAsText();
         }
         */

        if (snaID_Attachment != null) {
            String[] ansID_Attachment = snaID_Attachment.split(",");
            for (String snID_Attachment : ansID_Attachment) {
                //anID_Attachment.split()
                //String snID_Attachment;
                Attachment oAttachment = taskService.getAttachment(snID_Attachment);
                String sFileName = oAttachment.getName();
                String sFileExt = oAttachment.getType().split(";")[0];
                String sDescription = oAttachment.getDescription();
                LOG.info("oAttachment.getId()=" + oAttachment.getId() + ", sFileName=" + sFileName + ", sFileExt="
                        + sFileExt + ", sDescription=" + sDescription);
                InputStream oInputStream = taskService.getAttachmentContent(oAttachment.getId());
                DataSource oDataSource = new ByteArrayDataSource(oInputStream, sFileExt);

                //oMail._Attach(oDataSource, sFileName + "." + sFileExt, sDescription);
                oMail._Attach(oDataSource, sFileName + "." + sFileExt, sDescription);
            }
        }

        oMail.send();
    }

    @RequestMapping(value = "/getPatternFile", method = RequestMethod.GET)
    public void getPatternFile(
            @RequestParam(value = "sPathFile") String sPathFile,
            @RequestParam(value = "sContentType", required = false) String sContentType,
            HttpServletResponse response) throws ActivitiRestException {

        try {
            String contentType = sContentType == null ? Util.PATTERN_DEFAULT_CONTENT_TYPE : sContentType;
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.toString());
            byte[] resultObj = Util.getPatternFile(sPathFile);
            response.getOutputStream().write(resultObj);
        } catch (IllegalArgumentException | IOException e) {
            ActivitiRestException newErr = new ActivitiRestException("BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        } catch (Exception e) {
            ActivitiRestException newErr = new ActivitiRestException("SYSTEM_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            throw newErr;
        }
    }

    /**
     * issue 808. сервис ЗАПРОСА полей, требующих уточнения, c отсылкой уведомления гражданину
     *
     * @param nID_Protected - номер-�?Д заявки (защищенный)
     * @param saField       -- строка-массива полей (например: "[{'id':'sFamily','type':'string','value':'Белявский'},{'id':'nAge','type':'long'}]")
     * @param sMail         -- строка электронного адреса гражданина
     * @param sHead         -- строка заголовка письма //опциональный (если не задан, то "Необходимо уточнить данные")
     * @param sBody         -- строка тела письма //опциональный (если не задан, то пустота)
     * @throws ActivitiRestException
     * @throws CRCInvalidException
     */
    @RequestMapping(value = "/setTaskQuestions", method = RequestMethod.GET)
    public
    @ResponseBody
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
        sBody = sBody == null ? "" : sBody;
        String sToken = generateToken();
        try {
            LOG.info("try to update historyEvent_service by sID_Order=%s, nID_Protected-%s and nID_Server=%s and nID_Process=%s",
                    sID_Order, nID_Protected, nID_Server, nID_Process);
            if (nID_Protected == null && nID_Process != null) {
                nID_Protected = AlgorithmLuna.getProtectedNumber(nID_Process);
            }
            String historyEventService = updateHistoryEvent_Service(sID_Order, nID_Protected, nID_Server,
                    saField, sHead, sBody, sToken, "Запит на уточнення даних");
            LOG.info("....ok! successfully update historyEvent_service! event = " + historyEventService);
            sendEmail(sHead, createEmailBody(nID_Protected, saField, sBody, sToken), sMail);
            setInfo_ToActiviti("" + nID_Protected / 10, saField, sBody);//todo ask about sID_order (889)
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    "error during setTaskQuestions: " + e.getMessage(), e,
                    HttpStatus.FORBIDDEN);
        }
    }

    private String createEmailBody(Long nID_Protected, String soData, String sBody, String sToken)
            throws UnsupportedEncodingException {
        StringBuilder emailBody = new StringBuilder(sBody);
        emailBody.append("<br/>")
                .append(createTable(soData))
                .append("<br/>");
        String link = (new StringBuilder(generalConfig.sHostCentral())
                .append("/order/search?nID=")
                .append(nID_Protected)
                .append("&sToken=")
                .append(sToken))
                .toString();
        emailBody.append(link)
                .append("<br/>");
        return emailBody.toString();
    }

    private void sendEmail(String sHead, String sBody, String recipient) throws EmailException {
        oMail.reset();
        oMail._To(recipient)
                ._Head(sHead)
                ._Body(sBody);
        oMail.send();
    }

    private String createTable(String soData) throws UnsupportedEncodingException {
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
                    .append(record.opt("type") != null ? record.get("type").toString() : "??")
                    .append("</td><td>")
                    .append(record.opt("value") != null ? record.get("value").toString() : "")
                    .append("</td></tr>");
        }
        tableStr.append("</table>");
        return tableStr.toString();
    }

    //steals from DocumentAccessDaoImpl :)
    private String generateToken() {
        // 97-122 small character
        // 65-90 big character
        // 48-57 number
        StringBuilder os = new StringBuilder();
        Random ran = new Random();
        for (int i = 1; i <= 20; i++) {
            int a = ran.nextInt(3) + 1;
            switch (a) {
            case 1:
                int num = ran.nextInt((57 - 48) + 1) + 48;
                os.append((char) num);
                break;
            case 2:
                int small = ran.nextInt((122 - 97) + 1) + 97;
                os.append((char) small);
                break;
            case 3:
                int big = ran.nextInt((90 - 65) + 1) + 65;
                os.append((char) big);
                break;
            }
        }
        return os.toString();
    }

    @RequestMapping(value = "/setTaskAnswer", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskAnswer(
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @RequestParam(value = "saField") String saField,
            @RequestParam(value = "sToken") String sToken,
            @RequestParam(value = "sHead", required = false) String sHead,
            @RequestParam(value = "sBody", required = false) String sBody) throws ActivitiRestException {

        try {
            LOG.info("try to find history event_service by sID_Order=%s, nID_Protected-%s and nID_Server=%s", sID_Order,
                    nID_Protected, nID_Server);
            String historyEvent = historyEventService.getHistoryEvent(sID_Order, nID_Protected, nID_Server);
            LOG.info("....ok! successfully get historyEvent_service! event=" + historyEvent);
            JSONObject fieldsJson = new JSONObject(historyEvent);
            String processInstanceID = fieldsJson.get("nID_Task").toString();
            sHead = sHead != null ?
                    sHead :
                    "На заявку " + fieldsJson.getString("sID_Order") + " дана відповідь громаданином";
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
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceID).list();

            if (tasks != null) {
                runtimeService.setVariable(processInstanceID, "sAnswer", sBody);
                LOG.info("Added variable sAnswer to the process " + processInstanceID);

                LOG.info("Found " + tasks.size() + " tasks by nID_Protected...");
                for (Task task : tasks) {
                    LOG.info("task;" + task.getName() + "|" + task.getDescription() + "|" + task.getId());
                    TaskFormData data = formService.getTaskFormData(task.getId());
                    Map<String, String> newProperties = new HashMap<String, String>();
                    for (FormProperty property : data.getFormProperties()) {
                        if (property.isWritable()) {
                            newProperties.put(property.getId(), property.getValue());
                        }
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject record = jsonArray.getJSONObject(i);
                        newProperties.put((String) record.get("id"), (String) record.get("value"));
                        LOG.info("Set variable " + record.get("id") + " with value " + record.get("value"));
                    }
                    LOG.info("Updating form data for the task " + task.getId() + "|" + newProperties);
                    formService.saveFormData(task.getId(), newProperties);
                }
            }
            LOG.info("try to find history event_service by sID_Order=%s, nID_Protected-%s and nID_Server=%s", sID_Order,
                    nID_Protected, nID_Server);
            historyEvent = updateHistoryEvent_Service(sID_Order, nID_Protected, nID_Server, saField, sHead, null,
                    null, "Відповідь на запит по уточненню даних");
            LOG.info("....ok! successfully get historyEvent_service! event=" + historyEvent);
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e,
                    HttpStatus.FORBIDDEN);
        }
    }

    private String updateHistoryEvent_Service(String sID_Order, Long nID_Protected, Integer nID_Server,
            String saField, String sHead, String sBody, String sToken, String sID_Status) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order);
        params.put("nID_Protected", nID_Protected != null ? "" + nID_Protected : null);
        params.put("nID_Server", nID_Server != null ? "" + nID_Server : null);
        params.put("soData", saField);
        params.put("sHead", sHead);
        params.put("sBody", sBody);
        params.put("sToken", sToken);
        params.put("sID_Status", sID_Status);
        return historyEventService.updateHistoryEvent(null, sID_Status, true, params);
    }

    private void setInfo_ToActiviti(String snID_Process, String saField, String sBody) {
        try {
            LOG.info(String.format("try to set saField=%s and sBody=%s to snID_Process=%s", saField, sBody,
                    snID_Process));
            runtimeService.setVariable(snID_Process, "saFieldQuestion", saField);
            runtimeService.setVariable(snID_Process, "sQuestion", sBody);
            LOG.info(String.format("completed set saField=%s and sBody=%s to snID_Process=%s", saField, sBody,
                    snID_Process));
        } catch (Exception ex) {
            LOG.error("error during set variables to Activiti!", ex);
        }
    }

}
