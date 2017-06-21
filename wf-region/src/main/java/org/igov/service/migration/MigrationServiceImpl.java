package org.igov.service.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Attachment;
import org.apache.commons.lang.StringUtils;
import org.igov.analytic.model.access.AccessGroup;
import org.igov.analytic.model.access.AccessUser;
import org.igov.analytic.model.attribute.*;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.process.*;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
import org.igov.io.db.kv.analytic.impl.BytesMongoStorageAnalytic;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.migration.exception.MigrationException;
import org.igov.util.VariableMultipartFile;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
 */
@Service
public class MigrationServiceImpl implements MigrationService {

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

    @Autowired
    private AttributeTypeCustomDao attributeTypeCustomDao;

    @Autowired
    private AttributeNameDao attributeNameDao;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private BytesMongoStorageAnalytic bytesStorageAnalytic;

    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActionTaskService actionTaskService;

    private final static Logger LOG = LoggerFactory.getLogger(MigrationServiceImpl.class);

    @Override
    public void migrateOldRecords(String processId) {
        prepareAndSave(getHistoricProcessList(processId));
    }

    private List<HistoricProcessInstance> getHistoricProcessList(String processId) {
        return historyService.createNativeHistoricProcessInstanceQuery().sql(composeSql(getStartTime(), processId)).list();
    }

    private DateTime getStartTime() {
        Config config = configDao.findBy("name", "dateLastBackup").get();
        String dateTime = config.getsValue();
        DateTime time = DateTime.parse(dateTime);//date parsing doesn't work properly
        HistoricProcessInstance processInstance =
                historyService.createHistoricProcessInstanceQuery().finishedAfter(time.toDate())
                        .orderByProcessInstanceStartTime().asc().listPage(0, 1).get(0);
        return new DateTime(processInstance.getStartTime());
    }

    private String composeSql(DateTime startTime, String processId) {
        DateTime endTime = startTime.plusDays(3);
        if (processId != null)
            return "SELECT * from act_hi_procinst where proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null AND proc_inst_id_ =\'" + processId + "\'";
        else
            return "SELECT * from act_hi_procinst where start_time_ > TIMESTAMP \' "
                    + startTime.toString("yyyy-MM-dd HH:mm:ss")
                    + "\' AND start_time_ < TIMESTAMP \'" + endTime.toString("yyyy-MM-dd HH:mm:ss") + "\' AND proc_def_id_ not like \'%common_mreo_2%\' AND end_time_ is not null";
    }

    private void prepareAndSave(List<HistoricProcessInstance> historicProcessList) {
        for (HistoricProcessInstance historicProcess : historicProcessList) {
            Process processForSave = createProcessForSave(historicProcess);
            processDao.saveWithConfigBackup(processForSave);
        }
    }

    private Process createProcessForSave(HistoricProcessInstance historicProcess) {
        Process process = new Process();
        process.setsID_(historicProcess.getId());
        process.setoDateStart(new DateTime(historicProcess.getStartTime()));
        process.setoDateFinish(new DateTime(historicProcess.getEndTime()));
        process.setoSourceDB(getSourceDBForIGov());
        process.setaAttribute(createAttributes(historicProcess.getId(), process, null));
        process.setaProcessTask(createProcessTaskList(historicProcess.getId(), process));
        process.setsID_Data("");
        process.setCustomProcess(createCustomProcessToInsert(historicProcess, process));
        process.setaAccessGroup(getAccessGroupFromStartForm(historicProcess));//это со стартовой формы заполняется(act_hi_identitylink глянуть)
        process.setaAccessUser(null);
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
        customProcess.setsBusinessKey(historicProcess.getBusinessKey());
        return customProcess;
    }

