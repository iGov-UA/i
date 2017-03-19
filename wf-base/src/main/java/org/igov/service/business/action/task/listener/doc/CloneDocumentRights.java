package org.igov.service.business.action.task.listener.doc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.IdentityLink;
import org.igov.model.document.DocumentStepSubjectRight;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.document.DocumentStepService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.igov.service.business.process.ProcessSubjectService;
import org.igov.util.Tool;

/**
 *
 * @author Kovilin
 */
@Component("CloneDocumentRights")
public class CloneDocumentRights implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sKey_GroupPostfix;
    private Expression sKey_GroupPostfix_New;
    private Expression sKey_Step;
    
    @Autowired
    private DocumentStepService oDocumentStepService;
    
    @Autowired
    private HistoryService oHistoryService;
    
    @Override
    public void notify(DelegateTask delegateTask) {

        LOG.info("CloneDocumentRights start..." + delegateTask.getProcessInstanceId());
        
        HistoricProcessInstance oHistoricProcessInstance = oHistoryService.createHistoricProcessInstanceQuery().
                processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
        
        LOG.info("start user id is {}", oHistoricProcessInstance.getStartUserId());
        
        String sKey_GroupPostfix_Value = "";
        try {
            sKey_GroupPostfix_Value
                    = (this.sKey_GroupPostfix != null) ? getStringFromFieldExpression(this.sKey_GroupPostfix, delegateTask.getExecution()) : "";
            LOG.info("sKey_GroupPostfix_Value in clone listener is {}", sKey_GroupPostfix_Value);
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_Value: ", ex);
        }

        String sKey_GroupPostfix_New_Value = "";
        try {
            sKey_GroupPostfix_New_Value = (this.sKey_GroupPostfix_New != null) ? getStringFromFieldExpression(this.sKey_GroupPostfix_New, delegateTask.getExecution()) : 
                    oHistoricProcessInstance.getStartUserId();
            LOG.info("sKey_GroupPostfix_New_Value in clone listener is {}", sKey_GroupPostfix_New_Value);
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_New_Value: ", ex);
        }
        
        String sKey_Step_Value = "";
        try {
            sKey_Step_Value = (this.sKey_Step != null) ? getStringFromFieldExpression(this.sKey_Step, delegateTask.getExecution()) : "";
            LOG.info("sKey_Step_Value {}", sKey_Step_Value);
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_New_Value: ", ex);
        }
        
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStepService.cloneDocumentStepSubject(delegateTask.getProcessInstanceId(), sKey_GroupPostfix_Value, sKey_GroupPostfix_New_Value, sKey_Step_Value);
        
        /*Set<String> asGroup = new HashSet<>();
       
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                    asGroup.add(oDocumentStepSubjectRight.getsKey_GroupPostfix());
        }*/
        
        
        Set<String> asGroup_New = new HashSet<>();
        Set<IdentityLink> groupsNew = delegateTask.getCandidates();
        groupsNew.stream().forEach((groupNew) -> {
            asGroup_New.add(groupNew.getGroupId());
        });
        
        LOG.info("asGroup in clonedocument: {}", asGroup_New, delegateTask.getId());
        
        /*void addCandidateStarterGroup(String processDefinitionId,
                              String groupId)*/
       
        //delegateTask.addCandidateGroups(asGroup);
        
        LOG.info("aDocumentStepSubjectRight in listenet is: {} ", aDocumentStepSubjectRight);
        LOG.info("CloneDocumentRights finished");
    }
}
