package org.igov.service.listener;

import java.util.List;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 *
 * @author cepera
 */
@Component("assignGroupByPrefix")
public class AssignGroupByPrefixListener implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(AssignGroupByPrefixListener.class);
    private Expression resultField_Group;
    private Expression suffix_chief;
    private Expression suffix_empolyee;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String chief = getStringFromFieldExpression(this.suffix_chief, execution);
        LOG.info("suffix_chief: " + chief);
        String employee = getStringFromFieldExpression(this.suffix_empolyee, execution);
        LOG.info("suffix_empolyee: " + employee);
        String resultField_Group = getStringFromFieldExpression(this.resultField_Group, execution);
        LOG.info("resultField_Group: " + resultField_Group);
        
        // getting TaskOwner's user
        String sTaskOwner = delegateTask.getAssignee();
        LOG.info("TaskOwner: " + sTaskOwner);
        
        // List of TaskOwner's group
        List<Group> ownerGroup = execution.getEngineServices().getIdentityService().createGroupQuery()
                .groupMember(sTaskOwner).list();
        long nCountOfGroups = ownerGroup.stream()
                .filter(group -> group.getId().endsWith(chief)).count();
        LOG.info(String.format("Count of groups like %s is %s", chief, nCountOfGroups));

        if (nCountOfGroups != 1) {
            LOG.warn(String.format("Groups %s for users like boss are absent", chief));
            throw new ActivitiIllegalArgumentException(String.format("Групи %s для співробітника не існує, або їх декілька", chief));
        }
        
        Group oEmployeesGroup = ownerGroup.stream()
                .filter(group -> group.getId().endsWith(chief))
                .findFirst()
                .get();
        
        //Employees group
        String sEmploeesGroup = oEmployeesGroup.getId().replace(chief, employee);
        LOG.info("sEmploeesGroup: " + sEmploeesGroup );
        
        /*        
        long nCountOfEmployeesGroup = ownerGroup.stream()
                .filter(group -> group.getId().equals(sEmploeesGroup)).count();
        
        if (nCountOfEmployeesGroup != 1) {
            LOG.warn("Number emloyees groups for user is " + nCountOfEmployeesGroup);
            throw new ActivitiIllegalArgumentException("Групи підлеглих для співробітника як керівника не існує, або їх декілька");
        } 
        */
        
        execution.setVariable(resultField_Group, sEmploeesGroup);       

    }

}
