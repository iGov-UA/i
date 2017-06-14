/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.process;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubjectParentNode;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectStatusDao;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessSubjectTreeDao;
import org.igov.model.process.ProcessUser;
import org.igov.service.business.action.event.ActionEventHistoryService;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.conf.AttachmetService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author inna
 */
@Component("processSubjectService")
public class ProcessSubjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessSubjectService.class);
    private static final long FAKE_ROOT_PROCESS_ID = 0;

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    private IdentityService identityService;
    
    @Autowired
    private ActionEventHistoryService oActionEventHistoryService;
    
    @Autowired
    private TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    private ProcessSubjectDao processSubjectDao;

    @Autowired
    private ProcessSubjectTreeDao processSubjectTreeDao;

    @Autowired
    private ProcessSubjectStatusDao processSubjectStatusDao;
    
    @Autowired
    private AttachmetService oAttachmetService;
    
    @Autowired
    private ActionTaskService oActionTaskService;
    
    @Autowired
    private ProcessSubjectTaskService oProcessSubjectTaskService;

    public ProcessSubjectResult getCatalogProcessSubject(String snID_Process_Activiti, Long deepLevel, String sFind) {

        List<ProcessSubject> aChildResult = new ArrayList();
        List<ProcessSubjectTree> processSubjectRelations = new ArrayList<>(baseEntityDao.findAll(ProcessSubjectTree.class));
        List<ProcessSubjectParentNode> parentProcessSubjects = new ArrayList<>();
        Map<Long, List<ProcessSubject>> subjToNodeMap = new HashMap<>();
        Map<String, Long> mapGroupActiviti = new HashMap<>();
        ProcessSubjectParentNode parentProcessSubject = null;
        Set<Long> idParentList = new LinkedHashSet<>();
        for (ProcessSubjectTree processSubjectTree : processSubjectRelations) {
            final ProcessSubject parent = processSubjectTree.getProcessSubjectParent();

            if (parent.getId() != FAKE_ROOT_PROCESS_ID) {
                parentProcessSubject = new ProcessSubjectParentNode();
                final ProcessSubject child = processSubjectTree.getProcessSubjectChild();
                if (!idParentList.contains(parent.getId())) {
                    idParentList.add(parent.getId());
                    // устанавливаем парентов
                    parentProcessSubject.setGroup(parent);
                    // доавляем детей
                    parentProcessSubject.addChild(child);
                    parentProcessSubjects.add(parentProcessSubject);
                    // мапа парент -ребенок
                    subjToNodeMap.put(parent.getId(), parentProcessSubject.getChildren());
                    // мапа группа-ид парента
                    mapGroupActiviti.put(parent.getSnID_Process_Activiti(), parent.getId());
                } else {
                    for (ProcessSubjectParentNode processSubjectParentNode : parentProcessSubjects) {
                        // убираем дубликаты
                        if (processSubjectParentNode.getGroup().getId().equals(parent.getId())) {
                            // если дубликат парента-добавляем его детей к
                            // общему списку
                            processSubjectParentNode.getChildren().add(child);
                            // мапа парент-ребенок
                            subjToNodeMap.put(parent.getId(), processSubjectParentNode.getChildren());
                            // мапа группа-ид парента
                            mapGroupActiviti.put(parent.getSnID_Process_Activiti(), parent.getId());
                        }
                    }
                }
            }

        }

        // достаем ид snID_Process_Activiti которое на вход
        Long groupFiltr = mapGroupActiviti.get(snID_Process_Activiti);
        // детей его детей
        List<ProcessSubject> children = subjToNodeMap.get(groupFiltr);
        // children полный список первого уровня
        if (children != null && !children.isEmpty()) {

            // получаем только ид чилдренов полного списка детей первого уровня
            final List<Long> idChildren = Lists
                    .newArrayList(Collections2.transform(children, new Function<ProcessSubject, Long>() {
                        @Override
                        public Long apply(ProcessSubject subjectGroup) {
                            return subjectGroup.getId();
                        }
                    }));
            aChildResult.addAll(children);
            getChildren(children, idChildren, subjToNodeMap, idParentList, checkDeepLevel(deepLevel), 1, aChildResult);

        }

        List<ProcessSubject> aChildResultByUser = new ArrayList();
        if (!aChildResult.isEmpty()) {
            if (sFind != null && !sFind.isEmpty()) {
                for (ProcessSubject processSubject : aChildResult) {
                    List<ProcessUser> aSubjectUser = getUsersByGroupSubject(processSubject.getSnID_Process_Activiti());
                    final List<ProcessUser> processUserFiltr = Lists
                            .newArrayList(Collections2.filter(aSubjectUser, new Predicate<ProcessUser>() {
                                @Override
                                public boolean apply(ProcessUser processUser) {
                                    // получить только отфильтрованный список по
                                    // sFind в фио
                                    return processUser.getsFirstName().toLowerCase().contains(sFind.toLowerCase());
                                }
                            }));
                    // получаем только их логины
                    final List<String> sFindLogin = Lists
                            .newArrayList(Collections2.transform(processUserFiltr, new Function<ProcessUser, String>() {
                                @Override
                                public String apply(ProcessUser processUser) {
                                    return processUser.getsLogin();
                                }
                            }));

                    // и оставляем только processSubject чьи логины содержаться
                    // в отфильтрованном списке
                    if (sFindLogin.contains(processSubject.getsLogin())) {
                        aChildResultByUser.add(processSubject);
                    }
                }
            }
        }

        ProcessSubjectResult processSubjectResult = new ProcessSubjectResult();
        if (sFind != null && !sFind.isEmpty()) {
            processSubjectResult.setaProcessSubject(aChildResultByUser);
        } else {
            processSubjectResult.setaProcessSubject(aChildResult);
        }
        for (ProcessSubject processSubject : processSubjectResult.getaProcessSubject()) {
            processSubject.setaUser(getUsersByGroupSubject(processSubject.getsLogin()));
        }
        return processSubjectResult;

    }


    /**
     * Сохранить сущность
     *
     * @param snID_Process_Activiti
     * @param sLogin
     * @param sDatePlan
     * @param nOrder
     * @return
     */
    public ProcessSubject setProcessSubject(String snID_Process_Activiti, String sLogin, String sDatePlan, Long nOrder) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dtDatePlan = formatter.parseDateTime(sDatePlan);
        ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(1L);
        return processSubjectDao.setProcessSubject(snID_Process_Activiti, sLogin, dtDatePlan, nOrder, processSubjectStatus);
    }

    /**
     * проверяем входящий параметр deepLevel
     *
     * @param deepLevel
     * @return
     */
    public Long checkDeepLevel(Long deepLevel) {
        if (deepLevel == null || deepLevel.intValue() == 0) {
            return 1000L;
        }
        return deepLevel;
    }

    /**
     * Метод структуру иерархии согласно заданной глубины и группы
     *
     * @param aChildLevel результирующий список со всеми нужными нам детьми
     * @param anID_ChildLevel ид детей уровня на котором мы находимся
     * @param subjToNodeMap мапа соответствия всех ид перентов и список его
     * детей
     * @param anID_PerentAll ид всех перентов
     * @param deepLevelRequested желаемая глубина
     * @param deepLevelFact фактическая глубина
     * @param result
     * @return
     */
    public List<ProcessSubject> getChildren(List<ProcessSubject> aChildLevel, List<Long> anID_ChildLevel,
            Map<Long, List<ProcessSubject>> subjToNodeMap, Set<Long> anID_PerentAll, Long deepLevelRequested,
            int deepLevelFact, List<ProcessSubject> result) {

        List<ProcessSubject> aChildLevel_Result = new ArrayList<>();
        List<Long> anID_ChildLevel_Result = new ArrayList<>();

        LOG.info("aChildLevel: " + aChildLevel.size() + " anID_ChildLevel: " + anID_ChildLevel);
        if (deepLevelFact < deepLevelRequested.intValue()) {
            for (Long nID_ChildLevel : anID_ChildLevel) {
                if (anID_PerentAll.contains(nID_ChildLevel)) {
                    // достаем детей детей
                    aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
                    if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
                        LOG.info("nID_ChildLevel: " + nID_ChildLevel + " aChildLevel_Result: "
                                + aChildLevel_Result.size());
                        // получаем только ид чилдренов
                        List<Long> anID_Child = Lists.newArrayList(
                                Collections2.transform(aChildLevel_Result, new Function<ProcessSubject, Long>() {
                                    @Override
                                    public Long apply(ProcessSubject subjectGroup) {
                                        return subjectGroup.getId();
                                    }
                                }));
                        anID_ChildLevel_Result.addAll(anID_Child);
                        LOG.info("nID_ChildLevel: " + nID_ChildLevel + " anID_ChildLevel_Result: "
                                + anID_ChildLevel_Result.size());
                        // добавляем детей к общему списку детей
                        result.addAll(aChildLevel_Result);
                        LOG.info("result: " + result.size());
                    }
                }
            }
            deepLevelFact++;
            LOG.info("deepLevelFact: " + deepLevelFact + " deepLevelRequested: " + deepLevelRequested);
            if (deepLevelFact < deepLevelRequested.intValue()) {
                getChildren(aChildLevel_Result, anID_ChildLevel_Result, subjToNodeMap, anID_PerentAll,
                        checkDeepLevel(deepLevelRequested), deepLevelFact, result);
            }
        }
        return result;
    }


    /**
     * Получение списка юзеров по ид группы
     *
     * @param snID_Process_Activiti
     * @return
     */
    public List<ProcessUser> getUsersByGroupSubject(String snID_Process_Activiti) {

        List<ProcessUser> amsUsers = new ArrayList<>();
        List<User> aoUsers = snID_Process_Activiti != null
                ? identityService.createUserQuery().memberOfGroup(snID_Process_Activiti).list()
                : identityService.createUserQuery().list();

        for (User oUser : aoUsers) {
            ProcessUser processUser = ProcessUser.BuilderHelper.buildSubjectUser(
                    oUser.getId() == null ? "" : oUser.getId(),
                    oUser.getFirstName() == null ? "" : oUser.getFirstName(),
                    oUser.getLastName() == null ? "" : oUser.getLastName(),
                    oUser.getEmail() == null ? "" : oUser.getEmail(), null);
            amsUsers.add(processUser);

        }

        return amsUsers;

    }

    /**
     * Задать логин
     *
     * @param snID_Process_Activiti
     * @param sLogin
     * @return
     */
    public ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin) {
        return processSubjectDao.setProcessSubjectLogin(snID_Process_Activiti, sLogin);
    }

    /**
     * Задать номер заявки
     *
     * @param snID_Process_Activiti
     * @param nOrder
     * @return
     */
    public ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder) {
        return processSubjectDao.setProcessSubjectOrder(snID_Process_Activiti, nOrder);
    }

    /**
     * Задать статус
     *
     * @param snID_Process_Activiti
     * @param sID_ProcessSubjectStatus
     * @return
     */
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, String sID_ProcessSubjectStatus) {
        ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByExpected("sID", sID_ProcessSubjectStatus);
        return processSubjectDao.setProcessSubjectStatus(snID_Process_Activiti, processSubjectStatus);
    }

    /**
     * Задать статус и отчет
     *
     * @param snID_Process_Activiti
     * 
     * @param sID_ProcessSubjectStatus
     * @return
     */
    public ProcessSubject setProcessSubjectStatusAndReport(String snID_Process_Activiti, String sID_ProcessSubjectStatus, String sReport) {
        ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByExpected("sID", sID_ProcessSubjectStatus);
        return processSubjectDao.setProcessSubjectStatusAndReport(snID_Process_Activiti, processSubjectStatus, sReport);
    }

    /**
     * Задать дату
     *
     * @param snID_Process_Activiti
     * @param sDatePlan
     * @return
     */
    public ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, String sDatePlan) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dtDatePlan = formatter.parseDateTime(sDatePlan);
        return processSubjectDao.setProcessSubjectDatePlan(snID_Process_Activiti, dtDatePlan);
    }

    public void removeProcessSubject(ProcessSubject processSubject) {
        
        LOG.info("removeProcessSubject started...");
        
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processSubject.getSnID_Process_Activiti()).singleResult();
        LOG.info("processInstance {}", processInstance);
        
        if (processInstance != null) {
            runtimeService.deleteProcessInstance(processSubject.getSnID_Process_Activiti(), "deleted");
        }
        
        Optional<ProcessSubjectTree> processSubjectTreeToDelete = processSubjectTreeDao.findBy("processSubjectChild", processSubject);
        
        if(processSubjectTreeToDelete.isPresent()){
            LOG.info("processSubjectTreeToDelete {}", processSubjectTreeToDelete.get());
            processSubjectTreeDao.delete(processSubjectTreeToDelete.get());
        }

        processSubjectDao.delete(processSubject);
        LOG.info("removeProcessSubject ended...");
    }

    public void removeProcessSubjectDeep(ProcessSubject processSubject) {
        LOG.info("removeProcessSubjectDeep started...");
        ProcessSubjectResult processSubjectResult = getCatalogProcessSubject(processSubject.getSnID_Process_Activiti(), 0L, null);
        LOG.info("processSubjectResult {}", processSubjectResult.getaProcessSubject());
        List<ProcessSubject> aProcessSubject = processSubjectResult.getaProcessSubject();
        
        List<ProcessSubject> aReverseProcessSubject = Lists.reverse(aProcessSubject);
        
        for (ProcessSubject oProcessSubject : aReverseProcessSubject) {
            removeProcessSubject(oProcessSubject);
        }

        removeProcessSubject(processSubject);
        LOG.info("removeProcessSubjectDeep ended...");
    }

    public void editProcessSubject(ProcessSubject processSubject, Map<String, Object> mParamDocument) throws ParseException {

        ProcessSubjectResult processSubjectResult = getCatalogProcessSubject(processSubject.getSnID_Process_Activiti(), 0L, null);
        DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");
        if (processSubjectResult != null) {
            List<ProcessSubject> aProcessSubject_Child = processSubjectResult.getaProcessSubject();

            if (!aProcessSubject_Child.isEmpty()) {
                ProcessSubject oProcessSubject_Child = aProcessSubject_Child.get(0);

                ProcessInstance oProcessInstance = runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(oProcessSubject_Child.getSnID_Process_Activiti())
                        .includeProcessVariables()
                        .active()
                        .singleResult();

                if (oProcessInstance != null) {
                   
                    Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
                    LOG.info("mProcessVariable: " + mProcessVariable);
                   
                    for(String sProcessVariable: mProcessVariable.keySet()){
                	
                        try{
                            mProcessVariable.replace(sProcessVariable, df_StartProcess.format(mProcessVariable.get(sProcessVariable)));
                        }
                        catch(Exception ex){}
                        
                        try{
                            mProcessVariable.replace(sProcessVariable, 
                                    df_StartProcess.format(parseDate((String) mProcessVariable.get(sProcessVariable))));
                        }
                        catch(Exception ex){}
                        
                   }
                    
                    for(String sParamDocument: mProcessVariable.keySet()){
                	//if(mParamDocument.get(sParamDocument) != null){
                 	       LOG.info("mProcessVariable param : " +
                        //           "class:" + mParamDocument.get(sParamDocument).getClass() +
                                   " name: " + sParamDocument + " value: " +  mProcessVariable.get(sParamDocument));
                        //}
                    }
                   
                   Map<String, Object> mParamDocumentNew = new HashMap<>();
                   
                    for (String mKey : mParamDocument.keySet()) {
                   
                        Object oParamDocument = mParamDocument.get(mKey);
                        Object oProcessVariable = mProcessVariable.get(mKey);

                        if (oParamDocument != null) {
                            if (oProcessVariable != null) {
                                if (!(((String) oParamDocument).equals((String) oProcessVariable))) {
                                    mParamDocumentNew.put(mKey, oParamDocument);
                                }
                            } else {
                                mParamDocumentNew.put(mKey, null);
                            }
                        } else if (oProcessVariable != null) {
                            mParamDocumentNew.put(mKey, oProcessVariable);
                        }
                    }

                    LOG.info("mParamDocumentNew: " + mParamDocumentNew);
                    
                    String sOldHistoryData = "<tr><td>";
                    String sNewHistoryData = "<td>";
                    
                    if (!mParamDocumentNew.isEmpty()) {
                        
                        addEditHistoryEvent(processSubject.getSnID_Process_Activiti(), sNewHistoryData, sOldHistoryData,
                                    processSubject.getsLogin(), processSubject.getoProcessSubjectStatus().getId());
                        
                        for (ProcessSubject oProcessSubject : aProcessSubject_Child) {
                            oProcessSubject.setsDateEdit(new DateTime(df_StartProcess.parse(df_StartProcess.format(new Date()))));

                            DateTime datePlan = null;
                            if (mParamDocument.get("sDateExecution") != null) {
                                datePlan = new DateTime(parseDate((String) mParamDocument.get("sDateExecution")));
                                
                            }
                            
                            
                            
                            for (String mKey : mParamDocumentNew.keySet()) {
                                runtimeService.setVariable(oProcessSubject.getSnID_Process_Activiti(), mKey, mParamDocumentNew.get(mKey));
                                
                                LOG.info("mProcessVariable.get(mKey): " + mProcessVariable.get(mKey));
                                LOG.info("mParamDocumentNew.get(mKey): " + mParamDocumentNew.get(mKey));
                                
                                if(!mProcessVariable.get(mKey).equals(mParamDocumentNew.get(mKey))){
                                    sOldHistoryData = sOldHistoryData + mKey + " : " + mProcessVariable.get(mKey) + "\n";
                                    sNewHistoryData = sNewHistoryData + mKey + " : " + mParamDocumentNew.get(mKey) + "\n";
                                }
                            }
                            
                            addEditHistoryEvent(oProcessSubject.getSnID_Process_Activiti(), sNewHistoryData, sOldHistoryData,
                                    processSubject.getsLogin(), oProcessSubject.getoProcessSubjectStatus().getId());
                            
                            oProcessSubject.setsDatePlan(datePlan);
                            processSubjectDao.saveOrUpdate(oProcessSubject);

                        }
                    }
                }
            }
        }
    }
    
    public void addEditHistoryEvent(String snID_Process_Activiti, String sNewHistoryData, String sOldHistoryData, String sLogin, Long nID_Status) 
    {
        
        String sID_Order
                = generalConfig.getOrderId_ByProcess(Long.parseLong(snID_Process_Activiti));
        
        LOG.info("history data during document editing - snID_Process_Activiti: " + snID_Process_Activiti);
        LOG.info("history data during document editing - sID_Order: " + sID_Order);
        LOG.info("history data during document editing - sNewHistoryData: " + sNewHistoryData);
        LOG.info("history data during document editing - sOldHistoryData: " + sOldHistoryData);
        LOG.info("history data during document editing - sLogin: " + sLogin);
        
        Map<String, String> historyParam = new HashMap<>();

        historyParam.put("newData", sNewHistoryData + "</td></tr>");
        historyParam.put("oldData", sOldHistoryData + "</td>");
        //historyParam.put("newData", "старые данные");
        //historyParam.put("oldData", "новые данные");
        historyParam.put("nID_StatusType", nID_Status.toString());
        historyParam.put("sLogin", sLogin);

        try {
            //oActionEventHistoryService.addHistoryEvent(sID_Order,
            //       sLogin, historyParam, 14L);
        } catch (Exception ex) {
            LOG.info("Error saving history during document editing: {}", ex);
        }

    }

    public void setProcessSubjects(Map<String, Object> mParam, String snProcess_ID) {

        try {
            ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(1L);
            DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");

            String sFormatDateExecution = "";
            String sFormatDateRegistration = "";
            String sFormatDateDoc = "";
            Date oDateExecution = null;
            
            LOG.info("sFormatDateExecution: " + sFormatDateExecution);
            LOG.info("sFormatDateRegistration: " + sFormatDateRegistration);
            LOG.info("sFormatDateDoc: " + sFormatDateDoc);
            LOG.info("oDateExecution: " + oDateExecution);

            if ((mParam.get("sDateExecution") != null) && (!mParam.get("sDateExecution").equals(""))) {
                oDateExecution = parseDate((String)mParam.get("sDateExecution"));
                sFormatDateExecution = df_StartProcess.format(oDateExecution);
                LOG.info("oDateExecution: " + oDateExecution);
                LOG.info("sFormatDateExecution: " + sFormatDateExecution);
                mParam.replace("sDateExecution", sFormatDateExecution);
            }
            if ((mParam.get("sDateRegistration") != null) && (!mParam.get("sDateRegistration").equals(""))) {
                Date oDateRegistration = parseDate((String)mParam.get("sDateRegistration"));
                sFormatDateRegistration = df_StartProcess.format(oDateRegistration);
                LOG.info("oDateRegistration: " + oDateRegistration);
                LOG.info("sFormatDateRegistration: " + sFormatDateRegistration);
                mParam.replace("sDateRegistration", sFormatDateRegistration);
            }
            if ((mParam.get("sDateDoc") != null) && (!mParam.get("sDateDoc").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("sDateDoc"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("oDateDoc: " + oDateDoc);
                LOG.info("sFormatDateDoc: " + sFormatDateDoc);
                mParam.replace("sDateDoc", sFormatDateDoc);
            }
            if ((mParam.get("oDateBegin") != null) && (!mParam.get("oDateBegin").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("oDateBegin"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("oDateBegin: " + oDateDoc);
                LOG.info("sFormatoDateBegin: " + sFormatDateDoc);
                mParam.replace("oDateBegin", sFormatDateDoc);
            }
            if ((mParam.get("oDateEnd") != null) && (!mParam.get("oDateEnd").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("oDateEnd"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("oDateEnd: " + oDateDoc);
                LOG.info("sFormatoDateEnd: " + sFormatDateDoc);
                mParam.replace("oDateEnd", sFormatDateDoc);
            }
            if ((mParam.get("sDate_441") != null) && (!mParam.get("sDate_441").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("sDate_441"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("sDate_441: " + oDateDoc);
                LOG.info("sFormatsDate_441: " + sFormatDateDoc);
                mParam.replace("sDate_441", sFormatDateDoc);
            }
            if ((mParam.get("sDateApprove") != null) && (!mParam.get("sDateApprove").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("sDateApprove"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("sDateApprove: " + oDateDoc);
                LOG.info("sFormatsDateApprove: " + sFormatDateDoc);
                mParam.replace("sDateApprove", sFormatDateDoc);
            }
            if ((mParam.get("sDate_442") != null) && (!mParam.get("sDate_442").equals(""))) {
                Date oDateDoc = parseDate((String)mParam.get("sDate_442"));
                sFormatDateDoc = df_StartProcess.format(oDateDoc);
                LOG.info("sDate_442: " + oDateDoc);
                LOG.info("sFormatsDate_442: " + sFormatDateDoc);
                mParam.replace("sDate_442", sFormatDateDoc);
            }

            ProcessSubject oProcessSubjectParent = processSubjectDao.findByProcessActivitiId(snProcess_ID);

            Map<String, Object> mParamDocument = new HashMap<>();
            mParamDocument.putAll(mParam);
            
            //проверяем нет ли в базе такого объекта, если нет создаем, если есть - не создаем
            //иначе проверяем на необходимость редактирования
            if (oProcessSubjectParent == null) {
                if(mParam.get("sName_SubjectRole") != null){
                    oProcessSubjectParent = processSubjectDao
                            .setProcessSubject(snProcess_ID, (String)mParam.get("sName_SubjectRole"),
                                    new DateTime(oDateExecution), 0L, processSubjectStatus);
                }
                else{
                     oProcessSubjectParent = processSubjectDao
                            .setProcessSubject(snProcess_ID, "test_role",
                                    new DateTime(oDateExecution), 0L, processSubjectStatus);
                }
            } else {
                editProcessSubject(oProcessSubjectParent, mParamDocument);
            }

            List<ProcessSubjectTree> aProcessSubjectTreeChild = processSubjectTreeDao.findChildren(oProcessSubjectParent.getSnID_Process_Activiti()); // Find all children for document
            
            List<ProcessSubject> aProcessSubjectChild = getCatalogProcessSubject(snProcess_ID, 1L, null).getaProcessSubject();
            List<String> aProcessSubjectLoginToDelete = new ArrayList<>();
            
            
            
            for (ProcessSubject oProcessSubject : aProcessSubjectChild) {
                aProcessSubjectLoginToDelete.add(oProcessSubject.getsLogin());
            }
            
            JSONParser parser = new JSONParser();
            
            JSONObject oJSONObject = null;
            
            try{
                JSONObject oTableJSONObject = (JSONObject) parser.parse((String)mParam.get("sID_Attachment"));
                oJSONObject = (JSONObject) parser.parse(IOUtils.toString(oAttachmetService.getAttachment(null, null, 
                    (String)oTableJSONObject.get("sKey"), (String)oTableJSONObject.get("sID_StorageType")).getInputStream(), "UTF-8"));
                LOG.info("oTableJSONObject in listener: " + oJSONObject.toJSONString());
            }
            catch(Exception ex){
                InputStream attachmentContent = taskService.getAttachmentContent((String)mParam.get("sID_Attachment"));
                oJSONObject = (JSONObject) parser.parse(IOUtils.toString(attachmentContent, "UTF-8"));   // (JSONObject) new JSONParser().parse(IOUtils.toString(attachmentContent));
            }
            
            LOG.info("JSON table String: " + oJSONObject.toJSONString());
            JSONArray aJsonRow = (JSONArray) oJSONObject.get("aRow");

            List<String> aProcessSubjectLoginNew = new ArrayList<>();

            if (aJsonRow != null) {
                for (int i = 0; i < aJsonRow.size(); i++) {

                    Map<String, Object> mParamTask = new HashMap<>();
                    JSONObject oJsonField = (JSONObject) aJsonRow.get(i);

                    if (oJsonField != null) {
                        JSONArray aJsonField = (JSONArray) oJsonField.get("aField");

                        if (aJsonField != null) {
                            mParamTask.putAll(mParamDocument);
                            for (int j = 0; j < aJsonField.size(); j++) {
                                JSONObject oJsonMap = (JSONObject) aJsonField.get(j);
                                if (oJsonMap != null) {
                                    Object oId = oJsonMap.get("id");
                                    Object oValue = oJsonMap.get("value");
                                    if (oValue != null) {
                                        mParamTask.put((String) oId, (String) oValue);

                                    } else {
                                        mParamTask.put((String) oId, null);
                                    }
                                }
                            }
                            LOG.info("mParamTask in table is: " + mParamTask); //логируем всю мапу
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }

                    if (mParamTask.get("sLogin_isExecute") == null) {
                        continue;
                    }

                    boolean continueFlag = false;

                    for (ProcessSubjectTree oProcessSubjectTreeChild : aProcessSubjectTreeChild) {
                        if (oProcessSubjectTreeChild.getProcessSubjectChild().getsLogin().equals(mParamTask.get("sLogin_isExecute").toString())) {
                            continueFlag = true;
                            break;
                        }
                    }

                    if (continueFlag == false) {
                        ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey((String) mParamDocument.get("sTaskProcessDefinition"), mParamTask);
                        LOG.info("oProcessInstanceChild id: " + (oProcessInstanceChild != null ? oProcessInstanceChild.getId() : " oInstanse is null"));
                        if (oProcessInstanceChild != null) {
                            ProcessSubject oProcessSubjectChild = processSubjectDao
                                    .setProcessSubject(oProcessInstanceChild.getId(), (String) mParamTask.get("sLogin_isExecute"),
                                            new DateTime(oDateExecution), new Long(i + 1), processSubjectStatus);
                            ProcessSubjectTree oProcessSubjectTreeParent = new ProcessSubjectTree();
                            oProcessSubjectTreeParent.setProcessSubjectParent(oProcessSubjectParent);
                            oProcessSubjectTreeParent.setProcessSubjectChild(oProcessSubjectChild);
                            processSubjectTreeDao.saveOrUpdate(oProcessSubjectTreeParent);
                        }
                    } else {
                        aProcessSubjectLoginNew.add(mParamTask.get("sLogin_isExecute").toString());
                    }
                }

                if (!aProcessSubjectLoginToDelete.isEmpty()) {
                    aProcessSubjectLoginToDelete.removeAll(aProcessSubjectLoginNew);

                    for (ProcessSubject oProcessSubject : aProcessSubjectChild) {
                        for (String sLogin : aProcessSubjectLoginToDelete) {
                            if (oProcessSubject.getsLogin().equals(sLogin)) {

                                String sProcessSubjectStatus = oProcessSubject.getoProcessSubjectStatus().getsID();

                                if (!(sProcessSubjectStatus.equals("executed") || sProcessSubjectStatus.equals("notExecuted")
                                        || sProcessSubjectStatus.equals("unactual") || sProcessSubjectStatus.equals("closed"))) {
                                    removeProcessSubjectDeep(oProcessSubject);
                                }
                            }
                        }
                    }
                }
            } else {
                LOG.info("JSON array is null");
            }
        } catch (Exception e) {
            LOG.error("SetTasks listener throws an error: ", e);
            throw new RuntimeException(e);
        }
    }

    public Date parseDate(String sDate) throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");
        Date oDateReturn;
        try {
            oDateReturn = df.parse(sDate);
        } catch (java.text.ParseException ex) {
            oDateReturn = df_StartProcess.parse(sDate);
        }
        return oDateReturn;
    }
    
      
    /**
     * По ид процесса активити вынимаем всех детей. Если статус отличен от
     * статусов: executed;notExecuted;closed проставляем в статус сущности
     * ProcessSubject unactual и закрываем процесс-задачу с причиной unactual.
     * 
     * @param snID_Process_Activiti
     */
    public void updateStatusTaskTreeAndCloseProcess(String snID_Process_Activiti, String sID_ProcessSubjectStatus) {
    	
    	LOG.info("sID_ProcessSubjectStatus in updateStatusTaskTreeAndCloseProcess..." + sID_ProcessSubjectStatus);

	ProcessSubjectResult processSubjectResult = getCatalogProcessSubject(snID_Process_Activiti, 0L, null);

	if (processSubjectResult != null) {
	    List<ProcessSubject> aProcessSubject_Child = processSubjectResult.getaProcessSubject();
   
	    ProcessSubjectStatus oProcessSubjectStatusUnactual = processSubjectStatusDao.findByExpected("sID", sID_ProcessSubjectStatus);
	     LOG.info("oProcessSubjectStatusUnactual findByIdExpected = " + oProcessSubjectStatusUnactual);
	    DateFormat df_ProcessSubjectSafe = new SimpleDateFormat("dd/MM/yyyy");

	    for (ProcessSubject oProcessSubject_Сhild : aProcessSubject_Child) {
  
		String sProcessSubjectStatus = oProcessSubject_Сhild.getoProcessSubjectStatus().getsID();
		LOG.info("String sProcessSubjectStatus Сhild is....... = " + sProcessSubjectStatus);

		if (!(sProcessSubjectStatus.equals("executed") || sProcessSubjectStatus.equals("notExecuted")
			|| sProcessSubjectStatus.equals("unactual") || sProcessSubjectStatus.equals("closed")))	{

		    oProcessSubject_Сhild.setoProcessSubjectStatus(oProcessSubjectStatusUnactual);
		    LOG.info("String sProcessSubjectStatus Сhild is   now....... = " + sProcessSubjectStatus);
		    try {
			oProcessSubject_Сhild.setsDateEdit(
				new DateTime(df_ProcessSubjectSafe.parse(df_ProcessSubjectSafe.format(new Date()))));
		    } catch (ParseException e) {
			throw new RuntimeException(e);
		    }

		    processSubjectDao.saveOrUpdate(oProcessSubject_Сhild);

		    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
			    .processInstanceId(oProcessSubject_Сhild.getSnID_Process_Activiti()).singleResult();
		    
		    LOG.info("ProcessInstance....... = " + processInstance);
		    
		    if (processInstance != null) {
		    	LOG.info("ProcessInstance before delete = " + processInstance);
			runtimeService.deleteProcessInstance(oProcessSubject_Сhild.getSnID_Process_Activiti(),
				oProcessSubjectStatusUnactual.getsID());
			
		    }

		}
	    }
	}
    }
    
    /**
     * Изменение статуса процесса. По наличию логинов и статусу определяем какой
     * кейс должен отработать.
     * 
     * @param sID_ProcessSubjectStatus  Статус, который нужно установить 
     * @param snID_Task_Activiti        Ид таски
     * @param sLoginController          Логин контролирующего
     * @param sLoginExecutor            Логин исполнителя
     * @param sText                     Текстовое поле
     * @param sDatePlaneNew             Дата на которую нужно перенести срок
     * @return                          Процесс, который был изменен
     */
    public ProcessSubject setProcessSubjectStatus(
            String sID_ProcessSubjectStatus, String snID_Task_Activiti, String sLoginController, String sLoginExecutor,
            String sText, String sDatePlaneNew
    ) {
        
        /**
         *Определяем кто вызвал сервис (исполнитель или контролирующий). Пришел только
         * логин sLoginExecutor - исполнитель, пришел только логин sLoginController - контролирующий,
         * если пришло два логина - контролирующий.
         */
        String sLoginMain = sLoginController;
                
        if (sLoginExecutor != null && sLoginController == null) {
            
            sLoginMain = sLoginExecutor;
        
        } 
        
        String snID_Process_Activiti = oActionTaskService.getProcessInstanceIDByTaskID(snID_Task_Activiti);
        
        ProcessSubject oProcessSubjectMain = processSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLoginMain);
                  
        String sLoginRoleMain = oProcessSubjectMain.getsLoginRole();
        LOG.info("sLoginRoleMain={}", sLoginRoleMain);
        
        if (sLoginRoleMain.equals("Executor") || sLoginRoleMain.equals("Controller")) {

            ProcessSubjectStatus oProcessSubjectStatus = processSubjectStatusDao.findByExpected("sID", sID_ProcessSubjectStatus);
            
            DateTime dtCurrentDate = new DateTime();
            DateTime dtDatePlaneNew = null;
                
            if (sDatePlaneNew != null) {        
                dtDatePlaneNew = DateTime.parse(sDatePlaneNew, DateTimeFormat.forPattern("yyyy-MM-dd"));
            }
             
            //Исполнитель отработал задачу
            if ((sID_ProcessSubjectStatus.equals("executed") || sID_ProcessSubjectStatus.equals("notExecuted") 
                || sID_ProcessSubjectStatus.equals("unactual")) && sLoginRoleMain.equals("Executor")) {
                
                if (sText != null) {                          
                    oProcessSubjectMain.setsText(sText);
                }
                oProcessSubjectMain.setsDateEdit(dtCurrentDate);
                oProcessSubjectMain.setoProcessSubjectStatus(oProcessSubjectStatus);
                oProcessSubjectMain.setsDateFact(dtCurrentDate);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectMain);
                
            //Просьба о переносе срока исполнителем    
            } else if (sID_ProcessSubjectStatus.equals("requestTransfered") && sLoginRoleMain.equals("Executor")) {
                
                if (dtDatePlaneNew == null) {               
                    throw new RuntimeException("Did not send a request date sDatePlaneNew. To set this status you must send a date which you need to set.");
                }
                
                if (sText != null) {                          
                    oProcessSubjectMain.setsText(sText);
                }
                oProcessSubjectMain.setsDateEdit(dtCurrentDate);
                oProcessSubjectMain.setoProcessSubjectStatus(oProcessSubjectStatus);                
                oProcessSubjectMain.setsDatePlanNew(dtDatePlaneNew);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectMain);
            
            //Перенос срока контролирующим
            } else if (sID_ProcessSubjectStatus.equals("transfered") && sLoginRoleMain.equals("Controller")) {
                
                if (sLoginExecutor == null) {                
                    throw new RuntimeException("Did not send an executor login. To set this status you must to send executor's login besides controller's.");
                }
                
                if (dtDatePlaneNew == null) {               
                    throw new RuntimeException("Did not send a request date sDatePlaneNew. To set this status you must send a date which you need to set.");
                }
                
                //вносим изменения в контролера
                if (sText != null) {                          
                    oProcessSubjectMain.setsText(sText);
                }
                oProcessSubjectMain.setsDateEdit(dtCurrentDate);
                oProcessSubjectMain.setoProcessSubjectStatus(oProcessSubjectStatus);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectMain);
                
                //вносим изменения в исполнителя
                ProcessSubject oProcessSubjectExecutor = processSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLoginExecutor);
  
                oProcessSubjectExecutor.setsDateEdit(dtCurrentDate);
                oProcessSubjectExecutor.setsDatePlan(dtDatePlaneNew);
                oProcessSubjectExecutor.setsDatePlanNew(null);
                oProcessSubjectExecutor.setoProcessSubjectStatus(oProcessSubjectStatus);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectExecutor);
            
            //Контролирующий отклонил отчет    
            } else if (sID_ProcessSubjectStatus.equals("rejected") && sLoginRoleMain.equals("Controller")) {
                
                if (sLoginExecutor == null) {                
                    throw new RuntimeException("Did not send an executor login. To set this status you must to send executor's login besides controller's.");
                }
                
                //вносим изменения в контролера
                if (sText != null) {                          
                    oProcessSubjectMain.setsText(sText);
                }
                oProcessSubjectMain.setsDateEdit(dtCurrentDate);
                oProcessSubjectMain.setoProcessSubjectStatus(oProcessSubjectStatus);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectMain);
                
                //вносим изменения в исполнителя
                ProcessSubject oProcessSubjectExecutor = processSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLoginExecutor);

                oProcessSubjectExecutor.setsDateEdit(dtCurrentDate);
                oProcessSubjectExecutor.setsDateFact(null);
                oProcessSubjectExecutor.setsText(null);
                oProcessSubjectExecutor.setoProcessSubjectStatus(oProcessSubjectStatus);
                
                processSubjectDao.saveOrUpdate(oProcessSubjectExecutor);
               
            } else if ((sID_ProcessSubjectStatus.equals("executed") || sID_ProcessSubjectStatus.equals("notExecuted") 
                || sID_ProcessSubjectStatus.equals("unactual")) && sLoginRoleMain.equals("Controller")) {
                                
                List<ProcessSubject> aListOfOrocessSubjectToRemove = processSubjectDao.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
                LOG.info("aListOfOrocessSubjectToRemove={}", aListOfOrocessSubjectToRemove);
                
                for (ProcessSubject oProcessSubject : aListOfOrocessSubjectToRemove) {                                       
                    removeProcessSubjectDeep(oProcessSubject);                                           
                }
            }
            LOG.info("Setting a status complete.");
            
        } else {
        
            throw  new RuntimeException("Login=" + sLoginMain + " has no access to change a status.");
        }
        
        return oProcessSubjectMain;
    }

    public ProcessSubject syncProcessSubject(String snID_Process_Activiti, String snID_Task_Activiti, String sLogin) {
        
        ProcessSubject oProcessSubject = processSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLogin);
        
        oProcessSubject.setSnID_Task_Activiti(snID_Task_Activiti);
        
        processSubjectDao.saveOrUpdate(oProcessSubject);
        
        return oProcessSubject;
    }
}
