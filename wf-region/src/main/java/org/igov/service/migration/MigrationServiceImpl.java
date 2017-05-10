package org.igov.service.migration;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.igov.analytic.model.attribute.*;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.process.*;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.source.SourceDB;
import org.igov.model.core.Entity;
import org.igov.model.core.EntityDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dpekach on 01.05.17.
 * <p>
 * <p>Service is responsible for migrating outdated data from activiti historic tables to the next 4 analytic tables:
 * Process, ProcessTask, CustomProcess, CustomProcessTask. Tables with prefix 'Custom' are consist of fields,
 * that are present in act_hi_*, but are absent in corresponding 'Process' tables.
 * Table 'Config' serves for backup aims: it stores the last successfully migrated start_time_, so that
 * migration process shouldn't be started from the very beginning.</p>
 * <p>
 * <p>
 * <p>Migration algorithm</p>:
 * 0) Get last process_instance_id from 'Config' table: if process with such id is present in act_hi_procinst, delete it
 * 1) Get ordered by process_instance_id list of historic processes from act_hi_procinst;
 * 2) Fill Process/CustomProcess and ProcessTask/CustomProcessTask beans;
 * 3) Save only Process bean with ProcessDao;
 * 4) If populating analytic tables succeeds, then 'Config' table is updated with last process_instance_id
 * 5) Corresponding record is deleted from act_hi_procinst & act_hi_taskinst (this option is disabled in development mode)
 * <p>
 * Attribute & Attribute{*type*} это к соотношению CustomProcess & CustomProcessTask
 */
@Service
public class MigrationServiceImpl implements MigrationService {

    class AsyncUpdate implements Runnable {
        private final DateTime startDateTimeFromProcess;

        AsyncUpdate(DateTime startDateTimeFromProcess) {
            this.startDateTimeFromProcess = startDateTimeFromProcess;
        }

        @Override
        public void run() {
            updateConfigTable(startDateTimeFromProcess);
        }
    }

    private final HistoryService historyService;

    private List<HistoricProcessInstance> historicProcessList;

    private ProcessDao processDao;

    private ConfigDao configDao;

    private final static Logger LOG = LoggerFactory.getLogger(MigrationServiceImpl.class);

    @Autowired
    public MigrationServiceImpl(HistoryService historyService, ProcessDao processDao, ConfigDao configDao) {
        this.historyService = historyService;
        this.processDao = processDao;
        this.configDao = configDao;
    }

    @Override
    public void migrateOldRecords() {
        historicProcessList = getHistoricProcessList();
        prepareAndSave(historicProcessList);
    }

    private List<HistoricProcessInstance> getHistoricProcessList() {
        DateTime startTime = getStartTime();
        String sqlSelect = composeSql(startTime);
        return historyService.createNativeHistoricProcessInstanceQuery().sql(sqlSelect).list();
    }

    private DateTime getStartTime() {
        DateTime startDateFromConfig = getStartDate(Config.class);
        DateTime startDateFromProcess = getStartDate(Process.class);

        DateTime startTime;
        if (startDateFromConfig.isBefore(startDateFromProcess)) {
            startTime = startDateFromProcess;
//            Thread asyncUpdate = new Thread(new AsyncUpdate(startDateFromProcess));
//            asyncUpdate.start();
        } else {
            startTime = startDateFromConfig;
        }
        return startTime;
    }

    private void updateConfigTable(DateTime startDateFromProcess) {
        Config config = new Config();
        config.setsValue(startDateFromProcess.toString("yyyy-MM-dd HH:mm:ss"));
        configDao.saveOrUpdate(config);
    }

    private String composeSql(DateTime startTime) {
        startTime = startTime.plusDays(3);
        return "SELECT * from act_hi_procinst where start_time_ < TIMESTAMP \' 2016-07-23 00:00:00\' AND proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null AND proc_inst_id_ =\'22317510\'";
//        return "SELECT * from act_hi_procinst where start_time_ < TIMESTAMP \' "
//                + startTime.toString("yyyy-MM-dd HH:mm:ss")
//                + "\' AND proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null";
    }

