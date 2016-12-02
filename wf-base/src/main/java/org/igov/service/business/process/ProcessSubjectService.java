/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubjectParentNode;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessUser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectStatusDao;
import org.igov.model.process.ProcessSubjectTreeDao;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        for(ProcessSubject processSubject : processSubjectResult.getaProcessSubject()){
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
                        anID_ChildLevel_Result = Lists.newArrayList(
                                Collections2.transform(aChildLevel_Result, new Function<ProcessSubject, Long>() {
                                    @Override
                                    public Long apply(ProcessSubject subjectGroup) {
                                        return subjectGroup.getId();
                                    }
                                }));
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
     * @param nID_ProcessSubjectStatus
     * @return
     */
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, Long nID_ProcessSubjectStatus) {
        ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(nID_ProcessSubjectStatus);
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

    public void setProcessSubjects(String sTaskProcessDefinition, String sID_Attachment,
            String sContent, String sAutorResolution, String sTextResolution, 
            String sDateExecution, String snProcess_ID) {

        try {
            
            ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(1L);
            DateFormat df = new SimpleDateFormat("d.M.yyyy");
            ProcessSubject oProcessSubjectParent = null;

            //проверяем нет ли в базе такого объекта, если нет создаем, если есть - не создаем
            if (processSubjectDao.findByProcessActivitiId(snProcess_ID) == null){
                oProcessSubjectParent = processSubjectDao
                        .setProcessSubject(snProcess_ID, sAutorResolution,
                                new DateTime(df.parse(sDateExecution)), 0L, processSubjectStatus);
            
                LOG.info("SnID_Process_Activiti TEST:" + oProcessSubjectParent.getSnID_Process_Activiti());
            }else{
                oProcessSubjectParent = processSubjectDao.findByProcessActivitiId(snProcess_ID);
                LOG.info("SnID_Process_Activiti TEST:" + oProcessSubjectParent.getSnID_Process_Activiti());
            }
            
            List<ProcessSubjectTree> aProcessSubjectChild = processSubjectTreeDao.findChildren(oProcessSubjectParent.getSnID_Process_Activiti()); // Find all children for document
            
            ///Delete after testing--------
            if (aProcessSubjectChild != null){ 
                
                if (aProcessSubjectChild.isEmpty())
                {
                    LOG.info("aProcessSubjectChild is Empry");
                }
                
                int testCounter = 0;
                for (ProcessSubjectTree testChild : aProcessSubjectChild)
                {
                    testCounter++;
                    LOG.info("test child login number " + testCounter + " :" + testChild.getProcessSubjectChild().getsLogin() +
                             "test child ID: number " + testCounter + " :" + testChild.getProcessSubjectChild().getSnID_Process_Activiti());
                }
            }else{
                LOG.info("ProcessSubjectTree list is null");
            }///--------///
            
            LOG.info("SetTasks listener data: sTaskProcessDefinition_Value: "
                    + sTaskProcessDefinition + " sID_Attachment_Value: " + sID_Attachment + " sContent: "
                    + sContent + " sAutorResolution: " + sAutorResolution + " sTextResolution: "
                    + sTextResolution + " sDateExecution: " + sDateExecution);

            InputStream attachmentContent = taskService.getAttachmentContent(sID_Attachment);

            JSONParser parser = new JSONParser();
            JSONObject oJSONObject = (JSONObject) parser.parse(IOUtils.toString(attachmentContent, "UTF-8"));   // (JSONObject) new JSONParser().parse(IOUtils.toString(attachmentContent));
            LOG.info("JSON String: " + oJSONObject.toJSONString());

            LOG.info("JSON aRow is: " + oJSONObject.get("aRow").getClass());

            JSONArray aJsonRow = (JSONArray) oJSONObject.get("aRow");
            Map<String, Object> mParamDocument = new HashMap<>();
            mParamDocument.put("sTaskProcessDefinition", sTaskProcessDefinition);
            mParamDocument.put("sID_Attachment", sID_Attachment);
            mParamDocument.put("sContent", sContent);
            mParamDocument.put("sAutorResolution", sAutorResolution);
            mParamDocument.put("sDateExecution", sDateExecution);
            mParamDocument.put("sTextResolution", sTextResolution);

            if (aJsonRow != null) {
                for (int i = 0; i < aJsonRow.size(); i++) {

                    Map<String, Object> mParamTask = new HashMap<>();

                    LOG.info("json array element" + i + " is " + aJsonRow.get(i).toString());

                    JSONObject sJsonField = (JSONObject) aJsonRow.get(i);
                    JSONArray aJsonField = (JSONArray) sJsonField.get("aField");
                    mParamTask.putAll(mParamDocument);
                    for (int j = 0; j < aJsonField.size(); j++) {
                        JSONObject sJsonElem = (JSONObject) aJsonField.get(j);
                        String id = sJsonElem.get("id").toString();
                        String value = sJsonElem.get("value").toString();
                        mParamTask.put(id, value);
                    }
                    LOG.info("mParamTask: " + mParamTask); //логируем всю мапу
                    
                    boolean continueFlag = false;
                    
                    for (ProcessSubjectTree child:  aProcessSubjectChild)    
                    {
                        if (child.getProcessSubjectChild().getsLogin().equals(mParamTask.get("sLogin_isExecute").toString())){
                            continueFlag = true;
                            break;
                        }
                    }
                    
                    if (continueFlag == false)
                    {
                        ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey("system_task", mParamTask);
                        LOG.info("oProcessInstanceChild id: " + (oProcessInstanceChild != null ? oProcessInstanceChild.getId() : " oInstanse is null"));
                        if (oProcessInstanceChild != null) {
                            ProcessSubject oProcessSubjectChild = processSubjectDao
                                    .setProcessSubject(oProcessInstanceChild.getId(), (String) mParamTask.get("sLogin_isExecute"),
                                            new DateTime(df.parse(sDateExecution)), new Long(i + 1), processSubjectStatus);
                            ProcessSubjectTree oProcessSubjectTreeParent = new ProcessSubjectTree();
                            oProcessSubjectTreeParent.setProcessSubjectParent(oProcessSubjectParent);
                            oProcessSubjectTreeParent.setProcessSubjectChild(oProcessSubjectChild);
                            processSubjectTreeDao.saveOrUpdate(oProcessSubjectTreeParent);
                        }
                    }
                }
            } else {
                LOG.info("JSONArray is null");
            }
        } 
        catch (java.text.ParseException | IOException | ParseException e) {
            LOG.error("SetTasks listener throws an error: " + e.toString());
        }
    }
}
