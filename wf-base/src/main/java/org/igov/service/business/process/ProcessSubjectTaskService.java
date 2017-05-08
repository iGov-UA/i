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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author idenysenko
 */

@Service
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
            Map<String, Object> mProcessSubjectTask, ProcessSubjectTask oProcessSubjectTask, String snID_Process_Activiti) throws ParseException, Exception 
    {
        List<ProcessSubject> aProcessSubject = new ArrayList<>();
        for (Object oJsonProcessSubject : aJsonProcessSubject) {
            Map<String, Object> mProcessSubject
                    = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);
            
            ProcessSubject oProcessSubject = new ProcessSubject();
            oProcessSubject.setsTextType((String) mProcessSubjectTask.get("sTextType"));
            oProcessSubject.setsLogin((String) mProcessSubject.get("sLogin"));
            oProcessSubject.setsLoginRole((String) mProcessSubject.get("‘sLoginRole"));
            oProcessSubject.setoProcessSubjectTask(oProcessSubjectTask);
            
            /*if(((String) mProcessSubject.get("‘sLoginRole")).equals("Controller")){
                mParamTask.put("sLoginController", mProcessSubject.get("sLogin")); //только в бд!!!
            }*/
            
            oProcessSubject.setSnID_Process_Activiti(snID_Process_Activiti);                                                                                   
            DateTime datePlan = null;

            if (mProcessSubject.get("sDatePlan") != null) {
                datePlan = new DateTime(oProcessSubjectService.parseDate(
                        (String) mProcessSubject.get("sDatePlan")));
            }

            oProcessSubject.setsDatePlan(datePlan);
            aProcessSubject.add(oProcessSubject);
            
            oDocumentStepService.cloneDocumentStepSubject((String)mProcessSubjectTask.get("snID_Process_Activiti_Root"), 
                    (String)mProcessSubjectTask.get("sKey_Step_Document_To"), (String) mProcessSubject.get("sLogin"), "_", false);
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
    
    public void setProcessSubjectTaskList(Object oaProcessSubjectTask, String snId_Task){
        
        try{
            
            JSONArray aJsonProcessSubjectTask =  new JSONArray();
            aJsonProcessSubjectTask = (JSONArray) oaProcessSubjectTask;
            
            String sKey = oBytesDataInmemoryStorage.putBytes(aJsonProcessSubjectTask.toJSONString().getBytes());
            //oTaskService.setVariable(snId_Task, "sID_File_StorateTemp", sKey);
            
            for(Object oJsonProcessSubjectTask :  aJsonProcessSubjectTask){
                Map<String, Object> mProcessSubjectTask = JsonRestUtils.readObject((String)oJsonProcessSubjectTask, Map.class);
                JSONArray aJsonProcessSubject =  (JSONArray) mProcessSubjectTask.get("aProcessSubject");
                //ProcessSubject oProcessSubjectParent = oProcessSubjectDao.findByProcessActivitiId((String)mProcessSubjectTask.get("snID_Process_Activiti_Root"));
                
                if(mProcessSubjectTask.get("ProcessSubjectTask") == null){
                    //this is a new process
                    ProcessSubjectTask oProcessSubjectTask = new ProcessSubjectTask();
                    /// по snId_Task вытягиваем id процесса
                    oProcessSubjectTask.setSnID_Process_Activiti_Root((String)mProcessSubjectTask.get("snID_Process_Activiti_Root"));
                    oProcessSubjectTask.setsBody((String)mProcessSubjectTask.get("sBody"));
                    oProcessSubjectTask.setsHead((String)mProcessSubjectTask.get("sHead"));
                    /*oProcessSubjectTask.setaProcessSubject(aProcessSubject);*/
                    oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);
                    
                    Map<String, Object> mParamTask = new HashMap<>();
                    
                    mParamTask.put("sID_File_StorateTemp", sKey); 
                    mParamTask.put("sID_Order_Document", oGeneralConfig.
                    getOrderId_ByProcess((String)mProcessSubjectTask.get("snID_Process_Activiti_Root")));
            
                    ProcessInstance oProcessInstance = oRuntimeService.startProcessInstanceByKey((String) mProcessSubjectTask.get("sID_BP"), mParamTask); 
                    
                    List<ProcessSubject> aProcessSubject = 
                            setProcessSubjectList(aJsonProcessSubject, mProcessSubjectTask, oProcessSubjectTask, oProcessInstance.getId());
                    
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
