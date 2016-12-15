/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.process;
import java.text.ParseException;
import java.io.InputStream;
import java.text.DateFormat;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubjectParentNode;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.model.process.ProcessSubjectResultTree;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectStatusDao;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessSubjectTreeDao;
import org.igov.model.process.ProcessUser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author inna
 */
@Service
public class ProcessSubjectService {

    private static final Log LOG = LogFactory.getLog(ProcessSubjectService.class);
    private static final long FAKE_ROOT_PROCESS_ID = 0;

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    private IdentityService identityService;

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
    
  //Мапа для укладывания ид родителя и его детей в методе получения иерархии  getChildrenTree
    Map<Long, List<ProcessSubject>> getChildrenTreeRes = new HashMap<>();

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
     * Сервис получения полной иерархии (родитель - ребенок)
     * @param snID_Process_Activiti - ид процесса
     * @param deepLevel - глубина выборки
     * @param sFind - текст поиска (искать в ФИО, по наличию вхождения текста в ФИО)
     * @return
     */
    public ProcessSubjectResultTree getCatalogProcessSubjectTree(String snID_Process_Activiti, Long deepLevel, String sFind) {
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
                    // мапа парент_id -ребенок
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
                            // мапа парент_id -ребенок
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
        Map<Long, List<ProcessSubject>> hierarchyProcessSubject = new HashMap<>();
        // children полный список первого уровня
        if (children != null && !children.isEmpty()) {
        	final List<Long> idChildren = Lists
                    .newArrayList(Collections2.transform(children, new Function<ProcessSubject, Long>() {
                        @Override
                        public Long apply(ProcessSubject subjectGroup) {
                            return subjectGroup.getId();
                        }
                    }));
            aChildResult.addAll(children);
            
           hierarchyProcessSubject =  getChildrenTree(children, idChildren, subjToNodeMap, idParentList, checkDeepLevel(deepLevel), 1, aChildResult);
           LOG.info("subjToNodeMap " + subjToNodeMap);
           LOG.info("hierarchyProcessSubject " + hierarchyProcessSubject);
           LOG.info("aChildResult " + aChildResult);
        }

        List<ProcessSubject> aChildResultByUser = new ArrayList<>();
		if (aChildResult != null && !aChildResult.isEmpty()) {
			if (sFind != null && !sFind.isEmpty()) {
					for (ProcessSubject processSubject : aChildResult) {
						List<ProcessUser> aSubjectUser = getUsersByGroupSubject(
								processSubject.getSnID_Process_Activiti());
						final List<ProcessUser> processUserFiltr = Lists
								.newArrayList(Collections2.filter(aSubjectUser, new Predicate<ProcessUser>() {
									@Override
									public boolean apply(ProcessUser processUser) {
										// получить только отфильтрованный
										// список по
										// sFind в фио
										return processUser.getsFirstName().toLowerCase().contains(sFind.toLowerCase());
									}
								}));
						// получаем только их логины
						final List<String> sFindLogin = Lists.newArrayList(
								Collections2.transform(processUserFiltr, new Function<ProcessUser, String>() {
									@Override
									public String apply(ProcessUser processUser) {
										return processUser.getsLogin();
									}
								}));

						// и оставляем только processSubject чьи логины
						// содержаться
						// в отфильтрованном списке
						if (sFindLogin.contains(processSubject.getsLogin())) {
							aChildResultByUser.add(processSubject);

					}
				}
			}
		}

        ProcessSubjectResultTree processSubjectResultTree = new ProcessSubjectResultTree();
        if (sFind != null && !sFind.isEmpty()) {
        	processSubjectResultTree.setaProcessSubject(aChildResultByUser);
        } else {
        	processSubjectResultTree.setaProcessSubject(aChildResult);
        }
        for (ProcessSubject processSubject : processSubjectResultTree.getaProcessSubject()) {
            processSubject.setaUser(getUsersByGroupSubject(processSubject.getsLogin()));
            //получаем по ключу лист детей и устанавливаем 
            List<ProcessSubject> aChildResultByKey = hierarchyProcessSubject.get(processSubject.getId());
            if (aChildResultByKey != null && !aChildResultByKey.isEmpty()) {
				processSubject.setaProcessSubj(aChildResultByKey);
			}
        }
        return processSubjectResultTree;

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
    
    public Map<Long, List<ProcessSubject>> getChildrenTree(List<ProcessSubject> aChildLevel, List<Long> anID_ChildLevel,
            Map<Long, List<ProcessSubject>> subjToNodeMap, Set<Long> anID_PerentAll, Long deepLevelRequested,
            int deepLevelFact, List<ProcessSubject> result) {
        List<ProcessSubject> aChildLevel_Result = new ArrayList<>();
        List<Long> anID_ChildLevel_Result = new ArrayList<>();
        if (deepLevelFact < deepLevelRequested.intValue()) {
            for (Long nID_ChildLevel : anID_ChildLevel) {
                if (anID_PerentAll.contains(nID_ChildLevel)) {
                    // достаем детей детей
                    aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
                    if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
                        // получаем только ид чилдренов
                        List<Long> anID_Child = Lists.newArrayList(
                                Collections2.transform(aChildLevel_Result, new Function<ProcessSubject, Long>() {
                                    @Override
                                    public Long apply(ProcessSubject subjectGroup) {
                                        return subjectGroup.getId();
                                    }
                                }));
                        //если anID_ChildLevel больше 1, то всех ид складываем в лист
                        anID_ChildLevel_Result.addAll(anID_Child);
                        // добавляем детей к общему списку детей
                        result.addAll(aChildLevel_Result);
                        getChildrenTreeRes.put(nID_ChildLevel, aChildLevel_Result);
                    }
                }
            }
            deepLevelFact++;
            if (deepLevelFact < deepLevelRequested.intValue()) {
            	getChildrenTree(aChildLevel_Result,anID_ChildLevel_Result, subjToNodeMap, anID_PerentAll,
                        checkDeepLevel(deepLevelRequested), deepLevelFact, result);
            }
        }
        return getChildrenTreeRes;
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
        
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processSubject.getSnID_Process_Activiti()).singleResult();
        