    private ProcessTask createProcessTaskToInsert(HistoricTaskInstance taskInstance, Process process) {
        ProcessTask processTask = new ProcessTask();
        processTask.setoProcess(process);
        processTask.setoDateStart(new DateTime(taskInstance.getStartTime()));
        processTask.setoDateFinish(new DateTime(taskInstance.getEndTime()));
        processTask.setsID_(taskInstance.getId());
        processTask.setaAccessGroup(getAccessGroup(taskInstance, processTask, process));
        processTask.setaAccessUser(getAccessUser(taskInstance, processTask, process));
        processTask.setaAttribute(createAttributes(taskInstance.getId(), null, processTask));
        processTask.setCustomProcessTask(createCustomProcessTaskToInsert(taskInstance, processTask));
        return processTask;
    }

    private Set<AccessGroup> getAccessGroupFromStartForm(HistoricProcessInstance processInstance) {
        StartFormData startFormData = formService.getStartFormData(processInstance.getProcessDefinitionId());
        List<FormProperty> propertyList = startFormData.getFormProperties();
        for (FormProperty property : propertyList) {
            LOG.info(property.getName());
        }
        return null;
    }

    private Collection<FlowElement> getFlowElementsFromProcess(HistoricTaskInstance taskInstance) {
        ProcessDefinition definition = repositoryService.createNativeProcessDefinitionQuery().sql("SELECT process_definition.* FROM " +
                "act_re_procdef process_definition JOIN act_hi_procinst process_instance on process_instance.proc_def_id_" +
                "=process_definition.id_ AND process_instance.proc_inst_id_ = \'" + taskInstance.getProcessInstanceId() + "\';").singleResult();
        BpmnModel model = repositoryService.getBpmnModel(definition.getId());
        return model.getMainProcess().getFlowElements();
    }

    //TODO rewrite
    private Set<AccessUser> getAccessUser(HistoricTaskInstance taskInstance, ProcessTask processTask, Process process) {
        Set<AccessUser> resultSet = new HashSet<>();
        for (FlowElement oFlowElement : getFlowElementsFromProcess(taskInstance)) {
            if (oFlowElement instanceof UserTask) {
                UserTask oUserTask = (UserTask) oFlowElement;
                if (oUserTask.getId().equals(taskInstance.getTaskDefinitionKey())) {
                    resultSet.addAll(convertToAccessUserSet(oUserTask.getCandidateUsers(), processTask, process));
                }
            }
        }
        return resultSet;
    }

    private Set<AccessUser> convertToAccessUserSet(List<String> userList, ProcessTask processTask, Process process) {
        Set<AccessUser> resultSet = new HashSet<>();
        userList.forEach(group -> {
            AccessUser accessUser = new AccessUser();
            Set<Process> processes = new HashSet<>();
            processes.add(process);
            accessUser.setaProcess(processes);
            Set<ProcessTask> processTasks = new HashSet<>();
            processTasks.add(processTask);
            accessUser.setaProcessTask(processTasks);
            accessUser.setsID(group);
            resultSet.add(accessUser);
        });
        return resultSet;
    }

    //TODO rewrite this algorithm
    private Set<AccessGroup> getAccessGroup(HistoricTaskInstance taskInstance, ProcessTask processTask, Process process) {
        Set<AccessGroup> resultSet = new HashSet<>();
        for (FlowElement oFlowElement : getFlowElementsFromProcess(taskInstance)) {
            if (oFlowElement instanceof UserTask) {
                UserTask oUserTask = (UserTask) oFlowElement;
                if (oUserTask.getId().equals(taskInstance.getTaskDefinitionKey())) {
                    resultSet.addAll(convertToAccessGroupSet(oUserTask.getCandidateGroups(), processTask, process));
                }
            }
        }
        return resultSet;
    }

    private Set<AccessGroup> convertToAccessGroupSet(List<String> groupsList, ProcessTask processTask, Process process) {
        Set<AccessGroup> resultSet = new HashSet<>();
        groupsList.forEach(group -> {
            AccessGroup accessGroup = new AccessGroup();
            accessGroup.addaProcess(process);
            accessGroup.addaProcessTask(processTask);
            accessGroup.setsID(group);
            resultSet.add(accessGroup);
        });
        return resultSet;
    }

