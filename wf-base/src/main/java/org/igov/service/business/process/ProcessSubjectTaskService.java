package org.igov.service.business.process;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectStatusDao;

import org.igov.model.process.ProcessSubjectTask;
import org.igov.model.process.ProcessSubjectTaskDao;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessSubjectTreeDao;
import org.igov.service.business.document.DocumentStepService;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author idenysenko
 */

@Service
@Component("processSubjectTaskService")
public class ProcessSubjectTaskService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProcessSubjectTaskService.class);
    
    @Autowired
    private ProcessSubjectTaskDao oProcessSubjectTaskDao;
    
    @Autowired
    private ProcessSubjectDao oProcessSubjectDao;
    
    @Autowired
    private ProcessSubjectService oProcessSubjectService;
    
    @Autowired
    private ProcessSubjectTreeDao oProcessSubjectTreeDao;
    
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    
    @Autowired
    private TaskService oTaskService;
    
    @Autowired
    private RuntimeService oRuntimeService;

    @Autowired
    private GeneralConfig oGeneralConfig;
    
    @Autowired
    private DocumentStepService oDocumentStepService;
    
    @Autowired
    private ProcessSubjectStatusDao oProcessSubjectStatusDao;
    
    public List<String> getProcessSubjectLoginsWithoutTask(String snID_Process_Activiti, String sFilterLoginRole) throws RecordInmemoryException, org.json.simple.parser.ParseException{
        
        LOG.info("getProcessSubjectLoginsWithoutTask started...");
        LOG.info("snID_Process_Activiti {}", snID_Process_Activiti);
        LOG.info("sFilterLoginRole {}", sFilterLoginRole);
        
        String sKeyRedis = (String)oRuntimeService.getVariable(snID_Process_Activiti, "sID_File_StorateTemp");
        
        byte[] aByteTaskBody = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
        
        List<Task> aTaskActive = oTaskService.createTaskQuery()
                .processInstanceId(snID_Process_Activiti).active().list();
        
        List<String> aUserAssignee = new ArrayList<>();
        
        for(Task oTask : aTaskActive){
            aUserAssignee.add(oTask.getAssignee());
        }
        
        JSONParser parser = new JSONParser();
        
        List<String> aResultLogins = new ArrayList<>();
        
        if(aByteTaskBody != null){
            JSONArray aJsonProcessSubject =  
                    (JSONArray) ((JSONObject)parser.parse(new String(aByteTaskBody))).get("aProcessSubject");
            
            LOG.info("aJsonProcessSubject in getProcessSubjectLoginsWithoutTask: {}", 
                    aJsonProcessSubject.toJSONString());
            
            for (Object oJsonProcessSubject : aJsonProcessSubject) {
                
                String sLogin = (String)((JSONObject)oJsonProcessSubject).get("sLogin");
                
                if(!aUserAssignee.contains(sLogin))
                {
                    if(sFilterLoginRole != null && !sFilterLoginRole.equals(""))
                    {
                        if(sFilterLoginRole.equals((String) ((JSONObject)oJsonProcessSubject).get("sLoginRole"))){
                            aResultLogins.add(sLogin);
                        }
                    }else{
                        aResultLogins.add(sLogin);
                    }
                }
            }
        }
        LOG.info("aResultLogins in setProcessSubjectList {}", aResultLogins);
        return aResultLogins;
    }
    
    public void synctProcessSubjectTask(JSONArray oaProcessSubjectTask, String snId_Task){
        try{
            //LOG.info("aJsonProcessSubjectTask in synctProcessSubjectTask: {}", oaProcessSubjectTask.toJSONString());
            for(Object oJsonProcessSubjectTask :  oaProcessSubjectTask){
                
                String sActionType = (String)((JSONObject)oJsonProcessSubjectTask).get("sActionType");
                JSONArray aJsonProcessSubject =  (JSONArray) ((JSONObject)oJsonProcessSubjectTask).get("aProcessSubject");
                //LOG.info("oJsonProcessSubjectTask in oJsonProcessSubjectTask: {}", oJsonProcessSubjectTask);
                String sKey = oBytesDataInmemoryStorage.putBytes(((JSONObject)oJsonProcessSubjectTask).toJSONString().getBytes());
                LOG.info("Redis key in synctProcessSubjectTask: {}", sKey);

                if(sActionType.equals("set")){
                    setProcessSubjectTask(oJsonProcessSubjectTask, aJsonProcessSubject, sKey);
                }else if (sActionType.equals("edit")){
                    editProcessSubject(oJsonProcessSubjectTask, aJsonProcessSubject, sKey, snId_Task);
                }else if (sActionType.equals("delegate")){
                    LOG.info("delegating started...");

                    ProcessSubject oProcessSubjectController = getProcessSubjectByTask(snId_Task);
                    LOG.info("oProcessSubjectController is {}", oProcessSubjectController);
                    
                    List<ProcessSubjectTree> aProcessSubjectTree =
                            oProcessSubjectTreeDao.findChildren(oProcessSubjectController.getSnID_Process_Activiti());
                    
                    LOG.info("aProcessSubjectTree is {}", aProcessSubjectTree);
                    
                    if(aProcessSubjectTree.isEmpty()){
                        firstDelegateProcessSubject(oProcessSubjectController, oJsonProcessSubjectTask, 
                                aJsonProcessSubject, sKey);
                    }
                    else{
                        LOG.info("second delegating started...");
                        //this isn't first delegating
                        ProcessSubjectTask oProcessSubjectTask = oProcessSubjectTaskDao.findByIdExpected(
                            Long.parseLong((String)((JSONObject)oJsonProcessSubjectTask).get("snID_ProcessSubjectTask")));
                        
                        List<ProcessSubject> aProcessSubject_saved = 
                            oProcessSubjectDao.findAllBy("snID_Process_Activiti", 
                                    aProcessSubjectTree.get(0).getProcessSubjectChild().getSnID_Process_Activiti());

                        LongSummaryStatistics summaryStatistics = aProcessSubject_saved.stream()
                            .mapToLong(ProcessSubject::getnOrder)
                            .summaryStatistics();
                    
                        LOG.info("aProcessSubject_saved is {}", aProcessSubject_saved);

                        List<String> aNewLogin = new ArrayList<>();

                        for (Object oJsonProcessSubject : aJsonProcessSubject) {
                            aNewLogin.add((String)((JSONObject)oJsonProcessSubject).get("sLogin"));
                        }

                        LOG.info("aNewLogin is {}", aNewLogin);

                        List<ProcessSubject> aProcessSubject_ToUpdate = new ArrayList<>();

                        for(ProcessSubject oProcessSubject : aProcessSubject_saved){
                            if(!aNewLogin.contains(oProcessSubject.getsLogin())){
                               LOG.info("Login to delete in new task setting schema is {}", oProcessSubject.getsLogin());
                               removeProcessSubjectDeep(oProcessSubject);
                            }
                            else{
                                LOG.info("Login to update in new task setting schema is {}", oProcessSubject.getsLogin());
                                aProcessSubject_ToUpdate.add(oProcessSubject);
                            }
                        }

                        /*oProcessSubjectTask.setaProcessSubject(setProcessSubjectList(aJsonProcessSubject, 
                                (JSONObject)oJsonProcessSubjectTask, oProcessSubjectTask, 
                                oProcessSubjectController.getSnID_Process_Activiti(), aProcessSubject_ToUpdate));*/

                        setProcessSubjectList(aJsonProcessSubject, 
                                (JSONObject)oJsonProcessSubjectTask, oProcessSubjectTask, 
                                oProcessSubjectController.getSnID_Process_Activiti(), aProcessSubject_ToUpdate, summaryStatistics.getMax() + 1);

                        oProcessSubjectTask.setSnID_Process_Activiti_Root((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"));
                        oProcessSubjectTask.setsBody((String)((JSONObject)oJsonProcessSubjectTask).get("sBody"));
                        oProcessSubjectTask.setsHead((String)((JSONObject)oJsonProcessSubjectTask).get("sHead"));

                        oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);

                        oRuntimeService.setVariable(oProcessSubjectController.getSnID_Process_Activiti(), 
                                "sID_File_StorateTemp", sKey);
                    }
                }
                else{
                    throw new RuntimeException("There is wrong sActionType");
                }
            }
        }
        catch (Exception ex){
            throw new RuntimeException("Error task setting: " + ex.getMessage());
        }
    }
    
    private void editProcessSubject(Object oJsonProcessSubjectTask, JSONArray aJsonProcessSubject, String sKey, String snId_Task) throws Exception {
            
                    LOG.info("editing started....");
                    
                    ProcessSubjectTask oProcessSubjectTask = oProcessSubjectTaskDao.findByIdExpected(
                            Long.parseLong((String)((JSONObject)oJsonProcessSubjectTask).get("snID_ProcessSubjectTask")));
                       
                    ProcessSubject oProcessSubjectController = getProcessSubjectByTask(snId_Task);
                    
                    List<ProcessSubject> aProcessSubject_saved = 
                            oProcessSubjectDao.findAllBy("snID_Process_Activiti", oProcessSubjectController.getSnID_Process_Activiti());
                    
                    LongSummaryStatistics summaryStatistics = aProcessSubject_saved.stream()
                            .mapToLong(ProcessSubject::getnOrder)
                            .summaryStatistics();
                    
                    LOG.info("aProcessSubject_saved is {}", aProcessSubject_saved);
                    
                    List<String> aNewLogin = new ArrayList<>();
                    
                    for (Object oJsonProcessSubject : aJsonProcessSubject) {
                        aNewLogin.add((String)((JSONObject)oJsonProcessSubject).get("sLogin"));
                    }
                    
                    LOG.info("aNewLogin is {}", aNewLogin);
                    
                    List<ProcessSubject> aProcessSubject_ToUpdate = new ArrayList<>();
                    
                    for(ProcessSubject oProcessSubject : aProcessSubject_saved){
                        if(!aNewLogin.contains(oProcessSubject.getsLogin())){
                           LOG.info("Login to delete in new task setting schema is {}", oProcessSubject.getsLogin());
                           removeProcessSubjectDeep(oProcessSubject);
                        }
                        else{
                            LOG.info("Login to update in new task setting schema is {}", oProcessSubject.getsLogin());
                            aProcessSubject_ToUpdate.add(oProcessSubject);
                        }
                    }
                    
                    /*oProcessSubjectTask.setaProcessSubject(setProcessSubjectList(aJsonProcessSubject, 
                            (JSONObject)oJsonProcessSubjectTask, oProcessSubjectTask, 
                            oProcessSubjectController.getSnID_Process_Activiti(), aProcessSubject_ToUpdate));*/
                    
                    setProcessSubjectList(aJsonProcessSubject, 
                            (JSONObject)oJsonProcessSubjectTask, oProcessSubjectTask, 
                            oProcessSubjectController.getSnID_Process_Activiti(), aProcessSubject_ToUpdate, summaryStatistics.getMax() + 1);
                    
                    oProcessSubjectTask.setSnID_Process_Activiti_Root((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"));
                    oProcessSubjectTask.setsBody((String)((JSONObject)oJsonProcessSubjectTask).get("sBody"));
                    oProcessSubjectTask.setsHead((String)((JSONObject)oJsonProcessSubjectTask).get("sHead"));

                    oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);
                    
                    oRuntimeService.setVariable(oProcessSubjectController.getSnID_Process_Activiti(), 
                            "sID_File_StorateTemp", sKey);
                    
    }
    
    private void setProcessSubjectTask(Object oJsonProcessSubjectTask, JSONArray aJsonProcessSubject, String sKey) throws Exception {
        
        ProcessSubjectTask oProcessSubjectTask = new ProcessSubjectTask();
        oProcessSubjectTask.setSnID_Process_Activiti_Root((String) ((JSONObject) oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"));
        oProcessSubjectTask.setsBody((String) ((JSONObject) oJsonProcessSubjectTask).get("sBody"));
        oProcessSubjectTask.setsHead((String) ((JSONObject) oJsonProcessSubjectTask).get("sHead"));
        oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);
        LOG.info("oProcessSubjectTask in synctProcessSubjectTask: {}", oProcessSubjectTask);
        Map<String, Object> mParamTask = new HashMap<>();

        mParamTask.put("sID_File_StorateTemp", sKey);
        mParamTask.put("sID_Order_Document", oGeneralConfig.
                getOrderId_ByProcess((String) ((JSONObject) oJsonProcessSubjectTask).get("snID_Process_Activiti_Root")));

        ProcessInstance oProcessInstance = oRuntimeService.startProcessInstanceByKey((String) ((JSONObject) oJsonProcessSubjectTask).get("sID_BP"), mParamTask);

        List<ProcessSubject> aProcessSubject
                = setProcessSubjectList(aJsonProcessSubject, 
                        (JSONObject) oJsonProcessSubjectTask, oProcessSubjectTask, oProcessInstance.getId(), null, 0L);
        LOG.info("aProcessSubject in synctProcessSubjectTask: {}", aProcessSubject);
    }
    
    private void firstDelegateProcessSubject(ProcessSubject oProcessSubjectController, Object oJsonProcessSubjectTask, 
            JSONArray aJsonProcessSubject, String sKey) throws Exception {
        LOG.info("this is first delegating");
        ProcessSubjectTask oProcessSubjectTask = oProcessSubjectTaskDao.findByIdExpected(
                Long.parseLong((String) ((JSONObject) oJsonProcessSubjectTask).get("snID_ProcessSubjectTask")));

        LOG.info("oProcessSubjectTask is {}", oProcessSubjectTask);

        Map<String, Object> mParamTask = new HashMap<>();

        mParamTask.put("sID_File_StorateTemp", sKey);
        mParamTask.put("sID_Order_Document", oGeneralConfig.
                getOrderId_ByProcess((String) ((JSONObject) oJsonProcessSubjectTask).get("snID_Process_Activiti_Root")));

        ProcessInstance oProcessInstance = oRuntimeService.startProcessInstanceByKey((String) ((JSONObject) oJsonProcessSubjectTask).get("sID_BP"), mParamTask);
        LOG.info("oProcessSubjectTask is {}", oProcessSubjectTask);
        
        List<ProcessSubject> aProcessSubject
                = setProcessSubjectList(aJsonProcessSubject, (JSONObject) oJsonProcessSubjectTask, 
                        oProcessSubjectTask, oProcessInstance.getId(), null, 0L);
        LOG.info("aProcessSubject is {}", aProcessSubject);
        
        for (ProcessSubject oProcessSubject : aProcessSubject) {
            LOG.info("oProcessSubject is {}", aProcessSubject);
            saveProcessSubjectTree(oProcessSubjectController, oProcessSubject);
        }
    }
    
    private List<ProcessSubject> setProcessSubjectList(JSONArray aJsonProcessSubject, JSONObject oJsonProcessSubjectTask,
            ProcessSubjectTask oProcessSubjectTask, String snID_Process_Activiti, 
            List<ProcessSubject> aProcessSubject_ToUpdate, Long nStartOrder) throws ParseException, Exception 
    {
        LOG.info("setProcessSubjectList started..");
        LOG.info("nStartOrder is {}", nStartOrder);
        ProcessSubjectStatus oProcessSubjectStatus = oProcessSubjectStatusDao.findByIdExpected(1L);
        
        List<ProcessSubject> aProcessSubject = new ArrayList<>();
        
        Long nOrder = nStartOrder;
        
        for (Object oJsonProcessSubject : aJsonProcessSubject) {
            
            ProcessSubject oProcessSubject = null;
            
            if(aProcessSubject_ToUpdate != null){
                //update existing entity;
                for(ProcessSubject oProcessSubject_ToUpdate : aProcessSubject_ToUpdate){
                    if(oProcessSubject_ToUpdate.getsLogin().equals((String) ((JSONObject)oJsonProcessSubject).get("sLogin"))){
                        oProcessSubject = oProcessSubject_ToUpdate;
                        LOG.info("oProcessSubject to update is {}", oProcessSubject);
                        if (((JSONObject)oJsonProcessSubject).get("sDatePlan") != null) {
                            DateTime datePlan = new DateTime(oProcessSubjectService.parseDate(
                                    (String) ((JSONObject)oJsonProcessSubject).get("sDatePlan")));
                            oProcessSubject_ToUpdate.setsDatePlan(datePlan);
                        }
                        break;
                    }
                }
            }    
            
            if(oProcessSubject == null){
                //this is new entity;
                oProcessSubject = new ProcessSubject();
            }
            
            LOG.info("oJsonProcessSubject in setProcessSubjectList: {}", ((JSONObject)oJsonProcessSubject).toJSONString());
            
            oProcessSubject.setsTextType((String) ((JSONObject)oJsonProcessSubjectTask).get("sTextType"));
            oProcessSubject.setsLogin((String) ((JSONObject)oJsonProcessSubject).get("sLogin"));
            oProcessSubject.setsLoginRole((String) ((JSONObject)oJsonProcessSubject).get("sLoginRole"));
//            oProcessSubject.setoProcessSubjectTask(oProcessSubjectTask);
            oProcessSubject.setoProcessSubjectStatus(oProcessSubjectStatus);
            oProcessSubject.setsDateEdit(new DateTime(new Date()));
            oProcessSubject.setnOrder(nOrder);
            
            nOrder = nOrder + 1L;
            oProcessSubject.setSnID_Process_Activiti(snID_Process_Activiti);                                                                                   
            DateTime datePlan = null;

            if (((JSONObject)oJsonProcessSubject).get("sDatePlan") != null) {
                datePlan = new DateTime(oProcessSubjectService.parseDate(
                        (String) ((JSONObject)oJsonProcessSubject).get("sDatePlan")));
            }

            oProcessSubject.setsDatePlan(datePlan);
            aProcessSubject.add(oProcessSubject);
            LOG.info("oProcessSubject in setProcessSubjectList: {}", oProcessSubject);
            
            /*if((JSONObject)oJsonProcessSubjectTask.get("sKey_GroupPostfix") != null){
                oDocumentStepService.cloneDocumentStepSubject((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"), 
                    (String)((JSONObject)oJsonProcessSubjectTask).get("sKey_GroupPostfix"), (String) ((JSONObject)oJsonProcessSubject).get("sLogin"), "_", false);
            }*/
        }

        return oProcessSubjectDao.saveOrUpdate(aProcessSubject);
    }
    
    public void removeProcessSubject(ProcessSubject processSubject, boolean closeTaskFlag) {
        LOG.info("removeProcessSubject started...");
        
        if (closeTaskFlag) {
            if (processSubject.getSnID_Task_Activiti() != null) {
                LOG.info("TaskInstance to remove is {}", processSubject.getSnID_Task_Activiti());
                oTaskService.complete(processSubject.getSnID_Task_Activiti());
            }
        } else {
            LOG.info("ProcessInstance is to delete {}", processSubject.getSnID_Process_Activiti());
            ProcessInstance processInstance = oRuntimeService.createProcessInstanceQuery().processInstanceId(processSubject.getSnID_Process_Activiti()).singleResult();
            if (processInstance != null) {
                oRuntimeService.deleteProcessInstance(processSubject.getSnID_Process_Activiti(), "deleted");
            }
        }
        
        LOG.info("TaskInstance deleted..");
        Optional<ProcessSubjectTree> processSubjectTreeToDelete = oProcessSubjectTreeDao.findBy("processSubjectChild", processSubject);
        
        if(processSubjectTreeToDelete.isPresent()){
            LOG.info("processSubjectTreeToDelete {}", processSubjectTreeToDelete.get());
            oProcessSubjectTreeDao.delete(processSubjectTreeToDelete.get());
        }
        else{
            LOG.info("processSubjectTree is null");
        }
        
        LOG.info("deleted processSubject Id is {}", processSubject.getId());
        oProcessSubjectDao.delete(processSubject);
        LOG.info("removeProcessSubject ended...");
    }

    public void removeProcessSubjectDeep(ProcessSubject processSubject) {
        LOG.info("removeProcessSubjectDeep started...");
        ProcessSubjectResult processSubjectResult = oProcessSubjectService.getCatalogProcessSubject(processSubject.getSnID_Process_Activiti(), 0L, null);
        
        LOG.info("processSubjectResult {}", processSubjectResult.getaProcessSubject());
        List<ProcessSubject> aProcessSubject = processSubjectResult.getaProcessSubject();
        List<ProcessSubject> aReverseProcessSubject = Lists.reverse(aProcessSubject);
        
        for (ProcessSubject oProcessSubject : aReverseProcessSubject) {
            LOG.info("oProcessSubject catalog user is {}", oProcessSubject.getaUser());
            LOG.info("processSubject id to delete {}", oProcessSubject.getId());
            removeProcessSubject(oProcessSubject, false);
        }

        removeProcessSubject(processSubject, true);
        LOG.info("removeProcessSubjectDeep ended...");
    }
    
    /**
     * Получение ProcessSubject 
     * 
     * @param snID_Process_Activiti
     */
    private ProcessSubject getProcessSubjectByTask(String snID_Task_Activiti){
        return oProcessSubjectDao.findByExpected("snID_Task_Activiti", snID_Task_Activiti);
    }
    
    
    /**
     * Получение списка ProcessSubjectTask
     * 
     * @param snID_Process_Activiti
     * @return aListOfProcessSubjectTask
     */
    public List<ProcessSubjectTask> getProcessSubjectTask(final String snID_Process_Activiti) {
        
        List<ProcessSubjectTask> aListOfProcessSubjectTask  = oProcessSubjectTaskDao.findAllBy("snID_Process_Activiti_Root", snID_Process_Activiti);
        LOG.info("aListOfProcessSubjectTask={}", aListOfProcessSubjectTask);
        
        return aListOfProcessSubjectTask;
    }
    
    private void saveProcessSubjectTree(ProcessSubject oProcessSubjectParent, ProcessSubject oProcessSubjectChild){
        ProcessSubjectTree oProcessSubjectTreeParent = new ProcessSubjectTree();
        oProcessSubjectTreeParent.setProcessSubjectParent(oProcessSubjectParent);
        oProcessSubjectTreeParent.setProcessSubjectChild(oProcessSubjectChild);
        LOG.info("oProcessSubjectTreeParent is {}", oProcessSubjectTreeParent);
        oProcessSubjectTreeDao.saveOrUpdate(oProcessSubjectTreeParent);
    }
}
