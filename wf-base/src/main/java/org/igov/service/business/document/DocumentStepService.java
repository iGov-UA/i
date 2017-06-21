package org.igov.service.business.document;

import static org.igov.io.fs.FileSystemData.getFileData_Pattern;
import static org.igov.service.business.subject.SubjectGroupTreeService.HUMAN;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.action.vo.DocumentSubmitedUnsignedVO;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightDao;
import org.igov.model.document.DocumentStepSubjectRightField;
import org.igov.model.document.DocumentStepType;
import org.igov.model.document.DocumentSubjectRightPermitionDao;
import org.igov.model.document.DocumentSubjectRightPermition;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupResultTree;
import org.igov.service.business.subject.SubjectGroupTreeService;
import org.igov.service.conf.AttachmetService;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.Tool;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.igov.model.document.DocumentStepSubjectRightFieldDao;

@Component("documentStepService")
@Service
public class DocumentStepService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentStepService.class);

    @Autowired
    @Qualifier("documentStepDao")
    private GenericEntityDao<Long, DocumentStep> oDocumentStepDao;

    @Autowired
    private GenericEntityDao<Long, DocumentStepType> oDocumentStepTypeDao;

    @Autowired
    private DocumentStepSubjectRightDao oDocumentStepSubjectRightDao;

    @Autowired
    private DocumentStepSubjectRightFieldDao oDocumentStepSubjectRightFieldDao;

    @Autowired
    private AttachmetService oAttachmetService;

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

    @Autowired
    private SubjectGroupTreeService oSubjectGroupTreeService;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private DocumentSubjectRightPermitionDao oDocumentSubjectRightPermitionDao;

    public List<DocumentStep> setDocumentSteps(String snID_Process_Activiti, String soJSON) {
        JSONObject oJSON = new JSONObject(soJSON);
        List<DocumentStep> aDocumentStep_Result = new ArrayList<>();
        List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition = new ArrayList<>();
        // process common step if it exists
        Object oStep_Common = oJSON.opt("_");
        LOG.info("snID_Process_Activiti {} Common step is - {}", snID_Process_Activiti, oStep_Common);

        DocumentStepType oDocumentStepType = new DocumentStepType();
        oDocumentStepType.setId(1L);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet_Common = new ArrayList<>();

        if (oStep_Common != null) {
            DocumentStep oDocumentStep_Common = mapToDocumentStep(oStep_Common);
            oDocumentStep_Common.setnOrder(0L);// common step with name "_" has
            // order 0
            oDocumentStep_Common.setsKey_Step("_");
            oDocumentStep_Common.setSnID_Process_Activiti(snID_Process_Activiti);
            oDocumentStep_Common.setoDocumentStepType(oDocumentStepType);
            List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet = oDocumentStep_Common.aDocumentStepSubjectRight();
            if (aDocumentStepSubjectRightToSet != null) {
                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRightToSet) {
                    if (!oDocumentStepSubjectRight.getsKey_GroupPostfix().startsWith("_default_")) {

                        aDocumentStepSubjectRightToSet_Common.add(oDocumentStepSubjectRight);
                    }
                }
            }
            oDocumentStepDao.saveOrUpdate(oDocumentStep_Common);
            aDocumentStep_Result.add(oDocumentStep_Common);
        }
        // process all other steps
        // first of all we filter common step with name "_" and then just
        // convert each step from JSON to POJO
        List<String> asKey_Step = Arrays.asList(JSONObject.getNames(oJSON));
        List<String> asKey_Step_ExcludeCommon = asKey_Step.stream().filter(sKey_Step -> !"_".equals(sKey_Step))
                .collect(Collectors.toList());
        Set asKey_Step_Sort = new TreeSet(asKey_Step_ExcludeCommon);
        asKey_Step_ExcludeCommon = new ArrayList(asKey_Step_Sort);
        LOG.info("List of steps: {}", asKey_Step_ExcludeCommon);

        long i = 1L;
        for (String sKey_Step : asKey_Step_ExcludeCommon) {
            String[] asKey_Step_Split = sKey_Step.split(":");
            if (asKey_Step_Split.length == 2) {
                oDocumentStepType = oDocumentStepTypeDao.findByExpected("name", asKey_Step_Split[1]);
            }
            LOG.info("sKeyStep in setDocumentSteps is: {}", sKey_Step);
            List<String> asKey_Step_Common = new ArrayList<>();
            asKey_Step_Common.add(sKey_Step);
            aDocumentSubjectRightPermition
                    .addAll(getDocumentSubjectRightPermitions(oJSON.get(sKey_Step),
                            false, asKey_Step_Common));
            DocumentStep oDocumentStep = mapToDocumentStep(oJSON.get(sKey_Step));
            oDocumentStep.setnOrder(i++);
            oDocumentStep.setsKey_Step(asKey_Step_Split[0]);
            oDocumentStep.setSnID_Process_Activiti(snID_Process_Activiti);
            oDocumentStep.setoDocumentStepType(oDocumentStepType);
            LOG.info("before add: snID_Process_Activiti is: {} sKey_Step is: {} rights size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(),
                    oDocumentStep.aDocumentStepSubjectRight().size());
            List<DocumentStepSubjectRight> aoDocumentStepSubjectRights_CloneFromCommon = getCommon_DocumentStepSubjectRights(
                    aDocumentStepSubjectRightToSet_Common, oDocumentStep);
            LOG.info(
                    "add common subjectRignts: snID_Process_Activiti is: {} sKey_Step is: {} aoDocumentStepSubjectRights_CloneFromCommon size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(),
                    aoDocumentStepSubjectRights_CloneFromCommon.size());
            if (oDocumentStep.aDocumentStepSubjectRight() == null) {
                oDocumentStep.setaDocumentStepSubjectRight(new ArrayList<>());
            }
            oDocumentStep.aDocumentStepSubjectRight().addAll(aoDocumentStepSubjectRights_CloneFromCommon);
            LOG.info("after add: snID_Process_Activiti is: {} sKey_Step is: {} rights size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(),
                    oDocumentStep.aDocumentStepSubjectRight().size());

            LOG.info("oDocumentStep is before saving {}", oDocumentStep);
            LOG.info("oDocumentStep right is before saving {}", oDocumentStep.aDocumentStepSubjectRight());
            oDocumentStep = oDocumentStepDao.saveOrUpdate(oDocumentStep);
            aDocumentStep_Result.add(oDocumentStep);
        }

        LOG.info("Result list of steps: {}", aDocumentStep_Result);

        aDocumentSubjectRightPermition.addAll(getDocumentSubjectRightPermitions(oStep_Common, oStep_Common != null, asKey_Step_ExcludeCommon));
        //LOG.info("aDocumentSubjectRightPermition size is {}", aDocumentSubjectRightPermition.size());

        LOG.info("aDocumentSubjectRightPermition isn't null");
        for (DocumentStep oDocumentStep_Result : aDocumentStep_Result) {
            LOG.info("oDocumentStep_Result sKey_Step {}", oDocumentStep_Result.getsKey_Step());
            if (oDocumentStep_Result.aDocumentStepSubjectRight() != null) {
                LOG.info("oDocumentStep_Result rights is {}", oDocumentStep_Result.aDocumentStepSubjectRight());
                for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Result.aDocumentStepSubjectRight()) {
                    for (DocumentSubjectRightPermition oDocumentSubjectRightPermition : aDocumentSubjectRightPermition) {
                        LOG.info("oDocumentSubjectRightPermition.getsKeyGroup_Postfix() is {}", oDocumentSubjectRightPermition.getsKeyGroup_Postfix());
                        LOG.info("oDocumentStepSubjectRight.getsKey_GroupPostfix() is {}", oDocumentStepSubjectRight.getsKey_GroupPostfix());

                        if ((oDocumentSubjectRightPermition.getsKeyGroup_Postfix()
                                .equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())
                                && (oDocumentSubjectRightPermition.getnID_DocumentStepSubjectRight() == null))
                                && oDocumentSubjectRightPermition.getsKey_Step().equals(oDocumentStep_Result.getsKey_Step())) {
                            oDocumentSubjectRightPermition.setnID_DocumentStepSubjectRight(oDocumentStepSubjectRight.getId());
                            oDocumentSubjectRightPermitionDao.saveOrUpdate(oDocumentSubjectRightPermition);
                            LOG.info("oDocumentSubjectRightPermition saved is id: {} "
                                    + "DocumentStepSubjectRight id: {} "
                                    + "DocumentStepSubjectRight group: {}",
                                    oDocumentSubjectRightPermition.getId(),
                                    oDocumentStepSubjectRight.getId(),
                                    oDocumentStepSubjectRight.getsKey_GroupPostfix());
                        }
                    }
                }
            }
        }
        return aDocumentStep_Result;
    }

    private boolean isNew_DocumentStepSubjectRight(String snID_Process_Activiti, String sKey_Step_Document,
            String sKey_GroupPostfix_New) {

        List<DocumentStep> aCheckDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti",
                snID_Process_Activiti);
        boolean isNew = true;
        for (DocumentStep oCheckDocumentStep : aCheckDocumentStep) {
            if (oCheckDocumentStep.getsKey_Step().equals(sKey_Step_Document)) {
                return isNew_DocumentStepSubjectRights(oCheckDocumentStep, sKey_GroupPostfix_New);
            }
        }
        return isNew;
    }

    private boolean isNew_DocumentStepSubjectRights(DocumentStep oDocumentStep, String sKey_GroupPostfix_New) {
        List<DocumentStepSubjectRight> aoDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
        if (aoDocumentStepSubjectRight == null) {
            aoDocumentStepSubjectRight = new ArrayList<>();
        }
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aoDocumentStepSubjectRight) {
            if (oDocumentStepSubjectRight.getsKey_GroupPostfix().equalsIgnoreCase(sKey_GroupPostfix_New)) {
                LOG.info(
                        "double DocumentStepSubjectRight: snID_Process_Activiti is: {} sKey_Step is: {} sKey_GroupPostfix: {}",
                        oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(),
                        oDocumentStepSubjectRight.getsKey_GroupPostfix());
                return false;
            }
        }
        return true;
    }

    private List<DocumentStepSubjectRight> getCommon_DocumentStepSubjectRights(
            List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet_Common, DocumentStep oDocumentStep) {

        List<DocumentStepSubjectRight> aoDocumentStepSubjectRight_New = new ArrayList<>();
        if (!aDocumentStepSubjectRightToSet_Common.isEmpty()) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRightToSet_Common : aDocumentStepSubjectRightToSet_Common) {
                if (!isNew_DocumentStepSubjectRights(oDocumentStep,
                        oDocumentStepSubjectRightToSet_Common.getsKey_GroupPostfix())) {
                    continue;
                }
                DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
                oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep);
                oDocumentStepSubjectRight_New
                        .setsKey_GroupPostfix(oDocumentStepSubjectRightToSet_Common.getsKey_GroupPostfix());
                oDocumentStepSubjectRight_New.setbWrite(oDocumentStepSubjectRightToSet_Common.getbWrite());
                oDocumentStepSubjectRight_New.setbNeedECP(oDocumentStepSubjectRightToSet_Common.getbNeedECP());
                Object sName = oDocumentStepSubjectRightToSet_Common.getsName();
                if (sName != null) {
                    oDocumentStepSubjectRight_New.setsName((String) sName);
                }

                List<DocumentStepSubjectRightField> aoDocumentStepSubjectRightField_New = new ArrayList<>();
                for (DocumentStepSubjectRightField oDocumentStepSubjectRightField_From : oDocumentStepSubjectRightToSet_Common
                        .getDocumentStepSubjectRightFields()) {
                    DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                    oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField_From.getbWrite());
                    oDocumentStepSubjectRightField_New
                            .setsMask_FieldID(oDocumentStepSubjectRightField_From.getsMask_FieldID());
                    oDocumentStepSubjectRightField_New.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                    aoDocumentStepSubjectRightField_New.add(oDocumentStepSubjectRightField_New);
                }
                oDocumentStepSubjectRight_New.setDocumentStepSubjectRightFields(aoDocumentStepSubjectRightField_New);

                if (isNew_DocumentStepSubjectRight(oDocumentStep.getSnID_Process_Activiti(),
                        oDocumentStep.getsKey_Step(), oDocumentStepSubjectRight_New.getsKey_GroupPostfix())) {
                    aoDocumentStepSubjectRight_New.add(oDocumentStepSubjectRight_New);
                    LOG.info("oDocumentStepSubjectRight: {} is added", oDocumentStepSubjectRight_New);
                }

                LOG.info(
                        "in adding: snID_Process_Activiti is: {} sKey_Step is: {} sKey_GroupPostfix is: {} right size is: {} ",
                        oDocumentStepSubjectRight_New.getDocumentStep().getSnID_Process_Activiti(),
                        oDocumentStepSubjectRight_New.getDocumentStep().getsKey_Step(),
                        oDocumentStepSubjectRight_New.getsKey_GroupPostfix(),
                        oDocumentStepSubjectRight_New.getDocumentStepSubjectRightFields().size());
            }
        }
        return aoDocumentStepSubjectRight_New;

    }

    private List<DocumentSubjectRightPermition> getDocumentSubjectRightPermitions(Object oStep_JSON, boolean isCommonStep, List<String> asKey_Step_ExcludeCommon) {
        List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition = new ArrayList<>();

        JSONObject oStep = (JSONObject) oStep_JSON;
        LOG.info("try to parse step: {}", oStep);
        LOG.info("isCommonStep {}", isCommonStep);

        if (isCommonStep) {
            asKey_Step_ExcludeCommon.add("_");
        }

        LOG.info("stepCount {}", asKey_Step_ExcludeCommon.size());

        if (oStep == null) {
            return null;
        }

        String[] asKey_Group = JSONObject.getNames(oStep);
        if (asKey_Group != null) {
            for (String sKey_Group : asKey_Group) {

                JSONObject oGroup = oStep.optJSONObject(sKey_Group);
                JSONArray aPermition = oGroup.optJSONArray("aPermition");
                LOG.info("aPermition is {}", aPermition);

                if (aPermition != null) {
                    for (int j = 0; j < asKey_Step_ExcludeCommon.size(); j++) {
                        for (int i = 0; i < aPermition.length(); i++) {
                            LOG.info("Permition elem is {}", aPermition.getString(i));
                            DocumentSubjectRightPermition oDocumentSubjectRightPermition = new DocumentSubjectRightPermition();
                            oDocumentSubjectRightPermition.setPermitionType(aPermition.getString(i));
                            oDocumentSubjectRightPermition.setsKeyGroup_Postfix(sKey_Group);
                            oDocumentSubjectRightPermition.setsKey_Step(asKey_Step_ExcludeCommon.get(j));
                            //oDocumentSubjectRightPermition.setnID_DocumentStepSubjectRight(oDocumentStepSubjectRight.getId());

                            JSONObject oPermitionAcceptor = oGroup.optJSONObject("oPermitions_AddAcceptor");
                            LOG.info("oPermitionAcceptor is {}", oPermitionAcceptor);

                            if (oPermitionAcceptor != null && aPermition.getString(i).equals("AddAcceptor")) {
                                LOG.info("oPermitionAcceptor sKeyGroupe_Source is {}", oPermitionAcceptor.get("sKeyGroupe_Source"));
                                oDocumentSubjectRightPermition.setsKeyGroupeSource((String) oPermitionAcceptor.get("sKeyGroupe_Source"));
                            }

                            JSONObject oPermitionVisor = oGroup.optJSONObject("oPermitions_AddVisor");
                            LOG.info("oPermitionVisor is {}", oPermitionVisor);
                            if (oPermitionVisor != null && aPermition.getString(i).equals("AddVisor")) {
                                LOG.info("oPermitionVisor sKeyGroupe_Source is {}", oPermitionVisor.get("sKeyGroupe_Source"));
                                oDocumentSubjectRightPermition.setsKeyGroupeSource((String) oPermitionVisor.get("sKeyGroupe_Source"));
                            }
                            aDocumentSubjectRightPermition.add(oDocumentSubjectRightPermition);
                        }
                    }
                }
            }
        }
        return aDocumentSubjectRightPermition;
    }

    private DocumentStep mapToDocumentStep(Object oStep_JSON) {
        JSONObject oStep = (JSONObject) oStep_JSON;
        LOG.info("try to parse step: {}", oStep);
        if (oStep == null) {
            return null;
        }

        DocumentStep oDocumentStep = new DocumentStep();
        String[] asKey_Group = JSONObject.getNames(oStep);
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new ArrayList<>();
        if (asKey_Group != null) {
            for (String sKey_Group : asKey_Group) {

                JSONObject oGroup = oStep.optJSONObject(sKey_Group);
                LOG.info("group for step: {}", oGroup);

                if (oGroup == null) {
                    continue;
                }
                DocumentStepSubjectRight oDocumentStepSubjectRight = new DocumentStepSubjectRight();
                oDocumentStepSubjectRight.setsKey_GroupPostfix(sKey_Group);
                Boolean bWrite = null;

                //if((oGroup.get("bWrite") != null )&& (!"null".equals((String)oGroup.get("bWrite")))){
                if (!oGroup.isNull("bWrite")) {
                    LOG.info("oGroup.opt(bWrite) {}", oGroup.opt("bWrite"));
                    bWrite = (Boolean) oGroup.opt("bWrite");
                }

                /*if (bWrite == null) {
                    throw new IllegalArgumentException("Group " + sKey_Group + " hasn't property bWrite. Probably your json is wrong");
                }*/
                oDocumentStepSubjectRight.setbWrite(bWrite);

                Object oNeedECP = oGroup.opt("bNeedECP");
                boolean bNeedECP = false;
                if (oNeedECP != null) {
                    bNeedECP = (boolean) oNeedECP;
                }
                oDocumentStepSubjectRight.setbNeedECP(bNeedECP);

                Object sName = oGroup.opt("sName");
                if (sName != null) {
                    oDocumentStepSubjectRight.setsName((String) sName);
                }

                List<DocumentStepSubjectRightField> aDocumentStepSubjectRightField = mapToFields(oGroup,
                        oDocumentStepSubjectRight);
                oDocumentStepSubjectRight.setDocumentStepSubjectRightFields(aDocumentStepSubjectRightField);
                oDocumentStepSubjectRight.setDocumentStep(oDocumentStep);
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            }
        }
        oDocumentStep.setaDocumentStepSubjectRight(aDocumentStepSubjectRight);
        return oDocumentStep;
    }

    public DocumentStep getDocumentStep(String snID_Process_Activiti, String sKey_Step) {
        List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        final String SKEY_STEP_DOCUMENT = sKey_Step;
        DocumentStep oDocumentStep = aDocumentStep.stream().filter(
                o -> SKEY_STEP_DOCUMENT == null ? o.getnOrder().equals(1) : o.getsKey_Step().equals(SKEY_STEP_DOCUMENT))
                .findAny().orElse(null);

        LOG.info("oDocumentStep={}", oDocumentStep);
        if (oDocumentStep == null) {
            throw new IllegalStateException(
                    "There is no active Document Step, process variable sKey_Step=" + sKey_Step);
        }
        return oDocumentStep;
    }

    public Boolean removeDocumentStepSubject(String snID_Process_Activiti, String sKey_Step, String sKey_Group)
            throws Exception {

        LOG.info("started... sKey_Group={}, snID_Process_Activiti={}, sKey_Step={}", sKey_Group, snID_Process_Activiti,
                sKey_Step);

        Boolean bRemoved = false;

        try {

            DocumentStep oDocumentStep = getDocumentStep(snID_Process_Activiti, sKey_Step);
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
            //LOG.info("aDocumentStepSubjectRight is {}", aDocumentStepSubjectRight);
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                if (sKey_Group.equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                    //oDocumentStepSubjectRight = oDocumentStepSubjectRight;
                    oDocumentStepSubjectRightDao.delete(oDocumentStepSubjectRight);
                    bRemoved = true;
                    //aDocumentStepSubjectRight_New.add(o);
                    break;
                }
            }
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_Step=" + sKey_Step + "" + ",sKey_GroupPostfix=" + sKey_Group + "" + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
        return bRemoved;
    }

    private void delegate(String sKey_Group, String snID_Process_Activiti, String sKey_Group_Delegate, String sKey_Step) throws Exception {

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = cloneDocumentStepSubject(snID_Process_Activiti, sKey_Group, sKey_Group_Delegate, sKey_Step, true);

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (sKey_Group.equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                LOG.info("{} is equals {} in delegateDocumentStepSubject", sKey_Group, oDocumentStepSubjectRight.getsKey_GroupPostfix());
                oDocumentStepSubjectRight.setbWrite(null);
                oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight);
                break;
            }
        }
    }

    private void addVisor(DocumentStep oDocumentStep, List<DocumentStepSubjectRight> aDocumentStepSubjectRight,
            String sKey_Group, String snID_Process_Activiti, String sKey_Group_Delegate, String sKey_Step) throws Exception {
        /*LOG.info("addVisor params: oDocumentStep {}  aDocumentStepSubjectRight {} sKey_Group {} "
                + "snID_Process_Activiti {} sKey_Group_Delegate {} sKey_Step{}", oDocumentStep, aDocumentStepSubjectRight,
                sKey_Group, snID_Process_Activiti, sKey_Group_Delegate, sKey_Step);*/

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (sKey_Group.equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                LOG.info("sKey_Group in AddVisor is {}", sKey_Group);
                List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition
                        = oDocumentSubjectRightPermitionDao.findAllBy("nID_DocumentStepSubjectRight", oDocumentStepSubjectRight.getId());
                LOG.info("aDocumentSubjectRightPermition is {}", aDocumentSubjectRightPermition);

                for (DocumentSubjectRightPermition oDocumentSubjectRightPermition : aDocumentSubjectRightPermition) {
                    LOG.info("oDocumentSubjectRightPermition in AddVisor is {}", oDocumentSubjectRightPermition);
                    if (oDocumentSubjectRightPermition.getPermitionType().equals("AddVisor")) {
                        if (oDocumentSubjectRightPermition.getsKeyGroupeSource() != null) {

                            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_New = cloneDocumentStepSubject(snID_Process_Activiti,
                                    oDocumentSubjectRightPermition.getsKeyGroupeSource(), sKey_Group_Delegate, sKey_Step, true);

                            for (DocumentStepSubjectRight oDocumentStepSubjectRight_New : aDocumentStepSubjectRight_New) {
                                if (sKey_Group_Delegate.equals(oDocumentStepSubjectRight_New.getsKey_GroupPostfix())) {
                                    LOG.info("{} is equals {} in delegateDocumentStepSubject", sKey_Group, oDocumentStepSubjectRight_New.getsKey_GroupPostfix());
                                    oDocumentStepSubjectRight_New.setbWrite(false);
                                    oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight_New);
                                    break;
                                }
                            }

                        } else {
                            LOG.info("sKeyGroupeSource is null");
                            DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
                            oDocumentStepSubjectRight_New.setsKey_GroupPostfix(sKey_Group_Delegate);
                            oDocumentStepSubjectRight_New.setbWrite(false);
                            oDocumentStepSubjectRight_New.setbNeedECP(oDocumentStepSubjectRight.getbNeedECP());
                            oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep);

                            oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight_New);

                            LOG.info("oDocumentStepSubjectRightDao in AddVisor is {}", oDocumentStepSubjectRight_New.getId());

                            for (DocumentStepSubjectRightField oDocumentStepSubjectRightField : oDocumentStepSubjectRight.getDocumentStepSubjectRightFields()) {
                                DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                                oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField.getbWrite());
                                oDocumentStepSubjectRightField_New.setsMask_FieldID(oDocumentStepSubjectRightField.getsMask_FieldID());
                                oDocumentStepSubjectRightField.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                                oDocumentStepSubjectRightFieldDao.saveOrUpdate(oDocumentStepSubjectRightField);
                                LOG.info("DocumentStepSubjectRightField in AddVisor is {}", oDocumentStepSubjectRightField_New.getId());
                            }
                        }
                        break;
                    }
                }

                break;
            }
        }
    }

    private void addAcceptor(DocumentStep oDocumentStep, List<DocumentStepSubjectRight> aDocumentStepSubjectRight,
            String sKey_Group, String snID_Process_Activiti, String sKey_Group_Delegate, String sKey_Step) throws Exception {
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (sKey_Group.equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                LOG.info("sKey_Group in AddAcceptor is {}", sKey_Group);
                List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition
                        = oDocumentSubjectRightPermitionDao.findAllBy("nID_DocumentStepSubjectRight", oDocumentStepSubjectRight.getId());
                LOG.info("aDocumentSubjectRightPermition is {}", aDocumentSubjectRightPermition);

                for (DocumentSubjectRightPermition oDocumentSubjectRightPermition : aDocumentSubjectRightPermition) {
                    LOG.info("oDocumentSubjectRightPermition in addAcceptor is {}", oDocumentSubjectRightPermition);
                    if (oDocumentSubjectRightPermition.getPermitionType().equals("AddAcceptor")) {
                        if (oDocumentSubjectRightPermition.getsKeyGroupeSource() != null) {

                            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_New = cloneDocumentStepSubject(snID_Process_Activiti,
                                    oDocumentSubjectRightPermition.getsKeyGroupeSource(), sKey_Group_Delegate, sKey_Step, true);

                            for (DocumentStepSubjectRight oDocumentStepSubjectRight_New : aDocumentStepSubjectRight_New) {
                                if (sKey_Group_Delegate.equals(oDocumentStepSubjectRight_New.getsKey_GroupPostfix())) {
                                    LOG.info("{} is equals {} in delegateDocumentStepSubject", sKey_Group, oDocumentStepSubjectRight_New.getsKey_GroupPostfix());
                                    oDocumentStepSubjectRight_New.setbWrite(true);
                                    oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight_New);
                                    break;
                                }
                            }
                        } else {
                            LOG.info("sKeyGroupeSource is null");
                            DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
                            oDocumentStepSubjectRight_New.setsKey_GroupPostfix(sKey_Group_Delegate);
                            oDocumentStepSubjectRight_New.setbWrite(true);
                            oDocumentStepSubjectRight_New.setbNeedECP(oDocumentStepSubjectRight.getbNeedECP());
                            oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep);

                            oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight_New);

                            LOG.info("oDocumentStepSubjectRightDao in addAcceptor is {}", oDocumentStepSubjectRight_New.getId());

                            for (DocumentStepSubjectRightField oDocumentStepSubjectRightField : oDocumentStepSubjectRight.getDocumentStepSubjectRightFields()) {
                                DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                                oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField.getbWrite());
                                oDocumentStepSubjectRightField_New.setsMask_FieldID(oDocumentStepSubjectRightField.getsMask_FieldID());
                                oDocumentStepSubjectRightField.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                                oDocumentStepSubjectRightFieldDao.saveOrUpdate(oDocumentStepSubjectRightField);
                                LOG.info("DocumentStepSubjectRightField in addAcceptor is {}", oDocumentStepSubjectRightField_New.getId());
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public List<DocumentStepSubjectRight> delegateDocumentStepSubject(String snID_Process_Activiti, String sKey_Step,
            String sKey_Group, String sKey_Group_Delegate, String sOperationType) throws Exception {

        LOG.info("started... sKey_Group={}, snID_Process_Activiti={}, sKey_Step={}", sKey_Group, snID_Process_Activiti,
                sKey_Step);
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = new LinkedList();
        try {

            DocumentStep oDocumentStep = getDocumentStep(snID_Process_Activiti, sKey_Step);

            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
            //LOG.info("aDocumentStepSubjectRight is {}", aDocumentStepSubjectRight);

            if (sOperationType.equals("delegate")) {
                delegate(sKey_Group, snID_Process_Activiti, sKey_Group_Delegate, sKey_Step);
            }

            if (sOperationType.equals("AddAcceptor")) {
                addAcceptor(oDocumentStep, aDocumentStepSubjectRight, sKey_Group, snID_Process_Activiti, sKey_Group_Delegate, sKey_Step);
            }

            if (sOperationType.equals("AddVisor")) {
                addVisor(oDocumentStep, aDocumentStepSubjectRight, sKey_Group, snID_Process_Activiti, sKey_Group_Delegate, sKey_Step);
            }

            String nId_Task = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).
                    active().singleResult().getId();
            oTaskService.addCandidateGroup(nId_Task, sKey_Group_Delegate);
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_Step=" + sKey_Step + "" + ",sKey_GroupPostfix=" + sKey_Group + "" + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
        return aDocumentStepSubjectRight_Current;
    }

    private void reCloneRight(List<DocumentStepSubjectRight> aDocumentStepSubjectRight_To,
            DocumentStepSubjectRight oDocumentStepSubjectRight_From, String sKey_GroupPostfix_New) {

        try {

            for (int i = 0; i < aDocumentStepSubjectRight_To.size(); i++) {
                DocumentStepSubjectRight oDocumentStepSubjectRight_To = aDocumentStepSubjectRight_To.get(i);
                if (oDocumentStepSubjectRight_To.getsKey_GroupPostfix().equals(sKey_GroupPostfix_New)) {
                    if (oDocumentStepSubjectRight_To.getsDate() != null) {
                        LOG.info("DocumentStepSubjectRight_From when sDate isn't null: {}",
                                oDocumentStepSubjectRight_From);
                        LOG.info("DocumentStepSubjectRight_To equals _From with date {}", oDocumentStepSubjectRight_To);
                        oDocumentStepSubjectRight_To.setsDate(null);
                        oDocumentStepSubjectRight_To.setsDateECP(null);
                        List<DocumentStepSubjectRightField> aDocumentStepSubjectRightField_New = oDocumentStepSubjectRight_From
                                .getDocumentStepSubjectRightFields();

                        DocumentStepSubjectRightField oDocumentStepSubjectRightFieldRead_From = null;
                        DocumentStepSubjectRightField oDocumentStepSubjectRightFieldWrite_From = null;

                        if (aDocumentStepSubjectRightField_New.get(0).getbWrite() == true) {
                            oDocumentStepSubjectRightFieldWrite_From = aDocumentStepSubjectRightField_New.get(0);
                            oDocumentStepSubjectRightFieldRead_From = aDocumentStepSubjectRightField_New.get(1);
                        } else {
                            oDocumentStepSubjectRightFieldWrite_From = aDocumentStepSubjectRightField_New.get(1);
                            oDocumentStepSubjectRightFieldRead_From = aDocumentStepSubjectRightField_New.get(0);
                        }

                        if (oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(0)
                                .getbWrite() == true) {

                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(0)
                                    .setbWrite(oDocumentStepSubjectRightFieldWrite_From.getbWrite());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(0)
                                    .setsMask_FieldID(oDocumentStepSubjectRightFieldWrite_From.getsMask_FieldID());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(1)
                                    .setbWrite(oDocumentStepSubjectRightFieldRead_From.getbWrite());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(1)
                                    .setsMask_FieldID(oDocumentStepSubjectRightFieldRead_From.getsMask_FieldID());

                        } else {

                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(1)
                                    .setbWrite(oDocumentStepSubjectRightFieldWrite_From.getbWrite());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(1)
                                    .setsMask_FieldID(oDocumentStepSubjectRightFieldWrite_From.getsMask_FieldID());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(0)
                                    .setbWrite(oDocumentStepSubjectRightFieldRead_From.getbWrite());
                            oDocumentStepSubjectRight_To.getDocumentStepSubjectRightFields().get(0)
                                    .setsMask_FieldID(oDocumentStepSubjectRightFieldRead_From.getsMask_FieldID());
                        }

                        LOG.info("DocumentStepSubjectRight_To before saving is: {}", oDocumentStepSubjectRight_To);
                        oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight_To);
                        break;
                    }
                }
            }

        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + ",sKey_GroupPostfix_New=" + sKey_GroupPostfix_New
                    + " )");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
    }

    public List<DocumentStepSubjectRight> cloneDocumentStepSubject(String snID_Process_Activiti,
            String sKey_GroupPostfix, String sKey_GroupPostfix_New, String sKey_Step_Document_To, boolean bReClone)
            throws Exception {

        LOG.info(
                "cloneDocumentStepSubject started... sKey_GroupPostfix={}, snID_Process_Activiti={}, sKey_GroupPostfix_New={}, sKey_Step_Document={}",
                sKey_GroupPostfix, snID_Process_Activiti, sKey_GroupPostfix_New, sKey_Step_Document_To);

        String sKey_Step_Document_From = sKey_Step_Document_To;
        List<DocumentStepSubjectRight> resultList = new ArrayList<>();

        try {
            if (sKey_GroupPostfix.startsWith("_default_")) {
                sKey_Step_Document_From = "_";
            }

            String sSubjectType = oSubjectGroupTreeService.getSubjectType(sKey_GroupPostfix_New);
            LOG.info("sSubjectType in cloneRights is {}", sSubjectType);

            SubjectGroupResultTree oSubjectGroupResultTree = null;

            if (sSubjectType.equals("Organ")) {
                oSubjectGroupResultTree = oSubjectGroupTreeService.getCatalogSubjectGroupsTree(sKey_GroupPostfix_New,
                        1L, null, false, 1L, HUMAN);
            }

            Set<String> asID_Group_Activiti_New = new TreeSet<>();
            if (oSubjectGroupResultTree != null) {
                List<SubjectGroup> aSubjectGroups = oSubjectGroupResultTree.getaSubjectGroupTree();
                if (aSubjectGroups == null || aSubjectGroups.isEmpty()) {
                    throw new RuntimeException("aSubjectGroups=" + aSubjectGroups
                            + ". Not found any SubjectGroup by sKey_GroupPostfix_New=" + sKey_GroupPostfix_New
                            + " (sSubjectType=" + sSubjectType + ")");
                } else {
                    aSubjectGroups.forEach((oSubjectGroup) -> {
                        asID_Group_Activiti_New.add(oSubjectGroup.getsID_Group_Activiti());
                    });
                }
            } else {
                asID_Group_Activiti_New.add(sKey_GroupPostfix_New);
            }

            LOG.info("asID_Group_Activiti_New is {}", asID_Group_Activiti_New);

            List<DocumentStep> aDocumentStep_From = oDocumentStepDao.findAllBy("snID_Process_Activiti",
                    snID_Process_Activiti);
            LOG.info("aDocumentStep={}", aDocumentStep_From);

            List<DocumentStep> aDocumentStep_To = oDocumentStepDao.findAllBy("snID_Process_Activiti",
                    snID_Process_Activiti);
            LOG.info("aDocumentStep={}", aDocumentStep_To);

            final String SKEY_STEP_DOCUMENT_FROM = sKey_Step_Document_From;
            DocumentStep oDocumentStep_From = aDocumentStep_From.stream().filter(o -> SKEY_STEP_DOCUMENT_FROM == null
                    ? o.getnOrder().equals(1) : o.getsKey_Step().equals(SKEY_STEP_DOCUMENT_FROM)).findAny()
                    .orElse(null);

            LOG.info("oDocumentStep_From={}", oDocumentStep_From);
            if (oDocumentStep_From == null) {
                throw new IllegalStateException("There is no active Document Step, process variable sKey_Step_Document="
                        + sKey_Step_Document_From);
            }

            final String SKEY_STEP_DOCUMENT_TO = sKey_Step_Document_To;
            DocumentStep oDocumentStep_To = aDocumentStep_To.stream().filter(o -> SKEY_STEP_DOCUMENT_TO == null
                    ? o.getnOrder().equals(1) : o.getsKey_Step().equals(SKEY_STEP_DOCUMENT_TO)).findAny().orElse(null);

            LOG.info("oDocumentStep_To={}", oDocumentStep_To);
            if (oDocumentStep_To == null) {
                throw new IllegalStateException("There is no active Document Step, process variable sKey_Step_Document="
                        + sKey_Step_Document_To);
            }

            List<String> asID_Group_Activiti_New_Selected = new LinkedList();
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_From = oDocumentStep_From.aDocumentStepSubjectRight();
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_To = oDocumentStep_To.aDocumentStepSubjectRight();
            //LOG.info("aDocumentStepSubjectRight_From is {}", aDocumentStepSubjectRight_From);
            //LOG.info("aDocumentStepSubjectRight_To is {}", aDocumentStepSubjectRight_To);
            DocumentStepSubjectRight oDocumentStepSubjectRight_From = null;
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight_From) {
                if (sKey_GroupPostfix.equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                    oDocumentStepSubjectRight_From = oDocumentStepSubjectRight;
                    break;
                }
            }
            if (oDocumentStepSubjectRight_From == null) {
                throw new Exception("Can't find etalonn oDocumentStepSubjectRight_From");
            }

            LOG.info("!!! sKey_GroupPostfix: {} oDocumentStepSubjectRight_From.getsKey_GroupPostfix(): {}",
                    sKey_GroupPostfix, oDocumentStepSubjectRight_From.getsKey_GroupPostfix());

            for (String sID_Group_Activiti_New : asID_Group_Activiti_New) {
                if (isNew_DocumentStepSubjectRight(snID_Process_Activiti, oDocumentStep_To.getsKey_Step(),
                        sKey_GroupPostfix_New)) {
                    asID_Group_Activiti_New_Selected.add(sID_Group_Activiti_New);
                } else if (bReClone) {
                    reCloneRight(aDocumentStepSubjectRight_To, oDocumentStepSubjectRight_From, sKey_GroupPostfix_New);
                } else {
                    LOG.info("skip sKey_GroupPostfix_New: {} sKey_GroupPostfix: {}", sKey_GroupPostfix_New,
                            oDocumentStep_To.getsKey_Step());
                }

            }

            for (String sID_Group_Activiti_New_Selected : asID_Group_Activiti_New_Selected) {
                DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
                oDocumentStepSubjectRight_New.setsKey_GroupPostfix(sID_Group_Activiti_New_Selected);
                oDocumentStepSubjectRight_New.setbWrite(oDocumentStepSubjectRight_From.getbWrite());
                oDocumentStepSubjectRight_New.setbNeedECP(oDocumentStepSubjectRight_From.getbNeedECP());
                Object sName = oDocumentStepSubjectRight_From.getsName();
                if (sName != null) {
                    oDocumentStepSubjectRight_New.setsName((String) sName);
                }
                List<DocumentStepSubjectRightField> aDocumentStepSubjectRightField_New = new LinkedList();

                for (DocumentStepSubjectRightField oDocumentStepSubjectRightField_From : oDocumentStepSubjectRight_From
                        .getDocumentStepSubjectRightFields()) {
                    DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                    oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField_From.getbWrite());
                    oDocumentStepSubjectRightField_New
                            .setsMask_FieldID(oDocumentStepSubjectRightField_From.getsMask_FieldID());
                    oDocumentStepSubjectRightField_New.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                    aDocumentStepSubjectRightField_New.add(oDocumentStepSubjectRightField_New);
                }
                oDocumentStepSubjectRight_New.setDocumentStepSubjectRightFields(aDocumentStepSubjectRightField_New);
                oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep_To);
                LOG.info("right for step: {}", oDocumentStepSubjectRight_New);

                aDocumentStepSubjectRight_To.add(oDocumentStepSubjectRight_New);
                oDocumentStep_To.setaDocumentStepSubjectRight(aDocumentStepSubjectRight_To);
                resultList.add(oDocumentStepSubjectRight_New);
                LOG.info("aDocumentStepSubjectRight_To before saving is {} ", aDocumentStepSubjectRight_To);
                oDocumentStepDao.saveOrUpdate(oDocumentStep_To);
            }

            if (oDocumentStepSubjectRight_From.getsKey_GroupPostfix().startsWith("_default_")) {
                List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep_To.aDocumentStepSubjectRight();

                DocumentStepSubjectRight oDocumentStepSubjectRight_saved = null;

                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                    if (oDocumentStepSubjectRight.getsKey_GroupPostfix().equals(sKey_GroupPostfix_New)) {
                        oDocumentStepSubjectRight_saved = oDocumentStepSubjectRight;
                        LOG.info("sKey_GroupPostfix_New for permition in cloneRights is {}", sKey_GroupPostfix_New);
                        break;
                    }
                }

                List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition
                        = oDocumentSubjectRightPermitionDao.findAllBy("nID_DocumentStepSubjectRight", oDocumentStepSubjectRight_From.getId());

                for (DocumentSubjectRightPermition oDocumentSubjectRightPermition : aDocumentSubjectRightPermition) {
                    DocumentSubjectRightPermition oDocumentSubjectRightPermition_new = new DocumentSubjectRightPermition();
                    oDocumentSubjectRightPermition_new.setPermitionType(oDocumentSubjectRightPermition.getPermitionType());
                    oDocumentSubjectRightPermition_new.setnID_DocumentStepSubjectRight(oDocumentStepSubjectRight_saved.getId());
                    oDocumentSubjectRightPermition_new.setsKeyGroup_Postfix(sKey_GroupPostfix_New);
                    LOG.info("oDocumentStepSubjectRight_saved id for permition in cloneRights is {}", oDocumentStepSubjectRight_saved.getId());
                    oDocumentSubjectRightPermitionDao.saveOrUpdate(oDocumentSubjectRightPermition_new);
                    LOG.info("oDocumentSubjectRightPermition_new id for permition in cloneRights is {}", oDocumentSubjectRightPermition_new.getId());
                }
            }

        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_GroupPostfix=" + sKey_GroupPostfix + "" + ",sKey_GroupPostfix_New=" + sKey_GroupPostfix_New
                    + "" + ",sKey_Step_Document_To=" + sKey_Step_Document_To + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
        return resultList;
    }

    // TODO: Нужно выпилять из БП
    @Deprecated
    public List<DocumentStepSubjectRight> cloneDocumentStepSubject(String snID_Process_Activiti,
            String sKey_GroupPostfix, String sKey_GroupPostfix_New, String sKey_Step_Document_To) throws Exception {
        return cloneDocumentStepSubject(snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New,
                sKey_Step_Document_To, true); // Todo: set bReClone=false after
        // corecting BA
    }

    public List<String> getLoginsFromField(String snID_Process_Activiti, String sID_Field) throws Exception {
        return getLoginsFromField(snID_Process_Activiti, sID_Field, null);
    }

    public List<String> getLoginsFromField(String snID_Process_Activiti, String sID_Field, String sID_FieldTable) throws Exception {
        List<String> asLogin = new LinkedList();
        try {

            if (sID_FieldTable == null) {
                sID_FieldTable = "sLogin_isExecute";
            }

            String sValue = (String) runtimeService.getVariable(snID_Process_Activiti, sID_Field);
            // String soJSON=(String)
            // runtimeService.getVariable(snID_Process_Activiti, sID_Field);
            if (sValue.startsWith("{")) {// TABLE
                JSONParser parser = new JSONParser();

                org.json.simple.JSONObject oTableJSONObject = (org.json.simple.JSONObject) parser.parse(sValue);

                InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
                        (String) oTableJSONObject.get("sKey"), (String) oTableJSONObject.get("sID_StorageType"))
                        .getInputStream();

                org.json.simple.JSONObject oJSONObject = (org.json.simple.JSONObject) parser
                        .parse(IOUtils.toString(oAttachmet_InputStream, "UTF-8"));
                LOG.info("oTableJSONObject in listener: " + oJSONObject.toJSONString());

                LOG.info("oJSONObject in cloneDocumentStepFromTable is {}", oJSONObject.toJSONString());

                org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

                if (aJsonRow != null) {
                    for (int i = 0; i < aJsonRow.size(); i++) {
                        org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
                        LOG.info("oJsonField in cloneDocumentStepFromTable is {}", oJsonField);
                        if (oJsonField != null) {
                            org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
                            LOG.info("aJsonField in cloneDocumentStepFromTable is {}", aJsonField);
                            if (aJsonField != null) {
                                for (int j = 0; j < aJsonField.size(); j++) {
                                    org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
                                            .get(j);
                                    LOG.info("oJsonMap in cloneDocumentStepFromTable is {}", oJsonMap);
                                    if (oJsonMap != null) {
                                        Object oId = oJsonMap.get("id");
                                        if (((String) oId).equals(sID_FieldTable)
                                                || ((String) oId).equals("sID_Group_Activiti_isExecute")
                                                || ((String) oId).equals("sLogin_Approver")
                                                || ((String) oId).equals("sLogin_Addressee")) {
                                            Object oValue = oJsonMap.get("value");
                                            if (oValue != null) {
                                                LOG.info("oValue in cloneDocumentStepFromTable is {}", oValue);
                                                asLogin.add((String) oValue);
                                            } else {
                                                LOG.info("oValue in cloneDocumentStepFromTable is null");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LOG.info("JSON array is null in cloneDocumentStepFromTable is null");
                }
            } else {// Simple field with login
                asLogin.add(sValue);
            }
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sID_Field=" + sID_Field + ")", oException);
            throw oException;
        }
        return asLogin;
    }

    public List<DocumentStepSubjectRight> cloneDocumentStepFromTable(String snID_Process_Activiti, String sKey_Group,
            String sID_Field, String sKey_Step, boolean bReClone) throws Exception {
        return cloneDocumentStepFromTable(snID_Process_Activiti, sKey_Group, sID_Field, sKey_Step, bReClone, null);
    }

    public List<DocumentStepSubjectRight> cloneDocumentStepFromTable(String snID_Process_Activiti, String sKey_Group,
            String sID_Field, String sKey_Step, boolean bReClone, String sID_FieldTable) throws Exception {

        LOG.info("started...");
        LOG.info("sKey_Group={}, snID_Process_Activiti={}, sID_Field={}, sKey_Step={}", sKey_Group,
                snID_Process_Activiti, sID_Field, sKey_Step);
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Return = new ArrayList<>();
        try {
            List<String> asLogin = getLoginsFromField(snID_Process_Activiti, sID_Field, sID_FieldTable);
            for (String sLogin : asLogin) {
                List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = cloneDocumentStepSubject(
                        snID_Process_Activiti, sKey_Group, sLogin, sKey_Step, bReClone);
                aDocumentStepSubjectRight_Return.addAll(aDocumentStepSubjectRight_Current);
            }
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_GroupPostfix=" + sKey_Group + "" + ",sID_Field=" + sID_Field + ""
                    + ",sKey_Step_Document_To=" + sKey_Step + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
        return aDocumentStepSubjectRight_Return;
    }

    // TODO: Нужно выпилять из БП
    @Deprecated
    public List<DocumentStepSubjectRight> cloneDocumentStepFromTable(String snID_Process_Activiti,
            String sKey_GroupPostfix, String sID_Field, String sKey_Step_Document_To) throws Exception {
        return cloneDocumentStepFromTable(snID_Process_Activiti, sKey_GroupPostfix, sID_Field, sKey_Step_Document_To,
                false, null);
    }

    public List<DocumentStepSubjectRight> syncDocumentSubmitedsByField(String snID_Process_Activiti,
            String sKey_Group_Default, String sID_Field, String sKey_Step, boolean bReClone) throws Exception {

        LOG.info("started...");
        LOG.info("snID_Process_Activiti={}, sKey_Group_Default={}, sID_Field={}, sKey_Step={}", snID_Process_Activiti,
                sKey_Group_Default, sID_Field, sKey_Step);
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Return = new ArrayList<>();
        try {
            List<String> asLogin = getLoginsFromField(snID_Process_Activiti, sID_Field);
            // List<String> asLogin_Update = new LinkedList();
            // List<String> asLogin_Remove = new LinkedList();
            // List<String> asLogin_Add = new LinkedList();
            List<String> asLogin_Found = new LinkedList();

            DocumentStep oDocumentStep = getDocumentStep(snID_Process_Activiti, sKey_Step);
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_ForRemove = new LinkedList();
            //LOG.info("aDocumentStepSubjectRight is {}", aDocumentStepSubjectRight);

            // DocumentStepSubjectRight oDocumentStepSubjectRight = null;
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                //LOG.info("oDocumentStepSubjectRight={}", oDocumentStepSubjectRight);
                if (sID_Field.equals(oDocumentStepSubjectRight.getsID_Field())) {
                    String sLogin = oDocumentStepSubjectRight.getsKey_GroupPostfix();
                    if (asLogin.contains(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                        // asLogin_Update.add(sLogin);
                        // oDocumentStepSubjectRight.
                        // oDocumentStepSubjectRight = o;
                        // break;
                        // asLogin_Update.add(HUMAN)
                        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = cloneDocumentStepSubject(
                                snID_Process_Activiti, sKey_Group_Default, sLogin, sKey_Step, bReClone);
                        aDocumentStepSubjectRight_Return.addAll(aDocumentStepSubjectRight_Current);
                    } else {
                        // asLogin_Add.add(sLogin);
                        // asLogin_Remove.add(sLogin);
                        aDocumentStepSubjectRight_ForRemove.add(oDocumentStepSubjectRight);
                    }
                    asLogin_Found.add(sLogin);
                }
            }
            asLogin.removeAll(asLogin_Found);
            for (String sLogin : asLogin) {
                List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = cloneDocumentStepSubject(
                        snID_Process_Activiti, sKey_Group_Default, sLogin, sKey_Step, bReClone);
                aDocumentStepSubjectRight_Return.addAll(aDocumentStepSubjectRight_Current);
            }

            /*
			 * for(String sLogin : asLogin){ List<DocumentStepSubjectRight>
			 * aDocumentStepSubjectRight_Current =
			 * cloneDocumentStepSubject(snID_Process_Activiti, sKey_Group,
			 * sLogin, sKey_Step, bReClone);
			 * aDocumentStepSubjectRight_Return.addAll(
			 * aDocumentStepSubjectRight_Current); }
             */
            //LOG.info("aDocumentStepSubjectRight_ForRemove={}", aDocumentStepSubjectRight_ForRemove);
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_GroupPostfix=" + sKey_Group_Default + "" + ",sID_Field=" + sID_Field + ""
                    + ",sKey_Step_Document_To=" + sKey_Step + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
        return aDocumentStepSubjectRight_Return;
    }

    public Boolean cancelDocumentSubmit(String snID_Process_Activiti, String sKey_Step, String sKey_Group)
            throws Exception {

        LOG.info("started...");
        LOG.info("snID_Process_Activiti={}, sKey_Step={}, sKey_Group={}", snID_Process_Activiti, sKey_Step, sKey_Group);

        Boolean bCanceled = false;

        try {

            DocumentStep oDocumentStep = getDocumentStep(snID_Process_Activiti, sKey_Step);
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
            LOG.info("aDocumentStepSubjectRight is {}", aDocumentStepSubjectRight);

            for (int i = 0; i < aDocumentStepSubjectRight.size(); i++) {
                DocumentStepSubjectRight oDocumentStepSubjectRight = aDocumentStepSubjectRight.get(i);
                if (oDocumentStepSubjectRight.getsKey_GroupPostfix().equals(sKey_Group)) {
                    if (oDocumentStepSubjectRight.getsDate() != null) {
                        LOG.info("DocumentStepSubjectRight equals _From with date {}: " + "sKey_Group is: {}",
                                oDocumentStepSubjectRight.getsKey_GroupPostfix());
                        oDocumentStepSubjectRight.setsDate(null);
                        oDocumentStepSubjectRight.setsDateECP(null);
                        oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight);
                        bCanceled = true;
                        break;
                    }
                }
            }

        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sKey_GroupPostfix=" + sKey_Group + "" + ",sKey_Step_Document=" + sKey_Step + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }

        return bCanceled;
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

    public List<Map<String, Object>> getDocumentStepLogins(String snID_Process_Activiti) {// JSONObject

        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        List<Task> aTaskActive = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active()
                .list();
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

        ProcessInstance oProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti).active().singleResult();
        Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
        LOG.info("mProcessVariable={}", mProcessVariable);
        String snID_Task = oTaskActive.getId();
        List<FormProperty> aProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();
        for (FormProperty oProperty : aProperty) {
            mProcessVariable.put(oProperty.getId(), oProperty.getValue());
        }
        LOG.info("mProcessVariable(added)={}", mProcessVariable);

        String sKey_Step_Document = (String) mProcessVariable.get("sKey_Step_Document");

        List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        DocumentStep oDocumentStep_Common = aDocumentStep.stream().filter(o -> o.getsKey_Step().equals("_")).findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Common={}", oDocumentStep_Common);

        DocumentStep oDocumentStep_Active = aDocumentStep.stream().filter(
                o -> sKey_Step_Document == null ? o.getnOrder().equals(1) : o.getsKey_Step().equals(sKey_Step_Document))
                .findAny().orElse(null);
        LOG.info("oDocumentStep_Active={}", oDocumentStep_Active);
        if (oDocumentStep_Active == null) {
            throw new IllegalStateException(
                    "There is no active Document Sep, process variable sKey_Step_Document=" + sKey_Step_Document);
        }

        List<Map<String, Object>> amReturn = new LinkedList();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new LinkedList();
        if (oDocumentStep_Common != null) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Common.aDocumentStepSubjectRight()) {
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            }
        }

        List<DocumentStep> oDocumentStep_ExceptCommon = aDocumentStep.stream()
                .filter(o -> !o.getsKey_Step().equals("_")).collect(Collectors.toList());
        for (DocumentStep oDocumentStep : oDocumentStep_ExceptCommon) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep.aDocumentStepSubjectRight()) { //oDocumentStep_Active.getRights()
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            }
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            Map<String, Object> mParamDocumentStepSubjectRight = new HashMap();
            mParamDocumentStepSubjectRight.put("sDate", oDocumentStepSubjectRight.getsDate() == null ? ""
                    : formatter.print(oDocumentStepSubjectRight.getsDate()));// "2016-05-15
            mParamDocumentStepSubjectRight.put("sDateECP", oDocumentStepSubjectRight.getsDateECP() == null ? ""
                    : formatter.print(oDocumentStepSubjectRight.getsDateECP()));// "2016-05-15
            mParamDocumentStepSubjectRight.put("bNeedECP", oDocumentStepSubjectRight.getbNeedECP() == null ? false
                    : oDocumentStepSubjectRight.getbNeedECP());

            mParamDocumentStepSubjectRight.put("bWrite", oDocumentStepSubjectRight.getbWrite());// false
            mParamDocumentStepSubjectRight.put("sName",
                    oDocumentStepSubjectRight.getsName() == null ? "" : oDocumentStepSubjectRight.getsName());// "Главный
            // контроллирующий"
            String sID_Group = oDocumentStepSubjectRight.getsKey_GroupPostfix();
            List<User> aUser = oIdentityService.createUserQuery().memberOfGroup(sID_Group).list();
            LOG.info("getDocumentStepLogins sID_Group={}, aUser={}", sID_Group, aUser);
            List<Map<String, Object>> amUserProperty = new LinkedList();
            for (User oUser : aUser) {
                LOG.info("oDocumentStepSubjectRight.getsLogin() is {}", oDocumentStepSubjectRight.getsLogin());
                LOG.info("oUser.getId() is {}", oUser.getId());
                //if (oUser.getId().equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                Map<String, Object> mUser = new HashMap();
                mUser.put("sLogin", oUser.getId());
                mUser.put("sID_Group", sID_Group);
                mUser.put("sFIO", oUser.getLastName() + " " + oUser.getFirstName());
                amUserProperty.add(mUser);
                //}
            }
            mParamDocumentStepSubjectRight.put("aUser", amUserProperty);
            LOG.info("amUserProperty={}", amUserProperty);
            String sLogin = oDocumentStepSubjectRight.getsLogin();
            LOG.info("sLogin={}", sLogin);
            String sLogin_Referent = "", sFIO_Referent = "";
            if (sLogin != null) {
                User oUser = oIdentityService.createUserQuery().userId(sLogin).singleResult();
                if (oUser != null) {
                    sLogin_Referent = oUser.getId();
                    sFIO_Referent = oUser.getLastName() + " " + oUser.getFirstName();
                }
            }
            mParamDocumentStepSubjectRight.put("sLogin_Referent", sLogin_Referent);
            mParamDocumentStepSubjectRight.put("sFIO_Referent", sFIO_Referent);
            LOG.info("mParamDocumentStepSubjectRight={}", mParamDocumentStepSubjectRight);
            amReturn.add(mParamDocumentStepSubjectRight);
        }
        LOG.info("amReturn={}", amReturn);

        return amReturn;
    }

    public Map<String, Object> getDocumentStepRights(String sLogin, String snID_Process_Activiti) {// JSONObject
        // assume that we can have only one active task per process at the same
        // time

        long startTime = System.nanoTime();

        LOG.info("sLogin={}, snID_Process_Activiti={}", sLogin, snID_Process_Activiti);
        List<Task> aTaskActive = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active()
                .list();
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

        ProcessInstance oProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti).active().singleResult();
        Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
        LOG.info("mProcessVariable={}", mProcessVariable);
        String snID_Task = oTaskActive.getId();
        List<FormProperty> aProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();
        for (FormProperty oProperty : aProperty) {
            mProcessVariable.put(oProperty.getId(), oProperty.getValue());
        }
        LOG.info("mProcessVariable(added)={}", mProcessVariable);

        List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        DocumentStep oDocumentStep_Common = aDocumentStep.stream().filter(o -> o.getsKey_Step().equals("_")).findAny()
                .orElse(null);
        LOG.info("oDocumentStep_Common={}", oDocumentStep_Common);

        String sKey_Step_Document = (String) mProcessVariable.get("sKey_Step_Document");
        DocumentStep oDocumentStep_Active = aDocumentStep.stream().filter(
                o -> sKey_Step_Document == null ? o.getnOrder().equals(1) : o.getsKey_Step().equals(sKey_Step_Document))
                .findAny().orElse(null);
        LOG.info("oDocumentStep_Active={}", oDocumentStep_Active);
        if (oDocumentStep_Active == null) {
            throw new IllegalStateException(
                    "There is no active Document Step, process variable sKey_Step_Document=" + sKey_Step_Document);
        }

        List<Group> aGroup = identityService.createGroupQuery().groupMember(sLogin).list();
        Set<String> asID_Group = new HashSet<>();
        if (aGroup != null) {
            aGroup.stream().forEach(group -> asID_Group.add(group.getId()));
        }
        //LOG.info("sLogin={}, asID_Group={}", sLogin, asID_Group);

        long stopTime = System.nanoTime();

        LOG.info(
                "getDocumentStepRights 1st block time execution is: " + String.format("%,12d", (stopTime - startTime)));

        startTime = System.nanoTime();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common = new LinkedList();
        if (oDocumentStep_Common != null) {
            aDocumentStepSubjectRight_Common = oDocumentStep_Common.aDocumentStepSubjectRight().stream()
                    .filter(oRight -> asID_Group.contains(oRight.getsKey_GroupPostfix())).collect(Collectors.toList());
        }

        //LOG.info("aDocumentStepSubjectRight_Common={}", aDocumentStepSubjectRight_Common);
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Active = oDocumentStep_Active.aDocumentStepSubjectRight().stream()
                .filter(o -> asID_Group.contains(o.getsKey_GroupPostfix())).collect(Collectors.toList());
        //LOG.info("aDocumentStepSubjectRight_Active={}", aDocumentStepSubjectRight_Active);

        List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition = new ArrayList<>();

        for (DocumentStepSubjectRight oDocumentStepSubjectRight_Active : aDocumentStepSubjectRight_Active) {
            if (oDocumentStepSubjectRight_Active.getsKey_GroupPostfix().equals(sLogin)) {

                List<DocumentSubjectRightPermition> aDocumentSubjectRightPermition_finded
                        = oDocumentSubjectRightPermitionDao.findAllBy("nID_DocumentStepSubjectRight", oDocumentStepSubjectRight_Active.getId());

                LOG.info("aDocumentSubjectRightPermition_finded size is {}", aDocumentSubjectRightPermition_finded);
                aDocumentSubjectRightPermition.addAll(aDocumentSubjectRightPermition_finded);
                LOG.info("try to find DocumentSubjectRightPermition by id: {}", oDocumentStepSubjectRight_Active.getId());
                break;
            }
        }

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = aDocumentStepSubjectRight_Common;
        aDocumentStepSubjectRight.addAll(aDocumentStepSubjectRight_Active);
        //LOG.info("aDocumentStepSubjectRight={}", aDocumentStepSubjectRight);

        Map<String, Object> mReturn = new HashMap();

        Boolean bWrite = null;
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (oDocumentStepSubjectRight.getbWrite() != null) {
                if (bWrite == null) {
                    bWrite = false;
                }

                bWrite = bWrite || oDocumentStepSubjectRight.getbWrite();
            }
        }
        mReturn.put("bWrite", bWrite);
        LOG.info("bWrite={}", bWrite);

        List<String> asID_Field_Read = new LinkedList();
        List<String> asID_Field_Write = new LinkedList();

        List<FormProperty> aFormProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();

        stopTime = System.nanoTime();

        LOG.info(
                "getDocumentStepRights 2nd block time execution is: " + String.format("%,12d", (stopTime - startTime)));
        startTime = System.nanoTime();
        LOG.info("total FormProperty size is: " + aFormProperty.size());
        LOG.info("total aDocumentStepSubjectRight size is: " + aDocumentStepSubjectRight.size());

        Map<String, boolean[]> resultMap = new HashMap<>();
        for (FormProperty oProperty : aFormProperty) {
            groupSearch:
            {
                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                    LOG.info("oDocumentStepSubjectRight.getsKey_GroupPostfix()={}",
                            oDocumentStepSubjectRight.getsKey_GroupPostfix());

                    long loopStartTime = System.nanoTime();

                    for (DocumentStepSubjectRightField oDocumentStepSubjectRightField : oDocumentStepSubjectRight
                            .getDocumentStepSubjectRightFields()) {
                        String sMask = oDocumentStepSubjectRightField.getsMask_FieldID();
                        LOG.info("sMask={}", sMask);
                        LOG.info("total DocumentStepSubjectRightFields size is: "
                                + oDocumentStepSubjectRight.getDocumentStepSubjectRightFields().size());
                        if (sMask != null) {
                            Boolean bNot = false;
                            if (sMask.startsWith("!")) {
                                bNot = true;
                                sMask = sMask.substring(1);
                            }
                            Boolean bEqual = false;
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
                                if (!bStartWith && !bEndsWith && sMask.length() > 0) {
                                    bEqual = true;
                                }
                            }
                            LOG.info("bEndsWith={},bStartWith={},bAll={},bNot={}", bEndsWith, bStartWith, bAll, bNot);
                            long scLoopStartTime = System.nanoTime();

                            // for (FormProperty oProperty : aFormProperty) {
                            String sID = oProperty.getId();
                            Boolean bFound = false;
                            if (bStartWith && bEndsWith) {
                                bFound = sID.contains(sMask);
                            } else if (bStartWith) {
                                bFound = sID.startsWith(sMask);
                            } else if (bEndsWith) {
                                bFound = sID.endsWith(sMask);
                            } else if (bEqual) {
                                bFound = sID.equalsIgnoreCase(sMask);
                            }

                            LOG.info("sID={},bFound={},bAll={}", sID, bFound, bAll);
                            if (bAll || bFound) {
                                Boolean bWriteField = oDocumentStepSubjectRightField.getbWrite();
                                if (bNot) {
                                    resultMap.remove(sID);
                                } else if (bWriteField) {
                                    // asID_Field_Write_Temp.add(sID);
                                    if (resultMap.containsKey(sID)) {
                                        resultMap.replace(sID, new boolean[]{true, false});
                                    } else {
                                        resultMap.put(sID, new boolean[]{true, false});
                                    }

                                    break groupSearch;

                                } else {
                                    resultMap.put(sID, new boolean[]{false, true});
                                }
                                LOG.info("bWriteField={}", bWriteField);
                            }
                            // }

                            long scLoopStopTime = System.nanoTime();
                            LOG.info("2st loop time execution in getDocumentStepRights 3th block is: "
                                    + String.format("%,12d", (scLoopStopTime - scLoopStartTime)));
                        }
                    }

                    long loopStopTime = System.nanoTime();
                    LOG.info("1st loop time execution in getDocumentStepRights 3th block is: "
                            + String.format("%,12d", (loopStopTime - loopStartTime)));
                }
            }
        }

        stopTime = System.nanoTime();

        LOG.info(
                "getDocumentStepRights 3th block time execution is: " + String.format("%,12d", (stopTime - startTime)));
        startTime = System.nanoTime();
        LOG.info("asID_Field_Write(before)={}", asID_Field_Write);
        LOG.info("asID_Field_Read(before)={}", asID_Field_Read);
        LOG.info("asID_Field_Write(before) size={}", asID_Field_Write.size());
        LOG.info("asID_Field_Read(before) size={}", asID_Field_Read.size());

        TreeSet<String> asUnique_ID_Field_Write = new TreeSet<>(asID_Field_Write);
        TreeSet<String> asUnique_ID_Field_Read = new TreeSet<>(asID_Field_Read);

        LOG.info("asUnique_ID_Field_Write ={}", asUnique_ID_Field_Write);
        LOG.info("asUnique_ID_Field_Write ={}", asUnique_ID_Field_Read);
        LOG.info("asID_Field_Write size={}", asUnique_ID_Field_Write.size());
        LOG.info("asID_Field_Read size={}", asUnique_ID_Field_Read.size());

        List<String> asNewID_Field_Read = new LinkedList();
        List<String> asNewID_Field_Write = new LinkedList();

        for (String key : resultMap.keySet()) {
            boolean[] resultArray = resultMap.get(key);
            if (resultArray[0]) {
                asNewID_Field_Write.add(key);
            } else {
                asNewID_Field_Read.add(key);
            }
        }

        mReturn.put("asID_Field_Write", asNewID_Field_Write);
        mReturn.put("asID_Field_Read", asNewID_Field_Read);

        LOG.info("asNewID_Field_Write = {}", asID_Field_Write);
        LOG.info("asNewID_Field_Read ={}", asID_Field_Read);

        LOG.info("asID_Field_Write(after)={}", asID_Field_Write);
        LOG.info("asID_Field_Read(after)={}", asID_Field_Read);
        // LOG.info("mReturn={}", mReturn);
        stopTime = System.nanoTime();
        LOG.info(
                "getDocumentStepRights 4th block time execution is: " + String.format("%,12d", (stopTime - startTime)));
        LOG.info("aDocumentSubjectRightPermition {}", aDocumentSubjectRightPermition);
        mReturn.put("aDocumentSubjectRightPermition", aDocumentSubjectRightPermition);

        return mReturn;
    }

    public void syncDocumentGroups(DelegateTask delegateTask, List<DocumentStep> aDocumentStep) {

        Set<String> asGroup = new HashSet<>();
        for (DocumentStep oDocumentStep : aDocumentStep) {
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.aDocumentStepSubjectRight();
            if (aDocumentStepSubjectRight != null) {

                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                    asGroup.add(oDocumentStepSubjectRight.getsKey_GroupPostfix());
                    delegateTask.deleteCandidateGroup(oDocumentStepSubjectRight.getsKey_GroupPostfix());
                }
            }
        }
        LOG.info("asGroup in DocumentInit_iDoc {}", asGroup);

        List<String> asGroup_Old = new ArrayList<>();
        Set<IdentityLink> groupsOld = delegateTask.getCandidates();
        groupsOld.stream().forEach((groupOld) -> {
            asGroup_Old.add(groupOld.getGroupId());
        });
        LOG.info("asGroup_Old before setting: {} delegateTask: {}", asGroup_Old, delegateTask.getId());

        delegateTask.addCandidateGroups(asGroup);

        List<String> asGroup_New = new ArrayList<>();
        Set<IdentityLink> groupsNew = delegateTask.getCandidates();
        groupsNew.stream().forEach((groupNew) -> {
            asGroup_New.add(groupNew.getGroupId());
        });
        LOG.info("asGroup_New after setting: {} delegateTask: {}", asGroup_New, delegateTask.getId());
    }

    // public void checkDocumentInit(DelegateExecution execution) throws
    // IOException, URISyntaxException {//JSONObject
    public List<DocumentStep> checkDocumentInit(DelegateExecution execution, String sKey_GroupPostfix,
            String sKey_GroupPostfix_New) throws IOException, URISyntaxException, Exception {
        // assume that we can have only one active task per process at the same
        // time
        String snID_Process_Activiti = execution.getId();
        List<DocumentStep> aResDocumentStep = new ArrayList<>();
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
        String sKey_Step_Document = mProcessVariable.containsKey("sKey_Step_Document")
                ? (String) mProcessVariable.get("sKey_Step_Document") : null;
        if ("".equals(sKey_Step_Document)) {
            sKey_Step_Document = null;
        }
        LOG.info("BEFORE:sKey_Step_Document={}", sKey_Step_Document);

        if (sKey_Step_Document == null) {

            String sPath = "document/" + sID_BP + ".json";
            LOG.info("sPath={}", sPath);
            byte[] aByteDocument = getFileData_Pattern(sPath);
            if (aByteDocument != null && aByteDocument.length > 0) {
                String soJSON = soJSON = Tool.sData(aByteDocument);
                List<DocumentStep> aDocumentStep = setDocumentSteps(snID_Process_Activiti, soJSON);
                if (aDocumentStep != null && aDocumentStep.size() > 1) {
                    sKey_Step_Document = aDocumentStep.get(1).getsKey_Step();
                    LOG.info("AFTER:snID_Process_Activiti={} sKey_Step_Document={}", snID_Process_Activiti, sKey_Step_Document);
                    runtimeService.setVariable(snID_Process_Activiti, "sKey_Step_Document", sKey_Step_Document);
                }
            }

            if (sKey_GroupPostfix != null && !sKey_GroupPostfix.trim().equals("") && sKey_GroupPostfix_New != null
                    && !sKey_GroupPostfix_New.trim().equals("")) {
                LOG.info("start user id is {}", sKey_GroupPostfix_New);
                LOG.info("sKey_GroupPostfix is {}", sKey_GroupPostfix);

                List<DocumentStepSubjectRight> aDocumentStepSubjectRight = cloneDocumentStepSubject(
                        snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New, sKey_Step_Document);
            }

        }

        List<DocumentStep> aResultDocumentStep = new ArrayList<>();

        try {
            aResultDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti",
                    snID_Process_Activiti);
        } catch (Exception ex) {

        }

        LOG.info("aResultDocumentStep in initDocChecker is {}", aResultDocumentStep.size());

        for (DocumentStep oDocumentStep : aResultDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step_Document)) {
                LOG.info("founded DocumentStep in initDocChecker is {}", oDocumentStep);
                aResDocumentStep.add(oDocumentStep);
            }
        }

        LOG.info("aResDocumentStep in initDocChecker is {}", aResDocumentStep);

        return aResDocumentStep;
    }

    // 3.4) setDocumentStep(snID_Process_Activiti, bNext) //проставить номер шаг
    // (bNext=true > +1 иначе -1) в поле таски с id=sKey_Step_Document
    public String setDocumentStep(String snID_Process_Activiti, String sKey_Step) throws Exception {// JSONObject
        // assume that we can have only one active task per process at the same
        // time
        LOG.info("sKey_Step={}, snID_Process_Activiti={}", sKey_Step, snID_Process_Activiti);
        HistoricProcessInstance oProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(snID_Process_Activiti.trim()).includeProcessVariables().singleResult();
        if (oProcessInstance != null) {
            Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
            String sKey_Step_Document = mProcessVariable.containsKey("sKey_Step_Document")
                    ? (String) mProcessVariable.get("sKey_Step_Document") : null;
            if ("".equals(sKey_Step_Document)) {
                sKey_Step_Document = null;
            }

            if (sKey_Step_Document == null) {
                sKey_Step_Document = (String) runtimeService.getVariable(snID_Process_Activiti, "sKey_Step_Document");
            }

            LOG.debug("BEFORE:sKey_Step_Document={}", sKey_Step_Document);

            List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti",
                    snID_Process_Activiti);
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
        } else {
            throw new Exception("oProcessInstance is null snID_Process_Activiti = " + snID_Process_Activiti);
        }

        return "";
    }

    public Map<String, Object> isDocumentStepSubmitedAll(String snID_Process, String sLogin, String sKey_Step)
            throws Exception {
        LOG.info("isDocumentStepSubmitedAll: snID_Process {}, sKey_Step {} ...", snID_Process, sKey_Step);
        Map<String, Object> mReturn = new HashMap();
        long countSubmited = 0;
        long countNotSubmited = 0;
        List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti", snID_Process);//
        LOG.info("The size of list aDocumentStep is {}", (aDocumentStep != null ? aDocumentStep.size() : null));
        DocumentStep oFindedDocumentStep = null;

        for (DocumentStep oDocumentStep : aDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step)) {
                LOG.info("snID_Process {} getsKey_Step from oDocumentStep is = {} ", snID_Process,
                        oDocumentStep.getsKey_Step());
                oFindedDocumentStep = oDocumentStep;
                break;
            }
        }

        if (oFindedDocumentStep == null) {
            throw new Exception("DocumentStep not found");
        } else {
            boolean bSubmitedAll = true;
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oFindedDocumentStep.aDocumentStepSubjectRight()) {
                if (oDocumentStepSubjectRight != null) {
                    Boolean bWrite = oDocumentStepSubjectRight.getbWrite();
                    if (bWrite != null) {
                        LOG.info("oDocumentStepSubjectRight: " + oDocumentStepSubjectRight.getsKey_GroupPostfix()
                                + " sDate: " + oDocumentStepSubjectRight.getsDate());
                        DateTime sDate = oDocumentStepSubjectRight.getsDate();
                        LOG.info("sDate ={}", oDocumentStepSubjectRight.getsDate());
                        if (sDate == null) {
                            bSubmitedAll = false;
                            LOG.info("oDocumentStepSubjectRight: " + oDocumentStepSubjectRight.getsKey_GroupPostfix()
                                    + " sDate: " + oDocumentStepSubjectRight.getsDate() + "bSubmitedAll: " + bSubmitedAll);
                            //break;
                            countNotSubmited++;
                        } else {
                            countSubmited++;
                        }
                    }
                } else {
                    LOG.error("oDocumentStepSubjectRight is null");
                }

            }

            mReturn.put("bSubmitedAll", bSubmitedAll);
            mReturn.put("nCountSubmited", countSubmited);
            mReturn.put("nCountNotSubmited", countNotSubmited);
            mReturn.put("nCountSubmitePlan", (countSubmited + countNotSubmited));

            LOG.info("mReturn in isDocumentStepSubmitedAll {}", mReturn);

            return mReturn;
        }
    }

    public List<DocumentSubmitedUnsignedVO> getDocumentSubmitedUnsigned(String sLogin)
            throws JsonProcessingException, RecordNotFoundException, ParseException {

        List<DocumentSubmitedUnsignedVO> aResDocumentSubmitedUnsigned = new ArrayList<>();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStepSubjectRightDao.findAllBy("sLogin",
                sLogin);
        //LOG.info("aDocumentStepSubjectRight in method getDocumentSubmitedUnsigned = {}", aDocumentStepSubjectRight);
        DocumentStepSubjectRight oFindedDocumentStepSubjectRight;

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {

            if (oDocumentStepSubjectRight != null) {

                DateTime sDateECP = oDocumentStepSubjectRight.getsDateECP();
                LOG.info("sDateECP = ", oDocumentStepSubjectRight.getsDateECP());
                DateTime sDate = oDocumentStepSubjectRight.getsDate();
                LOG.info("sDate = ", oDocumentStepSubjectRight.getsDate());

                long sDS = oDocumentStepSubjectRight.getsDate().getMillis();
                String setsDateSubmit = convertMilliSecondsToFormattedDate(sDS);

                Boolean bNeedECP = oDocumentStepSubjectRight.getbNeedECP();
                // проверяем, если даты ецп нет, но есть дата подписания - нашли
                // нужный объект, который кладем в VO-обьект-обертку
                if (bNeedECP != null && bNeedECP != false && sDateECP == null) {
                    if (sDate != null) {
                        oFindedDocumentStepSubjectRight = oDocumentStepSubjectRight;
                        //LOG.info("oFindedDocumentStepSubjectRight= {}", oFindedDocumentStepSubjectRight);
                        // Достаем nID_Process_Activiti у найденного
                        // oDocumentStepSubjectRight через DocumentStep
                        String snID_Process_Activiti = oFindedDocumentStepSubjectRight.getDocumentStep()
                                .getSnID_Process_Activiti();
                        LOG.info("snID_Process of oFindedDocumentStepSubjectRight: {}", snID_Process_Activiti);
                        // Получаем sID_Order через generalConfig
                        long nID_Process = Long.valueOf(snID_Process_Activiti);
                        int nID_Server = generalConfig.getSelfServerId();
                        String sID_Order = generalConfig.getOrderId_ByProcess(nID_Server, nID_Process);

                        HistoricProcessInstance oHistoricProcessInstance = historyService
                                .createHistoricProcessInstanceQuery().processInstanceId(snID_Process_Activiti)
                                .singleResult();
                        LOG.info("oHistoricProcessInstance.id={}, snID_Process_Activiti={}", oHistoricProcessInstance.getId(), snID_Process_Activiti);

                        if (oHistoricProcessInstance != null) {
                            ProcessDefinition oProcessDefinition = repositoryService.createProcessDefinitionQuery()
                                    .processDefinitionId(oHistoricProcessInstance.getProcessDefinitionId())
                                    .singleResult();

                            if (oProcessDefinition != null) {
                                String sProcessName = oProcessDefinition.getName();

                                // вытаскиваем дату создания процесса
                                // Date sDateCreateProcess =
                                // oProcessInstance.getStartTime();
                                long sDCP = oHistoricProcessInstance.getStartTime().getTime();
                                String sDateCreateProcess = convertMilliSecondsToFormattedDate(sDCP);
                                LOG.info("sDateCreateProcess ", sDateCreateProcess);
                                // вытаскиваем название бп

                                // String sNameBP = oProcessInstance.getName();
                                // LOG.info("sNameBP {}", sNameBP);
                                // вытаскиваем список активных тасок по процесу
                                List<Task> aTask = oTaskService.createTaskQuery()
                                        .processInstanceId(oHistoricProcessInstance.getId()).active().list();
                                if (aTask.size() < 1 || aTask.get(0) == null) {
                                    continue;
                                }
                                // берем первую
                                Task oTaskCurr = aTask.get(0);
                                LOG.info("oTaskCurr ={} ", oTaskCurr);
                                // вытаскиваем дату создания таски
                                long sDCUT = oTaskCurr.getCreateTime().getTime();
                                String sDateCreateUserTask = convertMilliSecondsToFormattedDate(sDCUT);
                                // Date sDateCreateUserTask =
                                // oTaskCurr.getCreateTime();
                                LOG.info("sDateCreateUserTask = ", oTaskCurr.getCreateTime());
                                // и ее название
                                String sUserTaskName = oTaskCurr.getName();
                                // Создаем обьект=обертку, в который сетим
                                // нужные
                                // полученные поля
                                DocumentSubmitedUnsignedVO oDocumentSubmitedUnsignedVO = new DocumentSubmitedUnsignedVO();

                                oDocumentSubmitedUnsignedVO
                                        .setoDocumentStepSubjectRight(oFindedDocumentStepSubjectRight);
                                oDocumentSubmitedUnsignedVO.setsNameBP(sProcessName);
                                oDocumentSubmitedUnsignedVO.setsUserTaskName(sUserTaskName);
                                oDocumentSubmitedUnsignedVO.setsDateCreateProcess(sDateCreateProcess);
                                oDocumentSubmitedUnsignedVO.setsDateCreateUserTask(sDateCreateUserTask);
                                oDocumentSubmitedUnsignedVO.setsDateSubmit(setsDateSubmit);
                                oDocumentSubmitedUnsignedVO.setsID_Order(sID_Order);

                                aResDocumentSubmitedUnsigned.add(oDocumentSubmitedUnsignedVO);
                                //LOG.info("aResDocumentSubmitedUnsigned = {}", aResDocumentSubmitedUnsigned);
                            } else {
                                LOG.error(String.format("oProcessDefinition [id = '%s']  is null",
                                        oHistoricProcessInstance.getProcessDefinitionId()));

                            }
                        } else {
                            LOG.error(String.format("oHistoricProcessInstance [id = '%s']  is null",
                                    snID_Process_Activiti));
                        }

                    }

                } else {
                    LOG.info("oFindedDocumentStepSubjectRight not found");
                }
            }

        }

        return aResDocumentSubmitedUnsigned;
    }

    public void removeDocumentSteps(String snID_Process_Activiti) {
        List<DocumentStep> aDocumentStep = oDocumentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep finded...");
        if (aDocumentStep != null) {
            oDocumentStepDao.delete(aDocumentStep);
        }
        LOG.info("aDocumentStep deleted...");
    }

    public static String convertMilliSecondsToFormattedDate(Long milliSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

}
