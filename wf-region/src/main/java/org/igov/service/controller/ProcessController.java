/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.igov.analytic.model.access.AccessGroup;
import org.igov.analytic.model.access.AccessUser;
import org.igov.analytic.model.attribute.*;
import org.igov.analytic.model.process.CustomProcess;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.process.ProcessDao;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
import org.igov.io.db.kv.analytic.impl.FileMongoStorageAnalytic;
import org.igov.io.db.kv.statical.IFileStorage;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.service.ArchiveServiceImpl;
import org.igov.service.migration.MigrationService;
import org.igov.util.VariableMultipartFile;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import org.igov.model.action.task.core.ProcessIdCover;

/**
 * @author olga
 */
@Controller
@Api(tags = {"ProcessController - процессы и задачи"})
@RequestMapping(value = "/analytic/process")
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    private static final String JSON_TYPE = "Accept=application/json";

    @Autowired
    private ProcessDao processDao;

    @Autowired
    private SourceDBDao sourceDBDao;

    @Autowired
    private AttributeTypeDao attributeTypeDao;

    @Autowired
    private AttributeDao attributeDao;

    @Autowired
    private Attribute_FileDao attribute_FileDao;

    @Autowired
    private FileMongoStorageAnalytic analyticFileDataStorage;

    @Autowired
    private IFileStorage durableFileStorage;

    @Autowired
    private ArchiveServiceImpl archiveService;

    @Autowired
    HistoryService historyService;

    @Autowired
    private MigrationService migrationService;

    @ApiOperation(value = "/migration", notes = "#### Migration - миграция закрытых данных с активной БД в аналитическую")
    @RequestMapping(value = "/migration", method = RequestMethod.GET)
    public void migrate(@RequestParam(value = "processId", required = false) String processId) {
        LOG.info("Inside /migration service");
        migrationService.migrateOldRecords(processId);
    }


    @ApiOperation(value = "/backup", notes = "##### Process - сохранение процесса #####\n\n")
    @RequestMapping(value = "/backup", method = RequestMethod.GET)
    public
    @ResponseBody
    void backup() throws Exception {
        LOG.info("/backup!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        archiveService.archiveData();
        LOG.info("/backup ok!!!");
    }

    //http://localhost:8080/wf-region/service/analytic/process/duplicate
    @RequestMapping(value = "/duplicate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String duplicate() {
        StringBuilder stringBuilder = new StringBuilder();

        List<HistoricProcessInstance> processInstances = historyService.createHistoricProcessInstanceQuery().finished().list();

        for (HistoricProcessInstance instance : processInstances) {
            stringBuilder.append(createNewArchiveProcess(instance)).append("\n").append(createNewCustomArchiveProcess(instance)).append("\n-------------------");

        }
        return "Size of processInstances: " + processInstances.size() + "\n" + stringBuilder.toString();
    }

    //TODO rewrite this method as it provides multiple objects each time it is called
    private Process createNewArchiveProcess(HistoricProcessInstance instance) {
        Process archiveProcess = new Process();
        SourceDB sourceDB = new SourceDB();
        sourceDB.setName("iGov");
        archiveProcess.setoSourceDB(sourceDB);
        archiveProcess.setoDateStart(new DateTime(instance.getStartTime()));
        archiveProcess.setoDateFinish(new DateTime(instance.getEndTime()));
        String currentProcessInstanceId = instance.getId();

        List<HistoricVariableInstance> variablesList = historyService.createHistoricVariableInstanceQuery().processInstanceId(currentProcessInstanceId).list();
        archiveProcess.setaAttribute(createAttributeForArchiveProcess(variablesList));


        return archiveProcess;
    }

    //TODO write this method up to the end, it doesn't work now, ask Olga
    private List<Attribute> createAttributeForArchiveProcess(List<HistoricVariableInstance> variableInstances) {
        List<Attribute> resultList = new LinkedList<>();

        for (HistoricVariableInstance instance : variableInstances) {
            Attribute attribute = new Attribute();
        }
        return null;
    }


    private CustomProcess createNewCustomArchiveProcess(HistoricProcessInstance instance) {
        CustomProcess archiveCustomProcess = new CustomProcess();

        archiveCustomProcess.setnDuration(instance.getDurationInMillis());
        archiveCustomProcess.setsBusinessKey(instance.getBusinessKey());
        archiveCustomProcess.setsDeleteReason(instance.getDeleteReason());
        archiveCustomProcess.setsName(instance.getName());
        archiveCustomProcess.setsTenantId(instance.getTenantId());
        archiveCustomProcess.setsSuperProcessInstanceId(instance.getSuperProcessInstanceId());
        archiveCustomProcess.setsStartActivityId(instance.getStartActivityId());
        archiveCustomProcess.setsStartUserId(instance.getStartUserId());
        archiveCustomProcess.setsProcessDefinitionId(instance.getProcessDefinitionId());
        archiveCustomProcess.setsProcessInstanceId(instance.getProcessDefinitionId());

        archiveCustomProcess.setoProcess(createNewArchiveProcess(instance));
        return archiveCustomProcess;
    }

    //http://localhost:8080/wf-region/service/analytic/process/getProcesses?sID_=1
    //http://localhost:8080/wf-region/service/analytic/process/getProcesses?sID_=1
    @ApiOperation(value = "/getProcesses", notes = "##### Process - получение процесса #####\n\n")
    @RequestMapping(value = "/getProcesses", method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    List<Process> getProcesses(
            @ApiParam(value = "внутренний ид заявки", required = true) @RequestParam(value = "sID_") String sID_,
            @ApiParam(value = "ид источника", required = false) @RequestParam(value = "nID_Source", required = false) Long nID_Source) {
        LOG.info("/getProcess!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        List<Process> result = new ArrayList<>();
        try {
            LOG.info("/getProcess!!!!!!!!!!!!!!!!!!!!string sID_: " + sID_.trim());
            sID_ = sID_.trim().toUpperCase();
            if (sID_.length() >= 3) {
                if (sID_.indexOf("-") == 1) { //передали sID_Order вместо nID_Process
                    Long nID_Process = new ProcessIdCover(sID_, null, null, null).nID_Process();
                    sID_ = String.valueOf(nID_Process);
                }
                List<Process> processes = processDao.findAllBy("sID_", sID_);
                LOG.info("processes: " + processes.size());
                result.addAll(processes);
            }
        } catch (Exception ex) {
            LOG.error("ex: ", ex);
        }
        return result;
    }

    private Process createStub() {
        LOG.info("/createStub!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        Process process = new Process();
        ProcessTask processTask = new ProcessTask();
        Attribute attribute = new Attribute();
        Attribute attribute1 = new Attribute();
        Attribute_StringShort attribute_StringShort = new Attribute_StringShort();
        Attribute_File attribute_File = new Attribute_File();
        AccessGroup accessGroup = new AccessGroup();
        AccessUser accessUser = new AccessUser();
        SourceDB sourceDB = new SourceDB();
        AttributeType attributeType = new AttributeType();
        AttributeType attributeType1 = new AttributeType();
        //---------------------------
        process.setId(new Long(1));
        process.setoDateStart(new DateTime());
        process.setoDateFinish(new DateTime());
        process.setoSourceDB(sourceDB);
        process.setsID_("test");
        process.setsID_Data("test");

        List<ProcessTask> tasks = new ArrayList();
        tasks.add(processTask);
        process.setaProcessTask(tasks);

        List<Attribute> attributes = new ArrayList();
        attributes.add(attribute);
        attributes.add(attribute1);
        process.setaAttribute(attributes);
        //process.setaAttribute(attributes);
        //-------------------------------
        processTask.setId(new Long(1));
        processTask.setoDateStart(new DateTime());
        processTask.setoDateFinish(new DateTime());
        processTask.setsID_("test");
        Set<AccessGroup> accessGroups = new HashSet<>();
        accessGroups.add(accessGroup);
        processTask.setaAccessGroup(accessGroups);
        Set<AccessUser> accessUsers = new HashSet<>();
        accessUsers.add(accessUser);
        processTask.setaAccessUser(accessUsers);
        //------------------------------
        attribute.setId(new Long(1));
        attribute.setoAttributeType(attributeType);
        //attribute.setoAttribute_StingShort(attribute_StringShort);
        attribute.setoAttribute_File(attribute_File);
        attribute.setsID_("test");
        attribute.setName("test");

        attribute1.setId(new Long(2));
        attribute1.setoAttributeType(attributeType1);
        attribute1.setoAttribute_StringShort(attribute_StringShort);
        //attribute.setoAttribute_File(attribute_File);
        attribute1.setsID_("test");
        attribute1.setName("test");
        //------------------------------
        attribute_StringShort.setId(new Long(1));
        attribute_StringShort.setsValue("attribute_StringShort");
        //------------------------------
        attribute_File.setId(new Long(1));
        attribute_File.setsID_Data("test");
        attribute_File.setsFileName("test");
        attribute_File.setsContentType("pdf");
        attribute_File.setsExtName("txt");
        //-------------------------------
        accessGroup.setId(new Long(1));
        accessGroup.setsID("test");
        //--------------------------------
        accessUser.setId(new Long(1));
        accessUser.setsID("test");
        //--------------------------------
        sourceDB.setId(new Long(1));
        sourceDB.setName("Gorsovet");
        attributeType.setId(new Long(7));
        attributeType.setName("File");
        attributeType1.setId(new Long(3));
        attributeType1.setName("StingShort");
        return process;
    }

    //http://localhost:8080/wf-region/service/analytic/process/getFile?nID_Attribute_File=1
    @ApiOperation(value = "/getFile", notes = "##### File - получение контента файла #####\n\n")
    @RequestMapping(value = "/getFile", method = RequestMethod.GET, headers = {JSON_TYPE})
    public
    @ResponseBody
    byte[] getFile(
            @ApiParam(value = "внутренний ид заявки", required = true) @RequestParam(value = "nID_Attribute_File") Long nID_Attribute_File,
            HttpServletResponse httpResponse) throws RecordNotFoundException {
        //получение через дао из таблички с файлами файлов
        LOG.info("/getFile!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        VariableMultipartFile multipartFile = null;
        try {
            Optional<Attribute_File> attribute_File = attribute_FileDao.findById(nID_Attribute_File);
            if (attribute_File.isPresent()) {
                Attribute_File file = attribute_File.get();
                multipartFile = new VariableMultipartFile(
                        analyticFileDataStorage.openFileStream(String.valueOf(file.getsID_Data())),
                        file.getsFileName(), file.getsFileName() + "." + file.getsExtName(), file.getsContentType());
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setHeader("Content-disposition", "attachment; filename=" + multipartFile.getName());
                //httpResponse.setHeader("Content-Type", "application/octet-stream");
                httpResponse.setHeader("Content-Type", multipartFile.getContentType());
                httpResponse.setContentLength(multipartFile.getBytes() != null ? multipartFile.getBytes().length : 0);
            }
            LOG.info("multipartFile: " + multipartFile);
            return ((multipartFile != null && multipartFile.getBytes() != null) ?
                    multipartFile.getBytes() :
                    "".getBytes());
        } catch (Exception ex) {
            LOG.error("!!!Error: ", ex);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse
                    .setHeader("Content-disposition", "attachment; filename=fileNotFound.txt"); //"Content-Disposition"
            //httpResponse.setHeader("Content-Type", "application/octet-stream");
            httpResponse.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
            //httpResponse.setContentLength(10);
            return ("error: " + ex.getMessage()).getBytes();
        }
    }
}