    private DateTime getStartDate(Class<?> clazz) {
        DateTime time;
        if (clazz == Config.class) {
            Config config = configDao.findLatestConfig();
            String dateTime = config.getsValue();
            time = new DateTime(dateTime);//не уверен, нужно тесты написать
            return time;
        } else {
            Process process = processDao.findLatestProcess();
            time = process.getoDateStart();
            return time;
        }
    }

    private List<Process> prepareAndSave(List<HistoricProcessInstance> historicProcessList) {
        List<Process> resultList = new ArrayList<>(historicProcessList.size());
        for (HistoricProcessInstance historicProcess : historicProcessList) {
            Process processForSave = createProcessForSave(historicProcess);
            resultList.add(processForSave);

//            processDao.saveOrUpdate(processForSave);
//            Thread asyncUpdate = new Thread(new AsyncUpdate(processForSave.getoDateStart()));
//            asyncUpdate.start();
        }
        return resultList;
    }

    private SourceDB getSourceDBForIGov() {
        SourceDB sourceDB = new SourceDB();
        sourceDB.setName("iGov");
        return sourceDB;
    }

    private Process createProcessForSave(HistoricProcessInstance historicProcess) {
        Process process = new Process();

        process.setsID_(historicProcess.getBusinessKey());//спросить
        process.setoDateStart(new DateTime(historicProcess.getStartTime()));
        process.setoDateFinish(new DateTime(historicProcess.getEndTime()));
        process.setoSourceDB(getSourceDBForIGov());
        process.setaAttribute(createAttributesForProcess(historicProcess.getProcessVariables(), process, null));
        String processInstanceId = historicProcess.getId();
        List<HistoricVariableInstance> variableInstanceList = historyService.
                createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).orderByVariableName().asc().list();
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        List<ProcessTask> processTaskList = new ArrayList<>(taskInstanceList.size());
        taskInstanceList.forEach(taskInstance -> {
            ProcessTask processTask = createProcessTaskToInsert(taskInstance, process);
            processTaskList.add(processTask);
            //???????
            CustomProcess customProcess = createCustomProcessToInsert(historicProcess, process);
            //???????
            CustomProcessTask customProcessTask = createCustomProcessTaskToInsert(taskInstance, processTask);
        });