    private CustomProcessTask createCustomProcessTaskToInsert(HistoricTaskInstance taskInstance, ProcessTask processTask) {
        CustomProcessTask customProcessTask = new CustomProcessTask();

        customProcessTask.setoProcessTask(processTask);
        customProcessTask.setnDuration(taskInstance.getDurationInMillis());
        customProcessTask.setsFormKey(taskInstance.getFormKey());
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

    private List<ProcessTask> createProcessTaskList(String processId, Process process) {
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processId).list();
        List<ProcessTask> processTaskList = new ArrayList<>(taskInstanceList.size());
        taskInstanceList.forEach(taskInstance -> {
            ProcessTask processTask = createProcessTaskToInsert(taskInstance, process);
            processTaskList.add(processTask);
        });
        return processTaskList;
    }

    private List<Attribute> createAttributes(String instanceId, Process process, ProcessTask processTask) {
        List<HistoricVariableInstance> variableInstanceList = process != null ? historyService.
                createHistoricVariableInstanceQuery().processInstanceId(instanceId).list() :
                historyService.
                        createHistoricVariableInstanceQuery().taskId(instanceId).list();
        Map<String, Object> attributes = convertToAttributesMap(variableInstanceList);
        List<Attribute> resultList = new ArrayList<>(attributes.size());
        attributes.forEach((id, value) -> {
            Attribute attribute = new Attribute();
            if (process != null)
                attribute.setoProcess(process);
            else
                attribute.setoProcessTask(processTask);
            attribute.setoAttributeTypeCustom(createAttributeTypeCustom(id, process == null ? processTask.getoProcess().getsID_() : process.getsID_()));
            attribute.setoAttributeType(getAttributeType(value, attribute, process == null ? processTask.getoProcess().getsID_() : process.getsID_()));
            attribute.setName(id);
            attribute.setsID_(getsID_ForAttribute(id, process == null ? processTask.getoProcess().getsID_() : process.getsID_()));
            attribute.setoAttributeName(createAttributeName(id));
            resultList.add(attribute);
        });
        return resultList;
    }

    private String getsID_ForAttribute(String variableId, String processId) {
        List<HistoricVariableInstance> historicVariableInstance =
                historyService.createNativeHistoricVariableInstanceQuery()
                        .sql("SELECT * FROM act_hi_varinst where name_ = \'" + variableId + "\' AND proc_inst_id_ = \'" + processId + "\'").list();
        return historicVariableInstance.get(0).getId();
    }

    private AttributeTypeCustom createAttributeTypeCustom(String variableId, String processId) {
        List<HistoricVariableInstance> historicVariableInstance =
                historyService.createNativeHistoricVariableInstanceQuery()
                        .sql("SELECT * FROM act_hi_varinst where name_ = \'" + variableId + "\' AND proc_inst_id_ = \'" + processId + "\'").list();
        if (!attributeTypeCustomDao.findBy("name", historicVariableInstance.get(0).getVariableTypeName()).isPresent())
            throw new MigrationException("This id is missing in reference: " + historicVariableInstance.get(0).getVariableTypeName());
        return attributeTypeCustomDao.findBy("name", historicVariableInstance.get(0).getVariableTypeName()).get();
    }

    private AttributeName createAttributeName(String id) {
        if (!attributeNameDao.findBy("sID", id).isPresent())
            throw new MigrationException("This sID is missing in reference (attributeNameDao): " + id);
        return attributeNameDao.findBy("sID", id).get();
    }

    private Map<String, Object> convertToAttributesMap(List<HistoricVariableInstance> variables) {
        Map<String, Object> resultMap = new HashMap<>(variables.size());

        variables.forEach(entity -> {
            if (entity.getValue() != null)
                resultMap.put(entity.getVariableName(), entity.getValue());
        });
        return resultMap;
    }