        if(processInstance != null){
            runtimeService.deleteProcessInstance(processSubject.getSnID_Process_Activiti(), "deleted");
        }
        
        ProcessSubjectTree processSubjectTreeToDelete = processSubjectTreeDao.findByExpected("processSubjectChild", processSubject);
        processSubjectTreeDao.delete(processSubjectTreeToDelete);
        processSubjectDao.delete(processSubject);
    }

    public void removeProcessSubjectDeep(ProcessSubject processSubject) {
        ProcessSubjectResult processSubjectResult = getCatalogProcessSubject(processSubject.getSnID_Process_Activiti(), 0L, null);
        List<ProcessSubject> aProcessSubject = processSubjectResult.getaProcessSubject();
        List<ProcessSubject> aReverseProcessSubject = Lists.reverse(aProcessSubject);

        for (ProcessSubject oProcessSubject : aReverseProcessSubject) {
            removeProcessSubject(oProcessSubject);
        }

        removeProcessSubject(processSubject);
    }
    
 public void editProcessSubject(ProcessSubject processSubject, Map<String, Object> mParamDocument) throws ParseException{
        
        ProcessSubjectResult processSubjectResult = getCatalogProcessSubject(processSubject.getSnID_Process_Activiti(), 0L, null);
        
        if(processSubjectResult != null){
            List<ProcessSubject> aProcessSubject_Child = processSubjectResult.getaProcessSubject();
            
            if(!aProcessSubject_Child.isEmpty()){
                ProcessSubject oProcessSubject_Child = aProcessSubject_Child.get(0);

                ProcessInstance oProcessInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(oProcessSubject_Child.getSnID_Process_Activiti())
                    .includeProcessVariables()
                    .active()
                    .singleResult();

                Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
                LOG.info("mProcessVariable: " + mProcessVariable);

                Map<String, Object> mParamDocumentNew = new HashMap<>();

                for(String mKey : mParamDocument.keySet()){

                    Object oParamDocument = mParamDocument.get(mKey);
                    Object oProcessVariable = mProcessVariable.get(mKey);

                    if(oParamDocument != null){
                        if(oProcessVariable != null){
                            if(!(((String)oParamDocument).equals((String)oProcessVariable))){
                                mParamDocumentNew.put(mKey, oParamDocument);
                                LOG.info("--------------------------");
                                LOG.info("mParamDocument elem new: " + oParamDocument);
                                LOG.info("mProcessVariable elem: " + oProcessVariable);
                                LOG.info("--------------------------");
                             }
                        }
                        else{
                             mParamDocumentNew.put(mKey, null);
                        }
                    }else{
                        if(oProcessVariable != null){
                            mParamDocumentNew.put(mKey, oProcessVariable);
                        }
                    }
                }

                LOG.info("mParamDocumentNew: " + mParamDocumentNew);
                DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");

                if(!mParamDocumentNew.isEmpty()){

                    for(ProcessSubject oProcessSubject : aProcessSubject_Child){
                        oProcessSubject.setsDateEdit(new DateTime(df_StartProcess.parse(df_StartProcess.format(new Date()))));

                        DateTime datePlan = null;
                        if (mParamDocument.get("sDateExecution") != null){
                            datePlan = new DateTime(parseDate((String)mParamDocument.get("sDateExecution")));
                        }

                        oProcessSubject.setsDatePlan(datePlan);
                        processSubjectDao.saveOrUpdate(oProcessSubject);

                        for(String mKey : mParamDocumentNew.keySet()){
                            runtimeService.setVariable(oProcessSubject.getSnID_Process_Activiti(), mKey, mParamDocumentNew.get(mKey));
                        }
                    }
                }
            }
        }
    }

    /*public void setProcessSubjects(String sTaskProcessDefinition, String sID_Attachment,
    String sContent, String sAutorResolution, String sTextResolution,
    String sDateExecution, String snProcess_ID) {*/
    public void setProcessSubjects(Map<String, String> mParam, String snProcess_ID){

    try {
        ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(1L);
        DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");

        String sFormatDateExecution = "";
        String sFormatDateRegistration = "";
        String sFormatDateDoc = "";
        Date oDateExecution = null;

        /*if (mParam.get("sDateExecution") != null){
            oDateExecution = parseDate(mParam.get("sDateExecution"));
            sFormatDateExecution = df_StartProcess.format(oDateExecution);
        }
        if(mParam.get("sDateRegistration") != null ){
            Date oDateRegistration = parseDate(mParam.get("sDateRegistration"));
            sFormatDateRegistration = df_StartProcess.format(oDateRegistration);
        }

        if(mParam.get("sDateDoc") != null){
            Date oDateDoc = parseDate(mParam.get("sDateDoc"));
            sFormatDateDoc = df_StartProcess.format(oDateDoc);
        }*/

        if((mParam.get("sDateExecution") != null)&&(!mParam.get("sDateExecution").equals(""))){
            oDateExecution = parseDate(mParam.get("sDateExecution"));
            sFormatDateExecution = df_StartProcess.format(oDateExecution);
        }
        if((mParam.get("sDateRegistration") != null)&&(!mParam.get("sDateRegistration").equals(""))){
            Date oDateRegistration = parseDate(mParam.get("sDateRegistration"));
            sFormatDateRegistration = df_StartProcess.format(oDateRegistration);
        }
        if((mParam.get("sDateDoc") != null)&&(!mParam.get("sDateDoc").equals(""))){
            Date oDateDoc = parseDate(mParam.get("sDateDoc"));
            sFormatDateDoc = df_StartProcess.format(oDateDoc);
        }

        ProcessSubject oProcessSubjectParent = processSubjectDao.findByProcessActivitiId(snProcess_ID);

        Map<String, Object> mParamDocument = new HashMap<>();

        mParamDocument.put("sTaskProcessDefinition", mParam.get("sTaskProcessDefinition"));
        mParamDocument.put("sID_Attachment", mParam.get("sID_Attachment"));
        mParamDocument.put("sContent", mParam.get("sContent"));
        mParamDocument.put("sAutorResolution", mParam.get("sAutorResolution"));
        mParamDocument.put("sTextResolution", mParam.get("sTextResolution"));
        mParamDocument.put("sDateExecution", sFormatDateExecution);
        mParamDocument.put("sTypeDoc", mParam.get("sTypeDoc"));
        mParamDocument.put("sID_Order_GovPublic", mParam.get("sID_Order_GovPublic"));
        mParamDocument.put("sDateRegistration", sFormatDateRegistration);
        mParamDocument.put("sDateDoc", sFormatDateDoc);
        mParamDocument.put("sApplicant", mParam.get("sApplicant"));
        mParamDocument.put("nCountAttach", mParam.get("nCountAttach"));
        mParamDocument.put("sNote", mParam.get("sNote"));
        mParamDocument.put("asUrgently", mParam.get("asUrgently"));
        mParamDocument.put("asTypeResolution", mParam.get("asTypeResolution"));
        mParamDocument.put("sTextReport", mParam.get("sTextReport"));

        //проверяем нет ли в базе такого объекта, если нет создаем, если есть - не создаем
        //иначе проверяем на необходимость редактирования
        if (oProcessSubjectParent == null) {
            oProcessSubjectParent = processSubjectDao
                    .setProcessSubject(snProcess_ID, mParam.get("sAutorResolution"),
                            new DateTime(oDateExecution), 0L, processSubjectStatus);
        }else{
            editProcessSubject(oProcessSubjectParent, mParamDocument);
        }

        List<ProcessSubjectTree> aProcessSubjectTreeChild = processSubjectTreeDao.findChildren(oProcessSubjectParent.getSnID_Process_Activiti()); // Find all children for document
        InputStream attachmentContent = taskService.getAttachmentContent(mParam.get("sID_Attachment"));

        List<ProcessSubject> aProcessSubjectChild = getCatalogProcessSubject(snProcess_ID, 1L, null).getaProcessSubject();
        List<String> aProcessSubjectLoginToDelete = new ArrayList<>();

        for (ProcessSubject oProcessSubject : aProcessSubjectChild) {
            aProcessSubjectLoginToDelete.add(oProcessSubject.getsLogin());
        }

        JSONParser parser = new JSONParser();
        JSONObject oJSONObject = (JSONObject) parser.parse(IOUtils.toString(attachmentContent, "UTF-8"));   // (JSONObject) new JSONParser().parse(IOUtils.toString(attachmentContent));
        LOG.info("JSON String: " + oJSONObject.toJSONString());
        JSONArray aJsonRow = (JSONArray) oJSONObject.get("aRow");

        List<String> aProcessSubjectLoginNew = new ArrayList<>();

        if (aJsonRow != null) {
            for (int i = 0; i < aJsonRow.size(); i++) {

                Map<String, Object> mParamTask = new HashMap<>();
                JSONObject oJsonField = (JSONObject) aJsonRow.get(i);

                if(oJsonField != null){
                    JSONArray aJsonField = (JSONArray) oJsonField.get("aField");

                    if(aJsonField != null){
                        mParamTask.putAll(mParamDocument);
                        for (int j = 0; j < aJsonField.size(); j++) {
                            JSONObject oJsonMap = (JSONObject) aJsonField.get(j);
                            if(oJsonMap != null)
                            {
                                Object oId = oJsonMap.get("id");
                                Object oValue = oJsonMap.get("value");
                                if (oValue != null){
                                    mParamTask.put((String)oId, (String)oValue);

                                }else{
                                    mParamTask.put((String)oId, null);
                                }
                            }
                        }
                        LOG.info("mParamTask: " + mParamTask); //логируем всю мапу
                    }else{ 
                        continue;
                    }
                }else{
                    continue;
                }

                if (mParamTask.get("sLogin_isExecute") == null){
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
                    ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey("system_task", mParamTask);
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
                }else {
                    aProcessSubjectLoginNew.add(mParamTask.get("sLogin_isExecute").toString());
                }
            }

            if (!aProcessSubjectLoginToDelete.isEmpty()) {
                aProcessSubjectLoginToDelete.removeAll(aProcessSubjectLoginNew);

                for (ProcessSubject oProcessSubject : aProcessSubjectChild) {
                    for (String sLogin : aProcessSubjectLoginToDelete) {
                        if (oProcessSubject.getsLogin().equals(sLogin)) {

                            String sProcessSubjectStatus = oProcessSubject.getProcessSubjectStatus().getsID();

                            if(!(sProcessSubjectStatus.equals("executed")|| sProcessSubjectStatus.equals("notExecuted")||
                                sProcessSubjectStatus.equals("unactual")||sProcessSubjectStatus.equals("closed")))        
                            {
                                removeProcessSubjectDeep(oProcessSubject);
                            }
                        }
                    }
                }
            }
        }else {
            LOG.info("JSON array is null");
        }
    } catch (Exception e) {
        LOG.error("SetTasks listener throws an error: ", e);
        throw new RuntimeException(e);
    }
    }

    private Date parseDate(String sDate) throws java.text.ParseException {
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
}