        process.setaProcessTask(processTaskList);
        return process;
    }

    private List<Attribute> createAttributesForProcess(Map<String, Object> attributes, Process process, ProcessTask processTask) {
        List<Attribute> resultList = new ArrayList<>(attributes.size());
        attributes.forEach((id, value) -> {
            Attribute attribute = new Attribute();
            if (process != null)
                attribute.setoProcess(process);
            else
                attribute.setoProcessTask(processTask);
            attribute.setName(id);
            attribute.setoAttributeType(getAttributeType(value, attribute));
            resultList.add(attribute);
        });

        return resultList;

    }


    //TODO write generics for this method
    private AttributeType getAttributeType(Object obj, Attribute attribute) {
        AttributeType type = new AttributeType();
        Class<?> clazz = obj.getClass();
        if (clazz.getSimpleName().equalsIgnoreCase("string")) {
            String string = (String) obj;
            if (string.length() < 255) {
                type.setName("StringShort");
                Attribute_StringShort shortString = new Attribute_StringShort();
                shortString.setsValue(string);
                shortString.setoAttribute(attribute);
            } else {
                type.setName("StringLong");
                Attribute_StringLong longString = new Attribute_StringLong();
                longString.setsValue(string);
                longString.setoAttribute(attribute);
            }
        }

        if (clazz.getSimpleName().equalsIgnoreCase("integer")) {
            type.setName("Integer");
            Attribute_Integer integer = new Attribute_Integer();
            integer.setnValue((Integer) obj);
            integer.setoAttribute(attribute);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("boolean")) {
            type.setName("Boolean");
            Attribute_Boolean boolean_attr = new Attribute_Boolean();
            boolean_attr.setbValue((Boolean) obj);
            boolean_attr.setoAttribute(attribute);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("date")) {
            type.setName("Date");
            Attribute_Date date_attr = new Attribute_Date();
            date_attr.setoValue((DateTime) obj);
            date_attr.setoAttribute(attribute);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("float")) {
            type.setName("Float");
            Attribute_Float float_attr = new Attribute_Float();
            float_attr.setnValue((Double) obj);
            float_attr.setoAttribute(attribute);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("file")) {

        }

        return type;
    }

    private CustomProcess createCustomProcessToInsert(HistoricProcessInstance historicProcess, Process process) {
        CustomProcess customProcess = new CustomProcess();

        customProcess.setnDuration(historicProcess.getDurationInMillis());
        customProcess.setoProcess(process);
        customProcess.setsDeleteReason(historicProcess.getDeleteReason());
        customProcess.setsName(historicProcess.getName());
        customProcess.setsEndActivityId(historicProcess.getEndActivityId());
        customProcess.setsStartActivityId(historicProcess.getStartActivityId());
        customProcess.setsTenantId(historicProcess.getTenantId());
        customProcess.setsProcessDefinitionId(historicProcess.getProcessDefinitionId());
        customProcess.setsProcessInstanceId(historicProcess.getId());
        customProcess.setsSuperProcessInstanceId(historicProcess.getSuperProcessInstanceId());
        customProcess.setsStartUserId(historicProcess.getStartUserId());
        customProcess.setsBusinessKey(historicProcess.getBusinessKey());//спросить

        return customProcess;
    }


    private CustomProcessTask createCustomProcessTaskToInsert(HistoricTaskInstance taskInstance, ProcessTask processTask) {
        CustomProcessTask customProcessTask = new CustomProcessTask();

        customProcessTask.setoProcessTask(processTask);
        customProcessTask.setnDuration(taskInstance.getDurationInMillis());
        customProcessTask.setnFormKey(taskInstance.getFormKey());
        customProcessTask.setnPriority(taskInstance.getPriority());
        customProcessTask.setoClaimTime(new DateTime(taskInstance.getClaimTime()));
        customProcessTask.setoDueDate(new DateTime(taskInstance.getDueDate()));
        customProcessTask.setsAssignee(taskInstance.getAssignee());
        customProcessTask.setsCategory(taskInstance.getCategory());
        customProcessTask.setsDeleteReason(taskInstance.getDeleteReason());
        customProcessTask.setsDescription(taskInstance.getDescription());
        customProcessTask.setsExecutionId(taskInstance.getExecutionId());
        customProcessTask.setsName(taskInstance.getName());
        customProcessTask.setsOwner(taskInstance.getOwner());
        customProcessTask.setsParentTaskId(taskInstance.getParentTaskId());
        customProcessTask.setsProcessDefinitionId(taskInstance.getProcessDefinitionId());
        customProcessTask.setsProcessInstanceId(taskInstance.getProcessInstanceId());
        customProcessTask.setsTaskDefinitionKey(taskInstance.getTaskDefinitionKey());
        customProcessTask.setsTenantId(taskInstance.getTenantId());

        return customProcessTask;
    }

    private ProcessTask createProcessTaskToInsert(HistoricTaskInstance taskInstance, Process process) {
        ProcessTask processTask = new ProcessTask();
        processTask.setoProcess(process);
        processTask.setoDateStart(new DateTime(taskInstance.getStartTime()));
        processTask.setoDateFinish(new DateTime(taskInstance.getEndTime()));
        Map<String, Object> attributes = taskInstance.getTaskLocalVariables();

        processTask.setsID_(null);//спросить
        processTask.setaAccessGroup(null);//спросить
        processTask.setaAccessUser(null);//спросить
        processTask.setaAttribute(createAttributesForProcess(attributes, null, processTask));
        return processTask;
    }


    public List<HistoricProcessInstance> getTaskList() {
        return historicProcessList;
    }

}
