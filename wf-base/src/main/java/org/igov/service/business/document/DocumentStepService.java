package org.igov.service.business.document;

import java.io.IOException;
import java.net.URISyntaxException;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import static org.igov.io.fs.FileSystemData.getFileData_Pattern;
import org.igov.util.Tool;
import org.springframework.stereotype.Component;

@Component("documentStepService")
@Service
public class DocumentStepService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentStepService.class);

    @Autowired
    @Qualifier("documentStepDao")
    private GenericEntityDao<Long, DocumentStep> documentStepDao;

    @Autowired
    private TaskService oTaskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FormService oFormService;

    @Autowired
    private IdentityService oIdentityService;

    public void setDocumentSteps(String snID_Process_Activiti, String soJSON) {
        JSONObject steps = new JSONObject(soJSON);
        List<DocumentStep> resultSteps = new ArrayList<>();
        //process common step if it exists
        Object _step = steps.opt("_");
        LOG.info("Common step is - {}", _step);
        if (_step != null) {
            DocumentStep commonStep = mapToDocumentStep(_step);
            commonStep.setnOrder(0L);//common step with name "_" has order 0
            commonStep.setsKey_Step("_");
            commonStep.setSnID_Process_Activiti(snID_Process_Activiti);
            documentStepDao.saveOrUpdate(commonStep);
            resultSteps.add(commonStep);
        }
        //process all other steps
        //first of all we filter common step with name "_" and then just convert each step from JSON to POJO
        List<String> stepsNames = Arrays.asList(JSONObject.getNames(steps));
        LOG.info("List of steps: {}", stepsNames);
        stepsNames = stepsNames.stream().
                filter(stepName -> !"_".equals(stepName))
                .collect(Collectors.toList());
        long i = 1L;
        for (String stepName : stepsNames) {
            DocumentStep step = mapToDocumentStep(steps.get(stepName));
            step.setnOrder(i++);
            step.setsKey_Step(stepName);
            step.setSnID_Process_Activiti(snID_Process_Activiti);
            documentStepDao.saveOrUpdate(step);
            resultSteps.add(step);
        }
        LOG.info("Result list of steps: {}", resultSteps);
    }

    private DocumentStep mapToDocumentStep(Object singleStep) {
        JSONObject step = (JSONObject) singleStep;
        LOG.info("try to parse step: {}", step);
        if (step == null) {
            return null;
        }

        DocumentStep resultStep = new DocumentStep();
        String[] groupNames = JSONObject.getNames(step);
        List<DocumentStepSubjectRight> rights = new ArrayList<>();
        for (String groupName : groupNames) {
            JSONObject group = step.optJSONObject(groupName);
            LOG.info("group for step: {}", group);

            if (group == null) {
                continue;
            }
            Boolean bWrite = (Boolean) group.opt("bWrite");
            if (bWrite == null) {
                throw new IllegalArgumentException("Group " + groupName + " hasn't property bWrite.");
            }

            DocumentStepSubjectRight rightForGroup = new DocumentStepSubjectRight();
            rightForGroup.setsKey_GroupPostfix(groupName);
            rightForGroup.setbWrite(bWrite);

            Object sName = group.opt("sName");
            if (sName != null) {
                rightForGroup.setsName((String) sName);
            }

            List<DocumentStepSubjectRightField> fields = mapToFields(group, rightForGroup);
            rightForGroup.setDocumentStepSubjectRightFields(fields);
            rightForGroup.setDocumentStep(resultStep);
            LOG.info("right for step: {}", rightForGroup);
            rights.add(rightForGroup);
        }
        resultStep.setRights(rights);
        return resultStep;
    }

    private List<DocumentStepSubjectRightField> mapToFields(JSONObject group, DocumentStepSubjectRight rightForGroup) {
        List<DocumentStepSubjectRightField> resultFields = new ArrayList<>();
        String[] fieldNames = JSONObject.getNames(group);
        LOG.info("fields for right: {}", Arrays.toString(fieldNames));
        for (String fieldName : fieldNames) {
            if (fieldName == null || fieldName.equals("bWrite")) {
                continue;
            }
            if (fieldName.contains("Read")) {
                JSONArray masks = group.optJSONArray(fieldName);
                LOG.info("Read branch for masks: {}", masks);
                for (int i = 0; masks.length() > i; i++) {
                    String mask = masks.getString(i);
                    DocumentStepSubjectRightField field = new DocumentStepSubjectRightField();
                    field.setsMask_FieldID(mask);
                    field.setbWrite(false);
                    field.setDocumentStepSubjectRight(rightForGroup);
                    resultFields.add(field);
                }
            }
            if (fieldName.contains("Write")) {
                JSONArray masks = group.getJSONArray(fieldName);
                LOG.info("Write branch for masks: {}", masks);
                for (int i = 0; masks.length() > i; i++) {
                    String mask = masks.getString(i);
                    DocumentStepSubjectRightField field = new DocumentStepSubjectRightField();
                    field.setsMask_FieldID(mask);
                    field.setbWrite(true);
                    field.setDocumentStepSubjectRight(rightForGroup);
                    resultFields.add(field);
                }
            }
        }

        return resultFields;
    }

    public Map<String, Object> getDocumentStepLogins(String snID_Process_Activiti) {//JSONObject 
        //assume that we can have only one active task per process at the same time
        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        List<Task> aTaskActive = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active().list();
        if (aTaskActive.size() < 1 || aTaskActive.get(0) == null) {
            throw new IllegalArgumentException("Process with ID: " + snID_Process_Activiti + " has no active task.");
        }
        Task oTaskActive = aTaskActive.get(0);
        String sID_BP = oTaskActive.getProcessDefinitionId();
        LOG.info("sID_BP={}", sID_BP);
        if (sID_BP != null && sID_BP.contains(":")) {
            String[] as = sID_BP.split("\\:");
            sID_BP = as[0];
            LOG.info("FIX(:) sID_BP={}", sID_BP);
        }
        if (sID_BP != null && sID_BP.contains(".")) {
            String[] as = sID_BP.split("\\.");
            sID_BP = as[0];
            LOG.info("FIX(.) sID_BP={}", sID_BP);
        }

        ProcessInstance oProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti)
                .active()
                .singleResult();
        Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
        LOG.info("mProcessVariable={}", mProcessVariable);
        //Map<String, Object> mProcessVariable = new HashMap();
        String snID_Task = oTaskActive.getId();
        List<FormProperty> aProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();                    
        for (FormProperty oProperty : aProperty) {
            mProcessVariable.put(oProperty.getId(), oProperty.getValue());
            //String sID = oProperty.getId();
        }
        LOG.info("mProcessVariable(added)={}", mProcessVariable);

        List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        DocumentStep oDocumentStep_Common = aDocumentStep
                .stream()
                .filter(o -> o.getsKey_Step().equals("_"))
                .findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Common={}", oDocumentStep_Common);

        String sKey_Step_Document = (String) mProcessVariable.get("sKey_Step_Document");
        if (StringUtils.isEmpty(sKey_Step_Document)) {
            //throw new IllegalStateException("There is no active Document Sep." +
            //        " Process variable sKey_Step_Document is empty.");
            //sKey_Step_Document="1";
        }
        DocumentStep oDocumentStep_Active = aDocumentStep
                .stream()
                .filter(o -> sKey_Step_Document == null ? o.getnOrder().equals(1) : o.getsKey_Step().equals(sKey_Step_Document))
                .findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Active={}", oDocumentStep_Active);
        if (oDocumentStep_Active == null) {
            throw new IllegalStateException("There is no active Document Sep, process variable sKey_Step_Document="
                    + sKey_Step_Document);
        }

        Map<String, Object> mReturn = new HashMap();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new LinkedList();
        if(oDocumentStep_Common!=null){
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Common.getRights()) {
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
                //List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common
            }
        }
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Active.getRights()) {
            aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            //List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common
        }

        final String sGroupPrefix = new StringBuilder(sID_BP).append("_").toString();

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            Map<String, Object> mParamDocumentStepSubjectRight = new HashMap();
            mParamDocumentStepSubjectRight.put("sDate", oDocumentStepSubjectRight.getsDate());//"2016-05-15 12:12:34"
            mParamDocumentStepSubjectRight.put("bWrite", oDocumentStepSubjectRight.getbWrite());//false
            mParamDocumentStepSubjectRight.put("sName", oDocumentStepSubjectRight.getsName());//"Главный контроллирующий"
            String sID_Group = new StringBuilder(sGroupPrefix).append(oDocumentStepSubjectRight.getsKey_GroupPostfix()).toString();
            List<User> aUser = oIdentityService.createUserQuery().memberOfGroup(sID_Group).list();
            List<Map<String, Object>> a = new LinkedList();
            for (User oUser : aUser) {
                Map<String, Object> mUser = new HashMap();
                mUser.put("sLogin", oUser.getId());
                mUser.put("sFIO", oUser.getLastName() + "" + oUser.getFirstName());
                a.add(mUser);
            }
            mParamDocumentStepSubjectRight.put("aUser", aUser);
            String sLogin = oDocumentStepSubjectRight.getsLogin();
            if (sLogin != null) {
                User oUser = oIdentityService.createUserQuery().userId(sLogin).singleResult();
                mParamDocumentStepSubjectRight.put("sFIO", oUser.getLastName() + "" + oUser.getFirstName());
            }
            mReturn.put(oDocumentStepSubjectRight.getsLogin(), mParamDocumentStepSubjectRight);
        }

        return mReturn;
    }

    /*public DocumentStep oDocumentStep_Active(String snID_Process_Activiti){
        return oDocumentStep_Active;
    }*/
    public Map<String, Object> getDocumentStepRights(String sLogin, String snID_Process_Activiti) {//JSONObject
        //assume that we can have only one active task per process at the same time
        LOG.info("sLogin={}, snID_Process_Activiti={}", sLogin, snID_Process_Activiti);
        List<Task> aTaskActive = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active().list();
        if (aTaskActive.size() < 1 || aTaskActive.get(0) == null) {
            throw new IllegalArgumentException("Process with ID: " + snID_Process_Activiti + " has no active task.");
        }
        Task oTaskActive = aTaskActive.get(0);
        String sID_BP = oTaskActive.getProcessDefinitionId();
        LOG.info("sID_BP={}", sID_BP);
        if (sID_BP != null && sID_BP.contains(":")) {
            String[] as = sID_BP.split("\\:");
            sID_BP = as[0];
            LOG.info("FIX(:) sID_BP={}", sID_BP);
        }
        if (sID_BP != null && sID_BP.contains(".")) {
            String[] as = sID_BP.split("\\.");
            sID_BP = as[0];
            LOG.info("FIX(.) sID_BP={}", sID_BP);
        }

        ProcessInstance oProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti)
                .active()
                .singleResult();
        Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
        LOG.info("mProcessVariable={}", mProcessVariable);
        //Map<String, Object> mProcessVariable = new HashMap();
        String snID_Task = oTaskActive.getId();
        List<FormProperty> aProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();                    
        for (FormProperty oProperty : aProperty) {
            mProcessVariable.put(oProperty.getId(), oProperty.getValue());
            //String sID = oProperty.getId();
        }
        LOG.info("mProcessVariable(added)={}", mProcessVariable);

        List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        DocumentStep oDocumentStep_Common = aDocumentStep
                .stream()
                .filter(o -> o.getsKey_Step().equals("_"))
                .findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Common={}", oDocumentStep_Common);

        String sKey_Step_Document = (String) mProcessVariable.get("sKey_Step_Document");
        if (StringUtils.isEmpty(sKey_Step_Document)) {
            //throw new IllegalStateException("There is no active Document Sep." +
            //        " Process variable sKey_Step_Document is empty.");
            //sKey_Step_Document="1";
        }
        DocumentStep oDocumentStep_Active = aDocumentStep
                .stream()
                .filter(o -> sKey_Step_Document == null ? o.getnOrder().equals(1) : o.getsKey_Step().equals(sKey_Step_Document))
                .findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Active={}", oDocumentStep_Active);
        if (oDocumentStep_Active == null) {
            throw new IllegalStateException("There is no active Document Step, process variable sKey_Step_Document="
                    + sKey_Step_Document);
        }

        //DocumentStep = oDocumentStep_Active = oDocumentStep_Active(snID_Process_Activiti);
        List<Group> aGroup = identityService.createGroupQuery().groupMember(sLogin).list();
        Set<String> asID_Group = new HashSet<>();
        if (aGroup != null) {
            aGroup.stream().forEach(group -> asID_Group.add(group.getId()));
        }
        LOG.info("sLogin={}, asID_Group={}", sLogin, asID_Group);
        //Lets collect DocumentStepSubjectRight by according users groups

        final String sGroupPrefix = new StringBuilder(sID_BP).append("_").toString();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common = new LinkedList();
        if(oDocumentStep_Common!=null){
            aDocumentStepSubjectRight_Common = oDocumentStep_Common
                    .getRights()
                    .stream()
                    .filter(o -> asID_Group.contains(new StringBuilder(sGroupPrefix).append(o.getsKey_GroupPostfix())))
                    .collect(Collectors.toList());
        }
        LOG.debug("aDocumentStepSubjectRight_Common={}", aDocumentStepSubjectRight_Common);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Active = oDocumentStep_Active
                .getRights()
                .stream()
                .filter(o -> asID_Group.contains(new StringBuilder(sGroupPrefix).append(o.getsKey_GroupPostfix())))
                .collect(Collectors.toList());
        LOG.debug("aDocumentStepSubjectRight_Active={}", aDocumentStepSubjectRight_Active);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new LinkedList(aDocumentStepSubjectRight_Common);
        aDocumentStepSubjectRight.addAll(aDocumentStepSubjectRight_Active);
        LOG.debug("aDocumentStepSubjectRight={}", aDocumentStepSubjectRight);

        Map<String, Object> mReturn = new HashMap();

        Boolean bWrite = null;
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (bWrite == null) {
                bWrite = false;
            }
            bWrite = bWrite || oDocumentStepSubjectRight.getbWrite();
        }
        mReturn.put("bWrite", bWrite);

        List<String> asID_Field_Read = new LinkedList();
        List<String> asID_Field_Write = new LinkedList();

        List<FormProperty> a = oFormService.getTaskFormData(snID_Task).getFormProperties();

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            List<String> asID_Field_Read_Temp = new LinkedList();
            List<String> asID_Field_Write_Temp = new LinkedList();
            //Boolean bInclude=null;
            for (DocumentStepSubjectRightField oDocumentStepSubjectRightField : oDocumentStepSubjectRight.getDocumentStepSubjectRightFields()) {
                String sMask = oDocumentStepSubjectRightField.getsMask_FieldID();
                if (sMask != null) {
                    Boolean bNot = false;
                    if (sMask.startsWith("!")) {
                        bNot = true;
                        sMask = sMask.substring(1);
                    }
                    Boolean bEndsWith = false;
                    Boolean bStartWith = false;
                    Boolean bAll = "*".equals(sMask);
                    if (!bAll) {
                        if (sMask.startsWith("*")) {
                            bEndsWith = true;
                            sMask = sMask.substring(1);
                        }
                        if (sMask.endsWith("*")) {
                            bStartWith = true;
                            sMask = sMask.substring(0, sMask.length() - 1);
                        }
                    }
                    for (FormProperty oProperty : a) {
                        String sID = oProperty.getId();
                        Boolean bFound = false;
                        if (bStartWith && bEndsWith) {
                            bFound = sID.contains(sMask);
                        } else if (bStartWith) {
                            bFound = sID.startsWith(sMask);
                        } else if (bEndsWith) {
                            bFound = sID.endsWith(sMask);
                        }
                        if (bAll || bFound) {
                            Boolean bWriteField = oDocumentStepSubjectRightField.getbWrite();
                            if (bNot) {
                                if (bWriteField) {
                                    asID_Field_Write_Temp.remove(sID);
                                } else {
                                    asID_Field_Read_Temp.remove(sID);
                                }
                            } else if (bWriteField) {
                                asID_Field_Write_Temp.add(sID);
                            } else {
                                asID_Field_Read_Temp.add(sID);
                            }
                        }
                    }

                }
            }
            asID_Field_Read.addAll(asID_Field_Read_Temp);
            asID_Field_Write.addAll(asID_Field_Write_Temp);
        }

        for (String sID_Field_Write : asID_Field_Write) {
            asID_Field_Read.remove(sID_Field_Write);
        }

        mReturn.put("asID_Field_Write", asID_Field_Write);
        mReturn.put("asID_Field_Read", asID_Field_Read);

        return mReturn;
    }

    public void checkDocumentInit(DelegateExecution execution) throws IOException, URISyntaxException {//JSONObject
        //assume that we can have only one active task per process at the same time
        String snID_Process_Activiti = execution.getId();
        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        String sID_BP = execution.getProcessDefinitionId();
        LOG.info("sID_BP={}", sID_BP);
        if (sID_BP != null && sID_BP.contains(":")) {
            String[] as = sID_BP.split("\\:");
            sID_BP = as[0];
            LOG.info("FIX(:) sID_BP={}", sID_BP);
        }
        if (sID_BP != null && sID_BP.contains(".")) {
            String[] as = sID_BP.split("\\.");
            sID_BP = as[0];
            LOG.info("FIX(.) sID_BP={}", sID_BP);
        }

        Map<String, Object> mProcessVariable = execution.getVariables();
        String sKey_Step_Document = mProcessVariable.containsKey("sKey_Step_Document") ? (String) mProcessVariable.get("sKey_Step_Document") : null;
        if ("".equals(sKey_Step_Document)) {
            sKey_Step_Document = null;
        }
        LOG.info("BEFORE:sKey_Step_Document={}", sKey_Step_Document);

        if (sKey_Step_Document == null) {

            String sPath = "document/" + sID_BP + ".json";
            LOG.info("sPath={}", sPath);
            byte[] aByteDocument = getFileData_Pattern(sPath);
            if (aByteDocument != null && aByteDocument.length > 0) {
                String soJSON = null;
                soJSON = Tool.sData(aByteDocument);
                LOG.info("soJSON={}", soJSON);
                
                setDocumentSteps(snID_Process_Activiti, soJSON);

                List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
                LOG.info("aDocumentStep={}", aDocumentStep);

                if (aDocumentStep.size() > 1) {
                    DocumentStep oDocumentStep = aDocumentStep.get(1);
                    sKey_Step_Document = oDocumentStep.getsKey_Step();
                } else if (aDocumentStep.size() > 0) {
                    DocumentStep oDocumentStep = aDocumentStep.get(0);
                    sKey_Step_Document = oDocumentStep.getsKey_Step();
                    //sKey_Step_Document = aDocumentStep.get(0);
                } else {
                    sKey_Step_Document = "_";
                }
                
                
                LOG.info("AFTER:sKey_Step_Document={}", sKey_Step_Document);
                LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
                runtimeService.setVariable(snID_Process_Activiti, "sKey_Step_Document", sKey_Step_Document);
            }
        }
    }

