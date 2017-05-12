package org.igov.service.business.process;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    
    private List<ProcessSubject> setProcessSubjectList(JSONArray aJsonProcessSubject, 
            JSONObject oJsonProcessSubjectTask, ProcessSubjectTask oProcessSubjectTask, String snID_Process_Activiti) throws ParseException, Exception 
    {
        ProcessSubjectStatus oProcessSubjectStatus = oProcessSubjectStatusDao.findByIdExpected(1L);
        
        List<ProcessSubject> aProcessSubject = new ArrayList<>();
        
        Long nOrder = 0L;
        
        for (Object oJsonProcessSubject : aJsonProcessSubject) {
            /*Map<String, Object> mProcessSubject
                    = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);*/
            
            LOG.info("mProcessSubject in setProcessSubjectList: {}", ((JSONObject)oJsonProcessSubject).toJSONString());
            ProcessSubject oProcessSubject = new ProcessSubject();
            oProcessSubject.setsTextType((String) ((JSONObject)oJsonProcessSubjectTask).get("sTextType"));
            oProcessSubject.setsLogin((String) ((JSONObject)oJsonProcessSubject).get("sLogin"));
            oProcessSubject.setsLoginRole((String) ((JSONObject)oJsonProcessSubject).get("‘sLoginRole"));
            oProcessSubject.setoProcessSubjectTask(oProcessSubjectTask);
            oProcessSubject.setoProcessSubjectStatus(oProcessSubjectStatus);
            oProcessSubject.setsDateEdit(new DateTime(new Date()));
            oProcessSubject.setnOrder(nOrder);
            
            nOrder = nOrder + 1L;
            
            /*if(((String) mProcessSubject.get("‘sLoginRole")).equals("Controller")){
                mParamTask.put("sLoginController", mProcessSubject.get("sLogin")); //только в бд!!!
            }*/
            
            oProcessSubject.setSnID_Process_Activiti(snID_Process_Activiti);                                                                                   
            DateTime datePlan = null;

            if (((JSONObject)oJsonProcessSubject).get("sDatePlan") != null) {
                datePlan = new DateTime(oProcessSubjectService.parseDate(
                        (String) ((JSONObject)oJsonProcessSubject).get("sDatePlan")));
            }

            oProcessSubject.setsDatePlan(datePlan);
            aProcessSubject.add(oProcessSubject);
            LOG.info("oProcessSubject in setProcessSubjectList: {}", oProcessSubject);
            
            if((JSONObject)oJsonProcessSubjectTask.get("sKey_GroupPostfix") != null){
                oDocumentStepService.cloneDocumentStepSubject((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"), 
                    (String)((JSONObject)oJsonProcessSubjectTask).get("sKey_GroupPostfix"), (String) ((JSONObject)oJsonProcessSubject).get("sLogin"), "_", false);
            }
        }
        
        oProcessSubjectDao.saveOrUpdate(aProcessSubject);
        
        return aProcessSubject;
    }
    
    /*private void saveProcessSubjectTree(ProcessSubject oProcessSubjectParent, ProcessSubject oProcessSubjectChild){
        ProcessSubjectTree oProcessSubjectTreeParent = new ProcessSubjectTree();
        oProcessSubjectTreeParent.setProcessSubjectParent(oProcessSubjectParent);
        oProcessSubjectTreeParent.setProcessSubjectChild(oProcessSubjectChild);
        oProcessSubjectTreeDao.saveOrUpdate(oProcessSubjectTreeParent);
    }*/
    
    public List<String> getProcessSubjectLoginsWithoutTask(String snID_Process_Activiti, String sFilterLoginRole) throws RecordInmemoryException, org.json.simple.parser.ParseException{
        LOG.info("getProcessSubjectLoginsWithoutTask started...");
        LOG.info("snID_Process_Activiti {}", snID_Process_Activiti);
        LOG.info("sFilterLoginRole {}", sFilterLoginRole);
        String sKeyRedis = (String)oRuntimeService.getVariable(snID_Process_Activiti, "sID_File_StorateTemp");
        byte[] aByteTaskBody = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
        List<Task> aTaskActive = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active().list();
        List<String> aUserAssignee = new ArrayList<>();
        
        for(Task oTask : aTaskActive){
            aUserAssignee.add(oTask.getAssignee());
        }
        
        JSONParser parser = new JSONParser();
        
        List<String> aResultLogins = new ArrayList<>();
        
        if(aByteTaskBody != null){
            Map<String, Object> mProcessSubjectTask = JsonRestUtils.readObject(new String(aByteTaskBody), Map.class);
            //JSONArray aJsonProcessSubject =  (JSONArray) parser.parse((String)mProcessSubjectTask.get("aProcessSubject"));
            JSONArray aJsonProcessSubject =  (JSONArray) ((JSONObject)parser.parse(new String(aByteTaskBody))).get("aProcessSubject");
            LOG.info("aJsonProcessSubject in getProcessSubjectLoginsWithoutTask: {}", aJsonProcessSubject.toJSONString());
            for (Object oJsonProcessSubject : aJsonProcessSubject) {
                /*Map<String, Object> mProcessSubject
                        = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);*/
                
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
            JSONParser parser = new JSONParser();
            /*JSONArray aJsonProcessSubjectTask =  new JSONArray();
            aJsonProcessSubjectTask = (JSONArray) oaProcessSubjectTask.get("");*/
            
            //oTaskService.setVariable(snId_Task, "sID_File_StorateTemp", sKey);
            LOG.info("aJsonProcessSubjectTask in synctProcessSubjectTask: {}", oaProcessSubjectTask.toJSONString());
            
            //for(Object oJsonProcessSubjectTask :  aJsonProcessSubjectTask){
            for(Object oJsonProcessSubjectTask :  oaProcessSubjectTask){
                //Map<String, Object> mProcessSubjectTask = JsonRestUtils.readObject(oJsonProcessSubjectTask, Map.class);
                //JSONArray aJsonProcessSubject =  (JSONArray) parser.parse((String)mProcessSubjectTask.get("aProcessSubject"));
                JSONArray aJsonProcessSubject =  (JSONArray) ((JSONObject)oJsonProcessSubjectTask).get("aProcessSubject");
                LOG.info("oJsonProcessSubjectTask in oJsonProcessSubjectTask: {}", oJsonProcessSubjectTask);
                //ProcessSubject oProcessSubjectParent = oProcessSubjectDao.findByProcessActivitiId((String)mProcessSubjectTask.get("snID_Process_Activiti_Root"));
                if(((JSONObject)oJsonProcessSubjectTask).get("ProcessSubjectTask") == null){
                    //this is a new process
                    String sKey = oBytesDataInmemoryStorage.putBytes(((JSONObject)oJsonProcessSubjectTask).toJSONString().getBytes());
                    LOG.info("Redis key in synctProcessSubjectTask: {}", sKey);

                    ProcessSubjectTask oProcessSubjectTask = new ProcessSubjectTask();
                    /// по snId_Task вытягиваем id процесса
                    oProcessSubjectTask.setSnID_Process_Activiti_Root((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"));
                    oProcessSubjectTask.setsBody((String)((JSONObject)oJsonProcessSubjectTask).get("sBody"));
                    oProcessSubjectTask.setsHead((String)((JSONObject)oJsonProcessSubjectTask).get("sHead"));
                    /*oProcessSubjectTask.setaProcessSubject(aProcessSubject);*/
                    oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);
                    LOG.info("oProcessSubjectTask in synctProcessSubjectTask: {}", oProcessSubjectTask);
                    Map<String, Object> mParamTask = new HashMap<>();
                    
                    mParamTask.put("sID_File_StorateTemp", sKey); 
                    mParamTask.put("sID_Order_Document", oGeneralConfig.
                                getOrderId_ByProcess((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root")));
            
                    ProcessInstance oProcessInstance = oRuntimeService.startProcessInstanceByKey((String) ((JSONObject)oJsonProcessSubjectTask).get("sID_BP"), mParamTask); 
                    
                    List<ProcessSubject> aProcessSubject = 
                            setProcessSubjectList(aJsonProcessSubject, (JSONObject)oJsonProcessSubjectTask, oProcessSubjectTask, oProcessInstance.getId());
                    LOG.info("aProcessSubject in synctProcessSubjectTask: {}", aProcessSubject);
                }else{
                    //this is a process edit
                }
            }
        }
        catch (Exception ex){
            throw new RuntimeException("Error task setting: " + ex.getMessage());
        }
    }
    
}
