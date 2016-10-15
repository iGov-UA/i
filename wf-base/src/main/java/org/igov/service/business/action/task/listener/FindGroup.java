package org.igov.service.business.action.task.listener;

import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.springframework.stereotype.Component;

/**
 *
 * @author HS
 */
@Component("FindGroup")
public class FindGroup implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(FindGroup.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String group_pattern = (String) execution.getVariable("group_pattern");
        execution.setVariable("group_pattern_last", group_pattern);
        LOG.info("group_pattern: " + group_pattern);
        // определение user'а
        String user = delegateTask.getAssignee();
        LOG.info("user: " + user);
        execution.setVariable("user_last", user);
        // определение списка групп у user
        List<Group> userGroup;

        userGroup = execution.getEngineServices().getIdentityService().createGroupQuery().groupMember(user).list();

        // определение размера списка групп
        userGroup.size();
        LOG.info("userGroup.size: " + userGroup.size());

        List<Group> aGroupPatternFound = new ArrayList<Group>();
        aGroupPatternFound.size();
        LOG.info("aGroupPatternFound.size(): " + aGroupPatternFound.size()); //вывод размера коллекции до добавления элемента 

        for (int i = 0; i < userGroup.size(); i++) {
            LOG.info("userGroup.get(i).getId(): " + userGroup.get(i).getId());
            if (userGroup.get(i).getId().contains(group_pattern)) {
                aGroupPatternFound.add(userGroup.get(i));
                LOG.info("aGroupPatternFound.size(): " + aGroupPatternFound.size()); 
            }
        }

        //Проверка количеcтва элементов в коллекции aGroupPatternFound. Если отлично от единицы, то передать в переменную usertask2 none. Иначе название группы
        if (aGroupPatternFound.size() < 1) {
            LOG.info("aGroupPatternFound.size(): " + aGroupPatternFound.size());
            execution.setVariable("group_found", "[none]");
            
        } else if (aGroupPatternFound.size() > 1) {
            execution.setVariable("group_found", "[many]");
        } else {
           
            execution.setVariable("group_found", aGroupPatternFound.get(0).getId());     // передача ид-группы в значение group_found 

        }

    }

}
