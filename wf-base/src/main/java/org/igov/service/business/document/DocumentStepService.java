package org.igov.service.business.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.util.Hash;
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
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.action.vo.DocumentSubmitedUnsignedVO;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepDao;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightField;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.RecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.io.IOUtils;
import static org.igov.io.fs.FileSystemData.getFileData_Pattern;
import org.igov.model.document.DocumentStepSubjectRightDao;
import org.igov.model.document.DocumentStepSubjectRightFieldDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupResultTree;
import org.igov.service.business.subject.SubjectGroupTreeService;
import org.igov.util.Tool;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Component("documentStepService")
@Service
public class DocumentStepService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentStepService.class);

    @Autowired
    @Qualifier("documentStepDao")
    private GenericEntityDao<Long, DocumentStep> documentStepDao;

    /*@Autowired
    private DocumentStepDao oDocumentStepDao;*/
    @Autowired
    private DocumentStepSubjectRightDao oDocumentStepSubjectRightDao;

    @Autowired
    private TaskService oTaskService;

    @Autowired
    private DocumentStepSubjectRightFieldDao oDocumentStepSubjectRightFieldDao;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FormService oFormService;

    @Autowired
    private IdentityService oIdentityService;

    @Autowired
    private SubjectGroupTreeService oSubjectGroupTreeService;

    //public void setDocumentSteps(String snID_Process_Activiti, String soJSON) {
    public List<DocumentStep> setDocumentSteps(String snID_Process_Activiti, String soJSON) {
        JSONObject oJSON = new JSONObject(soJSON);
        List<DocumentStep> aDocumentStep_Result = new ArrayList<>();
        //process common step if it exists
        Object oStep_Common = oJSON.opt("_");
        LOG.info("Common step is - {}", oStep_Common);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet_Common = new ArrayList<>();

        if (oStep_Common != null) {
            DocumentStep oDocumentStep_Common = mapToDocumentStep(oStep_Common);
            oDocumentStep_Common.setnOrder(0L);//common step with name "_" has order 0
            oDocumentStep_Common.setsKey_Step("_");
            oDocumentStep_Common.setSnID_Process_Activiti(snID_Process_Activiti);

            List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet = oDocumentStep_Common.getRights();
            if (aDocumentStepSubjectRightToSet != null) {
                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRightToSet) {
                    if (!oDocumentStepSubjectRight.getsKey_GroupPostfix().startsWith("_default_")) {
                        aDocumentStepSubjectRightToSet_Common.add(oDocumentStepSubjectRight);
                    }
                }
            }
            documentStepDao.saveOrUpdate(oDocumentStep_Common);
            aDocumentStep_Result.add(oDocumentStep_Common);
        }
        //process all other steps
        //first of all we filter common step with name "_" and then just convert each step from JSON to POJO
        List<String> asKey_Step = Arrays.asList(JSONObject.getNames(oJSON));
        Set asKey_Step_Sort = new TreeSet(asKey_Step);
        asKey_Step = new ArrayList(asKey_Step_Sort);
        LOG.info("List of steps: {}", asKey_Step);
        /*asKey_Step = asKey_Step.stream().
                filter(sKey_Step -> !"_".equals(sKey_Step))
                .collect(Collectors.toList());*/

        LOG.info("List of steps: {}", asKey_Step);
        List<String> asKey_Step_ExcludeCommon = asKey_Step.stream().
                filter(sKey_Step -> !"_".equals(sKey_Step))
                .collect(Collectors.toList());
        long i = 1L;
        for (String sKey_Step : asKey_Step_ExcludeCommon) {

            LOG.info("sKeyStep in setDocumentSteps is: {}", sKey_Step);
            DocumentStep oDocumentStep = mapToDocumentStep(oJSON.get(sKey_Step));
            oDocumentStep.setnOrder(i++);
            oDocumentStep.setsKey_Step(sKey_Step);
            oDocumentStep.setSnID_Process_Activiti(snID_Process_Activiti);
            LOG.info("before add: snID_Process_Activiti is: {} sKey_Step is: {} rights size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(), oDocumentStep.getRights().size());
            List<DocumentStepSubjectRight> aoDocumentStepSubjectRights_CloneFromCommon
                    = getCommon_DocumentStepSubjectRights(aDocumentStepSubjectRightToSet_Common, oDocumentStep);
            LOG.info("add common subjectRignts: snID_Process_Activiti is: {} sKey_Step is: {} aoDocumentStepSubjectRights_CloneFromCommon size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(), aoDocumentStepSubjectRights_CloneFromCommon.size());
            //List<DocumentStepSubjectRight> aoDocumentStepSubjectRight = oDocumentStep.getRights();
            if (oDocumentStep.getRights() == null) {
                oDocumentStep.setRights(new ArrayList<>());
            }
            oDocumentStep.getRights().addAll(aoDocumentStepSubjectRights_CloneFromCommon);
            /*for (DocumentStepSubjectRight oDocumentStepSubjectRight_CloneFromCommon : aoDocumentStepSubjectRights_CloneFromCommon) {
                findUniqu_DocumentStepSubjectRight:
                {
                    for (DocumentStepSubjectRight oDocumentStepSubjectRight : aoDocumentStepSubjectRight) {
                        if (oDocumentStepSubjectRight.getsKey_GroupPostfix()
                                .equalsIgnoreCase(oDocumentStepSubjectRight_CloneFromCommon.getsKey_GroupPostfix())) {
                            LOG.info("double DocumentStepSubjectRight: snID_Process_Activiti is: {} sKey_Step is: {} sKey_GroupPostfix: {}",
                                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(), oDocumentStepSubjectRight.getsKey_GroupPostfix());
                            break findUniqu_DocumentStepSubjectRight;
                        }
                    }
                    aoDocumentStepSubjectRight.add(oDocumentStepSubjectRight_CloneFromCommon);
                }
            }*/
            //oDocumentStep.setRights(aoDocumentStepSubjectRight);

            LOG.info("after add: snID_Process_Activiti is: {} sKey_Step is: {} rights size is: {}",
                    oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(), oDocumentStep.getRights().size());

            LOG.info("oDocumentStep is before saving {}", oDocumentStep);
            LOG.info("oDocumentStep right is before saving {}", oDocumentStep.getRights());

            /*for(DocumentStepSubjectRight oCheckDocumentStepSubjectRight : oDocumentStep.getRights()){
                LOG.info("field in {} is {}" , oCheckDocumentStepSubjectRight.getsKey_GroupPostfix(), 
                        oCheckDocumentStepSubjectRight.getDocumentStepSubjectRightFields());
            }*/
            oDocumentStep = documentStepDao.saveOrUpdate(oDocumentStep);
            aDocumentStep_Result.add(oDocumentStep);
        }

        LOG.info("Result list of steps: {}", aDocumentStep_Result);
        return aDocumentStep_Result;
    }

    private boolean isNewStepRights(String snID_Process_Activiti, String sKey_Step_Document_To, String sKey_GroupPostfix_New) {
        List<DocumentStep> aCheckDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);

        boolean saveflag = true;

        for (DocumentStep oCheckDocumentStep : aCheckDocumentStep) {
            if (!oCheckDocumentStep.getsKey_Step().equals(sKey_Step_Document_To)) {
                continue;
            }

            List<DocumentStepSubjectRight> aCheckDocumentStepSubjectRight = oCheckDocumentStep.getRights();
            LOG.info("oCheckDocumentStep is {}", oCheckDocumentStep);
            LOG.info("aCheckDocumentStepRights is {}", aCheckDocumentStepSubjectRight);

            for (DocumentStepSubjectRight oCheckDocumentStepSubjectRight : aCheckDocumentStepSubjectRight) {
                LOG.info("right.getsKey_GroupPostfix() is {}", oCheckDocumentStepSubjectRight.getsKey_GroupPostfix());
                LOG.info("sKey_GroupPostfix_New is {}", sKey_GroupPostfix_New);
                if (oCheckDocumentStepSubjectRight.getsKey_GroupPostfix().equals(sKey_GroupPostfix_New)) {
                    saveflag = false;
                    break;
                }
            }

            if (!saveflag) {
                break;
            }
        }
        return saveflag;
    }

    private boolean isNew_DocumentStepSubjectRights(DocumentStep oDocumentStep,
            DocumentStepSubjectRight oDocumentStepSubjectRight_Common) {
        List<DocumentStepSubjectRight> aoDocumentStepSubjectRight = oDocumentStep.getRights();
        if (aoDocumentStepSubjectRight == null) {
            aoDocumentStepSubjectRight = new ArrayList<>();
        }
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aoDocumentStepSubjectRight) {
            if (oDocumentStepSubjectRight.getsKey_GroupPostfix()
                    .equalsIgnoreCase(oDocumentStepSubjectRight_Common.getsKey_GroupPostfix())) {
                LOG.info("double DocumentStepSubjectRight: snID_Process_Activiti is: {} sKey_Step is: {} sKey_GroupPostfix: {}",
                        oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(), oDocumentStepSubjectRight.getsKey_GroupPostfix());
                return false;
            }
        }
        return true;
    }

    private List<DocumentStepSubjectRight> getCommon_DocumentStepSubjectRights(
            List<DocumentStepSubjectRight> aDocumentStepSubjectRightToSet_Common,
            DocumentStep oDocumentStep) {

        List<DocumentStepSubjectRight> aoDocumentStepSubjectRight_New = new ArrayList<>();
        if (!aDocumentStepSubjectRightToSet_Common.isEmpty()) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRightToSet_Common : aDocumentStepSubjectRightToSet_Common) {
                if (!isNew_DocumentStepSubjectRights(oDocumentStep, oDocumentStepSubjectRightToSet_Common)) {
                    continue;
                }
                DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
                oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep);
                oDocumentStepSubjectRight_New.setsKey_GroupPostfix(oDocumentStepSubjectRightToSet_Common.getsKey_GroupPostfix());
                oDocumentStepSubjectRight_New.setbWrite(oDocumentStepSubjectRightToSet_Common.getbWrite());
                Object sName = oDocumentStepSubjectRightToSet_Common.getsName(); //oGroup.opt("sName");
                if (sName != null) {
                    oDocumentStepSubjectRight_New.setsName((String) sName);
                }

                /*DocumentStepSubjectRight oDocumentStepSubjectRight_Saved = 
                            oDocumentStepSubjectRightDao.saveOrUpdate(oNewDocumentStepSubjectRight_New);*/
                //LOG.info("oDocumentStepSubjectRight in checkInit is {}", oDocumentStepSubjectRight_Saved.getId());
                List<DocumentStepSubjectRightField> aoDocumentStepSubjectRightField_New = new ArrayList<>();
                for (DocumentStepSubjectRightField oDocumentStepSubjectRightField_From
                        : oDocumentStepSubjectRightToSet_Common.getDocumentStepSubjectRightFields()) {
                    DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                    oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField_From.getbWrite());
                    oDocumentStepSubjectRightField_New.setsMask_FieldID(oDocumentStepSubjectRightField_From.getsMask_FieldID());
                    oDocumentStepSubjectRightField_New.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                    aoDocumentStepSubjectRightField_New.add(oDocumentStepSubjectRightField_New);
                    //oDocumentStepSubjectRightFieldDao.saveOrUpdate(oDocumentStepSubjectRightField_New);
                    //LOG.info("in adding field: {} ", aoDocumentStepSubjectRightField_New);
                }
                oDocumentStepSubjectRight_New.setDocumentStepSubjectRightFields(aoDocumentStepSubjectRightField_New);

                if (isNewStepRights(oDocumentStep.getSnID_Process_Activiti(), oDocumentStep.getsKey_Step(),
                        oDocumentStepSubjectRight_New.getsKey_GroupPostfix())) {
                    aoDocumentStepSubjectRight_New.add(oDocumentStepSubjectRight_New);
                    LOG.info("oDocumentStepSubjectRight: {} is added", oDocumentStepSubjectRight_New);
                }

                LOG.info("in adding: snID_Process_Activiti is: {} sKey_Step is: {} sKey_GroupPostfix is: {} right size is: {} ",
                        oDocumentStepSubjectRight_New.getDocumentStep().getSnID_Process_Activiti(),
                        oDocumentStepSubjectRight_New.getDocumentStep().getsKey_Step(),
                        oDocumentStepSubjectRight_New.getsKey_GroupPostfix(),
                        oDocumentStepSubjectRight_New.getDocumentStepSubjectRightFields().size());
            }
        }
        return aoDocumentStepSubjectRight_New;

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

                /*if (sKey_Group.startsWith("_defaul")) {
                    continue;
                }*/
                JSONObject oGroup = oStep.optJSONObject(sKey_Group);
                LOG.info("group for step: {}", oGroup);

                if (oGroup == null) {
                    continue;
                }
                Boolean bWrite = (Boolean) oGroup.opt("bWrite");
                if (bWrite == null) {
                    throw new IllegalArgumentException("Group " + sKey_Group + " hasn't property bWrite.");
                }

                DocumentStepSubjectRight oDocumentStepSubjectRight = new DocumentStepSubjectRight();
                oDocumentStepSubjectRight.setsKey_GroupPostfix(sKey_Group);
                oDocumentStepSubjectRight.setbWrite(bWrite);

                Object sName = oGroup.opt("sName");
                if (sName != null) {
                    oDocumentStepSubjectRight.setsName((String) sName);
                }

                List<DocumentStepSubjectRightField> aDocumentStepSubjectRightField = mapToFields(oGroup, oDocumentStepSubjectRight);
                oDocumentStepSubjectRight.setDocumentStepSubjectRightFields(aDocumentStepSubjectRightField);
                oDocumentStepSubjectRight.setDocumentStep(oDocumentStep);
                //oDocumentStepSubjectRight.setsLogin(sKey_Group);
                LOG.info("right for step: {}", oDocumentStepSubjectRight);
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            }
        }
        oDocumentStep.setRights(aDocumentStepSubjectRight);
        return oDocumentStep;
    }

