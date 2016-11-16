package org.igov.service.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/*
 Необходимо реализовать слушатель, который определяет принадлежность указанного пользователя указанной группе
 Входные параметры:
 - id пользователя
 - название группы
 Выходной параметр:
 - id поля (стандартный ResultOut), куда записать результат ( "true" - если пользователь принадлежит группе, "false" - в противном случае)
 */

@Component("checkUserGroup")
public class CheckAreUserInGroup_Listener implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(CheckAreUserInGroup_Listener.class);

    @Autowired
    private RuntimeService runtimeService;
    public Expression sUsernameListener;   // Listener's переменные :
    public Expression sGroupListener;
    public Expression resultListener;

    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();
        String sUsername1 = getStringFromFieldExpression(this.sUsernameListener, execution);  // работает, это переменная Listener
        //String sUsername2 = delegateTask.getAssignee(); // username того, кому назначена таска
        //String sUsername3 = (String) runtimeService.getVariable(execution.getProcessInstanceId(), "sUsername");  // работает, это верхняя переменная, Процесса

        String sGroup = getStringFromFieldExpression(this.sGroupListener, execution);
        runtimeService.setVariable(execution.getProcessInstanceId(), "ResultOut", "false");

        boolean inGrp = false;
        List<Group> group = execution.getEngineServices().getIdentityService().createGroupQuery()
                .groupMember(sUsername1).list();

        if (group.size() == 0) {
            execution.setVariable(getStringFromFieldExpression(this.resultListener, execution), "No such user");
        } else {
            for (Group grp : group) {
                if (grp.getId().equals(sGroup)) {
                    inGrp = true;
                }
            }
            if (inGrp == true ) {
              //  runtimeService.setVariable(execution.getProcessInstanceId(), getStringFromFieldExpression(this.resultListener, execution), "true");
                execution.setVariable(getStringFromFieldExpression(this.resultListener, execution), "True");
            } else {
              //  runtimeService.setVariable(execution.getProcessInstanceId(), getStringFromFieldExpression(this.resultListener, execution), "false");
                execution.setVariable(getStringFromFieldExpression(this.resultListener, execution), "False");
            }
        }
    }
}
