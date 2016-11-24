package org.igov.model.process;
import org.igov.model.core.EntityDao;
import org.joda.time.DateTime;


public interface ProcessSubjectDao extends EntityDao<Long, ProcessSubject>{
    
    Long setProcessSubject(String snID_Process_Activiti__Parent, String sLogin, DateTime sDatePlan, Long nOrder);
    
    ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin);
    
    ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder);
    
    ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, Long nID_ProcessSubjectStatus);
    
    ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, DateTime sDatePlan);
}