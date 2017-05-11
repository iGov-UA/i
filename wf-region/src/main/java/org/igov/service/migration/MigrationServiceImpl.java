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
import org.igov.analytic.model.source.SourceDBDao;
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

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessDao processDao;

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private SourceDBDao sourceDBDao;

    @Autowired
    private AttributeTypeDao attributeTypeDao;

    private final static Logger LOG = LoggerFactory.getLogger(MigrationServiceImpl.class);

    @Override
    public void migrateOldRecords() {
        prepareAndSave(getHistoricProcessList());
    }

    private List<HistoricProcessInstance> getHistoricProcessList() {
        return historyService.createNativeHistoricProcessInstanceQuery().sql(composeSql(getStartTime())).list();
    }

//    private DateTime getStartTime() {
//        DateTime startDateFromConfig = getStartDate(Config.class);
//        DateTime startDateFromProcess = getStartDate(Process.class);
//
//        DateTime startTime;
//        if (startDateFromConfig.isBefore(startDateFromProcess)) {
//            startTime = startDateFromProcess;
//          Thread asyncUpdate = new Thread(new AsyncUpdate(startDateFromProcess));
//            asyncUpdate.start();
//        } else {
//            startTime = startDateFromConfig;
//        }
//        return startTime;
//    }

    private DateTime getStartTime() {
        LOG.info("Inside getStartTime()");
        Config config = configDao.findLatestConfig();
        String dateTime = config.getsValue();
        LOG.info("Start date from Config: {}", dateTime);
        DateTime time = new DateTime(dateTime);
        HistoricProcessInstance processInstance =
                historyService.createHistoricProcessInstanceQuery().finishedAfter(time.toDate())
                        .orderByProcessInstanceStartTime().asc().listPage(0, 1).get(0);
        LOG.info("Start date from act_hi_procinst: {}", processInstance.getStartTime());
        return new DateTime(processInstance.getStartTime());
    }

    private void updateConfigTable(DateTime startDateFromProcess) {
        Config config = new Config();
        config.setsValue(startDateFromProcess.toString("yyyy-MM-dd HH:mm:ss"));
        configDao.saveOrUpdate(config);
    }

    private String composeSql(DateTime startTime) {
        DateTime endTime = startTime.plusDays(3);
        return "SELECT * from act_hi_procinst where start_time_ < TIMESTAMP \' 2016-07-23 00:00:00\' AND proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null AND proc_inst_id_ =\'22317510\'";
//        return "SELECT * from act_hi_procinst where start_time_ > TIMESTAMP \' "
//               + startTime.toString("yyyy-MM-dd HH:mm:ss")
//                + "\' AND start_time_ < TIMESTAMP \'" + endTime.toString("yyyy-MM-dd HH:mm:ss") + "\' AND proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null";
    }

    private void prepareAndSave(List<HistoricProcessInstance> historicProcessList) {
        LOG.info("Inside prepareAndSave()");
        for (HistoricProcessInstance historicProcess : historicProcessList) {
            Process processForSave = createProcessForSave(historicProcess);
            LOG.info("Current processForSave object: {}", processForSave.toString());
            processDao.saveOrUpdate(processForSave);//within transaction with Config table
//            Thread asyncUpdate = new Thread(new AsyncUpdate(processForSave.getoDateStart()));
//            asyncUpdate.start();
        }

        LOG.info("After prepareAndSave()");
        Process processFromDb = processDao.findById(20001L).get();
        LOG.info("Process from DB: {}", processFromDb);
    }

    private Process createProcessForSave(HistoricProcessInstance historicProcess) {
        Process process = new Process();
        process.setsID_(historicProcess.getId());
        process.setoDateStart(new DateTime(historicProcess.getStartTime()));
        process.setoDateFinish(new DateTime(historicProcess.getEndTime()));
        process.setoSourceDB(getSourceDBForIGov());
        process.setaAttribute(createAttributes(historicProcess.getId(), process, null));
        process.setaProcessTask(createProcessTaskList(historicProcess.getId(), process, historicProcess));
        process.setsID_Data("sID_Data Process");
        return process;
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

    private ProcessTask createProcessTaskToInsert(HistoricTaskInstance taskInstance, Process process) {
        ProcessTask processTask = new ProcessTask();
        processTask.setoProcess(process);
        processTask.setoDateStart(new DateTime(taskInstance.getStartTime()));
        processTask.setoDateFinish(new DateTime(taskInstance.getEndTime()));
        processTask.setsID_(taskInstance.getId());
        processTask.setaAccessGroup(null);//спросить
        processTask.setaAccessUser(null);//спросить
        processTask.setaAttribute(createAttributes(taskInstance.getId(), null, processTask));
        return processTask;
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

    private SourceDB getSourceDBForIGov() {
        return sourceDBDao.findByIdExpected(2L);
    }

    private List<ProcessTask> createProcessTaskList(String processId, Process process, HistoricProcessInstance historicProcess) {
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processId).list();
        List<ProcessTask> processTaskList = new ArrayList<>(taskInstanceList.size());
        taskInstanceList.forEach(taskInstance -> {
            ProcessTask processTask = createProcessTaskToInsert(taskInstance, process);
            processTaskList.add(processTask);
            //???????
            createCustomProcessToInsert(historicProcess, process);
            //???????
            createCustomProcessTaskToInsert(taskInstance, processTask);
        });
        return processTaskList;
    }

    private List<Attribute> createAttributes(String instanceId, Process process, ProcessTask processTask) {
        List<HistoricVariableInstance> variableInstanceList = process != null ? historyService.
                createHistoricVariableInstanceQuery().processInstanceId(instanceId).list() :
                historyService.
                        createHistoricVariableInstanceQuery().taskId(instanceId).list();

        Map<String, Object> attributes = populateAttributes(variableInstanceList);
        List<Attribute> resultList = new ArrayList<>(attributes.size());
        attributes.forEach((id, value) -> {
            Attribute attribute = new Attribute();
            if (process != null)
                attribute.setoProcess(process);
            else
                attribute.setoProcessTask(processTask);
            attribute.setName(id);
            attribute.setoAttributeType(getAttributeType(value, attribute));
            attribute.setsID_("sID_ Attribute");
            resultList.add(attribute);
        });
        return resultList;
    }

    private Map<String, Object> populateAttributes(List<HistoricVariableInstance> variables) {
        Map<String, Object> resultMap = new HashMap<>(variables.size());

        variables.forEach(entity -> {
            if (entity.getValue() != null)
                resultMap.put(entity.getVariableName(), entity.getValue());
        });
        return resultMap;
    }


    //TODO write generics for this method
    private AttributeType getAttributeType(Object obj, Attribute attribute) {
        AttributeType type = null;
        Class<?> clazz = obj.getClass();
        if (clazz.getSimpleName().equalsIgnoreCase("string")) {
            String string = (String) obj;
            if (string.length() < 255) {
                type = attributeTypeDao.findById(3L).get();
                Attribute_StringShort shortString = new Attribute_StringShort();
                shortString.setsValue(string);
                shortString.setoAttribute(attribute);
                attribute.setoAttribute_StringShort(shortString);
            } else {
                type = attributeTypeDao.findById(4L).get();
                Attribute_StringLong longString = new Attribute_StringLong();
                longString.setsValue(string);
                longString.setoAttribute(attribute);
                attribute.setoAttribute_StringLong(longString);
            }
        }
        //УЗНАТЬ У ОЛИ!!!!!!!
        if (clazz.getSimpleName().equalsIgnoreCase("integer") ||
                clazz.getSimpleName().equalsIgnoreCase("long")) {
            type = attributeTypeDao.findById(1L).get();
            Attribute_Integer integer = new Attribute_Integer();
            integer.setnValue((Integer) obj);
            integer.setoAttribute(attribute);
            attribute.setoAttribute_Integer(integer);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("boolean")) {
            type = attributeTypeDao.findById(5L).get();
            Attribute_Boolean boolean_attr = new Attribute_Boolean();
            boolean_attr.setbValue((Boolean) obj);
            boolean_attr.setoAttribute(attribute);
            attribute.setoAttribute_Boolean(boolean_attr);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("date")) {
            type = attributeTypeDao.findById(6L).get();
            Attribute_Date date_attr = new Attribute_Date();
            date_attr.setoValue(new DateTime(obj));
            date_attr.setoAttribute(attribute);
            attribute.setoAttribute_Date(date_attr);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("float")) {
            type = attributeTypeDao.findById(2L).get();
            Attribute_Float float_attr = new Attribute_Float();
            float_attr.setnValue((Double) obj);
            float_attr.setoAttribute(attribute);
            attribute.setoAttribute_Float(float_attr);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("file")) {

        }

        return type;
    }
}
