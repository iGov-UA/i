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
import org.igov.model.process.ProcessSubjectTask;
import org.igov.model.process.ProcessSubjectTaskDao;

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
    
    @Autowired
    ProcessSubjectTaskDao oProcessSubjectTaskDao;
    
    private final static Logger LOG = LoggerFactory.getLogger(SyncProcessSubject.class);
    
    @Override
    public void notify(DelegateTask oDelegateTask) {
        
        String snID_Process_Activiti = oDelegateTask.getProcessInstanceId();
      
        String snID_Task_Activiti = oTaskService.createTaskQuery().processInstanceId(snID_Process_Activiti).active().singleResult().getId();
        LOG.info("SyncProcessSubject: snID_Process_Activiti={}", snID_Process_Activiti);
        
        Set<IdentityLink> aoCandidates = oDelegateTask.getCandidates();
        LOG.info("SyncProcessSubject: выбраные кандидаты aoCandidates={}", aoCandidates);
        
        //Login = UserId
        List<String> asLogin = new ArrayList<>();
        
        for (IdentityLink oCandidateLink : aoCandidates) {
            
            String sLogin = oCandidateLink.getUserId();
            
            if (sLogin != null) {
            
                asLogin.add(sLogin);
            
            }
        }
        
        LOG.info("SyncProcessSubject: выбраные логины asLogin={}", asLogin);
        
        for (String login : asLogin) {
        
            ProcessSubject oProcessSubject = oProcessSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, login);
            LOG.info("SyncProcessSubject: oProcessSubject={}", oProcessSubject);
            
            ProcessSubjectTask oProcessSubjectTask = oProcessSubjectTaskDao.findByExpected("nID", snID_Task_Activiti);
            LOG.info("SyncProcessSubject: oProcessSubjectTask={}", oProcessSubjectTask);
            
            oProcessSubject.setoProcessSubjectTask(oProcessSubjectTask);
            
            oProcessSubjectDao.saveOrUpdate(oProcessSubject);
        
        }
    }
    
}
