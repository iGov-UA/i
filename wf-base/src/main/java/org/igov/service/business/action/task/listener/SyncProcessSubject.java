package org.igov.service.business.action.task.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;

import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author idenysenko
 */
@Component("SyncProcessSubject")
public class SyncProcessSubject implements TaskListener {
    
    @Autowired
    TaskService oTaskService;
    
    @Autowired
    private ProcessSubjectDao oProcessSubjectDao;
        
    private final static Logger LOG = LoggerFactory.getLogger(SyncProcessSubject.class);
    
    @Override
    public void notify(DelegateTask oDelegateTask) {
        
        String snID_Process_Activiti = oDelegateTask.getProcessInstanceId();
      
        String snID_Task_Activiti = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active().singleResult().getId();
        
        Set<IdentityLink> aoCandidates = oDelegateTask.getCandidates();
        LOG.info("SyncProcessSubject: выбраные кандидаты aoCandidates={}", aoCandidates);
        
        //Login = GroupId
        List<String> asLogin = new ArrayList<>();
        
        for (IdentityLink oCandidateLink : aoCandidates) {
            
            String sCandidateGroupId = oCandidateLink.getGroupId();
            
            if (sCandidateGroupId != null) {
            
                asLogin.add(sCandidateGroupId);            
            }
        }
        
        if (asLogin.isEmpty() || asLogin.size() > 1) {
        
            throw new RuntimeException("Task has several candidates or no one.");
        }
        
        String sLogin = asLogin.get(0);
        
        ProcessSubject oProcessSubject = oProcessSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLogin);
            
        oProcessSubject.setSnID_Task_Activiti(snID_Task_Activiti);
            
        oProcessSubjectDao.saveOrUpdate(oProcessSubject);
    }
    
}