    //TODO write generics for this method
    private AttributeType getAttributeType(Object obj, Attribute attribute, String processId) {
        AttributeType type = null;
        Class<?> clazz = obj.getClass();
        if (clazz.getSimpleName().equalsIgnoreCase("string")) {
            String string = (String) obj;
            if (StringUtils.isNumeric(string) && StringUtils.isNotEmpty(string)) {
                type = attributeTypeDao.findById(7L).get();
                Attribute_File fileAttribute = new Attribute_File();
                AttachmentEntity file = (AttachmentEntity) actionTaskService.getAttachment(string, 0, processId);
                fileAttribute.setsContentType(file.getType());
                fileAttribute.setoAttribute(attribute);
                fileAttribute.setsFileName(file.getName());
                fileAttribute.setsExtName(file.getName().split("\\.")[1]);
                String newKey = transferAttachment(file.getContentId(), file.getName(), file.getType());
                fileAttribute.setsID_Data(newKey);
                attribute.setoAttribute_File(fileAttribute);
            } else if (string.startsWith("{") && string.contains("Mongo")) {
                type = attributeTypeDao.findById(7L).get();
                Attribute_File fileAttribute = new Attribute_File();
                Map<String, String> fileValuesMap = parseJsonStringToMap(string);
                fileAttribute.setsFileName(fileValuesMap.get("sFileNameAndExt"));
                fileAttribute.setsExtName(fileValuesMap.get("sFileNameAndExt").split("\\.")[1]);
                fileAttribute.setsContentType(fileValuesMap.get("sContentType"));
                String newKey = transferAttachment("Mongo-" + fileValuesMap.get("sKey"), fileValuesMap.get("sFileNameAndExt"), fileValuesMap.get("sContentType"));
                fileAttribute.setoAttribute(attribute);
                fileAttribute.setsID_Data(newKey);
                attribute.setoAttribute_File(fileAttribute);
            } else if (string.length() < 255) {
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

        if (clazz.getSimpleName().equalsIgnoreCase("integer")) {
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
        if (clazz.getSimpleName().equalsIgnoreCase("float")
                || clazz.getSimpleName().equalsIgnoreCase("double")) {
            type = attributeTypeDao.findById(2L).get();
            Attribute_Float float_attr = new Attribute_Float();
            float_attr.setnValue((Double) obj);
            float_attr.setoAttribute(attribute);
            attribute.setoAttribute_Float(float_attr);
        }
        if (clazz.getSimpleName().equalsIgnoreCase("long")) {
            type = attributeTypeDao.findById(8L).get();
            Attribute_Long long_attr = new Attribute_Long();
            long_attr.setnValue((Long) obj);
            long_attr.setoAttribute(attribute);
            attribute.setoAttribute_Long(long_attr);
        }
        return type;
    }

    private Map<String, String> parseJsonStringToMap(String stringToParse) {
        Map<String, Object> valueMap;
        Map<String, String> resultMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            valueMap = mapper.readValue(stringToParse, new TypeReference<Map<String, Object>>() {
            });
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                if (entry.getKey().equals("sFileNameAndExt")
                        || entry.getKey().equals("sContentType")
                        || entry.getKey().equals("sKey"))
                    resultMap.put(entry.getKey(), (String) entry.getValue());
            }
        } catch (IOException ex) {
            LOG.error("Error during json-string parsing:{}", ex.getCause());
        }

        return resultMap;
    }

    private String transferAttachment(String fileKey, String fileName, String contentType) {
        InputStream attachmentStream = ((org.igov.service.conf.TaskServiceImpl) taskService)
                .getAttachmentContentByMongoID(fileKey);
        if (attachmentStream == null) {
            throw new ActivitiObjectNotFoundException("Attachment with ID '"
                    + fileKey + "' doesn't have content associated with it.",
                    Attachment.class);
        }
        int nTo = fileName.lastIndexOf(".");
        if (nTo >= 0) {
            fileName = "attach_" + fileKey + "."
                    + fileName.substring(nTo + 1);
        }
        VariableMultipartFile multipartFile = new VariableMultipartFile(
                attachmentStream, fileName,
                fileName, contentType);

        byte[] result = multipartFile.getBytes();
        return bytesStorageAnalytic.saveData(result);
    }
}
