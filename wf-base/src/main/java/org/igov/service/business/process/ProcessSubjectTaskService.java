package org.igov.service.business.process;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;

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
        List<ProcessSubject> aProcessSubject = new ArrayList<>();
        for (Object oJsonProcessSubject : aJsonProcessSubject) {
            /*Map<String, Object> mProcessSubject
                    = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);*/
            
            LOG.info("mProcessSubject in setProcessSubjectList: {}", ((JSONObject)oJsonProcessSubject).toJSONString());
            ProcessSubject oProcessSubject = new ProcessSubject();
            oProcessSubject.setsTextType((String) ((JSONObject)oJsonProcessSubjectTask).get("sTextType"));
            oProcessSubject.setsLogin((String) ((JSONObject)oJsonProcessSubject).get("sLogin"));
            oProcessSubject.setsLoginRole((String) ((JSONObject)oJsonProcessSubject).get("‘sLoginRole"));
            oProcessSubject.setoProcessSubjectTask(oProcessSubjectTask);
            
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
            
            oDocumentStepService.cloneDocumentStepSubject((String)((JSONObject)oJsonProcessSubjectTask).get("snID_Process_Activiti_Root"), 
                    (String)((JSONObject)oJsonProcessSubjectTask).get("sKey_GroupPostfix"), (String) ((JSONObject)oJsonProcessSubjectTask).get("sLogin"), "_", false);
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
        String sKeyRedis = (String)oRuntimeService.getVariable(snID_Process_Activiti, "sID_File_StorateTemp");
        byte[] aByteTaskBody = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
        
        JSONParser parser = new JSONParser();
        List<String> aResultLogins = new ArrayList<>();
        
        if(aByteTaskBody != null){
            Map<String, Object> mProcessSubjectTask = JsonRestUtils.readObject(new String(aByteTaskBody), Map.class);
            JSONArray aJsonProcessSubject =  (JSONArray) parser.parse((String)mProcessSubjectTask.get("aProcessSubject"));
            
            for (Object oJsonProcessSubject : aJsonProcessSubject) {
                Map<String, Object> mProcessSubject
                        = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);
                
                if(sFilterLoginRole != null && !sFilterLoginRole.equals(""))
                {
                    if(sFilterLoginRole.equals((String) mProcessSubject.get("‘sLoginRole"))){
                        aResultLogins.add((String) mProcessSubject.get("sLogin"));
                    }
                }else{
                    aResultLogins.add((String) mProcessSubject.get("sLogin"));
                }
            }
        }
        
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