//3.4) setDocumentStep(snID_Process_Activiti, bNext) //проставить номер шаг (bNext=true > +1 иначе -1) в поле таски с id=sKey_Step_Document    
    public String setDocumentStep(String snID_Process_Activiti, String sKey_Step) throws Exception {//JSONObject
        //assume that we can have only one active task per process at the same time
        LOG.info("sKey_Step={}, snID_Process_Activiti={}", sKey_Step, snID_Process_Activiti);
        HistoricProcessInstance oProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti.trim())
                .includeProcessVariables()
                .singleResult();
        if (oProcessInstance != null) {
            Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
            //Map<String, Object> mProcessVariable = new HashMap();
            /*List<FormProperty> a = oFormService.getTaskFormData(snID_Task).getFormProperties();                    
            for (FormProperty oProperty : a) {
                mProcessVariable.put(oProperty.getId(), oProperty.getValue());
                //String sID = oProperty.getId();
            }*/

            String sKey_Step_Document = mProcessVariable.containsKey("sKey_Step_Document") ? (String) mProcessVariable.get("sKey_Step_Document") : null;
            if ("".equals(sKey_Step_Document)) {
                sKey_Step_Document = null;
            }
            LOG.debug("BEFORE:sKey_Step_Document={}", sKey_Step_Document);

            List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
            LOG.debug("aDocumentStep={}", aDocumentStep);

            if (sKey_Step != null) {
                sKey_Step_Document = sKey_Step;
            } else if (sKey_Step_Document == null) {
                if (aDocumentStep.size() > 1) {
                    aDocumentStep.get(1);
                } else if (aDocumentStep.size() > 0) {
                    aDocumentStep.get(0);
                } else {
                }
            } else {
                Long nOrder = null;
                for (DocumentStep oDocumentStep : aDocumentStep) {
                    if (nOrder != null) {
                        sKey_Step_Document = oDocumentStep.getsKey_Step();
                        break;
                    }
                    if (nOrder == null && sKey_Step_Document.equals(oDocumentStep.getsKey_Step())) {
                        nOrder = oDocumentStep.getnOrder();
                    }
                }
            }

            LOG.debug("AFTER:sKey_Step_Document={}", sKey_Step_Document);
            runtimeService.setVariable(snID_Process_Activiti, "sKey_Step_Document", sKey_Step_Document);
            //oProcessInstance.setProcessVariables();
            //runtimeService.set
            /*
            ProcessInstance oProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti)
                .active()
                .singleResult();
            Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
            */
            
            
        } else {
            throw new Exception("oProcessInstance is null snID_Process_Activiti = " + snID_Process_Activiti);
        }

        return "";
    }
}
