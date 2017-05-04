package org.igov.service.migration;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.process.*;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.source.SourceDB;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dpekach on 01.05.17.
 *
 * <p>Service is responsible for migrating outdated data from activiti historic tables to the next 4 analytic tables:
 * Process, ProcessTask, CustomProcess, CustomProcessTask. Tables with prefix 'Custom' are consist of fields,
 * that are present in act_hi_*, but are absent in corresponding 'Process' tables.
 * Table 'Config' serves for backup aims: it stores the last successfully migrated process_instance_id, so that
 * migration process shouldn't be started from the very beginning.</p>
 *
 *
 * <p>Migration algorithm</p>:
 * 0) Get last process_instance_id from 'Config' table: if process with such id is present in act_hi_procinst, delete it
 * 1) Get ordered by process_instance_id list of historic processes from act_hi_procinst;
 * 2) Fill Process/CustomProcess and ProcessTask/CustomProcessTask beans;
 * 3) Save only Process bean with ProcessDao;
 * 4) If populating analytic tables succeeds, then 'Config' table is updated with last process_instance_id
 * 5) Corresponding record is deleted from act_hi_procinst & act_hi_taskinst (this option is disabled in development mode)
 */
@Service
public class MigrationServiceImpl implements MigrationService {

    private final HistoryService historyService;

    private List<HistoricProcessInstance> historicProcessList;

    private ProcessDao processDao;

    private ConfigDao configDao;

    @Autowired
    public MigrationServiceImpl(HistoryService historyService, ProcessDao processDao, ConfigDao configDao) {
        this.historyService = historyService;
        this.processDao = processDao;
        this.configDao = configDao;
    }

    /**
//     * @param isTest if this flag is set to true, only 1 record is migrated from act_hi_* to corresponding tables
     */
    @Override
    public void migrateOldRecords() {
        historicProcessList =
                historyService.createHistoricProcessInstanceQuery().finished().includeProcessVariables()
                        .orderByProcessInstanceId().asc().list();
        synchronizeTables(historicProcessList.get(0).getId());
        getListWithPopulatedBeans(historicProcessList);


    }

    private void synchronizeTables(String minimalInstanceId) {
        if(configDao.exists(Long.valueOf(minimalInstanceId))) {
            historyService.deleteHistoricProcessInstance(minimalInstanceId);
        }
    }

    private List<Process> getListWithPopulatedBeans(List<HistoricProcessInstance> historicProcessList) {
        List<Process> resultList = new ArrayList<>(historicProcessList.size());

        for(HistoricProcessInstance historicProcess: historicProcessList) {
            Process processForSave = createProcessToInsert(historicProcess);
            resultList.add(processForSave);


            String processInstanceId = historicProcess.getId();
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            ProcessTask processTask = createProcessTaskToInsert(taskInstance, processForSave);

            //???????
            CustomProcess customProcess = createCustomProcessToInsert(historicProcess, processForSave);
            //???????
            CustomProcessTask customProcessTask = createCustomProcessTaskToInsert(taskInstance, processTask);

            processDao.saveOrUpdate(processForSave);
            Config config = new Config();
            config.setsValue(processInstanceId);
            configDao.saveOrUpdate(config);
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
        return resultList;
    }

    private SourceDB getSourceDBForIGov() {
        SourceDB sourceDB = new SourceDB();
        sourceDB.setName("iGov");
        return sourceDB;
    }

    private Process createProcessToInsert(HistoricProcessInstance historicProcess) {
        Process process = new Process();

        process.setsID_(historicProcess.getBusinessKey());//спросить
        process.setoDateStart(new DateTime(historicProcess.getStartTime()));
        process.setoDateFinish(new DateTime(historicProcess.getEndTime()));
        process.setoSourceDB(getSourceDBForIGov());


        Map<String, Object> attributes = historicProcess.getProcessVariables();
        //TODO нужно просеттить атрибуты процесса
        return process;
    }

    private CustomProcess createCustomProcessToInsert(HistoricProcessInstance historicProcess, Process process) {
        CustomProcess customProcess = new CustomProcess();

        customProcess.setnDuration(historicProcess.getDurationInMillis());
        customProcess.setoProcess(process);
        customProcess.setsDeleteReason(historicProcess.getDeleteReason());
        customProcess.setsName(historicProcess.getName());
        customProcess.setsEndActivityId(historicProcess.getEndActivityId());//изменить
        customProcess.setsStartActivityId(historicProcess.getStartActivityId());
        customProcess.setsTenantId(historicProcess.getTenantId());
        customProcess.setsProcessDefinitionId(historicProcess.getProcessDefinitionId());
        customProcess.setsProcessInstanceId(historicProcess.getId());
        customProcess.setsSuperProcessInstanceId(historicProcess.getSuperProcessInstanceId());
        customProcess.setsStartUserId(historicProcess.getStartUserId());
        customProcess.setsBusinessKey(historicProcess.getBusinessKey());//спросить


        return customProcess;
    }


    //TODO
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


        processTask.setsID_(null);//спросить
        processTask.setaAccessGroup(null);//спросить
        processTask.setaAccessUser(null);//спросить
        processTask.setaAttribute(null);//спросить
        return processTask;
    }


    public List<HistoricProcessInstance> getTaskList() {
        return historicProcessList;
    }

}
