package org.igov.service.business.process;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;

import org.igov.model.process.ProcessSubjectTask;
import org.igov.model.process.ProcessSubjectTaskDao;
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
    
    private List<ProcessSubject> setProcessSubjectList(JSONArray aJsonProcessSubject, Map<String, Object> mProcessSubjectTask) throws ParseException 
    {
        List<ProcessSubject> aProcessSubject = new ArrayList<>();
        for (Object oJsonProcessSubject : aJsonProcessSubject) {
            Map<String, Object> mProcessSubject
                    = JsonRestUtils.readObject((String) oJsonProcessSubject, Map.class);
            ProcessSubject oProcessSubject = new ProcessSubject();
            oProcessSubject.setsReportType((String) mProcessSubjectTask.get("sReportType"));
            oProcessSubject.setsLogin((String) mProcessSubject.get("sLogin"));

            DateTime datePlan = null;

            if (mProcessSubject.get("sDatePlan") != null) {
                datePlan = new DateTime(oProcessSubjectService.parseDate(
                        (String) mProcessSubject.get("sDatePlan")));
            }

            oProcessSubject.setsDatePlan(datePlan);
            aProcessSubject.add(oProcessSubject);
        }
        
        return aProcessSubject;
    }
    
    public void setProcessSubjectTaskList(Object oaProcessSubjectTask){
        
        try{
            JSONArray aJsonProcessSubjectTask =  new JSONArray();
            aJsonProcessSubjectTask = (JSONArray) oaProcessSubjectTask;

            for(Object oJsonProcessSubjectTask :  aJsonProcessSubjectTask){
                Map<String, Object> mProcessSubjectTask = JsonRestUtils.readObject((String)oJsonProcessSubjectTask, Map.class);
                JSONArray aJsonProcessSubject =  (JSONArray) mProcessSubjectTask.get("aProcessSubject");

                if(mProcessSubjectTask.get("ProcessSubjectTask") == null){
                    //this is a new process
                    ProcessSubjectTask oProcessSubjectTask = new ProcessSubjectTask();
                    oProcessSubjectTask.setSnID_Process_Activiti_Root((String)mProcessSubjectTask.get("snID_Process_Activiti_Root"));
                    oProcessSubjectTask.setsBody((String)mProcessSubjectTask.get("sBody"));
                    oProcessSubjectTask.setsHead((String)mProcessSubjectTask.get("sHead"));

                    List<ProcessSubject> aProcessSubject = 
                            setProcessSubjectList(aJsonProcessSubject, mProcessSubjectTask);
                    oProcessSubjectTask.setaProcessSubject(aProcessSubject);
                    oProcessSubjectTaskDao.saveOrUpdate(oProcessSubjectTask);
                    
                }else{
                    //this is a process edit
                }

                //oProcessSubjectTask.s
            }
        }
        catch (Exception ex){
            throw new RuntimeException("Error task setting: " + ex.getMessage());
        }
    }
    
}