//setDocumentStep(snID_Process_Activiti[, sKey_Step)]    
//3.1) setDocumentStepSubjectRight(snID_Process_Activiti, sKey_GroupPostfix, bWrite) //Установить право записи, равное bWrite, для ветки к путем sKey_Step/sKey_GroupPostfix
//3.2) cloneDocumentStepSubject(snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New) //Клонировать ветку права записи с путем sKey_Step/sKey_GroupPostfix в ветку с путем 
    public List<DocumentStepSubjectRight> cloneDocumentStepSubject(String snID_Process_Activiti, String sKey_GroupPostfix, String sKey_GroupPostfix_New,
            String sKey_Step_Document_To) {//JSONObject //Map<String, Object>
        LOG.info("cloneDocumentStepSubject started...");
        LOG.info("sKey_GroupPostfix={}, snID_Process_Activiti={}, sKey_GroupPostfix_New={}, sKey_Step_Document={}",
                sKey_GroupPostfix, snID_Process_Activiti, sKey_GroupPostfix_New, sKey_Step_Document_To);

        String sKey_Step_Document_From = sKey_Step_Document_To;
        
        if (sKey_GroupPostfix.startsWith("_default_")) {
            sKey_Step_Document_From = "_";
        }

        String sSubjectType = oSubjectGroupTreeService.getSubjectType(sKey_GroupPostfix_New);
        LOG.info("sSubjectType in cloneRights is {}", sSubjectType);

        SubjectGroupResultTree oSubjectGroupResultTree = null;

        if (sSubjectType.equals("Organ")) {
            try {
                oSubjectGroupResultTree = oSubjectGroupTreeService.getCatalogSubjectGroupsTree(sKey_GroupPostfix_New, 0L, null, false, 0L, "Human");
            } catch (Exception e) {
                LOG.error("subjectGroupResultTree FAIL during cloning: {}", e);
            }
        }

        List<String> asResultGroup = new ArrayList<>();

        if (oSubjectGroupResultTree != null) {
            List<SubjectGroup> aSubjectGroups = oSubjectGroupResultTree.getaSubjectGroupTree();
            for (SubjectGroup oSubjectGroup : aSubjectGroups) {
                asResultGroup.add(oSubjectGroup.getsID_Group_Activiti());
            }
        } else {
            asResultGroup.add(sKey_GroupPostfix_New);
        }

        LOG.info("asResultGroup is {}", asResultGroup);

        List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
        LOG.info("aDocumentStep={}", aDocumentStep);

        final String SKEY_STEP_DOCUMENT_FROM = sKey_Step_Document_From;
        DocumentStep oDocumentStep_From = aDocumentStep
                .stream()
                .filter(o -> SKEY_STEP_DOCUMENT_FROM == null ? o.getnOrder().equals(1)
                        : o.getsKey_Step().equals(SKEY_STEP_DOCUMENT_FROM))
                .findAny()
                .orElse(null);

        LOG.info("oDocumentStep_From={}", oDocumentStep_From);
        if (oDocumentStep_From == null) {
            throw new IllegalStateException("There is no active Document Step, process variable sKey_Step_Document="
                    + sKey_Step_Document_From);
        }

        final String SKEY_STEP_DOCUMENT_TO = sKey_Step_Document_To;
        DocumentStep oDocumentStep_To = aDocumentStep
                .stream()
                .filter(o -> SKEY_STEP_DOCUMENT_TO == null ? o.getnOrder().equals(1)
                        : o.getsKey_Step().equals(SKEY_STEP_DOCUMENT_TO))
                .findAny()
                .orElse(null);

        LOG.info("oDocumentStep_To={}", oDocumentStep_To);
        if (oDocumentStep_To == null) {
            throw new IllegalStateException("There is no active Document Step, process variable sKey_Step_Document="
                    + sKey_Step_Document_To);
        }

        List<DocumentStepSubjectRight> resultList = new ArrayList<>();

        for (String sResultGroup : asResultGroup) {

            DocumentStepSubjectRight oDocumentStepSubjectRight_New = new DocumentStepSubjectRight();
            LOG.info("oDocumentStep_From rights is {}", oDocumentStep_From.getRights());

            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_From = oDocumentStep_From.getRights();
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight_To = oDocumentStep_To.getRights();
            //List<DocumentStepSubjectRight> aDocumentStepSubjectRight_To = new ArrayList<>();
            //aDocumentStepSubjectRight_To.addAll(aDocumentStepSubjectRight_From);
            LOG.info("aDocumentStepSubjectRight_From is {}", aDocumentStepSubjectRight_From);
            LOG.info("aDocumentStepSubjectRight_To is {}", aDocumentStepSubjectRight_To);
            //for(DocumentStepSubjectRight oDocumentStepSubjectRight_Source : aDocumentStepSubjectRight_Source){
            //Iterator<DocumentStepSubjectRight> oDocumentStepSubjectRightIterator_From = aDocumentStepSubjectRight_From.iterator();
            //while (oDocumentStepSubjectRightIterator_From.hasNext()) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRight_From : aDocumentStepSubjectRight_From) {
                 if (sKey_GroupPostfix.equals(oDocumentStepSubjectRight_From.getsKey_GroupPostfix())) {
                    LOG.info("!!! sKey_GroupPostfix: {} oDocumentStepSubjectRight_From.getsKey_GroupPostfix(): {}", 
                            sKey_GroupPostfix, oDocumentStepSubjectRight_From.getsKey_GroupPostfix());
                    oDocumentStepSubjectRight_New.setsKey_GroupPostfix(sResultGroup);
                    oDocumentStepSubjectRight_New.setbWrite(oDocumentStepSubjectRight_From.getbWrite());
                    Object sName = oDocumentStepSubjectRight_From.getsName(); //oGroup.opt("sName");
                    if (sName != null) {
                        oDocumentStepSubjectRight_New.setsName((String) sName);
                    }
                    List<DocumentStepSubjectRightField> aDocumentStepSubjectRightField_New = new LinkedList();

                    for (DocumentStepSubjectRightField oDocumentStepSubjectRightField_From
                            : oDocumentStepSubjectRight_From.getDocumentStepSubjectRightFields()) {
                        DocumentStepSubjectRightField oDocumentStepSubjectRightField_New = new DocumentStepSubjectRightField();
                        oDocumentStepSubjectRightField_New.setbWrite(oDocumentStepSubjectRightField_From.getbWrite());
                        oDocumentStepSubjectRightField_New.setsMask_FieldID(oDocumentStepSubjectRightField_From.getsMask_FieldID());
                        oDocumentStepSubjectRightField_New.setDocumentStepSubjectRight(oDocumentStepSubjectRight_New);
                        //oDocumentStepSubjectRightFieldDao.saveOrUpdate(oDocumentStepSubjectRightField);
                        //oDocumentStepSubjectRightField_Source.getsMask_FieldID();
                        aDocumentStepSubjectRightField_New.add(oDocumentStepSubjectRightField_New);
                    }

                    oDocumentStepSubjectRight_New.setDocumentStepSubjectRightFields(aDocumentStepSubjectRightField_New);
                    oDocumentStepSubjectRight_New.setDocumentStep(oDocumentStep_To);
                    LOG.info("right for step: {}", oDocumentStepSubjectRight_New);
                    aDocumentStepSubjectRight_To.add(oDocumentStepSubjectRight_New);
                    oDocumentStep_To.setRights(aDocumentStepSubjectRight_To);

                    List<DocumentStep> aCheckDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);

                    saveNewDocumentStepSubjectRight_IfNotExist:
                    {
                        for (DocumentStep oCheckDocumentStep : aCheckDocumentStep) {
                            List<DocumentStepSubjectRight> aCheckDocumentStepSubjectRight = oCheckDocumentStep.getRights();
                            LOG.info("oCheckDocumentStep: {} aCheckDocumentStepRights is {}", oCheckDocumentStep.getsKey_Step(), aCheckDocumentStepSubjectRight);
                            if (oCheckDocumentStep.getsKey_Step().equalsIgnoreCase(oDocumentStep_To.getsKey_Step())) {
                                for (DocumentStepSubjectRight oCheckDocumentStepSubjectRight : aCheckDocumentStepSubjectRight) {
                                    LOG.info("sKey_GroupPostfix: {} sKey_GroupPostfix_New: {}", oCheckDocumentStepSubjectRight.getsKey_GroupPostfix(), sKey_GroupPostfix_New);
                                    if (oCheckDocumentStepSubjectRight.getsKey_GroupPostfix().equalsIgnoreCase(sKey_GroupPostfix_New)) { //
                                        LOG.info("saveNewDocumentStepSubjectRight_IfNotExist skip sKey_GroupPostfix_New: {} in step {}"
                                                + " becouse we have already have one in nID_CheckDocumentStep: {} sKey_GroupPostfix: {} nID_DocumentStepSubjectRight: {}",
                                                sKey_GroupPostfix_New, oDocumentStep_To.getsKey_Step(), oCheckDocumentStep.getId(), 
                                                oCheckDocumentStepSubjectRight.getsKey_GroupPostfix(), oCheckDocumentStepSubjectRight.getId());
                                        break saveNewDocumentStepSubjectRight_IfNotExist;
                                    }
                                }
                            }
                        }
                        documentStepDao.saveOrUpdate(oDocumentStep_To);
                    }

                    //                }catch(Exception ex){
                    //oTaskService.addCandidateGroup(snID_Task, oDocumentStepSubjectRight.getsKey_GroupPostfix());
                    //repositoryService.addCandidateStarterGroup(snID_Process_Activiti, oDocumentStepSubjectRight.getsKey_GroupPostfix());
                    //                }
                } else {
                    LOG.info("sKey_GroupPostfix is not equal Key_GroupPostfix");
                    LOG.info("sKey_GroupPostfix is: {}", sKey_GroupPostfix);
                    LOG.info("Key_GroupPostfix is: {} ", oDocumentStepSubjectRight_From.getsKey_GroupPostfix());
                }

            }
            resultList.add(oDocumentStepSubjectRight_New);
        }

        return resultList;
    }

    public List<DocumentStepSubjectRight> cloneDocumentStepFromTable(String snID_Process_Activiti, String sKey_GroupPostfix, String sID_Field,
            String sKey_Step_Document_To) {//JSONObject //Map<String, Object>
        LOG.info("cloneDocumentStepSubject started...");
        LOG.info("sKey_GroupPostfix={}, snID_Process_Activiti={}, sID_Field={}, sKey_Step_Document={}",
                sKey_GroupPostfix, snID_Process_Activiti, sID_Field, sKey_Step_Document_To);

        List<DocumentStepSubjectRight> resultList = new ArrayList<>();

        org.json.simple.JSONObject oJSONObject = (org.json.simple.JSONObject) runtimeService.getVariable(snID_Process_Activiti, sID_Field);

        LOG.info("oJSONObject in cloneDocumentStepFromTable is {}", oJSONObject.toJSONString());

        org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

        if (aJsonRow != null) {
            if (aJsonRow != null) {
                for (int i = 0; i < aJsonRow.size(); i++) {

                    //Map<String, Object> mParamTask = new HashMap<>();
                    org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
                    LOG.info("oJsonField in cloneDocumentStepFromTable is {}", oJsonField);
                    if (oJsonField != null) {
                        org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
                        LOG.info("aJsonField in cloneDocumentStepFromTable is {}", aJsonField);
                        if (aJsonField != null) {
                            for (int j = 0; j < aJsonField.size(); j++) {
                                org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField.get(j);
                                LOG.info("oJsonMap in cloneDocumentStepFromTable is {}", oJsonMap);
                                if (oJsonMap != null) {
                                    Object oId = oJsonMap.get("id");
                                    Object oValue = oJsonMap.get("value");
                                    if (oValue != null) {
                                        LOG.info("oValue in cloneDocumentStepFromTable is {}", oValue);
                                        resultList.addAll(cloneDocumentStepSubject(snID_Process_Activiti,
                                                sKey_GroupPostfix, (String) oValue, sKey_Step_Document_To));
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

        return resultList;
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

    public List<Map<String, Object>> getDocumentStepLogins(String snID_Process_Activiti) {//JSONObject //Map<String, Object>
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

        //Map<String, Object> mReturn = new HashMap();
        List<Map<String, Object>> amReturn = new LinkedList();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new LinkedList();
        if (oDocumentStep_Common != null) {
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Common.getRights()) {
                aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
                //List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common
            }
        }
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : oDocumentStep_Active.getRights()) {
            aDocumentStepSubjectRight.add(oDocumentStepSubjectRight);
            //List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common
        }

        //final String sGroupPrefix = new StringBuilder(sID_BP).append("_").toString();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            Map<String, Object> mParamDocumentStepSubjectRight = new HashMap();
            mParamDocumentStepSubjectRight.put("sDate", oDocumentStepSubjectRight.getsDate() == null ? "" : formatter.print(oDocumentStepSubjectRight.getsDate()));//"2016-05-15 12:12:34"
            mParamDocumentStepSubjectRight.put("bWrite", oDocumentStepSubjectRight.getbWrite());//false
            mParamDocumentStepSubjectRight.put("sName", oDocumentStepSubjectRight.getsName() == null ? "" : oDocumentStepSubjectRight.getsName());//"Главный контроллирующий"
            //String sID_Group = new StringBuilder(sGroupPrefix).append(oDocumentStepSubjectRight.getsKey_GroupPostfix()).toString();
            String sID_Group = oDocumentStepSubjectRight.getsKey_GroupPostfix();
            List<User> aUser = oIdentityService.createUserQuery().memberOfGroup(sID_Group).list();
            LOG.info("getDocumentStepLogins sID_Group={}, aUser={}", sID_Group, aUser);
            List<Map<String, Object>> amUserProperty = new LinkedList();
            for (User oUser : aUser) {
                LOG.info("oDocumentStepSubjectRight.getsLogin() is {}", oDocumentStepSubjectRight.getsLogin());
                LOG.info("oUser.getId() is {}", oUser.getId());
                if (oUser.getId().equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                    Map<String, Object> mUser = new HashMap();
                    mUser.put("sLogin", oUser.getId());
                    mUser.put("sFIO", oUser.getLastName() + " " + oUser.getFirstName());
                    amUserProperty.add(mUser);
                }
            }
            mParamDocumentStepSubjectRight.put("aUser", amUserProperty);
            LOG.info("amUserProperty={}", amUserProperty);
            String sLogin = oDocumentStepSubjectRight.getsLogin();
            LOG.info("sLogin={}", sLogin);
            if (sLogin != null) {
                User oUser = oIdentityService.createUserQuery().userId(sLogin).singleResult();
                if (oUser != null) {
                    mParamDocumentStepSubjectRight.put("sLogin_Referent", oUser.getId());
                    mParamDocumentStepSubjectRight.put("sFIO_Referent", oUser.getLastName() + " " + oUser.getFirstName());
                    //mReturn.put(sLogin, mParamDocumentStepSubjectRight);
                }
            }
            LOG.info("mParamDocumentStepSubjectRight={}", mParamDocumentStepSubjectRight);
            amReturn.add(mParamDocumentStepSubjectRight);
        }
        //LOG.info("mReturn={}", mReturn);
        LOG.info("amReturn={}", amReturn);

        return amReturn;//mReturn
    }

    /*public DocumentStep oDocumentStep_Active(String snID_Process_Activiti){
        return oDocumentStep_Active;
    }*/
    public Map<String, Object> getDocumentStepRights(String sLogin, String snID_Process_Activiti) {//JSONObject
        //assume that we can have only one active task per process at the same time

        long startTime = System.nanoTime();

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

        //final String sGroupPrefix = new StringBuilder(sID_BP).append("_").toString();
        long stopTime = System.nanoTime();

        LOG.info("getDocumentStepRights 1st block time execution is: " + String.format("%,12d", (stopTime - startTime)));

        startTime = System.nanoTime();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Common = new LinkedList();
        if (oDocumentStep_Common != null) {
            aDocumentStepSubjectRight_Common = oDocumentStep_Common
                    .getRights()
                    .stream()
                    //.filter(o -> asID_Group.contains(new StringBuilder(sGroupPrefix).append(o.getsKey_GroupPostfix()).toString()))
                    .filter(oRight -> asID_Group.contains(oRight.getsKey_GroupPostfix()))
                    .collect(Collectors.toList());
        }

        LOG.info("aDocumentStepSubjectRight_Common={}", aDocumentStepSubjectRight_Common);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Active = oDocumentStep_Active
                .getRights()
                .stream()
                //.filter(o -> asID_Group.contains(new StringBuilder(sGroupPrefix).append(o.getsKey_GroupPostfix()).toString()))
                .filter(o -> asID_Group.contains(o.getsKey_GroupPostfix()))
                .collect(Collectors.toList());
        LOG.info("aDocumentStepSubjectRight_Active={}", aDocumentStepSubjectRight_Active);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = aDocumentStepSubjectRight_Common;
        aDocumentStepSubjectRight.addAll(aDocumentStepSubjectRight_Active);
        LOG.info("aDocumentStepSubjectRight={}", aDocumentStepSubjectRight);

        Map<String, Object> mReturn = new HashMap();

        Boolean bWrite = null;
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            if (bWrite == null) {
                bWrite = false;
            }
            bWrite = bWrite || oDocumentStepSubjectRight.getbWrite();
        }
        mReturn.put("bWrite", bWrite);
        LOG.info("bWrite={}", bWrite);

        List<String> asID_Field_Read = new LinkedList();
        List<String> asID_Field_Write = new LinkedList();

        List<FormProperty> aFormProperty = oFormService.getTaskFormData(snID_Task).getFormProperties();

        stopTime = System.nanoTime();

        LOG.info("getDocumentStepRights 2nd block time execution is: " + String.format("%,12d", (stopTime - startTime)));
        startTime = System.nanoTime();
        LOG.info("total FormProperty size is: " + aFormProperty.size());
        LOG.info("total aDocumentStepSubjectRight size is: " + aDocumentStepSubjectRight.size());

        Map<String, boolean[]> resultMap = new HashMap<>();

        for (FormProperty oProperty : aFormProperty) {
            groupSearch:
            {
                for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                    List<String> asID_Field_Read_Temp = new LinkedList();
                    List<String> asID_Field_Write_Temp = new LinkedList();
                    //Boolean bInclude=null;
                    LOG.info("oDocumentStepSubjectRight.getsKey_GroupPostfix()={}", oDocumentStepSubjectRight.getsKey_GroupPostfix());

                    long loopStartTime = System.nanoTime();

                    for (DocumentStepSubjectRightField oDocumentStepSubjectRightField : oDocumentStepSubjectRight.getDocumentStepSubjectRightFields()) {
                        String sMask = oDocumentStepSubjectRightField.getsMask_FieldID();
                        LOG.info("sMask={}", sMask);
                        LOG.info("total DocumentStepSubjectRightFields size is: " + oDocumentStepSubjectRight.getDocumentStepSubjectRightFields().size());
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

                            //for (FormProperty oProperty : aFormProperty) {
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
                                    /*if (bWriteField) {
                                        asID_Field_Write_Temp.remove(sID);
                                    } else {
                                        asID_Field_Read_Temp.remove(sID);
                                    }*/
                                    resultMap.remove(sID);
                                } else if (bWriteField) {
                                    //asID_Field_Write_Temp.add(sID);
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
                            //}

                            long scLoopStopTime = System.nanoTime();
                            LOG.info("2st loop time execution in getDocumentStepRights 3th block is: " + String.format("%,12d", (scLoopStopTime - scLoopStartTime)));
                        }
                    }

                    long loopStopTime = System.nanoTime();
                    LOG.info("1st loop time execution in getDocumentStepRights 3th block is: " + String.format("%,12d", (loopStopTime - loopStartTime)));

                    /*asID_Field_Read.addAll(asID_Field_Read_Temp);
                    asID_Field_Write.addAll(asID_Field_Write_Temp);

                    LOG.info("asID_Field_Write_TMP={}", asID_Field_Read_Temp);
                    LOG.info("asID_Field_Read_TMP={}", asID_Field_Write_Temp);
                    LOG.info("asID_Field_Write_TMP size={}", asID_Field_Read_Temp.size());
                    LOG.info("asID_Field_Read_TMP size={}", asID_Field_Write_Temp.size()); */
                }
            }
        }

        stopTime = System.nanoTime();

        LOG.info("getDocumentStepRights 3th block time execution is: " + String.format("%,12d", (stopTime - startTime)));
        startTime = System.nanoTime();
        //mReturn.put("asID_Field_Write(0)", asID_Field_Write);
        //mReturn.put("asID_Field_Read(0)", asID_Field_Read);
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

        /*for (String sID_Field_Write : asID_Field_Write) {
            asID_Field_Read.remove(sID_Field_Write);
        }
        
        mReturn.put("asID_Field_Write", asID_Field_Write);
        mReturn.put("asID_Field_Read", asID_Field_Read);*/
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
        //LOG.info("mReturn={}", mReturn);
        stopTime = System.nanoTime();
        LOG.info("getDocumentStepRights 4th block time execution is: " + String.format("%,12d", (stopTime - startTime)));

        return mReturn;
    }

    public void syncDocumentGroups(DelegateTask delegateTask, List<DocumentStep> aDocumentStep) {

        Set<String> asGroup = new HashSet<>();
        for (DocumentStep oDocumentStep : aDocumentStep) {
            List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.getRights();
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

        /*if(!asGroup_Old.isEmpty()){
            asGroup.addAll(asGroup_Old);
        }*/
        delegateTask.addCandidateGroups(asGroup);

        List<String> asGroup_New = new ArrayList<>();
        Set<IdentityLink> groupsNew = delegateTask.getCandidates();
        groupsNew.stream().forEach((groupNew) -> {
            asGroup_New.add(groupNew.getGroupId());
        });
        LOG.info("asGroup_New after setting: {} delegateTask: {}", asGroup_New, delegateTask.getId());
    }

    //public void checkDocumentInit(DelegateExecution execution) throws IOException, URISyntaxException {//JSONObject
    public List<DocumentStep> checkDocumentInit(DelegateExecution execution, String sKey_GroupPostfix, String sKey_GroupPostfix_New) throws IOException, URISyntaxException {
        //assume that we can have only one active task per process at the same time
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

                //setDocumentSteps(snID_Process_Activiti, soJSON);
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

            if (sKey_GroupPostfix != null && !sKey_GroupPostfix.trim().equals("")
                    && sKey_GroupPostfix_New != null && !sKey_GroupPostfix_New.trim().equals("")) {

                // HistoricProcessInstance oHistoricProcessInstance = historyService.createHistoricProcessInstanceQuery().
                // processInstanceId(execution.getProcessInstanceId()).singleResult();
                /*ProcessInstance oProcessInstance = execution.getEngineServices().getRuntimeService()
                .createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
               
                String sKey_GroupPostfix_New = oProcessInstance.getStartUserId();*/
                LOG.info("start user id is {}", sKey_GroupPostfix_New);
                LOG.info("sKey_GroupPostfix is {}", sKey_GroupPostfix);

                List<DocumentStepSubjectRight> aDocumentStepSubjectRight
                        = cloneDocumentStepSubject(snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New, sKey_Step_Document);
                LOG.info("aDocumentStepSubjectRight in checkDocumentInit is {}", aDocumentStepSubjectRight);
            }

        }

        List<DocumentStep> aResultDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);

        LOG.info("aResultDocumentStep in initDocChecker is {}", aResultDocumentStep);

        for (DocumentStep oDocumentStep : aResultDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step_Document)) {
                LOG.info("founded DocumentStep in initDocChecker is {}", oDocumentStep);
                aResDocumentStep.add(oDocumentStep);
            }
        }

        LOG.info("aResDocumentStep in initDocChecker is {}", aResDocumentStep);

        return aResDocumentStep;
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

            if (sKey_Step_Document == null) {
                sKey_Step_Document = (String) runtimeService.getVariable(snID_Process_Activiti, "sKey_Step_Document");
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

    public Map<String, Boolean> isDocumentStepSubmitedAll(String snID_Process, String sLogin, String sKey_Step)
            throws Exception {
        LOG.info("isDocumentStepSubmitedAll: snID_Process {}, sKey_Step {} ...", snID_Process, sKey_Step);
        Map<String, Boolean> mReturn = new HashMap();
        List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", snID_Process);//
        LOG.info("The size of list aDocumentStep is {}", (aDocumentStep != null ? aDocumentStep.size() : null));
        DocumentStep oFindedDocumentStep = null;

        for (DocumentStep oDocumentStep : aDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step)) {
                LOG.info("snID_Process {} getsKey_Step from oDocumentStep is = {} ", snID_Process, oDocumentStep.getsKey_Step());
                oFindedDocumentStep = oDocumentStep;
                break;
            }
        }

        if (oFindedDocumentStep == null) {
            throw new Exception("DocumentStep not found");
        } else {
            boolean bSubmitedAll = true;
            for (DocumentStepSubjectRight oDocumentStepSubjectRight : oFindedDocumentStep.getRights()) {
                if (oDocumentStepSubjectRight != null) {
                    LOG.info("oDocumentStepSubjectRight: " + oDocumentStepSubjectRight.getsKey_GroupPostfix()
                            + " sDate: " + oDocumentStepSubjectRight.getsDate());
                    DateTime sDate = oDocumentStepSubjectRight.getsDate();
                    LOG.info("sDate ={}", oDocumentStepSubjectRight.getsDate());
                    if (sDate == null) {
                        bSubmitedAll = false;
                        LOG.info("oDocumentStepSubjectRight: " + oDocumentStepSubjectRight.getsKey_GroupPostfix()
                                + " sDate: " + oDocumentStepSubjectRight.getsDate() + "bSubmitedAll: " + bSubmitedAll);
                        break;
                    }
                } else {
                    LOG.error("oDocumentStepSubjectRight is null");
                }
            }
            mReturn.put("bSubmitedAll", bSubmitedAll);
            return mReturn;
        }
    }

    public List<DocumentSubmitedUnsignedVO> getDocumentSubmitedUnsigned(String sLogin)
            throws JsonProcessingException, RecordNotFoundException {

        List<DocumentSubmitedUnsignedVO> aResDocumentSubmitedUnsigned = new ArrayList<>();

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStepSubjectRightDao.findAllBy("sLogin",
                sLogin);
        LOG.info("aDocumentStepSubjectRight in method getDocumentSubmitedUnsigned = {}", aDocumentStepSubjectRight);
        DocumentStepSubjectRight oFindedDocumentStepSubjectRight = null;

        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {

            if (oDocumentStepSubjectRight != null) {

                DateTime sDateECP = oDocumentStepSubjectRight.getsDateECP();
                DateTime sDate = oDocumentStepSubjectRight.getsDate();
                LOG.info("sDateECP in method getDocumentSubmitedUnsigned is", sDateECP);
                LOG.info("sDate in method getDocumentSubmitedUnsigned is", sDateECP);
                // проверяем, если даты ецп нет, но есть дата подписания - нашли
                // нужный объект, который кладем в VO-обьект-обертку
                if (sDateECP == null) {
                    if (sDate != null) {
                        oFindedDocumentStepSubjectRight = oDocumentStepSubjectRight;
                    }

                } else {
                    LOG.info("oFindedDocumentStepSubjectRight not found");
                }

                // Достаем nID_Process у найденного oDocumentStepSubjectRight
                // через DocumentStep
                String snID_Process = oFindedDocumentStepSubjectRight.getDocumentStep().getSnID_Process_Activiti();

                String sID_Order = oFindedDocumentStepSubjectRight.getDocumentStep().getnOrder().toString();
                // через апи активити по nID_Process
                HistoricProcessInstance oProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(snID_Process).singleResult();

                if (oProcessInstance != null) {
                    // вытаскиваем дату создания процесса
                    Date sDateCreateProcess = oProcessInstance.getStartTime();
                    // вытаскиваем название бп
                    String sNameBP = oProcessInstance.getName();
                    // вытаскиваем список тасок по процесу
                    List<Task> tasks = oTaskService.createTaskQuery().processInstanceId(snID_Process).active().list();
                    if (tasks != null || !tasks.isEmpty()) {
                        // берем первую
                        Task oFirstTask = tasks.get(0);
                        // вытаскиваем дату создания таски
                        Date sDateCreateUserTask = oFirstTask.getCreateTime();
                        // и ее название
                        String sUserTaskName = oFirstTask.getName();

                        // Создаем обьект=обертку, в который сетим нужные
                        // полученные поля
                        DocumentSubmitedUnsignedVO oDocumentSubmitedUnsignedVO = new DocumentSubmitedUnsignedVO();

                        oDocumentSubmitedUnsignedVO.setoDocumentStepSubjectRight(oFindedDocumentStepSubjectRight);
                        oDocumentSubmitedUnsignedVO.setsNameBP(sNameBP);
                        oDocumentSubmitedUnsignedVO.setsUserTaskName(sUserTaskName);
                        oDocumentSubmitedUnsignedVO.setsDateCreateProcess(sDateCreateProcess);
                        oDocumentSubmitedUnsignedVO.setsDateCreateUserTask(sDateCreateUserTask);
                        oDocumentSubmitedUnsignedVO.setsDateSubmit(sDate);
                        oDocumentSubmitedUnsignedVO.setsID_Order(sID_Order);

                        aResDocumentSubmitedUnsigned.add(oDocumentSubmitedUnsignedVO);
                    } else {
                        LOG.error(String.format("Tasks for Process Instance [id = '%s'] not found", snID_Process));
                        throw new RecordNotFoundException();
                    }

                } else {
                    LOG.error(String.format("oProcessInstance [id = '%s']  is null", snID_Process));
                }

            }

        }
        return aResDocumentSubmitedUnsigned;
    }

}
