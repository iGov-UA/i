package org.igov.service.migration;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.igov.analytic.model.process.*;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.source.SourceDB;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by dpekach on 01.05.17.
 */

/*
Алгоритм действий:
1) Достаем с помощью historyService.createHistoricProcessInstanceQuery().finished().list() оконченные процессы
2) Проходим по каждому процессу; Создаем новый процесс Process & CustomProcess; сеттим в них необходиме атрибуты
3) Достали айдишку процесса; по айдишке процесса находим таску; заполняем ProcessTask & CustomProcessTask;
4) Сохраняем только Process;
5) Помещаем proc_inst_id в ConfigDao, если успешно


Непонятно: сортировка, application-context
 */
@Service
public class MigrationServiceImpl implements MigrationService {

    private final HistoryService historyService;

    private List<HistoricProcessInstance> processList;

    private ProcessDao processDao;

    @Autowired
    public MigrationServiceImpl(HistoryService historyService, ProcessDao processDao) {
        this.historyService = historyService;
        this.processDao = processDao;
    }

    @PostConstruct
    private void init() {
        processList =
                historyService.createHistoricProcessInstanceQuery().finished().includeProcessVariables().orderByProcessInstanceId().list();
    }

    @Override
    public void migrateOldRecords() {
        for(HistoricProcessInstance process: processList) {
            Process processToInsert = createProcessToInsert(process);

            String processInstanceId = process.getId();
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).singleResult();
            ProcessTask processTask = createProcessTaskToInsert(taskInstance);
            CustomProcessTask customProcessTask = createCustomProcessTaskToInsert(taskInstance, processTask);

            String successfullyInsertedId = processDao.saveOrUpdate(processToInsert).getId().toString();
        }
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

        //нужно просеттить атрибуты процесса
        return process;
    }

    private CustomProcess createCustomProcessToInsert(HistoricProcessInstance historicProcess, Process process) {
        CustomProcess customProcess = new CustomProcess();

        customProcess.setnDuration(historicProcess.getDurationInMillis());
        customProcess.setoProcess(process);
        customProcess.setsDeleteReason(historicProcess.getDeleteReason());
        customProcess.setsName(historicProcess.getName());
        customProcess.setsEndActivityId(historicProcess.getEndActivityId());//спросить
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

        return customProcessTask;
    }

    private ProcessTask createProcessTaskToInsert(HistoricTaskInstance taskInstance) {
        ProcessTask processTask = new ProcessTask();

        return processTask;
    }
    public List<HistoricProcessInstance> getTaskList() {
        return processList;
    }

}
